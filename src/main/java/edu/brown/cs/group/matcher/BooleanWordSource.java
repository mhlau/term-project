package edu.brown.cs.group.matcher;
import java.util.Map;
public interface BooleanWordSource extends WordSource {
  Boolean getProperty(String word, String property);
  Map<String, Boolean> getAllProperties(String word);
}
