package com.mlieber.KanjiKing.CardBox;

import java.util.HashMap;

import com.mlieber.KanjiKing.Db.Db;
import android.util.Log;

/**
 * A CardStore holds all information of kanji or vocabulary cards.
 * So a *Cardbox* just holds the kanji or Japanese vocabulary as
 * reference to a card stored here.
 * The CardStore itself uses lazy loading backed by the database.
 */
public class CardStore
{
    private final String TAG = "CardStore";

    private HashMap<String, Card> _map;
    private int _mode;
    private Db _db;

    public CardStore(Db db, int mode) {
        _db = db;
        _mode = mode;
        _map = new HashMap<String, Card>();
    }

    public void clear() {
        _map.clear();
    }

    public String[] getKeysByType(int type) {
        return _db.getKeysByType(type);
    }

    /**
     * Retrieves a Card, either by id or by Japanese as fallback.
     *
     * @param String str
     * @return Card
     */
    public Card get(String str) {
        Card c = _map.get(str);
        if (null != c)
            return c;

        try {
            c = fetchById(Integer.parseInt(str));
        } catch (Exception e) {
            c = fetchByJapanese(str);
        }

        if (null == c) {
            Log.e(TAG, "Could not find card " + str);
            return null;
        }

        // add to cache
        _map.put(str, c);

        return c;
    }

    public int size() {
        return _map.size();
    }

    public Card fetchById(int id) {
        return _db.findById(id);
    }

    public Card[] fetchByRadical(int radical) {
        return _db.findByRadical(radical);
    }

    public Card[] fetchByStrokes(int strokes) {
        return _db.findByStrokes(strokes);
    }

    public Card[] fetchByReading(String str) {
        return _db.findByReading(str);
    }

    public Card fetchByJapanese(String str) {
        Card[] cards = _db.findByJapanese(_mode, str);
        if ((cards.length > 0) && (null != cards[0])) {
            return cards[0];
        }

        return null;
    }

    public Card search(String str) {
        Card[] cards = _db.findByJapanese(Card.TYPE_KANJI, str);
        if ((cards.length > 0) && (null != cards[0])) {
            return cards[0];
        }

        return null;
    }
}
