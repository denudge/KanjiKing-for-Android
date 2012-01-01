package com.mlieber.KanjiKing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Card {

	private String _japanese	= null;
	private String _reading_on	= null;
	private String _reading_kun	= null;
	private String _grade   	= null;
	private int _frequency 		= 0;
	private int _hadamitzky	    = 0;
	private int _halpern		= 0;
	private int _radical		= 0;
	private int _strokes		= 0;
	private HashMap<String, String>	_meaning;

  	public Card(String japanese) {
		_japanese = japanese;
		_meaning = new HashMap<String, String>();
	}

	public String getJapanese() { return _japanese; }
	public String getOnReading() { return _reading_on; }
	public String getKunReading() { return _reading_kun; }
	public String getMeaning(String language) { return _meaning.get(language); }
	public int getFrequency() { return _frequency; }
	public int getHadamitzkyNumber() { return _hadamitzky; }
	public int getHalpernNumber() { return _halpern; }
	public int getStrokesCount() { return _strokes; }
	public int getRadical() { return _radical; }
	public String getGrade() { return _grade; }

	public Card setJapanese(String japanese) {
		_japanese = japanese;
		return this;
	}

	public Card setOnReading(String reading_on) {
		_reading_on = reading_on;
		return this;
	}

	public Card setKunReading(String reading_kun) {
		_reading_kun = reading_kun;
		return this;
	}

	public Card setMeaning(String language, String meaning) {
		_meaning.put(language, meaning);
		return this;
	}

	public Card setFrequency(int frequency) {
		_frequency = frequency;
		return this;
	}

	public Card setHadamitzky(int hadamitzky) {
		_hadamitzky = hadamitzky;
		return this;
	}

	public Card setHalpern(int halpern) {
		_halpern = halpern;
		return this;
	}

	public Card setStrokesCount(int strokes) {
		_strokes = strokes;
		return this;
	}

	public Card setRadical(int radical) {
		_radical = radical;
		return this;
	}

	public Card setGrade(String grade) {
		_grade = grade;
		return this;
	}
}

