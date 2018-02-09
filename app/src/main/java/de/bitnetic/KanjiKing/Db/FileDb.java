package de.bitnetic.KanjiKing.Db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by nudge on 11.10.16.
 */
class FileDb extends SQLiteOpenHelper
{
    private static final String TAG = "KanjiKing/FileDB";

    //The Android's default system path of the database.
    private static String DB_PATH = "/data/data/de.bitnetic.KanjiKing/databases/";
    private static String DB_NAME = "kanjiking.db";

    protected SQLiteDatabase _db;
    protected final Context _cxt;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param Context context
     */
    public FileDb(Context context) {
        super(context, DB_NAME, null, 1);
        _db = null;
        _cxt = context;
    }

    public SQLiteDatabase getDB() {
        return _db;
    }

    public void initialize() {
        try {
            create();
            open();
        } catch (Exception e) {
            Log.e(TAG, "Error creating or opening the DB." + e.getMessage(), e);
            throw new Error("Error creating or opening the DB.");
        }
    }

    @Override
    public synchronized void close() {
        if (_db != null)
            _db.close();

        _db = null;
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    private void create() throws IOException {
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
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean check() {
        SQLiteDatabase checkDB = null;
        Log.i(TAG, "Checking DB...");

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            Log.i(TAG, "Database already exists.");
        } catch (SQLiteException e) {
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
     */
    private void copy() throws IOException {
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

    private void open() throws SQLException {
        if (null != _db)
            return;

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        _db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }
}
