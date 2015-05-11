package edu.brown.cs.group.term_project;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//import java.nio.file.Files;

/**
 * Audio visualizer class. Uses pulseaudio monitors of audio outputs.
 */
public class Visualizer implements AutoCloseable {
  private List<BufferedReader> s;
  private List<Process> p;
  private int lastWord;
  /**
   * Constructor for audio visualizer class.
   * @throws IOException if there is an exception in reading audio data.
   */
  public Visualizer() throws IOException {
    s = new ArrayList<BufferedReader>();
    p =  new ArrayList<Process>();
    String[] getSourcesCommand = {
      "/bin/sh", "-c",
      "pacmd list |grep \".monitor>\" | sed \'s/.*name: <//\' |"
        + "sed \'s/>[^>]*//'"
    };
    Process sourcesP = Runtime.getRuntime().exec(getSourcesCommand);
    Scanner sbr = new Scanner(sourcesP.getInputStream());
    while (sbr.hasNextLine()) {
      String[] cmd = {
        "/bin/sh", "-c",
        "pacat --record -d"
          + sbr.nextLine()
          + "|od | cut -d \' \' -f 3"
          + "| stdbuf -i0 -o0 -e0 sed -u -n \'0~400p\'"
      };
      Process pr = Runtime.getRuntime().exec(cmd);
      BufferedReader sr = new BufferedReader(new InputStreamReader(
          pr.getInputStream()));
      s.add(sr);
      p.add(pr);
    }

    lastWord = 0;
  }
  private static final int BASE = 8;
  private static final int SHORT_SIGN_CONSTANT = 65536;
  /**
   * Returns a number representing the average amplitude of audio output
   * since last call. If there has been no audio output,
   * return the most recent value.
   * @return the value
   */
  public synchronized int getWord() {
    try {
      int tot = 0;
      int count = 0;
      for (BufferedReader sr : s) {
        while (sr.ready()) {
          count++;
          String snext = sr.readLine();
          System.out.println(snext);
          int i = Integer.parseInt(snext, BASE);
          tot += Math.abs(
            (i > Short.MAX_VALUE ? i - SHORT_SIGN_CONSTANT : i));
        }
      }
      lastWord = tot / count;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lastWord;
  }
  /**
   * Kills associated audio monitoring processes, and associated input
   * streams.
   * @throws IOException if an input stream does.
   */
  public void close() throws IOException {
    for (Process pr : p) {
      pr.destroy();
    }
    for (BufferedReader sr : s) {
      sr.close();
    }
  }
}
