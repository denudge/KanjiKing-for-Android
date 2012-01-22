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

    // sub objects
    private CardStore _cardstore    = null;
    private CardBox _box            = null;

    // Settings
    private static int _mode               = MODE_KANJI;
    private static boolean _endless        = true;
    private static int _max_freq           = 0;

    // The card view
    private Button _no_button, _yes_button, _hint_button;
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

    // @Override
    public void onPause(Bundle savedInstanceState)
    {
        saveToDisk();
        // super.onPause(savedInstanceState);
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
    
        if (_mode == MODE_KANJI)
        {
            _cardstore.loadFromXMLFile(getResources().getXml(R.xml.kanji1));
            _cardstore.loadFromXMLFile(getResources().getXml(R.xml.kanji2));
            _cardstore.loadFromXMLFile(getResources().getXml(R.xml.kanji3));
            _cardstore.loadFromXMLFile(getResources().getXml(R.xml.kanji4));
            _cardstore.loadFromXMLFile(getResources().getXml(R.xml.kanji5));
        }
        else
        {
            _cardstore.loadFromXMLFile(getResources().getXml(R.xml.vokabeln1));
            _cardstore.loadFromXMLFile(getResources().getXml(R.xml.vokabeln2));
            _cardstore.loadFromXMLFile(getResources().getXml(R.xml.vokabeln3));
            _cardstore.loadFromXMLFile(getResources().getXml(R.xml.vokabeln4));
            _cardstore.loadFromXMLFile(getResources().getXml(R.xml.vokabeln5));
        }

    }

    private void loadSettings() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        _mode       = Integer.parseInt(settings.getString("mode", "1"));
        _endless    = settings.getBoolean("endless", true);
        _max_freq   = Integer.parseInt(settings.getString("max_freq", "0"));
    }

    @Override
    protected void onResume() {
    	super.onResume();
        loadSettings();
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

        // Save changes
        editor.commit();       
    }
*/

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Locale.setDefault(Locale.JAPAN);

        // load settings
        loadSettings();

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
    }

    private final View.OnTouchListener screenTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (_no_button.getVisibility() == View.INVISIBLE) {
                    showAnswer();
                    return true;
                }
            }
            return true;
        }
    };

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    boolean result = super.onCreateOptionsMenu(menu);
    menu.add(1, MENU_EXPORT_ID, 1, "Export");
    menu.add(2, MENU_IMPORT_ID, 2, "Import");
    menu.add(3, MENU_RESET_ID, 3, "Reset");
    menu.add(4, MENU_SWITCH_MODE_KANJI_ID, 4, "Switch to kanji mode");
    menu.add(5, MENU_SWITCH_MODE_WORDS_ID, 5, "Switch to words mode");
    menu.add(6, MENU_SETTINGS_ID, 6, "Settings");

/*
    menu.add(3, PRACTICE_MODE_ID, 2, "Practice mode");
    SubMenu quiz_size_menu = menu.addSubMenu(3, QUIZ_SIZE_MENU, 3, "Quiz mode");
    quiz_size_menu.add(4, QUIZ_20_ID, 1, "20 questions");
    quiz_size_menu.add(5, QUIZ_50_ID, 2, "50 questions");
    quiz_size_menu.add(6, QUIZ_100_ID, 3, "100 questions");
    menu.add(7, CHOOSE_CARDS_ID, 4, "Choose cards");
*/
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
/*
    menu.setGroupVisible(1, !study_mode_);
    menu.setGroupVisible(2, study_mode_ || quiz_mode_);
    menu.setGroupVisible(3, !quiz_mode_);
*/
    return true;
  }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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


/*            case MENU_EXPORT_BIN:
                saveToExternalBin();
                return true;
            case MENU_IMPORT_BIN:
                loadFromExternalBin();
                showQuestion();
                return true;
*/
        }
