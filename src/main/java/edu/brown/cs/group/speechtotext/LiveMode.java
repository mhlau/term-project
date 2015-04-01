package edu.brown.cs.group.speechtotext;

import java.io.IOException;

import edu.cmu.sphinx.api.LiveSpeechRecognizer;

public class LiveMode extends SpeechToText {

	public LiveMode() throws IOException{
		this.recognizer = new LiveSpeechRecognizer(config);
	}
	
}
