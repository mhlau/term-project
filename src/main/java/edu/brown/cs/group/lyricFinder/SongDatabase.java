package edu.brown.cs.group.lyricFinder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SongDatabase {
  private Connection conn;

  public SongDatabase(String db) throws ClassNotFoundException {
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + db;

    try {
      this.conn = DriverManager.getConnection(urlToDB);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      System.out.println(e.getErrorCode());
      e.printStackTrace();
    }

    // if database doesn't already exist, build it...
    // otherwise, you're done
  }

  public void buildDatabase(String path) {
    
  }

  public void connectToDatabase(String path) {
    
  }
}
