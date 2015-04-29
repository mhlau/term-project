package edu.brown.cs.group.term_project;

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
import com.google.gson.JsonObject;
import edu.brown.cs.group.matcher.SongMatcher;
import edu.brown.cs.group.lyricFinder.Song;
import edu.brown.cs.group.ytsearch.YouTubeSearchRunner;

public class Gui {
  private static SongMatcher sm; 
  private static final Gson GSON = new Gson(); 
  private static final int PORT = 5235;

  public Gui(SongMatcher sm) {
    this.sm = sm;
    runSparkServer();
  }
  
  private static void runSparkServer() {
    Spark.setPort(PORT);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.get("/", new InitialLoadHandler(), new FreeMarkerEngine());
    Spark.post("/result", new YtVideoHandler());
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
  
  private static class YtVideoHandler implements Route {
    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String searchVal = qm.value("searchVal");
      String embedUrl = "";
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
          YouTubeSearchRunner.search(res.get(0).getTitle()
           + " " +  res.get(0).getArtist());
        }
        
        embedUrl = YouTubeSearchRunner.embedUrl();
        resultObject.addProperty("resultUrl", embedUrl);
      }
      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("resultUrl", resultObject)
          .build();
      return GSON.toJson(variables);
    }
  }
  
}
