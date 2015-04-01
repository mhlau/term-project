package edu.brown.cs.group.matcher;
import java.util.Set;
public interface WordSource {
  Set<String> properties();
  boolean hasWord(String word);
}
