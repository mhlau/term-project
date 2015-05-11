package edu.brown.cs.group.term_project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.common.collect.ImmutableMap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;


import edu.brown.cs.group.lyricFinder.Song;
import edu.brown.cs.group.matcher.SongMatcher;
import edu.brown.cs.group.speechtotext.SpeechThread;
import edu.brown.cs.group.ytsearch.YouTubeSearchRunner;

import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;
/**
 * Class for handling graphical user interface.
 */
public final class Gui {
  private static SongMatcher sm;
  private static List<String> bufferedRequests;
  private static List<JsonObject> bufferedResponses;
  private static final boolean DEBUG = true;
  private static final Gson GSON = new Gson();
  private static final int PORT = 5235;
  private static Visualizer vis;
  private static SpeechThread speechThread = null;
  private static final int URL_OFFSET = 100000;
  private static final int WORD_WAIT = 500;
  private static final int NUM_RESULTS = 5;
  private static Queue<String> words = new LinkedList<>();
  /**
   * Filler to mark the class as static.
   */
  private Gui() {
    //static class...
  }
  /**
   * Initializer ste Gui, and starts the spark server.
   * @param sim the SongMatcher
   * @throws IOException if there is an io problem
   */
  public static void setup(SongMatcher sim) throws IOException {
    sm = sim;
    vis = new Visualizer();
    bufferedRequests = new ArrayList<String>();
    bufferedResponses = new ArrayList<JsonObject>();
    runSparkServer();
  }

  /**
   * Removes words from words, adds them to a list and returns that list.
   * @return the words
   */
  private static List<String> getWords() {
    List<String> toReturn = new ArrayList<>();

    while (!words.isEmpty()) {
      toReturn.add(words.poll());
    }

    return toReturn;
  }
  /**
   * Adds a word to the words list.
   * @param word a word
   */
  public static void addWord(String word) {
    words.add(word);
  }

