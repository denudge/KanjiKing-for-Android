package com.mlieber.KanjiKing;

import android.app.Activity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.util.Log;
import android.text.TextUtils;
import android.content.res.Configuration;

import org.apache.http.protocol.HTTP;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.net.Uri;

import android.widget.Button;
// import android.widget.ProgressBar;
import android.widget.TextView;

import android.view.View;
import android.view.MotionEvent;

import java.io.*;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.view.Menu;
import android.view.MenuItem;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;

public class KanjiKing extends Activity
{
    private static final String TAG = "KanjiKing";
    public static final int MODE_KANJI = 1;
    public static final int MODE_WORDS = 2;
    public static final int MENU_EXPORT_ID = Menu.FIRST;
    public static final int MENU_IMPORT_ID = Menu.FIRST + 1;
    public static final int MENU_RESET_ID = Menu.FIRST + 2;
    public static final int MENU_SWITCH_MODE_KANJI_ID = Menu.FIRST + 3;
    public static final int MENU_SWITCH_MODE_WORDS_ID = Menu.FIRST + 4;
    public static final int MENU_SETTINGS_ID = Menu.FIRST + 5;
    public static final int MENU_SEARCH_ID = Menu.FIRST + 6;

    // sub objects
    protected static CardStore _cardstore   = null;
    protected static CardBox _box           = null;
    protected static DBHelper _dbhelper     = null;

    // Settings
    private static int _mode               = MODE_KANJI;
    private static boolean _endless        = false;
    private static boolean _show_words     = true;
    private static int _max_freq           = 0;
    private static String _language        = "de";

    // The card view
    private Button _no_button, _yes_button, _hint_button;
    private Button _word[];

    // private ProgressBar _overall_score;
    private TextView _hint_field;
    private WebView _card_webview;


    public static int getMode()
    {
        return _mode;
    }

    public static boolean getEndless()
    {
        return _endless;
    }

    public static int getMaxFreq()
    {
        return _max_freq;
    }

    public static String getLanguage()
    {
        return _language;
    }

    public static CardStore getCardStore()
    {
        return _cardstore;
    }

    public static CardBox getCardBox()
    {
        return _box;
    }

    public void onPause(Bundle savedInstanceState)
    {
        saveToDisk();
        _dbhelper.close();
    }

    @Override
    public void onDestroy()
    {
        saveToDisk();
        _dbhelper.close();
        super.onDestroy();
    }

    public String getFilename()
    {
        if (_mode == MODE_KANJI)
            return "kanjibox";
        else
            return "wordbox";

    }

    public void loadCardStore()
    {
        _cardstore = new CardStore();
        _cardstore.clear();
    }

    private void loadSettings()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        _mode       = Integer.parseInt(settings.getString("mode", "1"));
        _endless    = settings.getBoolean("endless", false);
        _max_freq   = Integer.parseInt(settings.getString("max_freq", "0"));
        _language   = settings.getString("language", "de");
        Log.i(TAG, "Settings activated.");
    }

    @Override
    protected void onResume()
    {
    	super.onResume();
        loadSettings();
        initializeDB();
    }
