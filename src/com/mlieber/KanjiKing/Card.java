package com.mlieber.KanjiKing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import android.util.Log;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Card implements java.io.Serializable {

    private static final String TAG = "Card";

    public static final int TYPE_KANJI    = 1;
    public static final int TYPE_WORD     = 2;
    public static final int TYPE_RADICAL  = 3;

	private String _japanese	= null;
	private String _reading_on	= null;
	private String _reading_kun	= null;
    private Style  _style       = null;

    private int _id             = 0;
    private int _pack           = 0;
    private int _grade       	= 0;
	private int _frequency 		= 0;
	private int _frequency2 	= 0;
	private int _hadamitzky	    = 0;
	private int _halpern		= 0;
	private int _radical		= 0;
	private int _strokes		= 0;
    private int _type           = TYPE_KANJI;

	private HashMap<String, String>	_meaning;
    private HashMap<String, String> _hint;
    private Vector<String> _words;

    public Card(String japanese) {
		_japanese   = japanese;
		_meaning    = new HashMap<String, String>();
		_hint       = new HashMap<String, String>();
        _words      = new Vector<String>();
	}

    public Card() {
		_meaning    = new HashMap<String, String>();
		_hint       = new HashMap<String, String>();
        _words      = new Vector<String>();
    }

	public String getJapanese() { return _japanese; }
	public String getOnReading() { return _reading_on; }
	public String getKunReading() { return _reading_kun; }
	public int getFrequency() { return _frequency; }
	public int getFrequency2() { return _frequency2; }
	public int getHadamitzkyNumber() { return _hadamitzky; }
	public int getHalpernNumber() { return _halpern; }
	public int getStrokesCount() { return _strokes; }
	public int getRadical() { return _radical; }
	public int getGrade() { return _grade; }
	public int getId() { return _id; }
	public int getPack() { return _pack; }
    public String getStyle() { if (_style == null ) return null; return _style.toString(); }
    public int getType() { return _type; }

    public Vector<String> getWords() { return _words; }

    public Card addWord(String word) {
        _words.add(word);
        return this;
    }

    public String getMeaning(String language) { return _meaning.get(language); }
	public String getHint(String language) { return _hint.get(language); }

    public String getInfo() {
        StringBuilder sb = new StringBuilder();
                
        sb.append("F: ")
           .append(getFrequency())
            .append(" / G: ")
            .append(getGrade())
            .append(" / S: ")
            .append(getStrokesCount())
            .append(" / R: ")
            .append(getRadical())
            .append(" / H: ")
            .append(getHadamitzkyNumber());

        return sb.toString();
    }
    
    public Card setType(int type) {
        _type = type;
        return this;
    }

    public Card setStyle(Style style) {
        _style = style;
        return this;
    }

	public Card setJapanese(String japanese) {
		_japanese = japanese;
		return this;
	}

	public Card setOnReading(String reading_on) {
		_reading_on = reading_on;
		return this;
	}

	public Card setKunReading(String reading_kun) {
		_reading_kun = reading_kun;
		return this;
	}

	public Card setMeaning(String language, String meaning) {
		_meaning.put(language, meaning);
		return this;
	}

	public Card setHint(String language, String hint) {
		_hint.put(language, hint);
		return this;
	}

	public Card setFrequency(int frequency) {
		_frequency = frequency;
		return this;
	}

	public Card setFrequencyi2(int frequency) {
		_frequency2 = frequency;
		return this;
	}

	public Card setHadamitzky(int hadamitzky) {
		_hadamitzky = hadamitzky;
		return this;
	}

	public Card setHalpern(int halpern) {
		_halpern = halpern;
		return this;
	}

	public Card setStrokesCount(int strokes) {
		_strokes = strokes;
		return this;
	}

	public Card setRadical(int radical) {
		_radical = radical;
		return this;
	}

	public Card setGrade(int grade) {
		_grade = grade;
		return this;
	}



    public boolean hasReading(String reading, boolean subStrings)
    {
        // First, search direct hits
        if (null != _reading_on) {
            if (_reading_on.equals(reading))
                return true;
        }

        if (null != _reading_kun) {
            if (_reading_kun.equals(reading))
                return true;
        }

        // Then, search general appearance
        String haystack = "";
        if (null != _reading_on)
            haystack = _reading_on;
        if (null != _reading_kun)
            haystack = haystack + ", " + _reading_kun;

        if (-1 == haystack.indexOf(reading))
            return false;

        if (subStrings)
            return true;

        // Then, search by pattern
        String pattern = "^(.+[;, -]+)?(" + reading + ")([;, \\(-]+.+)?$";
        if (haystack.matches(pattern))
            return true;

        return false;
    }



    public boolean hasMeaning(String meaning, String language, boolean subStrings)
    {
        String _meaning = getMeaning(language);

        if (null == _meaning)
            return false;

        if (-1 == _meaning.indexOf(meaning))
            return false;

        if (subStrings)
            return true;

        // Then, search by pattern
        String pattern = "^(.+[;, -])?(" + meaning + ")([;, \\(-].+)?$";
        if (_meaning.matches(pattern))
            return true;

        return false;
    }

	public Card setPack(int pack) {
		_pack = pack;
		return this;
	}


    /*********************** DB stuff ***************************/

    protected static Card[] loadCardsFromCursor(Cursor mCursor)
    {
        if (null == mCursor)
            return new Card[0];
       
        if (! mCursor.moveToFirst()) {
            mCursor.close();
            return new Card[0];
        }

        Vector<Card> v = new Vector<Card>();
        do {
            Card c = new Card();
            c.loadFromCursor(mCursor);
            if (c.getId() > 0)
                v.add(c);
        } while (mCursor.moveToNext());

        Card[] ar = new Card[v.size()];
        v.toArray(ar);

        mCursor.close();
        return ar;
    }

    protected static String dbQueryFields = "_id, pack, type, japanese, reading_on, reading_kun"
                                        + ", frequency, frequency2, grade, strokes, radical"
                                        + ", hadamitzky, halpern, words";

    protected static Card[] findByFilter(SQLiteDatabase db, String filter, String value)
    {
        String stmt = "select " + dbQueryFields
                    + " from card where "
                    + filter;
        Cursor mCursor = db.rawQuery(stmt, value == null ? new String[0] : new String[] { value });
        Card[] cards = loadCardsFromCursor(mCursor);
        mCursor.close();
        return cards;
    }

    public static String[] getKeysByType(SQLiteDatabase db, int type)
    {
        String stmt = "select _id"
                    + " from card where "
                    + "type=?"
                    + "order by _id ASC";

        Cursor mCursor = db.rawQuery(stmt, new String[] { type + ""});

        if (null == mCursor)
            return new String[0];

        if (! mCursor.moveToFirst()) {
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

    public static Card[] findByType(SQLiteDatabase db, int type)
    {
        return findByFilter(db, "type=" + type, null);
    }

    public static Card[] findByPack(SQLiteDatabase db, int pack)
    {
        return findByFilter(db, "pack=" + pack, null);
    }

    public static Card[] findByJapanese(SQLiteDatabase db, int type, String japanese)
    {
        String filter = "japanese= ?";
        if (type > 0)
            filter = filter + " AND type=" + type;
        return findByFilter(db, filter, japanese);
    }

    public static Card findById(SQLiteDatabase db, int id)
    {
        Card[] cards = findByFilter(db, "_id=?", id + "");
        if (cards.length > 0)
            return cards[0];
        return null;
    }

    public boolean loadFromCursor(Cursor mCursor)
    {
        if (mCursor.isNull(0))
            return false;
        
        _id             = mCursor.getInt(0);
        _pack           = mCursor.getInt(1);
        _type           = mCursor.getInt(2);
        _japanese       = mCursor.getString(3);
        _reading_on     = mCursor.getString(4);
        _reading_kun    = mCursor.getString(5);
        _frequency      = mCursor.getInt(6);
        _frequency2     = mCursor.getInt(7);
        _grade          = mCursor.getInt(8);
        _strokes        = mCursor.getInt(9);
        _radical        = mCursor.getInt(10);
        _hadamitzky     = mCursor.getInt(11);
        _halpern        = mCursor.getInt(12);
        
        String _words = mCursor.getString(13);
        if ((null != _words) && ("" != _words)) {
            String[] words = _words.split(",");

            for (int c = 0; c < words.length; c++) {
                if (words[c] != "")
                    addWord(words[c]);
            }
        }

        loadLanguage("de");

        return true;
    }

    protected boolean loadLanguage(String lang)
    {
        String stmt = "select meaning, hint"
                    + " from card_lang"
                    + " where card = ?"
                    + " AND language = ?";

        Cursor mCursor = KanjiKing.getDB().rawQuery(stmt, new String[] { _id + "", lang});

        if (null == mCursor)
            return false;

        if (! mCursor.moveToFirst()) {
            mCursor.close();
            return false;
        }

        if (mCursor.isNull(0)) {
            mCursor.close();
            return false;
        }
        
        setMeaning(lang, mCursor.getString(0));
        setHint(lang, mCursor.getString(1));
        mCursor.close();
        return true;
    }

    public static Card[] list(SQLiteDatabase db, int type)
    {
        return findByType(db, type);
    }

/*
    public boolean load(SQLiteDatabase db)
    {
        if (1 > _id)
            return false;

        Cursor mCursor = this._load(db, _id, 0);

        if (null == mCursor)
            return false;

        if (!mCursor.moveToFirst()) {
            mCursor.close();
            return false;
        }

        if (! this.loadFromCursor(mCursor)) {
            mCursor.close();
            return false;
        }

        mCursor.close();
        return true;
    }

    public boolean save(SQLiteDatabase db)
    {
        ContentValues values = new ContentValues();
        values.put("pack", _pack);
        values.put("type", _type);
        values.put("japanese", _japanese);
        values.put("reading_on", _reading_on);
        values.put("reading_kun", _reading_kun);
        values.put("frequency", _frequency);
        values.put("frequency2", _frequency2);
        values.put("grade", _grade);
        values.put("strokes", _strokes);
        values.put("radical", _radical);
        values.put("hadamitzky", _hadamitzky);
        values.put("halpern", _halpern);
        int erg;

        if (_id > 0) {
            Log.i(TAG, "Updating card " + _japanese + " with id " + _id);
            return (db.update("card", values,
                "_id=" + _id, null) > 0);
        }

        erg = (int) db.insert("card", null, values);
        if (erg > 0) {
            _id = erg;
            return true;
        } else {
            Log.i(TAG, "Card " + _japanese + " not could be stored: " + erg);
        }
        
        return false;
    }


    public boolean delete(SQLiteDatabase db)
    {
        if (1 > _id)
            return false;

        return (db.delete("card", "_id=" + _id, null) > 0);
    }
*/


}

