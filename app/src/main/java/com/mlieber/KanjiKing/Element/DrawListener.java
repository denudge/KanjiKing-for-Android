package com.mlieber.KanjiKing.Element;

import java.util.Vector;

/**
 * Created by nudge on 05.11.16.
 */
public interface DrawListener {
    void onStrokeStart(Vector xstrokes, Vector ystrokes);
    void onStrokeFinish(Vector xstrokes, Vector ystrokes);
}
