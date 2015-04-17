package edu.brown.cs.group.speechtotext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;

public class LiveMode extends SpeechToText {

	public LiveMode() throws IOException{
		initialize();
		this.recognizer = new LiveSpeechRecognizer(configuration);
	}

	@Override
	public List<String> getWords() {
		List<String> toReturn = new ArrayList<>();
		((LiveSpeechRecognizer) recognizer).startRecognition(true);
		System.out.println("READY");
		SpeechResult result = recognizer.getResult();
		for (WordResult r: result.getWords()){
			if (!r.isFiller()){
				toReturn.add(r.getWord().toString());
			}
		}
		((LiveSpeechRecognizer) recognizer).stopRecognition();
		return toReturn;
	}
	
	public List<SpokenWord> getTimedWords() {
		List<SpokenWord> toReturn = new ArrayList<>();
		((LiveSpeechRecognizer) recognizer).startRecognition(true);
		System.out.println("READY");
		SpeechResult result = recognizer.getResult();
		
		for (WordResult r: result.getWords()){
			if (!r.isFiller()){
				String word = r.getWord().toString();
				double time = r.getTimeFrame().getStart();
				toReturn.add(new SpokenWord(word, time));
			}
		}
		((LiveSpeechRecognizer) recognizer).stopRecognition();
		return toReturn;
	}


}
