package edu.brown.cs.group.lyricFinder;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SongDatabaseThread extends Thread {
  private Connection conn;
  private int start, end;
  
  public SongDatabaseThread(Connection conn, int start, int end) {
    this.conn = conn;
    this.start = start;
    this.end = end;
  }

  @Override
  public void run() {
    System.out.println("Thread " + start + "-" + end + " started.");

    String insert = "INSERT INTO song VALUES(?,?,?,?);";

    try (PreparedStatement ps = conn.prepareStatement(insert)) {
      
      for (int id = start; id < end; id++) {
        try {
          Document doc = Jsoup.connect("http://songmeanings.com/songs/view/" + id).get();
          String pageTitle = doc.title();
          if (pageTitle.equals("Error retrieving lyric")) {
            continue;
          }

          String artistAndTitle = pageTitle.replace(" Lyrics | SongMeanings", "");
          String[] split = artistAndTitle.split(" - ");

          Elements lyrics = doc.body().getElementsByClass("lyric-box");
          for (Element l : lyrics) {
            String s = l.text().replace(" Edit Lyrics Edit Wiki Add Video", "");
            String noPunc = s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
            /*
            System.out.println("id: " + id);
            System.out.println("artist: " + split[0]);
            System.out.println("title: " + split[1]);
            System.out.println("lyrics: " + s);
            System.out.println();
            */
            ps.setInt(1, id);
            ps.setString(2, split[0]);
            ps.setString(3, split[1]);
            ps.setString(4, noPunc);

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
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    System.out.println("Thread " + start + "-" + end + " finished.");
  }
}