/*
      Intent i = new Intent(this, ConfigureDecks.class);
      ConfigureDecks.Param p = new ConfigureDecks.Param();
      p.selected_subsets_list = new ArrayList<BitSet>(deck_sub_selections_);
      p.deck_names = new ArrayList<String>();
      p.deck_sizes = new ArrayList<Integer>();
      for (Deck deck : collection_.decks()) {
        p.deck_names.add(deck.name());
        p.deck_sizes.add(deck.cards().size());
      }
      p.cards_per_subset = CARDS_PER_SUBSET;
      ConfigureDecks.PARAM.put(i, p);
      startActivityForResult(i, CONFIGURE_DECKS);
      return true;
    case REVIEW_MODE_ID:
      startReviewMode();
      return true;
    case PRACTICE_MODE_ID:
      study_mode_ = false;
      quiz_mode_ = false;
      current_deck_.startPracticeMode();
      showNextCard();
      return true;
    case QUIZ_20_ID:
      quiz_mode_ = true;
      study_mode_ = false;
      current_deck_.startQuizMode(20);
      showNextCard();
      return true;
    case QUIZ_50_ID:
      quiz_mode_ = true;
      study_mode_ = false;
      current_deck_.startQuizMode(50);
      showNextCard();
      return true;
    case QUIZ_100_ID:
      quiz_mode_ = true;
      study_mode_ = false;
      current_deck_.startQuizMode(100);
      showNextCard();
      return true;
    }
*/
        return super.onOptionsItemSelected(item);
    }

    private boolean showQuestion()
    {
        _no_button.setVisibility(View.INVISIBLE);
        _yes_button.setVisibility(View.INVISIBLE);
        _hint_field.setVisibility(View.INVISIBLE);
        _hint_button.setVisibility(View.INVISIBLE);

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

  private boolean showCard(String str, boolean show_japanese, boolean show_explanation) {
    StringBuilder card_html = new StringBuilder();

    Card card = _cardstore.get(str);

    if (card == null)
    {
        Log.i(TAG, "Card cannot be found.");
        return false;
    }

    _hint_field.setText(card.getHint());
    if ((!show_japanese)&&(card.getHint() != null))
        _hint_button.setVisibility(View.VISIBLE);

    card_html.append("<html>")
        .append("<head>")
        .append("<title>KanjiKing</title>")
        .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");

    if (card.getStyle() != null) {
      card_html.append("<style type=\"text/css\">")
          .append(TextUtils.htmlEncode(card.getStyle()))
          .append("</style>");
    }

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

    if (show_japanese)
    {
        card_html
            .append("<div class=\"info\">")
            .append("F: ")
            .append(card.getFrequency())
            .append(" / G: ")
            .append(card.getGrade())
            .append(" / S: ")
            .append(card.getStrokesCount())
            .append(" / R: ")
            .append(card.getRadical())
            .append(" / H: ")
            .append(card.getHadamitzkyNumber())
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

    if (show_explanation)
    {
        if (card.getMeaning("de") != null)
            card_html.append("<div class=\"meaning de\">")
                .append(TextUtils.htmlEncode(card.getMeaning("de")))
                .append("</div>");

        if (card.getMeaning("en") != null)
            card_html.append("<div class=\"meaning en\">")
                .append(TextUtils.htmlEncode(card.getMeaning("en")))
                .append("</div>");
    }
    else
        card_html.append("<div class=\"meaning de\">&nbsp;</div>");

    card_html.append("</body>");

    if (_card_webview == null)
    {
        Log.i(TAG, "Webview is null!");
        return false;
    }
   
    _card_webview.loadDataWithBaseURL(null, card_html.toString(), "text/html", HTTP.UTF_8, null);
    // _card_webview.loadData(card_html.toString(), "text/html", HTTP.UTF_8);
    _card_webview.setBackgroundColor(0xff000000);
    // _card_webview.setVisibility(View.VISIBLE);
    return true;
  }

/*
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        try {
            savedInstanceState.putInt("mode", _mode);
        } catch (Exception e) {
            Log.v(TAG, "Error saving instance state: " + e.getMessage());
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            _mode = savedInstanceState.getInt("mode", MODE_KANJI);
        } catch (Exception e) {
            Log.v(TAG, "Error restoring instance state: " + e.getMessage());
        }
    }
*/

/*
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
 
        // Set a new layout - framework will automatically choose portrait or landscape as needed
        // Reconnect all listeners for controls in the layout
    }
*/


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

/*
    public void loadFromExternalBin()
    {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        
        try {
            File file = new File(extStorageDirectory, "/kanjibox.dump");
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                _box = (CardBox) ois.readObject();
                Toast.makeText(KanjiKing.this, "Imported to kanjibox.dump", Toast.LENGTH_LONG).show();
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "DESERIALIZATION FAILED (CLASS NOT FOUND):" + e.getMessage(), e);
                return;
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(KanjiKing.this, e.toString(), Toast.LENGTH_LONG).show();
        } catch (StreamCorruptedException e) {
            Log.e(TAG, "DESERIALIZATION FAILED (CORRUPT):" + e.getMessage(), e);
            return;
        } catch (IOException e) {
            Log.e(TAG, "DESERIALIZATION FAILED (IO EXCEPTION):"+e.getMessage(), e);
            return;
        }
    }

    public void saveToExternalBin()
    {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File file = new File(extStorageDirectory, "/kanjibox.dump");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(_box);
            byte[] buf = bos.toByteArray();
            fos.write(buf);
            fos.flush();
            fos.close(); 
            Toast.makeText(KanjiKing.this, "Exported to kanjibox.dump", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            Toast.makeText(KanjiKing.this, e.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // e.printStackTrace();
            Log.e(TAG, "DESERIALIZATION FAILED (IO EXCEPTION):"+e.getMessage(), e);
            return;
        }
    }
*/
    
}
