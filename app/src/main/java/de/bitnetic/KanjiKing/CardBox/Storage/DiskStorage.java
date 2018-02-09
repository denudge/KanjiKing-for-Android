package de.bitnetic.KanjiKing.CardBox.Storage;

import de.bitnetic.KanjiKing.CardBox.CardBox;
import de.bitnetic.KanjiKing.KanjiKing;

import java.io.*;

import android.util.Log;
import android.content.Context;

/**
 * Created by nudge on 09.10.16.
 */
public class DiskStorage implements IStorage
{
    private static final String TAG = "KanjiKing/DiskStorage";

    @Override
    public String getTargetFilename(int mode) {
        return StorageHelper.getFilename(mode);
    }

    @Override
    public void save(Context ctx, CardBox box, int mode) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(box);
            byte[] buf = bos.toByteArray();

            FileOutputStream fos = ctx.openFileOutput(StorageHelper.getFilename(mode), Context.MODE_PRIVATE);
            fos.write(buf);
            fos.close();
        } catch (Exception e) {
            Log.v(TAG, "Error saving the cardbox: " + e.getMessage(), e);
            throw new Exception(e.toString());
        }
    }

    @Override
    public CardBox load(Context ctx, int mode) throws Exception {
        try {
            InputStream instream = ctx.openFileInput(StorageHelper.getFilename(mode));
            ObjectInputStream ois = new ObjectInputStream(instream);
            CardBox box = (CardBox) ois.readObject();
            return box;
        } catch (Exception e) {
            Log.v(TAG, "Error loading the cardbox: " + e.getMessage(), e);
            throw new Exception(e.toString());
        }
    }
}
