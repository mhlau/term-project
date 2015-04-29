package edu.brown.cs.group.ytsearch;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

/**
 * Class for running YouTube searches from search query strings.
 * @author Micah Lau (mhlau)
 *
 */
public class YouTubeSearchRunner {

  private static final String PROPERTIES_FILEPATH =
      "src/main/java/edu/brown/cs/group/ytsearch/youtube.properties";
  private static final String YOUTUBE_VIDEO_URL_PREFIX =
      "https://www.youtube.com/watch?v=";
  private static final String YOUTUBE_EMBED_URL_PREFIX = 
      "https://www.youtube.com/embed/";
  private static final long NUM_VIDEOS_RETURNED = 1;
  private static final boolean DEBUG = true;
  private static YouTube youtube;
  private static List<SearchResult> searchResultList;
  private static String resultUrl;
  private static String embedUrl;

  /**
   * Private constructor - contains only static methods.
   */
  private YouTubeSearchRunner() {

  }

  /**
   * Performs a YouTube search given a query string.
   * @param query The query string to search.
   */
  public static void search(String query) {
    // Set up API key from property file.
    Properties properties = new Properties();
    try {
      FileInputStream in = new FileInputStream(PROPERTIES_FILEPATH);
      properties.load(in);
    } catch (IOException e) {
      System.err.println(
          "ERROR: There was a problem reading " + PROPERTIES_FILEPATH
           + ": " + e.getCause() + ": " + e.getMessage());
    }
    // Perform search.
    try {
      youtube = new YouTube.Builder(
          new NetHttpTransport(),
          new JacksonFactory(),
          new HttpRequestInitializer() {
              @Override
              public void initialize(HttpRequest request) throws IOException {
              }
          })
          .setApplicationName("youtube-cmdline-search")
          .build();
      YouTube.Search.List search = youtube.search().list("id,snippet");
      String apiKey = properties.getProperty("youtube.apikey");
      search.setKey(apiKey);
      search.setQ(query);
      search.setType("video");
      search.setFields(
          "items(id/kind,id/videoId,snippet/title,snippet/"
          + "thumbnails/default/url)");
      search.setMaxResults(NUM_VIDEOS_RETURNED);
      SearchListResponse searchResponse = search.execute();
      searchResultList = searchResponse.getItems();
      setResults();
    } catch (GoogleJsonResponseException e) {
      System.err.println("ERROR: a service error occurred: "
          + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
    } catch (IOException e) {
      System.err.println(
          "ERROR: an I/O error occurred: "
          + e.getCause() + " : " + e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  /**
   * Uses the search result to set URL results.
   */
  private static void setResults() {
    Iterator<SearchResult> searchResultsIterator = searchResultList.iterator();
    while (searchResultsIterator.hasNext()) {
      SearchResult video = searchResultsIterator.next();
      ResourceId rId = video.getId();
      if (rId.getKind().equals("youtube#video")) {
        resultUrl = YOUTUBE_VIDEO_URL_PREFIX + rId.getVideoId();
        embedUrl = YOUTUBE_EMBED_URL_PREFIX + rId.getVideoId();
        if (DEBUG) {
          System.out.println("[DEBUG] Result URL: "
            + YOUTUBE_VIDEO_URL_PREFIX + rId.getVideoId());
          System.out.println("[DEBUG] Embed URL: " 
            + YOUTUBE_EMBED_URL_PREFIX + rId.getVideoId());
          System.out.println("[DEBUG] Title: " + video.getSnippet().getTitle());
        }
      }
    }
  }
  
  /**
   * Accesses the URL of the top video in the search.
   * @return The YouTube video URL.
   */
  public static String resultUrl() {
    return resultUrl;
  }
  
  /**
   * Accesses the YouTube URL for embedding purposes.
   * @return The YouTube embed URL.
   */
  public static String embedUrl() {
    return embedUrl;
  }

}
