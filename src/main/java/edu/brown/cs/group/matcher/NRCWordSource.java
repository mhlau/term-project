package edu.brown.cs.group.matcher;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

public class NRCWordSource implements BooleanWordSource, Serializable {
  private static final long serialVersionUID = 564575476764L;
  private final static Set<String> properties = new HashSet<String>(
    Arrays.asList("anger", "anticipation", "disgust", "fear",
    "joy", "negative", "positive", "sadness", "surprise", "trust"));
  private final Map<String, Map<String, Boolean>> data;
  public NRCWordSource(File in) throws IOException {
    data = new HashMap<String, Map<String, Boolean>>();
    Scanner scn = new Scanner(new BufferedReader(new FileReader(in)));
    while (scn.hasNext()) {
      String word = scn.next();
      String prop = scn.next();
      int is = scn.nextInt();
      Boolean b = (is == 1);
      if (data.containsKey(word)) {
        data.get(word).put(prop, b);
      } else {
        HashMap<String, Boolean> ne = new HashMap<String, Boolean>();
        ne.put(prop, b);
        data.put(word, ne);
      }
    }
  }
  @Override
  public Set<String> properties() {
    return properties;
  }
  @Override  
  public boolean hasWord(String word) {
    return data.containsKey(word);
  }
  @Override  
  public Boolean getProperty(String word, String property) {
    if (data.containsKey(word)) {
      return data.get(word).get(property);
    } else {
      return null;
    }
  }
  @Override  
  public Map<String, Boolean> getAllProperties(String word) {
    if (data.containsKey(word)) {
      return new HashMap<String, Boolean>(data.get(word));
    } else {
      return null;
    }
  }
}

