package edu.brown.cs.group.term_project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
import com.google.gson.JsonObject;

import edu.brown.cs.group.matcher.SongMatcher;
import edu.brown.cs.group.speechtotext.LiveMode;
import edu.brown.cs.group.lyricFinder.Song;
import edu.brown.cs.group.ytsearch.YouTubeSearchRunner;

public class Gui {
  
  private static SongMatcher sm; 
  private static final boolean DEBUG = true;
  private static final Gson GSON = new Gson(); 
  private static final int PORT = 5235;

  public Gui(SongMatcher sm) {
    Gui.sm = sm;
    runSparkServer();
  }
  
  private static void runSparkServer() {
    Spark.setPort(PORT);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.get("/", new InitialLoadHandler(), new FreeMarkerEngine());
    Spark.post("/result", new YtVideoHandler());
    Spark.post("/record", new RecordHandler());
  }
  
  private static class InitialLoadHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("title", "CS032 Term Project")
          .build();
      return new ModelAndView(variables, "term-project.ftl");
    }
  }
  
  private static class RecordHandler implements Route {
    @Override
    public Object handle(Request request, Response response) {
      if (DEBUG) {
        System.out.println("[DEBUG] Recording.");
      }
      LiveMode lm;
      JsonArray resultArray = new JsonArray();
      try {
        lm = new LiveMode();
        for (String word : lm.getWords()) {
          JsonObject wordObject = new JsonObject();
          wordObject.addProperty("word", word);
          resultArray.add(wordObject);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("words", resultArray)
          .build();
      return variables;
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
            if (i == 0) {
              url = YouTubeSearchRunner.embedUrl();
              resultObject.addProperty("embedUrl", url);
            } else {
              url = YouTubeSearchRunner.resultUrl();
              resultObject.addProperty("resultUrl" + i, url);
              resultObject.addProperty("resultTitle" + i,
                  YouTubeSearchRunner.resultTitle());
            }
          }
        }
      }
      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("result", resultObject)
          .build();
      return GSON.toJson(variables);
    }
  }
  
}
