package edu.brown.cs.group.speechtotext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;

public class FromFile extends SpeechToText {
	
	String filename;
	
	public FromFile(String filename) throws IOException{
		this.recognizer = new StreamSpeechRecognizer(configuration);
		this.filename = filename;
	}

	@Override
	List<String> start() throws FileNotFoundException {
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

}

