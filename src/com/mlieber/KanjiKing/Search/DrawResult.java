package com.mlieber.KanjiKing.Search;

import android.util.Log;
import java.util.Vector;

/**
 * Created by nudge on 05.11.16.
 */
public class DrawResult {
    private static final String TAG = "DrawResult";

    public static final int RATING_SIZE = 10;

    private String[] top_rated;
    private Vector xstrokes, ystrokes;

    public DrawResult(Vector xstrokes, Vector ystrokes) {
        top_rated = new String[RATING_SIZE];
        this.xstrokes = xstrokes;
        this.ystrokes = ystrokes;
        analyze();
    }

    public String[] getTopRated() {
        return top_rated;
    }

    private void analyze() {
        clear();
        // TODO: Implement analysis

        if ((xstrokes == null) || (ystrokes == null)) {
            Log.v(TAG, "xstrokes or ystrokes null! Giving up analysis...");
            return;
        }

        if (xstrokes.size() != ystrokes.size()) {
            Log.v(TAG, "xstrokes and ystrokes size differ! Giving up analysis...");
            return;
        }

        if ((xstrokes.size() == 0) || (xstrokes.size() > Strokes.STROKES.length)) {
            Log.v(TAG, "xstrokes.size "+xstrokes.size()+" out of STROKES range (0-"+Strokes.STROKES.length+"). Giving up analysis...");
            return;
        }

        String[] availableStrokes = Strokes.STROKES[xstrokes.size()-1];

        // TODO: Use real analysis instead of this random result
        for (int i = 0; i < top_rated.length && i < availableStrokes.length; i++) {
            Stroke s = new Stroke(availableStrokes[i]);
            top_rated[i] = s.kanji;
        }
    }

    private void clear() {
        top_rated = new String[] {"", "", "", "", "", "", "", "", "", "" };
    }
}
