package com.mlieber.KanjiKing.Search;

/**
 * Created by nudge on 11.10.16.
 */
public class Criteria
{
    private int radical;

    private int strokes;

    private String reading;

    private String meaning;

    private String searchPhrase;

    public int getRadical() {
        return radical;
    }

    public Criteria setRadical(int radical) {
        this.radical = radical;
        return this;
    }

    public int getStrokes() {
        return strokes;
    }

    public Criteria setStrokes(int strokes) {
        this.strokes = strokes;
        return this;
    }

    public String getReading() {
        return reading;
    }

    public Criteria setReading(String reading) {
        this.reading = reading;
        return this;
    }

    public String getMeaning() {
        return meaning;
    }

    public Criteria setMeaning(String meaning) {
        this.meaning = meaning;
        return this;
    }

    public String getSearchPhrase() {
        return searchPhrase;
    }

    public Criteria setSearchPhrase(String searchPhrase) {
        this.searchPhrase = searchPhrase;
        return this;
    }

    public boolean isEmpty() {
        return (((reading == null) || (reading.equals("")))
                && ((meaning == null) || (meaning.equals("")))
                && ((searchPhrase == null) || (searchPhrase.equals("")))
                && (strokes < 1)
                && (radical < 1));
    }
}
