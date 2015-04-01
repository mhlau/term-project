package edu.brown.cs.group.lyricFinder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SongDatabase {
  private Connection conn;
  // add maps later as caching
  
  /*
  // Just for testing stuff
  public static void main(String args[]) {
    System.out.println("Calling buildDatabase");
    buildDatabase("");
    System.out.println("Returned from buildDatabase");
  }
  */
  
  public SongDatabase(String db) throws ClassNotFoundException {
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + db;

    try {
      this.conn = DriverManager.getConnection(urlToDB);
    } catch (SQLException e) {
      // check for some sort of 'does not exist' error
      // if you get that, create the database (call buildDatabase())
      // if it's some other error, report it
      // TODO Auto-generated catch block
      System.out.println(e.getErrorCode());
      System.out.println(e.getSQLState());
      e.printStackTrace();
    }

    // if database doesn't already exist, build it...
    // otherwise, you're done
  }

  // this should prob not take anything and just use the connection...
  private static void buildDatabase(String path) {
    System.out.println("Building database...");
    // CREATE TABLE song(id INT, artist TEXT, title TEXT, lyrics TEXT);
    
    try {
      for (int id = 1; id <= 5; id++) {
        Document doc = Jsoup.connect("http://songmeanings.com/songs/view/" + id).get();

        // somehow catch if the page with this ID doesn't actually exist...
        String artistAndTitle = doc.title();
        if (artistAndTitle.equals("Error retrieving lyric")) {
          continue;
        }

        String[] split = artistAndTitle.split(" - "); // find a better regexp
        for (String s : split) { 
          System.out.println(s);
        }

        Elements lyrics = doc.body().getElementsByClass("lyric-box");
        for (Element l : lyrics) {
          String s = l.text();
          System.out.println(artistAndTitle + "\n " + s);
        }
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  // Will most likely not be needed
  private void connectToDatabase(String path) {
    
  }

  public Song getSong(int id) {
    return null;
  }

  public List<Song> getAllSongs() {
    return null;
  }
}
