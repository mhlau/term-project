package edu.brown.cs.group.term_project;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import edu.brown.cs.group.lyricFinder.Song;
import edu.brown.cs.group.lyricFinder.SongDatabase;
import edu.brown.cs.group.matcher.SongMatcher;
import edu.brown.cs.group.matcher.SongSourceDistance;
import edu.brown.cs.group.matcher.TwoSourceMatcher;

/**
 *
 */
public final class Main {

  private Main() {

  }

  /**
   *
   * @param args .
   * @throws Exception .
   */
  public static void main(String[] args) throws Exception {
    SongDatabase db = new SongDatabase(args[0], 15);
    List<Song> allSongs = db.getAllSongs();
    SongMatcher sm = new TwoSourceMatcher(
      new SongSourceDistance(
        new BufferedReader(new FileReader(args[1])) , allSongs),
      new SongSourceDistance(
        new BufferedReader(new FileReader(args[2])) , allSongs),
      allSongs, .8);
    new Gui(sm);
  }
}
