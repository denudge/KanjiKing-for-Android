package com.mlieber.KanjiKing.Search;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nudge on 11.10.16.
 */
public class Criteria
{
    private Set<Integer> radicals = new HashSet<Integer>();

    private int strokes;

    private String reading;

    private String meaning;

    private String searchPhrase;

    public Set<Integer> getRadicals() {
        return radicals;
    }

    public Criteria setRadicals(Set<Integer> radicals) {
        this.radicals.clear();
        this.radicals.addAll(radicals);
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
                && (radicals.isEmpty()));
    }
}
