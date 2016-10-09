package com.mlieber.KanjiKing.Db;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mlieber.KanjiKing.CardBox.Card;

public class Db extends SQLiteOpenHelper
{
    private static final String TAG = "Db";

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.mlieber.KanjiKing/databases/";
    private static String DB_NAME = "kanjiking.db";

    private SQLiteDatabase _db; 
    private final Context _cxt;

    protected static String dbQueryFields = "_id, pack, type, japanese, reading_on, reading_kun"
            + ", frequency, frequency2, grade, strokes, radical"
            + ", hadamitzky, halpern, words";

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public Db(Context context)
    {
        super(context, DB_NAME, null, 1);
        _db = null;
        _cxt = context;
    }	

    public SQLiteDatabase getDB()
    {
        return _db;
    }

    public void initialize()
    {
        try {
            create();
        } catch (IOException ioe) {
            Log.e(TAG, "Error creating the DB." + ioe.getMessage(), ioe);
            throw new Error("Error creating the DB.");
        }

        try {
            open();
        } catch (SQLException sqle) {
            throw sqle;
        }
    }

    @Override
    public synchronized void close()
    {
        if(_db != null)
            _db.close();

        _db = null;
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) { }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    private void create() throws IOException
    {
        boolean dbExist = check();

        if (dbExist)
            return;

        // By calling this method and empty database will be created into the default system path
        // of your application so we are gonna be able to overwrite that database with our database.
        this.getReadableDatabase();

        try {
            copy();
        } catch (IOException e) {
            Log.i(TAG, "Error copying DB.");
            throw new Error("Error copying DB.");
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean check()
    {
        SQLiteDatabase checkDB = null;
        Log.i(TAG, "Checking DB...");

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            Log.i(TAG, "Database already exists.");
        } catch(SQLiteException e) {
            Log.i(TAG, "Database does not exist yet.");
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copy() throws IOException
    {
        //Open your local db as the input stream
        InputStream myInput = _cxt.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[4096];
        int length;

        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    private void open() throws SQLException
    {
        if (null != _db)
            return;
        
        //Open the database
        String myPath = DB_PATH + DB_NAME;
        _db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    public Card[] list(int type) {
        return findByType(type);
    }

    public String[] getKeysByType(int type)
    {
        String stmt = "select _id"
                + " from card where "
                + "type=?"
                + "order by frequency ASC";

        Cursor mCursor = _db.rawQuery(stmt, new String[] { type + ""});

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

    public Card[] findByType(int type) {
        return findByFilter("type=" + type, null);
    }

    public Card[] findByPack(int pack) {
        return findByFilter("pack=" + pack, null);
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

    public Card[] findByRadical(int radical)
    {
        String stmt = "select " + dbQueryFields
                + " from card where"
                + " radical=" + radical
                + " AND type=" + Card.TYPE_KANJI
                + ";";

        Cursor mCursor = _db.rawQuery(stmt, new String[0]);
        Card[] cards = loadCardsFromCursor(mCursor);
        mCursor.close();
        return cards;
    }

    public Card[] findByStrokes(int strokes)
    {
        String stmt = "select " + dbQueryFields
                + " from card where"
                + " strokes=" + strokes
                + " AND type=" + Card.TYPE_KANJI
                + ";";

        Cursor mCursor = _db.rawQuery(stmt, new String[0]);
        Card[] cards = loadCardsFromCursor(mCursor);
        mCursor.close();
        return cards;
    }

    protected Card[] findByFilter(String filter, String value)
    {
        String stmt = "select " + dbQueryFields
                + " from card where "
                + filter;

        Cursor mCursor = _db.rawQuery(stmt, value == null ? new String[0] : new String[] { value });
        Card[] cards = loadCardsFromCursor(mCursor);
        mCursor.close();
        return cards;
    }

    public Card[] findByReading(String reading)
    {
        String stmt = "select " + dbQueryFields
                + " from card where "
                + " (reading_on='" + reading.replace("'", "\\'") + "'"
                + " OR reading_kun='" + reading.replace("'", "\\'") + "')"
                + " AND type=" + Card.TYPE_KANJI
                + ";";

        Cursor mCursor = _db.rawQuery(stmt, new String[0]);
        Card[] cards = loadCardsFromCursor(mCursor);
        mCursor.close();
        return cards;
    }

    protected Card[] loadCardsFromCursor(Cursor mCursor)
    {
        if (null == mCursor)
            return new Card[0];

        if (! mCursor.moveToFirst()) {
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

    protected Card loadFromCursor(Cursor mCursor) {
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

    protected Card loadLanguage(Card card, String lang) {
        String stmt = "select meaning, hint"
                + " from card_lang"
                + " where card = ?"
                + " AND language = ?";

        Cursor mCursor = _db.rawQuery(stmt, new String[] { card.getId() + "", lang});

        if (null == mCursor)
            return card;

        if (! mCursor.moveToFirst()) {
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
