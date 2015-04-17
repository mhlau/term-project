package edu.brown.cs.group.term_project;
import edu.brown.cs.group.lyricFinder.SongDatabase;
import edu.brown.cs.group.lyricFinder.Song;
import edu.brown.cs.group.matcher.BooleanWordSource;
import edu.brown.cs.group.matcher.NRCWordSource;
import edu.brown.cs.group.matcher.SongMatcher;
import edu.brown.cs.group.matcher.NormalizedVectorDistanceSongMatcher;
import edu.brown.cs.group.speechtotext.FromFile;
import edu.brown.cs.group.speechtotext.LiveMode;
import edu.brown.cs.group.speechtotext.SpokenWord;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

public final class Main {

  private Main() {

  }

  public static void main(String[] args) throws Exception {   //Returns names of top 5 songs
    System.out.println("Main is running.");
    SongDatabase db = new SongDatabase(args[0]);
    BooleanWordSource ws = new NRCWordSource(new File(args[1]));
    
    List<String> dialogue = new ArrayList<String>();
    SongMatcher sm = new NormalizedVectorDistanceSongMatcher(ws, db);
    
    if (args.length > 2 && args[2].equals("--speech")){
    	LiveMode lm = new LiveMode();
    	dialogue = lm.getWords();
    } else {
	    Scanner sc = new Scanner(System.in);
	    sc.useDelimiter("[^a-zA-Z]");
	    while (sc.hasNext()) {
	      dialogue.add(sc.next().toLowerCase());
	    }
	    sc.close();
    }
    
    List<Song> res = sm.match(dialogue, 5);
    for (Song s : res) {
      System.out.println(s.getTitle());
    }
  }

}
