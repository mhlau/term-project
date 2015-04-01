package edu.brown.cs.group.lyricFinder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SongDatabase {
  private Connection conn;

  public SongDatabase(String db) throws ClassNotFoundException {
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + db;

    /*
    try {
      this.conn = DriverManager.getConnection(urlToDB);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      System.out.println(e.getErrorCode());
      e.printStackTrace();
    }

    // if database doesn't already exist, build it...
    // otherwise, you're done
     * 
     */
  }

  // this should prob not take anything and just use the connection...
  public void buildDatabase(String path) {
    try {
      for (int id = 1; id <= 5; id++) {
        Document doc = Jsoup.connect("http://songmeanings.com/songs/view/" + id).get();
        String artistAndTitle = doc.title();
        Elements lyrics = doc.body().getElementsByClass("holder lyric-box");
        for (Element l : lyrics) {
          String s = l.text();
          System.out.println(artistAndTitle + " " + s);
        }
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void connectToDatabase(String path) {
    
  }
}
