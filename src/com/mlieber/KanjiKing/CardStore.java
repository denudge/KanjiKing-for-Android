package com.mlieber.KanjiKing;

import java.util.Map;
import java.util.HashMap;

import com.mlieber.KanjiKing.Db.Db;
import org.xmlpull.v1.XmlPullParserException;
import android.content.res.XmlResourceParser;
import java.io.IOException;
import android.util.Log;

/**
 * A Card store holds all information of kanji or vocabulary cards.
 * So a *Cardbox* just holds the kanji or Japanese vocabulary as
 * reference to a card stored here.
 * Downside is that you need to have the full card store loaded
 * when opening a cardbox, e.g. with personal words
 */
public class CardStore
{
    private HashMap<String, Card> _map;
    private final String TAG = "CardStore";
    private int _mode;
    private Db _db;

    CardStore(Db db, int mode)
    {
        _db = db;
        _mode = mode;
        _map = new HashMap<String, Card>();
    }

    public void clear()
    {
        _map.clear();
    }

    public String[] getKeysByType(int type)
    {
        return _db.getKeysByType(type);
    }

    public Card get(String str)
    {
        Card c = _map.get(str);
        if (null != c)
            return c;
       
        try {
            int a = Integer.parseInt(str);
            c = _db.findById(a);
        } catch (Exception e) {
            Card[] cards = _db.findByJapanese(_mode, str);
            if ((cards.length > 0) && (null != cards[0]))
                c = cards[0];
            else
                return null;
        }

        if (null == c)
            return null;

        _map.put(str, c);
        return c;
    }

    public int size()
    {
        return _map.size();
    }
}
