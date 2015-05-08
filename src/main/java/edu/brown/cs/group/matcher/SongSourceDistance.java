package edu.brown.cs.group.matcher;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import  edu.brown.cs.group.lyricFinder.Song;

public class SongSourceDistance {
  private Map<String, Set<Short>> topicNumbers;
  private Map<Song, short[]> countIndices;
  private Map<Song, float[]> countValues;
  private Map<Short, Integer> catCounts;
  private short[] vi = null;
  private float[] vv = null;
  public SongSourceDistance(BufferedReader in, List<Song> songs) {
    topicNumbers = new HashMap<String, Set<Short>>();
    countIndices = new HashMap<Song, short[]>();
    countValues = new HashMap<Song, float[]>();
    catCounts = new HashMap<Short, Integer>();
    Scanner scn = new Scanner(in);
    int i = 0;
    while (scn.hasNextLine()) {

      Scanner ln = new Scanner(scn.nextLine());
      if (ln.hasNext()) {
        String word = ln.next();
        Set<Short> cats = new HashSet<Short>();
        while (ln.hasNextInt()) {
          cats.add((short) (ln.nextInt() + Short.MIN_VALUE));
        }
        topicNumbers.put(word, cats);
      }
      ln.close();
    }
    scn.close();
    i = 0;
    for (Song song : songs) {
      Map<Short, Float>  nc = normCounts(song.getLyrics());
      short[] ind = new short[nc.size()];
      float[] val = new float[nc.size()];
      SortedSet<Short> ss = new TreeSet<Short>(nc.keySet());
      int c = 0;
      for (short l : ss) {
        ind[c] = l;
        val[c] = nc.get(l);
        c++;
      }
      countIndices.put(song, ind);
      countValues.put(song, val);
      System.out.println(i++);
    }
  }
  public void setDialogue(List<String> dialogue) {
    Map<Short, Float> v = normCounts(dialogue);
    vi = new short[v.size()];
    vv = new float[v.size()];
    SortedSet<Short> ss = new TreeSet<Short>(v.keySet());
    int c = 0;
    for (short l : ss) {
      vi[c] = l;
      vv[c] = v.get(l);
      c++;
    }
  }
  public double distance(Song s) {
    return dist(countIndices.get(s), countValues.get(s), vi, vv);
  }
  private double dist(short [] aInd, float [] aVals, short [] bInd,
    float [] bVals) {
    int aCounter = 0;
    int bCounter = 0;
    double res = 0;
    while (aCounter < aInd.length && bCounter < bInd.length) {
      if (aInd[aCounter] == bInd[bCounter]) {
        res += (aVals[aCounter] - bVals[bCounter]) * (aVals[aCounter]
          - bVals[bCounter]);
        aCounter++;
        bCounter++;
      } else if (aInd[aCounter] < bInd[bCounter]) {
        res += aVals[aCounter] * aVals[aCounter];
        aCounter++;
      } else {
        res += bVals[bCounter] * bVals[bCounter];
        bCounter++;
      }
    }
    while (bCounter < bInd.length) {
      res += bVals[bCounter] * bVals[bCounter];
      bCounter++;
    }
    while (aCounter < aInd.length) {
      res += aVals[aCounter] * aVals[aCounter];
      aCounter++;
    }
    return res;
  }
  private Map<Short, Float> normCounts(List<String> words) {
    catCounts.clear();
    for (String lyr : words) {
      if (topicNumbers.containsKey(lyr)) {
        for (Short c : topicNumbers.get(lyr)) {

          if (!catCounts.containsKey(c)) {
            catCounts.put(c, 1);
          } else {
            catCounts.put(c, catCounts.get(c) + 1);
          }
        }
      }
    }
    Map<Short, Float> normCatCounts = new HashMap<Short, Float>();
    int tot = 0;
    for (Integer i : catCounts.values()) {
      tot += i * i;
    }
    float norm = (float) Math.sqrt(tot);
    for (Map.Entry<Short, Integer> kv : catCounts.entrySet()) {
      normCatCounts.put(kv.getKey(), kv.getValue() / norm);
    }
    return normCatCounts;
  }
}
