package edu.brown.cs.group.lyricFinder;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author cmg1
 *
 */
public class Song implements Serializable {
  private static final long serialVersionUID = 2195261848992500743L;
  private int id;
  private String artist;
  private String title;
  private List<String> lyrics;

  /**
   *
   * @param id .
   * @param artist .
   * @param title .
   * @param lyrics .
   */
  public Song(int id, String artist, String title, List<String> lyrics) {
    this.id = id;
    this.artist = artist;
    this.title = title;
    this.lyrics = lyrics;
  }

  /**
   *
   * @return .
   */
  public int getID() {
    return id;
  }

  /**
   *
   * @return .
   */
  public String getArtist() {
    return artist;
  }

  /**
   *
   * @return .
   */
  public String getTitle() {
    return title;
  }

  /**
   *
   * @return .
   */
  public List<String> getLyrics() {
    return lyrics;
  }
}
