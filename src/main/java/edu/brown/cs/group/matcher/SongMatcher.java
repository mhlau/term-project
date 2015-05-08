package edu.brown.cs.group.matcher;
import java.util.List;

import edu.brown.cs.group.lyricFinder.Song;

public interface SongMatcher {
  List<Song> match(List<String> dialogue, int numResults);
}
