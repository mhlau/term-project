package edu.brown.cs.group.matcher;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import  edu.brown.cs.group.lyricFinder.Song;

public class TwoSourceMatcher implements SongMatcher {
  private SongSourceDistance a;
  private SongSourceDistance b;
  private List<Song> songs;
  private double w;
  public TwoSourceMatcher(SongSourceDistance a, SongSourceDistance b,
    List<Song> songs, double aWeight) {
    this.a = a;
    this.b = b;
    this.songs = songs;
    w = aWeight;
  }
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
  private class EntryOrder implements Comparator<Map.Entry<Song, Double>> {
    @Override
    public int compare(Map.Entry<Song, Double> o1,
        Map.Entry<Song, Double> o2) {
      return o1.getValue().compareTo(o2.getValue());
    }
  }
}
