package edu.brown.cs.group.term_project;
import edu.brown.cs.group.lyricFinder.SongDatabase;
import edu.brown.cs.group.lyricFinder.Song;
import edu.brown.cs.group.matcher.BooleanWordSource;
import edu.brown.cs.group.matcher.NRCWordSource;
import edu.brown.cs.group.matcher.SongMatcher;
import edu.brown.cs.group.matcher.NormalizedVectorDistanceSongMatcher;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

public final class Main {

  private Main() {

  }

  public static void main(String[] args) throws Exception {   //Returns names of top 5 songs
    System.out.println("Main is running.");
        
    SongDatabase db = new SongDatabase(args[0], Integer.valueOf(args[2]));
    BooleanWordSource ws = new NRCWordSource(new File(args[1]));
    SongMatcher sm = new NormalizedVectorDistanceSongMatcher(ws, db);
    Scanner sc = new Scanner(System.in);
    sc.useDelimiter("[^a-zA-Z]");
    List<String> dialog = new ArrayList<String>();
    while (sc.hasNext()) {
      dialog.add(sc.next().toLowerCase());
    }
    sc.close();
    List<Song> res = sm.match(dialog, 5);
    for (Song s : res) {
      System.out.println(s.getTitle());
    }
  }

}
