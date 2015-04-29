package edu.brown.cs.group.term_project;
import edu.brown.cs.group.lyricFinder.SongDatabase;
import edu.brown.cs.group.lyricFinder.Song;
import edu.brown.cs.group.matcher.BooleanWordSource;
import edu.brown.cs.group.matcher.NRCWordSource;
import edu.brown.cs.group.matcher.SongMatcher;
import edu.brown.cs.group.matcher.NormalizedVectorDistanceSongMatcher;
import edu.brown.cs.group.matcher.TopicMatcher;
import edu.brown.cs.group.matcher.SongSourceDistance;
import edu.brown.cs.group.matcher.TwoSourceMatcher;
import edu.brown.cs.group.speechtotext.FromFile;
import edu.brown.cs.group.speechtotext.LiveMode;
import edu.brown.cs.group.speechtotext.SpokenWord;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
public final class Main {

  private Main() {

  }

  public static void main(String[] args) throws Exception { 
    SongDatabase db = new SongDatabase(args[0], 15);
    List<Song> allSongs = db.getAllSongs();
    SongMatcher sm = new TwoSourceMatcher(
      new SongSourceDistance(
        new BufferedReader(new FileReader(args[1])) , allSongs),
      new SongSourceDistance(
        new BufferedReader(new FileReader(args[2])) , allSongs),
      allSongs, .8);
    Gui gui = new Gui(sm);
  }
}
