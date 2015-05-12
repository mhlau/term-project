package edu.brown.cs.group.matcher;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import  edu.brown.cs.group.lyricFinder.Song;
/**
 * Matches songs to dialogues based on a weighted average of two word
 * categorizations.
 */
public class TwoSourceMatcher implements SongMatcher {
  private SongSourceDistance a;
  private SongSourceDistance b;
  private List<Song> songs;
  private double w;
  /**
   * Constructor for TwoSourceMatcher.
   * @param a the first word category source to use.
   * @param b the second word category source to use.
   * @param songs the list of possible songs to recommend.
   * @param aWeight the weight to give to the first song source.
   */
  public TwoSourceMatcher(SongSourceDistance a, SongSourceDistance b,
    List<Song> songs, double aWeight) {
    this.a = a;
    this.b = b;
    this.songs = songs;
    w = aWeight;
  }
  /**
   * Returns closest song recommendations based on a dialogue.
   * @param  dialogue a dialogue, as a list of lower-case words
   * @param numResults the maximum number of recommendations to return.
   * @return an ordered list of recommended songs.
   */
  @Override
  public List<Song> match(List<String> dialogue, int numResults) {
    List<Map.Entry<Song, Double>> toSort =
      new ArrayList<Map.Entry<Song, Double>>();
    a.setDialogue(dialogue);
    b.setDialogue(dialogue);
    for (Song s : songs) {
      toSort.add(new AbstractMap.SimpleEntry<Song, Double>(
        s, Math.pow(a.distance(s), w)
        * Math.pow(b.distance(s), (1.0 - w))));
    }

    List<Song> ret = new ArrayList<Song>();
    EntryOrder oder = new EntryOrder();
    while (toSort.size() > 0 && numResults > 0) {
      numResults--;
      Map.Entry<Song, Double> br = Collections.min(toSort, oder);
      ret.add(br.getKey());
      toSort.remove(br);
    }
    return ret;
  }
  /**
   * Comparator used to sort songs by closeness of match.
   */
  private class EntryOrder implements Comparator<Map.Entry<Song, Double>> {
    @Override
    public int compare(Map.Entry<Song, Double> o1,
        Map.Entry<Song, Double> o2) {
      return o1.getValue().compareTo(o2.getValue());
    }
  }
}
