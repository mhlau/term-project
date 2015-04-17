package edu.brown.cs.group.speechtotext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechAligner;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;

public class FromFile extends SpeechToText {
	
	String filename;
	
	public FromFile(String filename) throws IOException{
		initialize();
		this.recognizer = new StreamSpeechRecognizer(configuration);
		this.filename = filename;
	}

	@Override
	public List<String> getWords() throws FileNotFoundException {
		ArrayList<String> toReturn = new ArrayList<String>();
		((StreamSpeechRecognizer) recognizer).startRecognition(new FileInputStream(filename));
		System.out.println("READY");
		SpeechResult result = recognizer.getResult();
		for (WordResult r: result.getWords()){
			toReturn.add(r.getWord().toString());
		}
		((StreamSpeechRecognizer) recognizer).stopRecognition();
		return toReturn;
	}
	
	public List<SpokenWord> getTimedWords() throws FileNotFoundException {
		List<SpokenWord> toReturn = new ArrayList<>();
		((StreamSpeechRecognizer) recognizer).startRecognition(new FileInputStream(filename));
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

