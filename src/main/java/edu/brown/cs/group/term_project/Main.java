package edu.brown.cs.group.term_project;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import edu.brown.cs.group.lyricFinder.Song;
import edu.brown.cs.group.lyricFinder.SongDatabase;
import edu.brown.cs.group.matcher.SongMatcher;
import edu.brown.cs.group.matcher.SongSourceDistance;
import edu.brown.cs.group.matcher.TwoSourceMatcher;

// urls
// display lyrics
// karaoke mode ?


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
	 
	  if (args.length != 3){
		  System.out.println("USAGE: ./run <song database> <word category file> <word sentiment file>");
		  System.out.println("For example, ./run evenBiggerDB.sqlite3 alls.words NRCnice.txt ");
		  System.exit(1);
	  }
	 
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
