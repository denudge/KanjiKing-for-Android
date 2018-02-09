package de.bitnetic.KanjiKing.CardBox;

import java.util.HashMap;
import java.util.Vector;

public class Card implements java.io.Serializable
{
    private static final String TAG = "Card";

    public static final int TYPE_KANJI = 1;
    public static final int TYPE_WORD = 2;
    public static final int TYPE_RADICAL = 3;

    private String _japanese = null;
    private String _reading_on = null;
    private String _reading_kun = null;
    private Style _style = null;

    private int _id = 0;
    private int _pack = 0;
    private int _grade = 0;
    private int _frequency = 0;
    private int _frequency2 = 0;
    private int _hadamitzky = 0;
    private int _halpern = 0;
    private int _radical = 0;
    private int _strokes = 0;
    private int _type = TYPE_KANJI;

    private HashMap<String, String> _meaning;
    private HashMap<String, String> _hint;
    private Vector<String> _words;

    public Card() {
        _meaning = new HashMap<String, String>();
        _hint = new HashMap<String, String>();
        _words = new Vector<String>();
    }

    public Card(String japanese) {
        this();
        _japanese = japanese;
    }

    public String getJapanese() {
        return _japanese;
    }

    public String getOnReading() {
        return _reading_on;
    }

    public String getKunReading() {
        return _reading_kun;
    }

    public int getFrequency() {
        return _frequency;
    }

    public int getFrequency2() {
        return _frequency2;
    }

    public int getHadamitzkyNumber() {
        return _hadamitzky;
    }

    public int getHalpernNumber() {
        return _halpern;
    }

    public int getStrokesCount() {
        return _strokes;
    }

    public int getRadical() {
        return _radical;
    }

    public int getGrade() {
        return _grade;
    }

    public int getId() {
        return _id;
    }

    public int getPack() {
        return _pack;
    }

    public String getStyle() {
        if (_style == null) return null;
        return _style.toString();
    }

    public int getType() {
        return _type;
    }

    public Vector<String> getWords() {
        return _words;
    }

    public Card addWord(String word) {
        _words.add(word);
        return this;
    }

    public String getMeaning(String language) {
        return _meaning.get(language);
    }

    public String getHint(String language) {
        return _hint.get(language);
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

    public Card setId(int id) {
        _id = id;
        return this;
    }

    public Card setFrequency(int frequency) {
        _frequency = frequency;
        return this;
    }

    public Card setFrequency2(int frequency) {
        _frequency2 = frequency;
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

    public Card setPack(int pack) {
        _pack = pack;
        return this;
    }

    public boolean hasReading(String reading, boolean subStrings) {
        // First, search direct hits
        if (null != _reading_on) {
            if (_reading_on.equals(reading))
                return true;
        }

        if (null != _reading_kun) {
            if (_reading_kun.equals(reading))
                return true;
        }

        // Then, search general appearance
        String haystack = "";
        if (null != _reading_on)
            haystack = _reading_on;
        if (null != _reading_kun)
            haystack = haystack + ", " + _reading_kun;

        if (!haystack.contains(reading))
            return false;

        if (subStrings)
            return true;

        // Then, search by pattern
        String pattern = "^(.+[;, -]+)?(" + reading + ")([;, \\(-]+.+)?$";
        if (haystack.matches(pattern))
            return true;

        return false;
    }

    public boolean hasMeaning(String meaning, String language, boolean subStrings) {
        String _meaning = getMeaning(language);

        if (null == _meaning)
            return false;

        if (!_meaning.contains(meaning))
            return false;

        if (subStrings)
            return true;

        // Then, search by pattern
        String pattern = "^(.+[;, -])?(" + meaning + ")([;, \\(-].+)?$";
        if (_meaning.matches(pattern))
            return true;

        return false;
    }
}
