package com.mlieber.KanjiKing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Card implements java.io.Serializable {

    public static final int TYPE_KANJI = 1;
    public static final int TYPE_WORD  = 2;

	private String _japanese	= null;
	private String _reading_on	= null;
	private String _reading_kun	= null;
    private Style   _style      = null;

    private int _grade       	= 0;
	private int _frequency 		= 0;
	private int _hadamitzky	    = 0;
	private int _halpern		= 0;
	private int _radical		= 0;
	private int _strokes		= 0;
    private int _type           = TYPE_KANJI;

	private HashMap<String, String>	_meaning;
    private HashMap<String, String> _hint;

    public Card(String japanese) {
		_japanese   = japanese;
		_meaning    = new HashMap<String, String>();
		_hint       = new HashMap<String, String>();
	}

	public String getJapanese() { return _japanese; }
	public String getOnReading() { return _reading_on; }
	public String getKunReading() { return _reading_kun; }
	public int getFrequency() { return _frequency; }
	public int getHadamitzkyNumber() { return _hadamitzky; }
	public int getHalpernNumber() { return _halpern; }
	public int getStrokesCount() { return _strokes; }
	public int getRadical() { return _radical; }
	public int getGrade() { return _grade; }
    public String getStyle() { if (_style == null ) return null; return _style.toString(); }
    public int getType() { return _type; }

	public String getMeaning(String language) { return _meaning.get(language); }
	public String getHint(String language) { return _hint.get(language); }

    public String getInfo() {
        StringBuilder sb = new StringBuilder();
                
        sb.append("F: ")
           .append(getFrequency())
            .append(" / G: ")
            .append(getGrade())
            .append(" / S: ")
            .append(getStrokesCount())
            .append(" / R: ")
            .append(getRadical())
            .append(" / H: ")
            .append(getHadamitzkyNumber());

        return sb.toString();
    }
    
    public Card setType(int type) {
        _type = type;
        return this;
    }

    public Card setStyle(Style style) {
        _style = style;
        return this;
    }

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

	public Card setHint(String language, String hint) {
		_hint.put(language, hint);
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

	public Card setGrade(int grade) {
		_grade = grade;
		return this;
	}

}

