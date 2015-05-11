package edu.brown.cs.group.matcher;
import java.util.List;

import edu.brown.cs.group.lyricFinder.Song;
/**
 * Interface for getting song recommendations based on a dialogue.
 */
public interface SongMatcher {
  /**
   * Gets song recommendations based on a dialogue.
   * @param dialogue a dialogue, as a list of lowercase words.
   * @param numResults the maximum number of reccomendations to return.
   * @return the list of recommended songs.
   */
  List<Song> match(List<String> dialogue, int numResults);
}
