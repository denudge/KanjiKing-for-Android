package com.mlieber.KanjiKing.Explanation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;

/**
 * Created by nudge on 17.10.16.
 */
public class ExplanationService
{
    private static final String WADOKU_URL = "http://wadoku.de/search/";

    public static void explain(Context cxt, String word) {
        String url = buildUrl(word);
        final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
        cxt.startActivity(intent);
    }

    private static String buildUrl(String word) {
        return WADOKU_URL + word;
    }
}
