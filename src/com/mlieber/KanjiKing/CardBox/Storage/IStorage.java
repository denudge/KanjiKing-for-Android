package com.mlieber.KanjiKing.CardBox.Storage;

import android.content.Context;
import com.mlieber.KanjiKing.CardBox.CardBox;

/**
 * Created by nudge on 10.10.16.
 */
public interface IStorage
{
    String getTargetFilename(int mode);

    void save(Context ctx, CardBox box, int mode) throws Exception;

    CardBox load(Context ctx, int mode) throws Exception;
}
