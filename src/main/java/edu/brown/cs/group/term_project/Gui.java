package edu.brown.cs.group.term_project;

import java.util.Map;

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

import edu.brown.cs.group.ytsearch.YouTubeSearchRunner;

public class Gui {
  
  private static final Gson GSON = new Gson(); 

  public Gui() {
    runSparkServer();
  }
  
  private static void runSparkServer() {
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
      System.out.println(searchVal);
      String embedUrl = "";
      JsonObject resultObject = new JsonObject();
      if (searchVal != null) {
        YouTubeSearchRunner.search(searchVal);
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
