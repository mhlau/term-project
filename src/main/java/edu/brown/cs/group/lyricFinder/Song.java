package edu.brown.cs.group.lyricFinder;

import java.io.Serializable;
import java.util.List;

public class Song implements Serializable {
  private static final long serialVersionUID = 2195261848992500743L; // possibly temporary
  private int id;
  private String artist;
  private String title;
  private List<String> lyrics; // lyrics should be a list of single words, no punctuation
                               // they do contain punctuation for now

  public Song(int id, String artist, String title, List<String> lyrics) {
    this.id = id;
    this.artist = title;
    this.title = title;
    this.lyrics = lyrics;
  }

  public int getID() {
    return id;
  }

  public String getArtist() {
    return artist;
  }

  public String getTitle() {
    return title;
  }

  public List<String> getLyrics() {
    return lyrics;
  }
}