  private static void runSparkServer() {
    FreeMarkerEngine freeMarker =  new FreeMarkerEngine();
    Spark.setPort(PORT);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.get("/", new InitialLoadHandler(), freeMarker);
    Spark.post("/result", new YtVideoHandler());
    Spark.post("/record", new RecordHandler());
    Spark.post("/visualize", new VisHandler());
    Spark.post("/download", new DownloadHandler());
    Spark.get("/:id", new ReloadHandler(), freeMarker);
  }
  /**
   * Handles initial page loading.
   */
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
  /**
   * Handles page loading from a saved url.
   */
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
      if (id / URL_OFFSET >= bufferedRequests.size() || id / URL_OFFSET < 0) {
        valid = false;
      }
      if (valid) {
        Map<String, Object> variables
          = new ImmutableMap.Builder<String, Object>()
            .put("title", "CS032 Term Project")
            .put("boxContents",  bufferedRequests.get(id / URL_OFFSET))
            .put("oldResults",  GSON.toJson(
               bufferedResponses.get(id / URL_OFFSET)))
            .put("resultsOrdering",  GSON.toJson(
               new JsonPrimitive(id % URL_OFFSET)))
            .build();
        return new ModelAndView(variables, "term-project.ftl");
      } else {
        Map<String, Object> variables
          = new ImmutableMap.Builder<String, Object>()
              .put("title", "CS032 Term Project")
              .put("boxContents", "Enter search text here.")
              .put("oldResults", "")
              .put("resultsOrdering", "")
              .build();
        return new ModelAndView(variables, "term-project.ftl");
      }
    }
  }
  /**
   * Handles requests for recorded sound.
   */
  private static class RecordHandler implements Route {
    @Override
    public Object handle(Request request, Response response) {
      if (DEBUG) {
        System.out.println("[DEBUG] Recording.");
      }
      List<String> newWords = null;
      try {
        if (speechThread == null) {
          speechThread = new SpeechThread();
          speechThread.start();
        }
        Thread.sleep(WORD_WAIT);
        newWords = getWords();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }

      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("words", newWords)
          .build();
      return GSON.toJson(variables);
    }
  }
  /**
   * Handles requests song recommendations, and returns Youtube links.
   */
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
        // Perform matching from input text to song name/artist.
        List<String> dialogue = new ArrayList<String>();
        Scanner sc = new Scanner(searchVal);
        sc.useDelimiter("[^a-zA-Z]");
        while (sc.hasNext()) {
          dialogue.add(sc.next().toLowerCase());
        }
        sc.close();
        List<Song> res = sm.match(dialogue, NUM_RESULTS);
        // Search Youtube for URLs corresponding to the top 5 matches.
        if (res.size() > 0) {
          for (int i = 0; i < res.size(); i++) {
            YouTubeSearchRunner.search(res.get(i).getTitle()
                + " " +  res.get(i).getArtist());
            url = YouTubeSearchRunner.embedUrl();
            resultUrl.add(new JsonPrimitive(url));
            resultTitle.add(new JsonPrimitive(
              YouTubeSearchRunner.resultTitle()));
            resultLyrics.add(new JsonPrimitive(
              getLyricsHTML(res.get(i).getID())));
          }
        }
      }
      resultObject.add("resultUrl", resultUrl);
      resultObject.add("resultTitle", resultTitle);
      resultObject.add("resultLyrics", resultLyrics);
      resultObject.add("saveId", new JsonPrimitive(
        "" + bufferedRequests.size()));
      bufferedRequests.add(searchVal);
      bufferedResponses.add(resultObject);
      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("result", resultObject)
          .build();
      return GSON.toJson(variables);
    }

    private String getLyricsHTML(int songID) {
      try {
        Document doc = Jsoup.connect("http://songmeanings.com/songs/view/"
          + songID).get();
        Elements lyrics = doc.body().getElementsByClass("lyric-box");
        String l = lyrics.first().html().replace("<div style="
          + "\"min-height: 25px; margin:0; padding: 12px 0 0 0; border-top:"
          + "1px dotted #ddd;\">", "");
        l = l.replace("<a href=\"/songs/edit/" + songID
          + "/\" id=\"lyrics-edit\" class=\"editbutton\""
          + "title=\"Edit Lyrics\">Edit Lyrics</a>", "");
        l = l.replace("<a href=\"/songs/edit/" + songID
          + "/?type=wiki\" id=\"lyrics-wiki-edit\" class="
          + "\"editbutton\" title=\"Edit Song Wiki\">Edit Wiki</a>", "");
        l = l.replace("<a href=\"/songs/edit/" + songID + "/?type=video\" id="
          + "\"lyrics-video-add\" class=\"editbutton\" title=\"Add Music Video"
          + "\">Add Video</a>", "");
        return l.replace("</div>", "");

      } catch (IOException e) {
        System.err.println("ERROR: IOException when trying to get"
          + "lyrics for video.");
        return "";
      }
    }
  }
  /**
   * Handles requests for Visualizer data.
   */
  private static class VisHandler implements Route {
    @Override
    public Object handle(Request request, Response response) {
      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("level", vis.getWord())
          .build();
      return GSON.toJson(variables);
    }
  }
  /**
   * Handles requests for song downloading.
   */
  private static class DownloadHandler implements Route {
    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String resultUrl = qm.value("currentResults");
      String[] commands = {"python", "youtube-dl/downloadAudio.py", resultUrl};
      String s;
      try {
        Process p = Runtime.getRuntime().exec(commands);
        BufferedReader stdInput = new BufferedReader(
            new InputStreamReader(p.getInputStream()));
        BufferedReader stdError = new BufferedReader(
            new InputStreamReader(p.getErrorStream()));
        if (DEBUG) {
          System.out.println("[DEBUG] Standard output of youtube-dl:");
          while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
          }
          System.out.println("[DEBUG] Standard error of youtube-dl:");
          while ((s = stdError.readLine()) != null) {
            System.out.println("[DEBUG] " + s);
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(1);
      }
      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("success", true)
          .build();
      return GSON.toJson(variables);
    }
  }
}
