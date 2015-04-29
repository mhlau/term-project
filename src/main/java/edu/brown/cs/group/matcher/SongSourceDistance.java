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
public class SongSourceDistance {
  private Map<String,Set<Integer>> topicNumbers;
  private Map<Song,Map<Integer,Double>> counts;  
  private Map<Integer, Integer> catCounts;  
  private Map<Integer, Double> v = null;
  public SongSourceDistance(BufferedReader in, List<Song> songs) {
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
  public void setDialogue(List<String> dialogue) {
    v = normCounts(dialogue);
  }
  public double distance(Song s) {
    return dist(counts.get(s), v);
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
