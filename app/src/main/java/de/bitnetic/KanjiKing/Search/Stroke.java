package de.bitnetic.KanjiKing.Search;

/**
 * Created by nudge on 04.11.16.
 *
 * Represents a stroke entry for a single Kanji or Kana.
 * Intended use is to fill the same Stroke object over and over
 * to avoid memory allocation while iterating over the stroke map.
 */
public class Stroke {

    public String kanji;

    public String strokes;

    public String args;

    public Stroke(String line) {
        this.fill(line);
    }

    public void clear() {
        kanji = null;
        strokes = null;
        args = null;
    }

    /**
     * Parses a line in the form "<Kanji> | <Strokes> [| <Args>]"
     * If the string not matches the form, all fields of the return Stroke (this) will be empty.
     *
     * @param line
     * @return Stroke
     */
    public Stroke fill(String line) {
        clear();

        if ((line.length() == 0)||(line.charAt(0)=='#')) {
            return this;
        }

        // devide Kanji from line
        int index = line.indexOf('|');
        if (index==-1) {
            return this;
        }
        kanji = line.substring(0,1);
        line = line.substring(index+1);

        //
        index = line.indexOf('|');
        if (index == -1) {
            strokes = line;
        } else {
            strokes = line.substring(0,index);
            args = line.substring(index+1);
        }

        return this;
    }
}
