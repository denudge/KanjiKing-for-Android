package com.mlieber.KanjiKing.CardBox;

import com.mlieber.KanjiKing.KanjiKing;
import java.io.*;
import android.util.Log;
import android.content.Context;

/**
 * Created by nudge on 09.10.16.
 */
public class DiskStorage
{
    private static final String TAG = "KanjiKing/DiskStorage";

    private static final String FILENAME_KANJI = "kanjibox";

    private static final String FILENAME_WORDS = "wordbox";

    public static String getFilename(int mode)
    {
        if (mode == KanjiKing.MODE_KANJI)
            return "kanjibox";
        else
            return "wordbox";
    }

    public static void saveToDisk(Context ctx, CardBox box, int mode)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(box);
            byte[] buf = bos.toByteArray();

            FileOutputStream fos = ctx.openFileOutput(getFilename(mode), Context.MODE_PRIVATE);
            fos.write(buf);
            fos.close();
        } catch (IOException e) {
            Log.v(TAG, "Error Serialising the cardbox: " + e.getMessage(), e);
        }
    }

    public static CardBox loadFromDisk(Context ctx, int mode)
    {
        InputStream instream = null;
        try {
            instream = ctx.openFileInput(getFilename(mode));
        } catch (FileNotFoundException e) {
            Log.v(TAG, "Error opening the cardbox file: " + e.getMessage());
            return null;
        }

        CardBox box = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(instream);
            try {
                box = (CardBox) ois.readObject();
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "DESERIALIZATION FAILED (CLASS NOT FOUND):" + e.getMessage(), e);
                return box;
            }
        } catch (StreamCorruptedException e) {
            Log.e(TAG, "DESERIALIZATION FAILED (CORRUPT):" + e.getMessage(), e);
            return box;
        } catch (IOException e) {
            Log.e(TAG, "DESERIALIZATION FAILED (IO EXCEPTION):"+e.getMessage(), e);
            return box;
        }

        return box;
    }

}
