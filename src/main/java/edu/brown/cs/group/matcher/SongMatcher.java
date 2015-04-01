package edu.brown.cs.group.matcher;
import edu.brown.cs.group.lyricFinder.Song;
import java.util.List;
public interface SongMatcher {
  List<Song> match(List<String> dialogue, int numResults);
}