package com.mlieber.KanjiKing.Db;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.mlieber.KanjiKing.CardBox.Card;
import com.mlieber.KanjiKing.Search.Criteria;

import java.util.Vector;

public class Db extends FileDb
{
    private static final String TAG = "KanjiKing/DB";

    protected static String DB_QUERY_FIELDS = "card._id, card.pack, type, japanese, reading_on, reading_kun"
            + ", frequency, frequency2, grade, strokes, radical"
            + ", hadamitzky, halpern, words";

    public Db(Context context) {
        super(context);
    }

    public String[] getKeysByType(int type) {
        String stmt = "select _id"
                + " from card where "
                + "type=?"
                + "order by frequency ASC";

        Cursor mCursor = _db.rawQuery(stmt, new String[]{type + ""});

        if (null == mCursor)
            return new String[0];

        if (!mCursor.moveToFirst()) {
            mCursor.close();
            return new String[0];
        }

        Vector<String> v = new Vector<String>();

        do {
            v.add(mCursor.getInt(0) + "");
        } while (mCursor.moveToNext());

        String[] ar = new String[v.size()];
        v.toArray(ar);

        mCursor.close();
        return ar;
    }

    public Card[] findByJapanese(int type, String japanese) {
        String filter = "japanese= ?";
        if (type > 0)
            filter = filter + " AND type=" + type;
        return findByFilter(filter, japanese);
    }

    public Card findById(int id) {
        Card[] cards = findByFilter("_id=?", id + "");
        if (cards.length > 0)
            return cards[0];
        return null;
    }

    public Card[] findByRadical(int radical) {
        return findByFilter(
                "radical=" + radical
                + " AND type=" + Card.TYPE_KANJI,
                null
        );
    }

    public Card[] findByStrokes(int strokes) {
        return findByFilter(
                "strokes=" + strokes
                + " AND type=" + Card.TYPE_KANJI,
                null
        );
    }

    public Card[] findByReading(String reading) {
        return findByFilter(
                    "(reading_on='" + mask(reading) + "'"
                    + " OR reading_kun='" + mask(reading) + "')"
                    + " AND type=" + Card.TYPE_KANJI,
                    null
        );
    }

    public Card[] findByType(int type) {
        return findByFilter("type=" + type, null);
    }

    public Card[] listRadicals() {
        return findByFilter("type="+Card.TYPE_RADICAL + " ORDER BY radical ASC", null);
    }

    public Card[] findByCriteria(Criteria criteria) {
        if (criteria.isEmpty()) {
            return new Card[0];
        }

        String stmt = new SearchStatement(criteria).toString();
        return findByStatement(stmt, null);
    }

    public static String mask(String str) {
        if (str == null) {
            return null;
        }

        return str.replace("'", "\\'");
    }

    private Card[] findByFilter(String filter, String value) {
        String stmt = "select " + DB_QUERY_FIELDS
                + " from card where "
                + filter
                + ";";

        return findByStatement(stmt, value);
    }

    private Card[] findByStatement(String stmt, String value) {
        Cursor mCursor = _db.rawQuery(stmt, value == null ? new String[0] : new String[]{value});
        Card[] cards = loadCardsFromCursor(mCursor);
        mCursor.close();
        return cards;
    }

    private Card[] loadCardsFromCursor(Cursor mCursor) {
        if (null == mCursor)
            return new Card[0];

        if (!mCursor.moveToFirst()) {
            mCursor.close();
            return new Card[0];
        }

        Vector<Card> v = new Vector<Card>();
        do {
            Card c = loadFromCursor(mCursor);
            if (c.getId() > 0)
                v.add(c);
        } while (mCursor.moveToNext());

        Card[] ar = new Card[v.size()];
        v.toArray(ar);

        mCursor.close();
        return ar;
    }

    private Card loadFromCursor(Cursor mCursor) {
        if (mCursor.isNull(0))
            return null;

        Card card = new Card(mCursor.getString(3));
        card.setId(mCursor.getInt(0));
        card.setPack(mCursor.getInt(1));
        card.setType(mCursor.getInt(2));

        card.setOnReading(mCursor.getString(4));
        card.setKunReading(mCursor.getString(5));
        card.setFrequency(mCursor.getInt(6));
        card.setFrequency2(mCursor.getInt(7));

        card.setGrade(mCursor.getInt(8));
        card.setStrokesCount(mCursor.getInt(9));
        card.setRadical(mCursor.getInt(10));
        card.setHadamitzky(mCursor.getInt(11));
        card.setHalpern(mCursor.getInt(12));

        String _words = mCursor.getString(13);
        if ((null != _words) && ("" != _words)) {
            String[] words = _words.split(",");

            for (int c = 0; c < words.length; c++) {
                if (words[c] != "")
                    card.getWords().add(words[c]);
            }
        }

        this.loadLanguage(card, "de");

        return card;
    }

    private Card loadLanguage(Card card, String lang) {
        String stmt = "select meaning, hint"
                + " from card_lang"
                + " where card = ?"
                + " AND language = ?";

        Cursor mCursor = _db.rawQuery(stmt, new String[]{card.getId() + "", lang});

        if (null == mCursor)
            return card;

        if (!mCursor.moveToFirst()) {
            mCursor.close();
            return card;
        }

        if (mCursor.isNull(0)) {
            mCursor.close();
            return card;
        }

        card.setMeaning(lang, mCursor.getString(0));
        card.setHint(lang, mCursor.getString(1));
        mCursor.close();

        return card;
    }
}
