package edu.brown.cs.group.speechtotext;

public class SpokenWord {
	String word;
	double time;
	
	public SpokenWord(String word, double time){
		this.word = word;
		this.time = time;
	}
	
	@Override
	public String toString() {
		return "(" + word + ", " + time + ") ";
	}
}
