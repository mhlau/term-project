package edu.brown.cs.group.lyricFinder;

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

public class SongDatabase {
  private final int NUM_THREADS = 4;
  private Connection conn;
  private Map<Integer, Song> idToSongMap;
  
  /*
  // Just for testing stuff
  public static void main(String args[]) {
    String testString = "Pink Floyd - See Emily Play Lyrics | SongMeanings";
    
    String[] test1 = testString.split(" - ");
    System.out.println("test1");
    for (String s : test1) {
      System.out.println(s);
    }
    System.out.println();
    
    String[] test2 = testString.split(" Lyrics | SongMeanings");
    System.out.println("test2");
    for (String s : test2) {
      System.out.println(s);
    }
    System.out.println();
    
    String replacement = testString.replace(" Lyrics | SongMeanings", "");
    String[] test3 = replacement.split(" - ");
    System.out.println("test3");
    for (String s : test3) {
      System.out.println(s);
    }
    
    String testLyrics = "What I've kept with me And what I've thrown away And where the hell I've ended up On this glory random day Were the things I really cared about Just left along the way For being too pent up and proud Woke up way too late Feeling hung over and old And the sun was shining bright And I walked barefoot down the road Started thing about my old man It seems that all men Want to get into a car and go anywhere? Here I stand, Sad and free I can't cry and I can't see What I've done God, what have I done So don't you know I'm numb, man No I don't feel a thing at all Cause its all smiles and business these days And I am indifferent to the loss I've faith that there's a soul Whose leading me around I wonder if she knows Which way is down I poured my heart out I poured my heart out It evaporated, see? Blind man on a canyon's edge Of a Panoramic scene Or maybe I'm a kite That's flying high & random Dangling a string Or slumped over in a vacant room Head on a stranger's knee I'm sure back home They think I've lost my mind.";
    String words = testLyrics.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    System.out.println(words);
    String[] wordsSplit = words.split("\\s+");
    for (String s : wordsSplit) {
      System.out.println(s);
    }
  }
  */
  
  public SongDatabase(String db, int songsToGetIfRebuild) throws ClassNotFoundException {
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + db;

    try {
      this.conn = DriverManager.getConnection(urlToDB);

      if (dbShouldBeBuilt()) {
        buildDatabase(songsToGetIfRebuild);
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      System.out.println(e.getErrorCode());
      System.out.println(e.getSQLState());
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
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

  private void buildDatabase(int numSongs) throws SQLException, InterruptedException {
    System.out.println("Building database...");
    String schema = "CREATE TABLE song(id INT PRIMARY KEY, artist TEXT, title TEXT, lyrics TEXT);";
    buildTable(schema);
    
    SongDatabaseThread[] sd = new SongDatabaseThread[NUM_THREADS];

    int start = 1;
    int songsPerThread = numSongs / NUM_THREADS;
    System.out.println("Songs per thread: " + songsPerThread);
    int end = songsPerThread;
    for (int i = 0; i < sd.length; i++) {
      sd[i] = new SongDatabaseThread(conn, start, end);
      sd[i].start();
      start = end;
      end += songsPerThread;
    }
    long startTime = System.currentTimeMillis();
    System.out.println("Threads started, now waiting to join...");
    for (SongDatabaseThread sdt : sd) {
      sdt.join();
    }
    long endTime = System.currentTimeMillis();
    long totalTime = endTime - startTime;
    System.out.println("Database built. Took " + totalTime + " ms");
  }

  private void buildTable(String schema) throws SQLException {
    PreparedStatement prep = conn.prepareStatement(schema);
    prep.executeUpdate();
    prep.close();
  }

  /**
   * 
   * @param id
   * @return
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

  /**
   * 
   * @return
   */
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
