package edu.brown.cs.group.term_project;
import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;

//import java.nio.file.Files;


public class Visualizer implements AutoCloseable{
  private Process p;
  private BufferedReader s;
  int lastWord;
  public Visualizer() throws IOException {
    String[] cmd = {
        "/bin/sh",
        "-c",
        "pacat --record -d"
            + "alsa_output.pci-0000_00_10.1.analog-stereo.monitor"
            + "|od | cut -d \' \' -f 3 | stdbuf -i0 -o0 -e0 sed -u -n '0~400p'"
        };
    Process p = Runtime.getRuntime().exec(cmd);
    s = new BufferedReader(new InputStreamReader(p.getInputStream()));
    lastWord = 0;
  }
  public synchronized int getWord() {
    try {
      int tot = 0;
      int count = 0;
      while (s.ready()) {
        count++;
        String snext = s.readLine();
        System.out.println(snext);
        int i = Integer.parseInt(snext, 8);
        tot += Math.abs((i > Short.MAX_VALUE ? i-65536: i)) ;
      }
      lastWord = tot/count;
    } catch (Exception e) {
    }
    return lastWord;
  }
  public void close() throws IOException {
    p.destroy();
   // Files.delete("visfile.txt");
  }
  
}
