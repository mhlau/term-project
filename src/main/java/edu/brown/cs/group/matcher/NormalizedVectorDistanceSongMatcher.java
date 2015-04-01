package edu.brown.cs.group.matcher;
import edu.brown.cs.group.lyricFinder.Song;
import edu.brown.cs.group.lyricFinder.SongDatabase;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.Collections;
import java.io.Serializable;
public class NormalizedVectorDistanceSongMatcher implements SongMatcher, 
  Serializable {
  private static final long serialVersionUID = 342343L;
  Map<Integer, double[]> songVectors;
  List<String>  cats;
  SongDatabase ld;
  BooleanWordSource ws;
  public NormalizedVectorDistanceSongMatcher(BooleanWordSource ws, SongDatabase
    ld) {
    this.ld  = ld;
    this.ws = ws;
    List<Song> songs = ld.getAllSongs();
    songVectors = new  HashMap<Integer, double[]>();
    cats = new ArrayList<String>(ws.properties());
    for (Song s: songs) {
      songVectors.put(s.getID(), vectorize(s.getLyrics()));
    }
  }
  private double[] vectorize(List<String> words) {
    int[] counts = new int[cats.size()];
    for (String word: words) {
      if (ws.hasWord(word)) {
        for (int i = 0; i < cats.size(); i++) {
          counts[i] += (ws.getProperty(word, cats.get(i))?1:0);
        }
      }
    }
    double sum = 0.0;
    for (int i : counts) {
      sum += i;
    }
    if (sum == 0) {
      sum = 1.0;
    }
    double[] vect = new double[cats.size()];
    for (int i = 0; i < cats.size(); i++) {
      vect[i] = counts[i]/sum;
    }
    return vect;
  }
  private static double dist(double[] a, double[] b) {
    double ret = 0;
    for (int i = 0; i < (a.length < b.length ? a.length : b.length); i++) {
      ret += (a[i]-b[i])*(a[i]-b[i]);
    }
    return ret;
  }
  @Override
  public List<Song> match(List<String> dialogue, int numResults) {
    double[] v = vectorize(dialogue);
    List<Map.Entry<Integer, Double>> toSort = 
      new LinkedList<Map.Entry<Integer, Double>>();
    for (Map.Entry<Integer, double[]> m : songVectors.entrySet()) {
      toSort.add(new AbstractMap.SimpleEntry<Integer, Double>(
        m.getKey(), dist(m.getValue(), v)));
    }
    List<Song> ret = new ArrayList<Song>();
    EntryOrder oder = new EntryOrder();
    while (toSort.size() > 0 && numResults > 0) {
      numResults--;
      Map.Entry<Integer, Double> b = Collections.min(toSort, oder);
      ret.add(ld.getSong(b.getKey()));
      toSort.remove(b);
    }
    return ret;
  }
  private class EntryOrder implements Comparator<Map.Entry<Integer, Double>> {
    @Override
    public int compare(Map.Entry<Integer, Double> o1,
        Map.Entry<Integer, Double> o2) {
      return o1.getValue().compareTo(o2.getValue());
    }
  }
}
