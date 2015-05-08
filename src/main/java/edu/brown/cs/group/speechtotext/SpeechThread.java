package edu.brown.cs.group.speechtotext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.group.term_project.Gui;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;

/**
 * Listens to mic input and updates cache of words in GUI.
 *
 * @author gpane
 *
 */
public class SpeechThread extends Thread {

  private static boolean stopped = false;

  private LiveSpeechRecognizer recognizer;

  /**
   * Constructor for speech thread. Sets recognizer with default configurations.
   * @throws IOException .
   */
  public SpeechThread() throws IOException {
    Configuration configuration = new Configuration();
    // Set path to acoustic model.
    configuration
      .setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
    // Set path to dictionary.
    configuration
      .setDictionaryPath(
        "resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
    // Set language model.
    configuration
      .setLanguageModelPath(
        "resource:/edu/cmu/sphinx/models/en-us/en-us.lm.dmp");
    recognizer = new LiveSpeechRecognizer(configuration);
  }

  /**
   * Removes words from Gui.words, adds them to a list and returns that list.
   * @return .
   */
  public List<String> getWords() {
    List<String> toReturn = new ArrayList<>();

    while (!Gui.words.isEmpty()) {
      toReturn.add(Gui.words.poll());
    }

    return toReturn;
  }

  /**
   * Called when the thread is started. Listens for speech input
   * and updates the cache in Gui with the recognized words.
   */
  public void run() {
    recognizer.startRecognition(!stopped);
    System.out.println("READY");
    SpeechResult result = recognizer.getResult();
    for (WordResult r : result.getWords()) {
      if (!r.isFiller()) {
        Gui.words.add(r.getWord().toString());
      }
    }
// int i = 0;
// while (keepGoing){
// i++;
// Gui.words.add("word " + i);
// try {
// Thread.sleep(1000);
// } catch (InterruptedException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// }
  }

  /**
   * Stops the speech recognition and marks stopped as true
   * (so that it can resume again by calling startRecognition(false).
   */
  public void stopRecognition() {
    stopped = true;
    recognizer.stopRecognition();
    // keepGoing = false;
  }
}

