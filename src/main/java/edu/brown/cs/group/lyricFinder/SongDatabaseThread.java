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

/**
 *
 * @author cmg1
 *
 */
public class SongDatabaseThread extends Thread {
  private static final int REPORT_PROGRESS = 100;
  private Connection conn;
  private int start, end;

  /**
   *
   * @param conn .
   * @param start .
   * @param end .
   */
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
        if (id % REPORT_PROGRESS == 0) {
          System.out.println("Thread " + start + "-" + end
                           + "is on song " + id);
        }

        try {
          Document doc =
            Jsoup.connect("http://songmeanings.com/songs/view/" + id).get();
          String pageTitle = doc.title();
          if (pageTitle.equals("Error retrieving lyric")) {
            continue;
          }

          String artistAndTitle =
            pageTitle.replace(" Lyrics | SongMeanings", "");
          String[] split = artistAndTitle.split(" - ");

          Elements lyrics = doc.body().getElementsByClass("lyric-box");
          for (Element l : lyrics) {
            String s = l.text().replace(" Edit Lyrics Edit Wiki Add Video", "");
            String noPunc = s.replaceAll("[^a-zA-Z ]", "").toLowerCase();

            ps.setInt(SongDatabase.SONG_ID_COLUMN, id);
            ps.setString(SongDatabase.SONG_ARTIST_COLUMN, split[0]);
            ps.setString(SongDatabase.SONG_TITLE_COLUMN, split[1]);
            ps.setString(SongDatabase.SONG_LYRICS_COLUMN, noPunc);

            ps.addBatch();
          }
        } catch (HttpStatusException | SocketTimeoutException e) {
          continue;
        }
      }

      ps.executeBatch();
      ps.close();
    } catch (IOException e) {
      System.err.println("ERROR: IOException in thread " + start + "-" + end);
      return;
    } catch (SQLException e) {
      System.err.println("ERROR: SQLException in thread " + start + "-" + end);
      return;
    }

    System.out.println("Thread " + start + "-" + end + " finished.");
  }
}
