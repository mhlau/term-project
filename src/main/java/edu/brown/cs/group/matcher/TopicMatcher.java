package edu.brown.cs.group.matcher;
import  edu.brown.cs.group.lyricFinder.Song;
import java.util.Set;
import java.util.Map;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;
import java.io.BufferedReader;

public class TopicMatcher implements SongMatcher {
  private Map<String,Set<Integer>> topicNumbers;
  private Map<Song,Map<Integer,Double>> counts;  
  private Map<Integer, Integer> catCounts;  
  public TopicMatcher(BufferedReader in, List<Song> songs) {
    topicNumbers = new HashMap<String,Set<Integer>>();
    counts = new HashMap<Song,Map<Integer,Double>>();
    catCounts = new HashMap<Integer, Integer>();
    Scanner scn = new Scanner(in);
    while (scn.hasNextLine()) {
      Scanner ln = new Scanner(scn.nextLine());
      if (ln. hasNext()) {
        String word = ln.next();
        Set<Integer> cats = new HashSet<Integer>();
        while (ln.hasNextInt()) {
          cats.add(ln.nextInt());
        }
        topicNumbers.put(word, cats);
      }
      ln.close();
    }
    for (Song song : songs) {
      counts.put(song, normCounts(song.getLyrics()));
    }
  }
  private static double dist(Map<Integer, Double> a, Map<Integer, Double> b) {
    double ret = 0;
    
    
    for (Integer r : a.keySet()) {
      if (b.containsKey(r)) {
        ret += (a.get(r)-b.get(r))*(a.get(r)-b.get(r));
      } else {
        ret += a.get(r)*a.get(r);
      }
    }
    for (Integer r : b.keySet()) {
      if (!a.containsKey(r)) {
        ret += b.get(r)*b.get(r);
      } 
    }    
    return ret;
  }
  
    @Override
  public List<Song> match(List<String> dialogue, int numResults) {
    Map<Integer, Double> v = normCounts(dialogue);
    List<Map.Entry<Song, Double>> toSort = 
      new LinkedList<Map.Entry<Song, Double>>();
    int i = 0;
    for (Map.Entry<Song, Map<Integer, Double>> m : counts.entrySet()) {
      toSort.add(new AbstractMap.SimpleEntry<Song, Double>(
        m.getKey(), dist(m.getValue(), v)));
    }

    List<Song> ret = new ArrayList<Song>();
    EntryOrder oder = new EntryOrder();
    while (toSort.size() > 0 && numResults > 0) {
      numResults--;
      Map.Entry<Song, Double> b = Collections.min(toSort, oder);
      ret.add(b.getKey());
      toSort.remove(b);
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
  
  
  
  
  private Map<Integer, Double> normCounts(List<String> words) {
    catCounts.clear();
    for (String lyr : words) {
      if (topicNumbers.containsKey(lyr)) {
        for (Integer c : topicNumbers.get(lyr)) {

          if (!catCounts.containsKey(c)) {
            catCounts.put(c, 1);
          } else {
            catCounts.put(c,catCounts.get(c)+1);
          }
        }
      }
    }
    
    Map<Integer, Double> normCatCounts = new HashMap<Integer, Double>();
    int tot = 0;
    for (Integer i : catCounts.values()) {
      tot += i*i;
    }
    double norm = Math.sqrt(tot);
    for (Map.Entry<Integer, Integer> kv : catCounts.entrySet()) {
      normCatCounts.put(kv.getKey(), kv.getValue()/norm);
    }    
    return normCatCounts;
  }
  
}
