package edu.brown.cs.group.term_project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import edu.brown.cs.group.matcher.SongMatcher;
import edu.brown.cs.group.speechtotext.LiveMode;
//import edu.brown.cs.group.speechtotext.SpeechThread;
import edu.brown.cs.group.lyricFinder.Song;
import edu.brown.cs.group.ytsearch.YouTubeSearchRunner;

public class Gui {
  
  private static SongMatcher sm; 
  private static List<String> bufferedRequests;
  private static List<JsonObject> bufferedResponses;   
  private static final boolean DEBUG = true;
  private static final Gson GSON = new Gson(); 
  private static final int PORT = 5235;
 // public static SpeechThread speechThread = null;
  public static Queue<String> words = new LinkedList<>();

  public Gui(SongMatcher sm) throws IOException {
    Gui.sm = sm;
    bufferedRequests = new ArrayList<String>();
    bufferedResponses = new ArrayList<JsonObject>();    
    runSparkServer();
  }
  
  private static void runSparkServer() {
    FreeMarkerEngine freeMarker =  new FreeMarkerEngine();
    Spark.setPort(PORT);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.get("/", new InitialLoadHandler(), freeMarker);
    Spark.post("/result", new YtVideoHandler());
    Spark.post("/record", new RecordHandler());
    Spark.get("/:id", new ReloadHandler(), freeMarker);   
  }
  
  private static class InitialLoadHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("title", "CS032 Term Project")
          .put("boxContents", "Enter search text here.")
          .put("oldResults", "")
          .put("resultsOrdering", "")
          .build();
      return new ModelAndView(variables, "term-project.ftl");
    }
  }
  
  
  private static class ReloadHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      int id = 0;
      boolean valid = true;
      
      try {
        id = Integer.valueOf(request.params(":id"));
      } catch (NumberFormatException e) {
        valid = false;
      }
      if (id/100000 >= bufferedRequests.size() || id/100000 < 0) {
        valid = false;
      }
      if (valid) {
        Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
            .put("title", "CS032 Term Project")
            .put("boxContents",  bufferedRequests.get(id/100000))
            .put("oldResults",  GSON.toJson(bufferedResponses.get(id/100000)))
            .put("resultsOrdering",  GSON.toJson(new JsonPrimitive(id%100000)))
            .build();
        return new ModelAndView(variables, "term-project.ftl");
      } else {
        Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("title", "CS032 Term Project")
          .put("boxContents", "Enter search text here.")
          .put("oldResults", "")
          .put("resultsOrdering", "")
          .build();
        return new ModelAndView(variables, "term-project.ftl");        
      }
    }
  }  
  private static class RecordHandler implements Route {
    @Override
    public Object handle(Request request, Response response) {
      if (DEBUG) {
        System.out.println("[DEBUG] Recording.");
      }
      
      System.out.println("start record handler");
      
      List<String> newWords = null;
      
      try {
    //	  if (speechThread == null){
//	    	  speechThread = new SpeechThread();
//	    	  speechThread.start();
  //  	  }
		  Thread.sleep(2000);
//	      newWords = speechThread.getWords();
	  } catch (InterruptedException e) {
		  e.printStackTrace();
//	  } catch (IOException e) {
		// TODO Auto-generated catch block

//		e.printStackTrace();
	}
      
//      LiveMode lm;
//      JsonArray resultArray = new JsonArray();
//      try {
//        lm = new LiveMode();
//        for (String word : lm.getWords()) {
//          JsonObject wordObject = new JsonObject();
//          wordObject.addProperty("word", word);
//          resultArray.add(wordObject);
//        }
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("words", newWords)
          .build();
      return GSON.toJson(variables);
    }
  }
  
  private static class YtVideoHandler implements Route {
    @Override
    public Object handle(Request request, Response response) {
      if (DEBUG) {
        System.out.println("[DEBUG] Youtube search being processed.");
      }
      QueryParamsMap qm = request.queryMap();
      String searchVal = qm.value("searchVal");
      String url = "";
      JsonObject resultObject = new JsonObject();
      JsonArray resultUrl = new JsonArray();
      JsonArray resultTitle = new JsonArray();
      
      JsonArray resultLyrics = new JsonArray();
      
      if (searchVal != null) {
        List<String> dialogue = new ArrayList<String>();
        Scanner sc = new Scanner(searchVal);
        sc.useDelimiter("[^a-zA-Z]");
        while (sc.hasNext()) {
          dialogue.add(sc.next().toLowerCase());
        }
        sc.close();
        List<Song> res = sm.match(dialogue, 5);
	
        if (res.size() > 0) {

          for (int i = 0; i < res.size(); i++) {
            YouTubeSearchRunner.search(res.get(i).getTitle()
                + " " +  res.get(i).getArtist());
     
            url = YouTubeSearchRunner.embedUrl();

            resultUrl.add(new JsonPrimitive(url));
            resultTitle.add(new JsonPrimitive(YouTubeSearchRunner.resultTitle()));
            resultLyrics.add(new JsonPrimitive(getLyricsHTML(res.get(i).getID())));
          }
        }
      }
      resultObject.add("resultUrl", resultUrl);
      resultObject.add("resultTitle", resultTitle);
      resultObject.add("resultLyrics", resultLyrics);
      resultObject.add("saveId", new JsonPrimitive(""+bufferedRequests.size()));
      bufferedRequests.add(searchVal);
      bufferedResponses.add(resultObject);
      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("result", resultObject)
          .build();
      return GSON.toJson(variables);
    }

    private String getLyricsHTML(int songID) {
      try {
        Document doc = Jsoup.connect("http://songmeanings.com/songs/view/" + songID).get();
        Elements lyrics = doc.body().getElementsByClass("lyric-box");
        String l = lyrics.first().html().replace("<div style=\"min-height: 25px; margin:0; padding: 12px 0 0 0; border-top: 1px dotted #ddd;\">", "");
        l = l.replace("<a href=\"/songs/edit/" + songID + "/\" id=\"lyrics-edit\" class=\"editbutton\" title=\"Edit Lyrics\">Edit Lyrics</a>", "");
        l = l.replace("<a href=\"/songs/edit/" + songID + "/?type=wiki\" id=\"lyrics-wiki-edit\" class=\"editbutton\" title=\"Edit Song Wiki\">Edit Wiki</a>", "");
        l = l.replace("<a href=\"/songs/edit/" + songID + "/?type=video\" id=\"lyrics-video-add\" class=\"editbutton\" title=\"Add Music Video\">Add Video</a>", "");
        return l.replace("</div>", "");

      } catch (IOException e) {
        System.err.println("ERROR: IOException when trying to get lyrics for video.");
        return "";
      }
    }
  }
  
}
