package edu.brown.cs.group.speechtotext;

import java.io.FileNotFoundException;
import java.util.List;

import edu.cmu.sphinx.api.AbstractSpeechRecognizer;
import edu.cmu.sphinx.api.Configuration;

public abstract class SpeechToText {
	Configuration configuration = new Configuration();
	
	AbstractSpeechRecognizer recognizer;

	public void initialize(){
		// Set path to acoustic model.
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        // Set path to dictionary.
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        // Set language model.
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.dmp");
	}
	
	abstract public List<String> getWords() throws FileNotFoundException;
	
}
