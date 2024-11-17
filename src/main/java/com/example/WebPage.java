package com.example;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class WebPage{
	public String url;
	public String name;
	public WordCounter counter;
	public double score;

	public WebPage(String url, String name){
		this.url = url;
		this.name = name;
		this.counter = new WordCounter(url);
	}

	public void setScore(ArrayList<Keyword> keywords) throws IOException, URISyntaxException{
		score = 0;
		// YOUR TURN
		// 1. calculate the score of this webPage
		
	    for (Keyword keyword : keywords) {
	        // Count occurrences of the keyword in the webpage content
	        int count = counter.countKeyword(keyword.name);
	        // Multiply count by keyword weight to calculate score contribution for this keyword
	        score += count * keyword.weight;
	    }

	}
}