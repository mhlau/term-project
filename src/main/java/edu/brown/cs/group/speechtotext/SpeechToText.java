package edu.brown.cs.group.speechtotext;

import edu.cmu.sphinx.api.AbstractSpeechRecognizer;
import edu.cmu.sphinx.api.Configuration;

public abstract class SpeechToText {
	Configuration config;
	AbstractSpeechRecognizer recognizer;
}
