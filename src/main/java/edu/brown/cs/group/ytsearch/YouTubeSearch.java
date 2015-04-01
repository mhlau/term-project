package edu.brown.cs.group.ytsearch;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Search;

public class YouTubeSearch {
  
  private static final String PROPERTIES_FILENAME = "youtube.properties";
  private static final long NUMBER_OF_VIDEOS_RETURNED = 1;
  private YouTube youtube;
  
  public YouTubeSearch(String query) {
    Properties properties = new Properties();
    try {
      InputStream inStream = Search.class.getResourceAsStream(
          "/" + PROPERTIES_FILENAME);
      properties.load(inStream);
    } catch (IOException e) {
      // TODO: Handle exeception better.
      System.err.println(
          "ERROR: There was a problem reading " + PROPERTIES_FILENAME
           + ": " + e.getCause() + ": " + e.getMessage());
    }
  }
  
}
