package edu.brown.cs.group.matcher;
import  edu.brown.cs.group.lyricFinder.Song;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
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
public class SongSourceDistanceNoBuffer {

  private Map<String,Set<Short>> topicNumbers;
 
  private Map<Short, Integer> catCounts;  
  Map<Short, Float> normCatCountsVBuff;
  Map<Short, Float> normCatCountsSBuff;
  public SongSourceDistanceNoBuffer(BufferedReader in, List<Song> songs) {
    normCatCountsVBuff = new HashMap<Short, Float>();
    normCatCountsSBuff = new HashMap<Short, Float>();
    topicNumbers = new HashMap<String,Set<Short>>();

    catCounts = new HashMap<Short, Integer>();
    Scanner scn = new Scanner(in);
    int i = 0;
    while (scn.hasNextLine()) {

      Scanner ln = new Scanner(scn.nextLine());
      if (ln. hasNext()) {
        String word = ln.next();
        Set<Short> cats = new HashSet<Short>();
        while (ln.hasNextInt()) {
          cats.add((short)(ln.nextInt() + Short.MIN_VALUE));
        }
        topicNumbers.put(word, cats);
      }
      ln.close();
    }
  
  }
  public void setDialogue(List<String> dialogue) {
   normCount(dialogue, normCatCountsVBuff);
  }
  public double distance(Song s) {
    normCount(s.getLyrics(), normCatCountsSBuff);
    return dist(normCatCountsVBuff, normCatCountsSBuff);
  }
  private double dist(Map<Short, Float> v, Map<Short, Float> s) {


    double res = 0;
    for (Short r : v.keySet()) {
      if (s.containsKey(r)) {
        float diff = v.get(r) - s.get(r);
        res += diff*diff;
      } else {
        float diff = v.get(r);
        res += diff*diff;
      }
    }
    for (Short r : s.keySet()) {
      if (!v.containsKey(r)) {
        float diff = s.get(r);
        res += diff*diff;
      }
    }
    return res;
  }
  

private void  normCount(List<String> words,  Map<Short, Float> buff) {
    catCounts.clear();
    buff.clear();

    for (String lyr : words) {
      if (topicNumbers.containsKey(lyr)) {
        for (Short c : topicNumbers.get(lyr)) {

          if (!catCounts.containsKey(c)) {
            catCounts.put(c, 1);
          } else {
            catCounts.put(c,catCounts.get(c)+1);
          }
        }
      }
    }
    
    int tot = 0;
    for (Integer i : catCounts.values()) {
      tot += i*i;
    }
    float norm = (float) Math.sqrt(tot);
    for (Map.Entry<Short, Integer> kv : catCounts.entrySet()) {
      buff.put(kv.getKey(), kv.getValue()/norm);
    }    
  }
  
}


