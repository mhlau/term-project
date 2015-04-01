package edu.brown.cs.group.speechtotext;

import java.io.IOException;

import edu.cmu.sphinx.api.StreamSpeechRecognizer;

public class FromFile extends SpeechToText {
	
	public FromFile(String filename) throws IOException{
		this.recognizer = new StreamSpeechRecognizer(config);
	}
}
