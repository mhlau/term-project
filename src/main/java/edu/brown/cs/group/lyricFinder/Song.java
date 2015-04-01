package edu.brown.cs.group.lyricFinder;

import java.io.Serializable;
import java.util.List;

public class Song implements Serializable {
  private static final long serialVersionUID = 2195261848992500743L; // possibly temporary
  private int id;
  private String title;
  private String artist;
  private List<String> lyrics; // lyrics should be a list of single words, no punctuation

  public Song(int id, String title, String artist, List<String> lyrics) {
    this.id = id;
    this.title = title;
    this.artist = title;
    this.lyrics = lyrics;
  }

  public int getID() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getArtist() {
    return artist;
  }

  public List<String> getLyrics() {
    return lyrics;
  }
}
