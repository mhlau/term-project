package edu.brown.cs.group.lyricFinder;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.HttpStatusException;

public class SongDatabase {
  private final int NUM_OF_SONGS_TO_GET = 20;
  private Connection conn;
  private Map<Integer, Song> idToSongMap;
  
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

      if (dbShouldBeBuilt()) {
        buildDatabase();
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      System.out.println(e.getErrorCode());
      System.out.println(e.getSQLState());
      e.printStackTrace();
    }

    this.idToSongMap = new HashMap<>();
  }

  private boolean dbShouldBeBuilt() {
    DatabaseMetaData md;
    try {
      md = conn.getMetaData();
      ResultSet rs = md.getTables(null, null, "%", null);
      while (rs.next()) {
        if (rs.getString(3).equals("song")) {
          return false;
        }
      }
      return true;
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
  }

  private void buildDatabase() throws SQLException {
    System.out.println("Building database...");
    String schema = "CREATE TABLE song(id INT, artist TEXT, title TEXT, lyrics TEXT);";
    buildTable(schema);
    String insert = "INSERT INTO song VALUES(?,?,?,?);";
    PreparedStatement ps = conn.prepareStatement(insert);

    try {
      for (int id = 1; id <= NUM_OF_SONGS_TO_GET; id++) {
        try {
          Document doc = Jsoup.connect("http://songmeanings.com/songs/view/" + id).get();
          String artistAndTitle = doc.title();
          if (artistAndTitle.equals("Error retrieving lyric")) {
            continue;
          }

          String[] split = artistAndTitle.split(" - "); // find a better regexp
          /*
          for (String s : split) { 
            System.out.println(s);
          }
          */

          Elements lyrics = doc.body().getElementsByClass("lyric-box");
          for (Element l : lyrics) {
            String s = l.text();
            
            ps.setInt(1, id);
            ps.setString(2, split[0]);
            ps.setString(3, split[1]);
            ps.setString(4, s);
            //System.out.println(artistAndTitle + "\n " + s);
            ps.addBatch();
          }
        } catch (HttpStatusException | SocketTimeoutException e) {
          continue;
        }
      }

      ps.executeBatch();
      ps.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void buildTable(String schema) throws SQLException {
    PreparedStatement prep = conn.prepareStatement(schema);
    prep.executeUpdate();
    prep.close();
  }

  // Will most likely not be needed
  /*
  private void connectToDatabase(String path) {
    
  }
  */

  public Song getSong(int id) {
    if (idToSongMap.containsKey(id)) {
      return idToSongMap.get(id);
    }

    String query = "SELECT * FROM song WHERE id=?;";

    PreparedStatement ps;
    try {
      ps = conn.prepareStatement(query);
      ps.setInt(1, id);

      ResultSet rs = ps.executeQuery();

      Song song = null;
      if (rs.next()) {
        // create song
        List<String> lyrics = Arrays.asList(rs.getString(4).split(" "));
        song = new Song(rs.getInt(1), rs.getString(2), rs.getString(3), lyrics);
      }
      rs.close();

      idToSongMap.put(id, song);
      return song;
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
  }

  public List<Song> getAllSongs() {
    String query = "SELECT * FROM song;";

    PreparedStatement ps;
    try {
      ps = conn.prepareStatement(query);
      ResultSet rs = ps.executeQuery();

      List<Song> songs = new ArrayList<>();
      while (rs.next()) {
        int songID = rs.getInt(1);
        if (idToSongMap.containsKey(songID)) {
          songs.add(idToSongMap.get(songID));
        } else {
          List<String> lyrics = Arrays.asList(rs.getString(4).split(" "));
          Song song = new Song(songID, rs.getString(2), rs.getString(3), lyrics);
          idToSongMap.put(songID, song);
          songs.add(song);
        }
      }
      rs.close();

      return songs;
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
  }
}
