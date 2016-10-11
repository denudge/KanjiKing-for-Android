package com.mlieber.KanjiKing;

import android.app.Activity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.util.Log;
import android.text.TextUtils;
import android.content.res.Configuration;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.net.Uri;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mlieber.KanjiKing.Activity.About;
import com.mlieber.KanjiKing.Activity.Search;
import com.mlieber.KanjiKing.Activity.Settings;
import com.mlieber.KanjiKing.CardBox.Card;
import com.mlieber.KanjiKing.CardBox.CardBox;
import com.mlieber.KanjiKing.CardBox.CardStore;
import com.mlieber.KanjiKing.CardBox.Storage.DiskStorage;
import com.mlieber.KanjiKing.CardBox.Storage.XmlStorage;
import com.mlieber.KanjiKing.Db.Db;

import java.io.*;

import com.mlieber.KanjiKing.Element.CardView;
import com.mlieber.KanjiKing.Element.KanjiInfo;
import org.apache.http.protocol.HTTP;

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
    public static final int MENU_ABOUT_ID = Menu.FIRST + 7;

    // sub objects
    protected static CardStore _cardstore = null;
    protected static CardBox _box = null;
    protected static Db _db = null;

    // Settings
    private static int _mode = MODE_KANJI;
    private static boolean _endless = false;
    private static boolean _show_words = true;
    private static int _max_freq = 0;
    private static String _language = "de";

    // The card view
    private Button _no_button, _yes_button, _hint_button;
    private Button _word[];

    // private ProgressBar _overall_score;
    private TextView _hint_field;
    private WebView _card_webview;


    public static int getMode() {
        return _mode;
    }

    public static boolean getEndless() {
        return _endless;
    }

    public static int getMaxFreq() {
        return _max_freq;
    }

    public static String getLanguage() {
        return _language;
    }

    public static CardStore getCardStore() {
        return _cardstore;
    }

    public static CardBox getCardBox() {
        return _box;
    }

    public static Db getDb() {
        return _db;
    }

    public void onPause(Bundle savedInstanceState) {
        saveToDisk();
        _db.close();
    }

    @Override
    public void onDestroy() {
        saveToDisk();
        _db.close();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSettings();

        if (null == _db)
            _db = new Db(this);
        _db.initialize();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load settings
        loadSettings();

        // start DB
        if (null == _db)
            _db = new Db(this);
        _db.initialize();

        // Load card store
        initializeCardStore();

        // Construct a box if not already done
        loadFromDisk();
        if (_box == null) {
            _box = new CardBox(_cardstore, _mode, CardBox.ORDER_FREQUENCY, true, _max_freq, _endless);
        }

        // we start with the card view
        setContentView(R.layout.card);

        // Initialize layout
        _card_webview = (WebView) findViewById(R.id.card_webview);
        _yes_button = (Button) findViewById(R.id.yes_button);
        _no_button = (Button) findViewById(R.id.no_button);
        _hint_button = (Button) findViewById(R.id.hint_button);
        _hint_field = (TextView) findViewById(R.id.hint_field);

        _word = new Button[5];
        _word[0] = (Button) findViewById(R.id.word1);
        _word[1] = (Button) findViewById(R.id.word2);
        _word[2] = (Button) findViewById(R.id.word3);
        _word[3] = (Button) findViewById(R.id.word4);
        _word[4] = (Button) findViewById(R.id.word5);

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
                String url = "http://wadoku.de/search/" + ((Button) v).getText();

                final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
                startActivity(intent);
            }
        };

        for (int i = 0; i < 5; i++) {
            _word[i].setOnClickListener(wordExplainer);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(1, MENU_EXPORT_ID, 1, "Export");
        menu.add(2, MENU_IMPORT_ID, 2, "Import");
        menu.add(3, MENU_RESET_ID, 3, "Reset");
        menu.add(4, MENU_SWITCH_MODE_KANJI_ID, 4, "Switch to kanji mode");
        menu.add(5, MENU_SWITCH_MODE_WORDS_ID, 5, "Switch to words mode");
        menu.add(6, MENU_SETTINGS_ID, 6, "Settings");
        menu.add(7, MENU_SEARCH_ID, 6, "Search");
        menu.add(8, MENU_ABOUT_ID, 7, "About");

        return result;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_EXPORT_ID:
                saveToXml();
                return true;
            case MENU_IMPORT_ID:
                loadFromXml();
                showQuestion();
                return true;
            case MENU_RESET_ID:
                reset();
                return true;
            case MENU_SWITCH_MODE_KANJI_ID:
            case MENU_SWITCH_MODE_WORDS_ID:
                switchMode();
                return true;
            case MENU_SETTINGS_ID:
                Intent settingsActivity = new Intent(getBaseContext(), Settings.class);
                startActivity(settingsActivity);
                loadSettings();
                return true;
            case MENU_SEARCH_ID:
                Intent searchActivity = new Intent(getBaseContext(), Search.class);
                startActivity(searchActivity);
                return true;
            case MENU_ABOUT_ID:
                Intent aboutActivity = new Intent(getBaseContext(), About.class);
                startActivity(aboutActivity);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final View.OnTouchListener screenTouchListener = new View.OnTouchListener() {
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


    /************************************** LOGIC ******************************************/

    private void initializeCardStore() {
        _cardstore = new CardStore(_db, _mode);
        _cardstore.clear();
    }

    private void loadSettings() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        // _mode       = Integer.parseInt(settings.getString("mode", "1"));
        _endless = settings.getBoolean("endless", false);
        _max_freq = Integer.parseInt(settings.getString("max_freq", "0"));
        _language = settings.getString("language", "de");
        Log.i(TAG, "Settings activated.");
    }

    private void reset() {
        _box = new CardBox(_cardstore, _mode, CardBox.ORDER_FREQUENCY, true, _max_freq, _endless);
        showQuestion();
    }

    /**
     * When the mode is changed,
     * we need to create a new card store
     * and reload the last box status from disk.
     */
    private void switchMode() {
        if (_mode == MODE_KANJI)
            _mode = MODE_WORDS;
        else
            _mode = MODE_KANJI;

        initializeCardStore();

        loadFromDisk();
        if (_box == null) {
            _box = new CardBox(_cardstore, _mode, CardBox.ORDER_FREQUENCY, true, _max_freq, _endless);
        }

        showQuestion();
    }

    private boolean showQuestion() {
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

    private boolean showAnswer() {
        _no_button.setVisibility(View.VISIBLE);
        _yes_button.setVisibility(View.VISIBLE);
        return showCard(_box.getNextCard(), true, true);
    }

    private void showHint() {
        _hint_field.setVisibility(View.VISIBLE);
        _hint_button.setVisibility(View.INVISIBLE);
    }

    private boolean showCard(String str, boolean show_japanese, boolean show_explanation) {
        Card card = _cardstore.get(str);

        if (card == null) {
            Log.i(TAG, "Card cannot be found.");
            return false;
        }

        if (View.INVISIBLE == _hint_field.getVisibility()) {
            if (null != card.getHint(_language)) {
                _hint_field.setText(card.getHint(_language));
                _hint_button.setVisibility(View.VISIBLE);
            }
        }

        String card_html = new CardView(card, _box, _language, show_japanese, show_explanation).toString();

        if ((show_japanese) && (show_explanation)) {
            // card_html.append("<div class=\"words\">");
            int i = 0;
            for (String word : card.getWords()) {
                _word[i].setText(word);
                _word[i].setVisibility(View.VISIBLE);

                i++;
                if (i >= 5)
                    break;
            }
            // card_html.append("</div>");
        }

        if (_card_webview == null) {
            Log.i(TAG, "Webview is null!");
            return false;
        }

        _card_webview.loadDataWithBaseURL(null, card_html, "text/html", HTTP.UTF_8, null);
        _card_webview.setBackgroundColor(0xff000000);
        return true;
    }


    /********************** Storage functions ******************************/

    private void saveToDisk() {
        try {
            DiskStorage storage = new DiskStorage();
            storage.save(this, _box, _mode);
        } catch (Exception e) {
            Log.v(TAG, "Error saving cardbox to disk!");
            Toast.makeText(KanjiKing.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadFromDisk() {
        try {
            DiskStorage storage = new DiskStorage();
            _box = storage.load(this, _mode);
        } catch (Exception e) {
            Log.v(TAG, "Error loading cardbox from disk!");
            Toast.makeText(KanjiKing.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveToXml() {
        try {
            XmlStorage storage = new XmlStorage();
            storage.save(this, _box, _mode);
            Toast.makeText(KanjiKing.this, "Exported to " + storage.getTargetFilename(_mode), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(KanjiKing.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadFromXml() {
        try {
            XmlStorage storage = new XmlStorage();
            _box = storage.load(this, _mode);
            Toast.makeText(KanjiKing.this, "Imported from " + storage.getTargetFilename(_mode), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(KanjiKing.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
