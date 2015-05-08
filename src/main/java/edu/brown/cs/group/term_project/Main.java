package edu.brown.cs.group.term_project;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import edu.brown.cs.group.lyricFinder.Song;
import edu.brown.cs.group.lyricFinder.SongDatabase;
import edu.brown.cs.group.matcher.SongMatcher;
import edu.brown.cs.group.matcher.SongSourceDistance;
import edu.brown.cs.group.matcher.TwoSourceMatcher;

/**
 * Main class. main method of this class called on execution.
 */
public final class Main {

  private static final double TOPIC_WEIGHT = 0.8;
  private static final int NUM_ARGS = 3;

  private Main() {

  }

  /**
   * main method. Called on program execution.
   * @param args command line arguments
   */
  public static void main(String[] args) {
    if (args.length != NUM_ARGS) {
      System.out.println("USAGE: ./run <song database>"
        + "<word category file> <word sentiment file>");
      System.out.println("For example, "
        + "./run evenBiggerDB.sqlite3 alls.words NRCnice.txt ");
      System.exit(1);
    }

    SongDatabase db;
    try {
      db = new SongDatabase(args[0], Integer.valueOf("15")); // teehee
      List<Song> allSongs = db.getAllSongs();
      SongMatcher sm = new TwoSourceMatcher(
        new SongSourceDistance(
          new BufferedReader(new FileReader(args[1])) , allSongs),
        new SongSourceDistance(
          new BufferedReader(
            new FileReader(args[2])) , allSongs), allSongs, TOPIC_WEIGHT);
      Gui.setup(sm);
    } catch (ClassNotFoundException e) {
      System.out.println("Unable to connect to SQL database");
    } catch (IOException e) {
      System.out.println("IOException from somewhere");
    }
  }
}

