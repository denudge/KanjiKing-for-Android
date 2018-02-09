package com.mlieber.KanjiKing.CardBox.Storage;

import com.mlieber.KanjiKing.CardBox.CardBox;
import com.mlieber.KanjiKing.KanjiKing;

import java.io.*;

import android.util.Log;
import android.content.Context;

import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Created by nudge on 10.10.16.
 */
public class XmlStorage implements IStorage {
    private static final String TAG = "KanjiKing/XmlStorage";

    @Override
    public String getTargetFilename(int mode) {
        return StorageHelper.getFilename(mode) + ".xml";
    }

    @Override
    public void save(Context ctx, CardBox box, int mode) throws Exception {
        try {
            File file = getTargetFile(mode);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(box.asXML().getBytes("UTF-8"));
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.v(TAG, "Error saving the cardbox: " + e.getMessage(), e);
            throw new Exception(e.toString());
        }
    }

    @Override
    public CardBox load(Context ctx, int mode) throws Exception {
        try {
            CardBox box = new CardBox(
                    KanjiKing.getCardStore(),
                    mode,
                    CardBox.ORDER_FREQUENCY,
                    false,
                    KanjiKing.getMaxFreq(),
                    KanjiKing.getEndless()
            );

            File file = getTargetFile(mode);
            FileInputStream fis = new FileInputStream(file);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(fis, "UTF-8");
            box.loadFromXML(xpp);
            return box;
        } catch (Exception e) {
            Log.v(TAG, "Error loading the cardbox: " + e.getMessage(), e);
            throw new Exception(e.toString());
        }
    }

    private File getTargetFile(int mode) {
        return new File(StorageHelper.getExternalStorageDirectory(), "/" + getTargetFilename(mode));
    }
}
