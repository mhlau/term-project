package edu.brown.cs.group.term_project;
import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

//import java.nio.file.Files;


public class Visualizer implements AutoCloseable{
  private List<BufferedReader> s;
  private List<Process> p;
  int lastWord;
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
  public synchronized int getWord() {
    try {
      int tot = 0;
      int count = 0;
      for (BufferedReader sr : s) {
        while (sr.ready()) {
          count++;
          String snext = sr.readLine();
          System.out.println(snext);
          int i = Integer.parseInt(snext, 8);
          tot += Math.abs((i > Short.MAX_VALUE ? i-65536: i)) ;
        }
      }
      lastWord = tot/count;
    } catch (Exception e) {
    }
    return lastWord;
  }
  public void close() throws IOException {
    for (Process pr : p) {
      pr.destroy();
    }
    for (BufferedReader sr : s) {
      sr.close();
    }    
  }
  
}
