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
  private final int SONG_ID_COLUMN = 1;
  private final int SONG_ARTIST_COLUMN = 2;
  private final int SONG_TITLE_COLUMN = 3;
  private final int SONG_LYRICS_COLUMN = 4;
  private final int NUM_THREADS = 4;
  private Connection conn;
  private Map<Integer, Song> idToSongMap;
  
  public SongDatabase(String db, int songsToGetIfRebuild) throws ClassNotFoundException {
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + db;

    try {
      this.conn = DriverManager.getConnection(urlToDB);

      if (dbShouldBeBuilt()) {
        buildDatabase(songsToGetIfRebuild);
      }
    } catch (SQLException e) {
      System.err.println("ERROR: SQLException when connecting to database or while building it.");
    } catch (InterruptedException e) {
      System.err.println("ERROR: InterruptedException while building database.");
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
      System.err.println("ERROR: SQLException when checking if database should be built.");
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
    System.out.println("Database built. Took " + totalTime + " milliseconds");
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
      System.err.println("ERROR: SQLException when getting song with ID " + id + ".");
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
        int songID = rs.getInt(SONG_ID_COLUMN);
        if (idToSongMap.containsKey(songID)) {
          songs.add(idToSongMap.get(songID));
        } else {
          List<String> lyrics =
              Arrays.asList(rs.getString(SONG_LYRICS_COLUMN).split(" "));
          Song song = new Song(songID, rs.getString(SONG_ARTIST_COLUMN),
                               rs.getString(SONG_TITLE_COLUMN), lyrics);
          idToSongMap.put(songID, song);
          songs.add(song);
        }
      }
      rs.close();

      return songs;
    } catch (SQLException e) {
      System.err.println("ERROR: SQLException when getting all songs.");
      return null;
    }
  }
}