/*
    @Override
    protected void onStop() {
       super.onStop();
    
        // Change preferences
        SharedPreferences settings = getSharedPreferences(“MyParams”, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("endless", _endless);
        editor.putInteger("mode", _mode);
        editor.putInteger("max_freq", _max_freq);
        editor.putString("language", _language);

        // Save changes
        editor.commit();       
    }
*/

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // load settings
        loadSettings();

        // start DB
        initializeDB();

        // Load card store
        loadCardStore();

        // Construct a box if not already done
        loadFromDisk();
        if (_box == null)
            _box = new CardBox(CardBox.ORDER_FREQUENCY, true);

        // we start with the card view
        setContentView(R.layout.card);

        // Initialize layout
        _card_webview = (WebView)findViewById(R.id.card_webview);
        _yes_button = (Button)findViewById(R.id.yes_button);
        _no_button = (Button)findViewById(R.id.no_button);
        _hint_button = (Button)findViewById(R.id.hint_button);
        _hint_field = (TextView)findViewById(R.id.hint_field);

        _word = new Button[5];
        _word[0] = (Button)findViewById(R.id.word1);
        _word[1] = (Button)findViewById(R.id.word2);
        _word[2] = (Button)findViewById(R.id.word3);
        _word[3] = (Button)findViewById(R.id.word4);
        _word[4] = (Button)findViewById(R.id.word5);

        _card_webview.setOnTouchListener(screenTouchListener);

        // _overall_score.setVisibility(View.INVISIBLE);
        _hint_field.setVisibility(View.INVISIBLE);

        // and display the next card on startup
        showQuestion();
    
        _no_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _box.answer(false);
                saveToDisk();
                showQuestion();
            }
        });

        _yes_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _box.answer(true);
                saveToDisk();
                showQuestion();
            }
        });

        _hint_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showHint();
            }
        });

        View.OnClickListener wordExplainer = new View.OnClickListener() {
            public void onClick(View v) {
                String url = "http://www.wadoku.de/index.jsp?search=search&phrase=" + ((Button) v).getText() + "&search=suche";

                final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
                startActivity(intent);
                
                // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                // startActivity(browserIntent);
            }       
        };

        for (int i = 0; i < 5; i++) {
            _word[i].setOnClickListener(wordExplainer);
        }
    }

    private final View.OnTouchListener screenTouchListener = new View.OnTouchListener()
    {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (View.INVISIBLE == _no_button.getVisibility()) {
                    showAnswer();
                    return true;
                }
            }
            return true;
        }
    };

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    boolean result = super.onCreateOptionsMenu(menu);
    menu.add(1, MENU_EXPORT_ID, 1, "Export");
    menu.add(2, MENU_IMPORT_ID, 2, "Import");
    menu.add(3, MENU_RESET_ID, 3, "Reset");
    menu.add(4, MENU_SWITCH_MODE_KANJI_ID, 4, "Switch to kanji mode");
    menu.add(5, MENU_SWITCH_MODE_WORDS_ID, 5, "Switch to words mode");
    menu.add(6, MENU_SETTINGS_ID, 6, "Settings");
    menu.add(7, MENU_SEARCH_ID, 6, "Search");

    return result;
  }
    
  
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if (_mode == MODE_KANJI) {
          menu.findItem(MENU_SWITCH_MODE_WORDS_ID).setVisible(true);
          menu.findItem(MENU_SWITCH_MODE_KANJI_ID).setVisible(false);
        } else {
          menu.findItem(MENU_SWITCH_MODE_WORDS_ID).setVisible(false);
          menu.findItem(MENU_SWITCH_MODE_KANJI_ID).setVisible(true);
        }

        return true;
  }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case MENU_EXPORT_ID:
                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                File file = new File(extStorageDirectory, "/" + getFilename() + ".xml");
                saveToFile(file, true);
                return true;
            case MENU_IMPORT_ID:
                extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                file = new File(extStorageDirectory, "/" + getFilename() + ".xml");
                loadFromFile(file, true);
                showQuestion();
                return true;
            case MENU_RESET_ID:
                _box = new CardBox(CardBox.ORDER_FREQUENCY, true);
                showQuestion();
                return true;
            case MENU_SWITCH_MODE_KANJI_ID:
            case MENU_SWITCH_MODE_WORDS_ID:
                if (_mode == MODE_KANJI)
                    _mode = MODE_WORDS;
                else
                    _mode = MODE_KANJI;

                loadCardStore();
                _box = null;
                loadFromDisk();
                if (_box == null)
                    _box = new CardBox(CardBox.ORDER_FREQUENCY, true);
                showQuestion();
                return true;

            case MENU_SETTINGS_ID:
                Intent settingsActivity = new Intent(getBaseContext(),
                       Settings.class);
                startActivity(settingsActivity);
                loadSettings();
                return true;

            case MENU_SEARCH_ID:
                Intent searchActivity = new Intent(getBaseContext(),
                       Search.class);
                startActivity(searchActivity);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean showQuestion()
    {
        _no_button.setVisibility(View.INVISIBLE);
        _yes_button.setVisibility(View.INVISIBLE);

        // Reset hint
        _hint_field.setText(null);
        _hint_field.setVisibility(View.INVISIBLE);
        _hint_button.setVisibility(View.INVISIBLE);

        for (int i = 0; i < 5; i++) {
            _word[i].setText("");
            _word[i].setVisibility(View.INVISIBLE);
        }

        if (_box.findNextList() % 2 == 1)
            return showCard(_box.getNextCard(), false, true);
        else
            return showCard(_box.getNextCard(), true, false);
    }

    private boolean showAnswer()
    {
        _no_button.setVisibility(View.VISIBLE);
        _yes_button.setVisibility(View.VISIBLE);
        return showCard(_box.getNextCard(), true, true);
    }

    private void showHint()
    {
        _hint_field.setVisibility(View.VISIBLE);
        _hint_button.setVisibility(View.INVISIBLE);
    }

  private boolean showCard(String str, boolean show_japanese, boolean show_explanation)
  {
    StringBuilder card_html = new StringBuilder();

    Card card = _cardstore.get(str);

    if (card == null)
    {
        Log.i(TAG, "Card cannot be found.");
        return false;
    }

    if (View.INVISIBLE == _hint_field.getVisibility()) {
        if (null != card.getHint(_language)) {
            _hint_field.setText(card.getHint(_language));
            _hint_button.setVisibility(View.VISIBLE);
        }
    }

    String style= "body {text-align: center; color: white; }\n"
                + "div.info, div.status { color: #888888; font-size:90%; }\n"
                + "div.japanese {font-size: 500%; color: #99ff99; }\n"
		        + "div.reading_on {font-size: 230%; color: #9090ff; font-variant: small-caps; }\n"
		        + "div.reading_kun {font-size: 200%; color: #9090ff; }\n"
                + "div.meaning {color: #e0e0e0;}\n";

    card_html.append("<html>")
        .append("<head>")
        .append("<title>KanjiKing</title>")
        .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");

        card_html.append("<style type=\"text/css\">")
            .append(TextUtils.htmlEncode(style))
            .append("</style>");

    card_html.append("</head>")
        .append("<body>");

    card_html.append("<div class=\"status\">")
        .append(TextUtils.htmlEncode(_box.getStatus()))
        .append("</div><br>");

    // ableiten, ob wir im Wort- oder Kanji-Modus sind
    boolean show_reading = false;
    if (card.getType()==Card.TYPE_KANJI)
        show_reading = show_explanation;
    else
        show_reading = (show_japanese && show_explanation);

    if (show_japanese) {
        card_html
            .append("<div class=\"info\">")
            .append(card.getInfo())
            .append("</div><br>");

        if (card.getJapanese() != null)
            card_html.append("<div class=\"japanese\">")
                .append(TextUtils.htmlEncode(card.getJapanese()))
                .append("</div>");
    } else {
        card_html.append("<div class=\"info\">&nbsp;</div><br>")
            .append("<div class=\"japanese\">&nbsp;</div>");
    }

    if (show_reading) {
        if (card.getOnReading() != null)
            card_html.append("<div class=\"reading_on\">")
                .append(TextUtils.htmlEncode(card.getOnReading()))
                .append("</div>");

        if (card.getKunReading() != null)
            card_html.append("<div class=\"reading_kun\">")
                .append(TextUtils.htmlEncode(card.getKunReading()))
                .append("</div>");
    }
    else 
        card_html.append("<div class=\"reading_kun\">&nbsp;</div>");

    if (show_explanation) {
        if (card.getMeaning(_language) != null) {
            card_html.append("<div class=\"meaning\">")
                .append(TextUtils.htmlEncode(card.getMeaning(_language)))
                .append("</div>");
        } else {
            card_html.append("<div class=\"meaning\">(no meaning available, switch language)</div>");
        }

    }
    else
        card_html.append("<div class=\"meaning\">&nbsp;</div>");

    if ((show_japanese) && (show_explanation)) {
        card_html.append("<div class=\"words\">");
        int i = 0;
        for (String word : card.getWords()) {
            _word[i].setText(word);
            _word[i].setVisibility(View.VISIBLE);
            /*
            card_html.append("<div class=\"word\">")
                // .append("<a href=\"http://www.wadoku.de/index.jsp?search=search&phrase=" + TextUtils.htmlEncode(word) + "&search=suche\">")
                .append(TextUtils.htmlEncode(word))
                // .append("</a>")
                .append("</div>");
            */
            i++;
            if (i >= 5)
                break;
        }
        card_html.append("</div>");
    }

    card_html.append("</body>");

    if (_card_webview == null)
    {
        Log.i(TAG, "Webview is null!");
        return false;
    }
   
    _card_webview.loadDataWithBaseURL(null, card_html.toString(), "text/html", HTTP.UTF_8, null);
    _card_webview.setBackgroundColor(0xff000000);
    // _card_webview.setVisibility(View.VISIBLE);
    return true;
  }




    /********************** Storage functions ******************************/

    public void saveToDisk() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(_box);
            byte[] buf = bos.toByteArray();

            FileOutputStream fos = openFileOutput(getFilename(), Context.MODE_PRIVATE);
            fos.write(buf);
            fos.close(); 
        } catch (IOException e) { 
          Log.v(TAG, "Error Serialising the cardbox: " + e.getMessage(), e);
        } 
    }

    public void loadFromDisk() {
        InputStream instream = null;
        try {
            instream = openFileInput(getFilename());
        } catch (FileNotFoundException e) {
            Log.v(TAG, "Error opening the cardbox file: " + e.getMessage()); 
            return;
        }
       
        try {
            ObjectInputStream ois = new ObjectInputStream(instream);
            try {
                _box = (CardBox) ois.readObject();
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "DESERIALIZATION FAILED (CLASS NOT FOUND):" + e.getMessage(), e);
                return;
            }
        } catch (StreamCorruptedException e) {
            Log.e(TAG, "DESERIALIZATION FAILED (CORRUPT):" + e.getMessage(), e);
            return;
        } catch (IOException e) {
            Log.e(TAG, "DESERIALIZATION FAILED (IO EXCEPTION):"+e.getMessage(), e);
            return;
        }
    }


    public void saveToFile(File file, boolean toasts)
    {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(_box.asXML().getBytes("UTF-8"));
            fos.flush();
            fos.close(); 
            Toast.makeText(KanjiKing.this, "Exported to " + getFilename() + ".xml", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            if (toasts)
                Toast.makeText(KanjiKing.this, e.toString(), Toast.LENGTH_LONG).show();
            return;
        } catch (IOException e) {
            if (toasts)
                Toast.makeText(KanjiKing.this, e.toString(), Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void loadFromFile(File file, boolean toasts)
    {
        FileInputStream fis;

        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            if (toasts)
                Toast.makeText(KanjiKing.this, e.toString(), Toast.LENGTH_LONG).show();
            return;
        }

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(fis, "UTF-8");
            _box = new CardBox(CardBox.ORDER_FREQUENCY, false);
            _box.loadFromXML(xpp);
        } catch (XmlPullParserException e) {
            if (toasts)
                Toast.makeText(KanjiKing.this, e.toString(), Toast.LENGTH_LONG).show();
            return;
        }
    }


    
    /********************** DB functions ******************************/

    public static SQLiteDatabase getDB()
    {
        return _dbhelper.getDB();
    }

    private void initializeDB()
    {
        if (null == _dbhelper)
            _dbhelper = new DBHelper(this);

        try {
            _dbhelper.createDataBase();
        } catch (IOException ioe) {
            Log.e(TAG, "Error creating the DB." + ioe.getMessage(), ioe);
            throw new Error("Error creating the DB.");
        }

        try {
            _dbhelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
    }

}

