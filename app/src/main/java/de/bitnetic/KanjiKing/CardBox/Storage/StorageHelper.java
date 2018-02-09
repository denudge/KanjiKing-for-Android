package de.bitnetic.KanjiKing.CardBox.Storage;

import de.bitnetic.KanjiKing.KanjiKing;
import android.os.Environment;

/**
 * Created by nudge on 10.10.16.
 */
public class StorageHelper
{
    private static final String FILENAME_KANJI = "kanjibox";

    private static final String FILENAME_WORDS = "wordbox";


    public static String getFilename(int mode)
    {
        if (mode == KanjiKing.MODE_KANJI)
            return FILENAME_KANJI;
        else
            return FILENAME_WORDS;
    }

    public static String getExternalStorageDirectory()
    {
        return Environment.getExternalStorageDirectory().toString();
    }
}
