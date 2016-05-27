package ch.com.guessmusic.entities;

/**
 * Created by Chenhao on 2016/5/20.
 */
public class Song {

  private String url;
  private String name;
  private int length;

  public Song() {
  }

  public Song(String url, int length, String name) {
    this.url = url;
    this.length = length;
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  @Override public String toString() {
    return "Song{" +
        "url='" + url + '\'' +
        ", name='" + name + '\'' +
        ", length=" + length +
        '}';
  }
}
