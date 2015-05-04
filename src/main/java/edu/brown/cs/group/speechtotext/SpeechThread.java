package edu.brown.cs.group.speechtotext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.brown.cs.group.term_project.Gui;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;

public class SpeechThread extends Thread {
	
	private Gson gson = new Gson();
	private boolean keepGoing = true;
	
	private LiveSpeechRecognizer recognizer;
	
	public SpeechThread() throws IOException {
		Configuration configuration = new Configuration();
		// Set path to acoustic model.
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        // Set path to dictionary.
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        // Set language model.
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.dmp");
        recognizer = new LiveSpeechRecognizer(configuration);
	}
	
	public List<String> getWords() {
		List<String> toReturn = new ArrayList<>();
		
		while(!Gui.words.isEmpty()){
			toReturn.add(Gui.words.poll());
		}

		return toReturn;
	}
	
	public void run() {
		recognizer.startRecognition(true);
		System.out.println("READY");
		SpeechResult result = recognizer.getResult();
		for (WordResult r: result.getWords()){
			if (!r.isFiller()){
				Gui.words.add(r.getWord().toString());
			}
		}
		int i = 0;
		while (keepGoing){
			i++;
			Gui.words.add("word " + i);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void stopRecognition() {
		recognizer.stopRecognition();
//		keepGoing = false;
	}
	
	
	
}
