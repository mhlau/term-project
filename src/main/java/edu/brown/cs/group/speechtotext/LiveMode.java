package edu.brown.cs.group.speechtotext;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;

public class LiveMode extends SpeechToText {

	public LiveMode() throws IOException{
		this.recognizer = new LiveSpeechRecognizer(configuration);
	}

	@Override
	List<String> start() throws FileNotFoundException {
		ArrayList<String> toReturn = new ArrayList<String>();
		((LiveSpeechRecognizer) recognizer).startRecognition(true);
		System.out.println("READY");
		SpeechResult result = recognizer.getResult();
		for (WordResult r: result.getWords()){
			toReturn.add(r.getWord().toString());
		}
		((LiveSpeechRecognizer) recognizer).stopRecognition();
		return toReturn;
	}


}
