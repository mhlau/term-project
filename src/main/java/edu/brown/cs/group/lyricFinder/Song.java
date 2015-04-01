package edu.brown.cs.group.lyricFinder;

import java.io.Serializable;

public class Song implements Serializable {
  private static final long serialVersionUID = 2195261848992500743L; // temporary
  private int id;
  private String title;
  private String artist;
  private String lyrics;

  public Song(int id, String title, String artist, String lyrics) {
    this.id = id;
    this.title = title;
    this.artist = title;
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

  public String getLyrics() {
    return lyrics;
  }
}
