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
import android.widget.ProgressBar;
import android.widget.TextView;

import android.view.View;
import android.view.MotionEvent;

public class KanjiKing extends Activity
{
    private CardStore _cardstore;
    private static final String TAG = "KanjiKing";
    private CardBox _box = null;
  
    // The card view
    private Button _no_button, _yes_button;
    private ProgressBar _overall_score;
    private TextView _kanji_number;
    private WebView _card_webview;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _cardstore = new CardStore();
        _cardstore.loadFromXMLFile(getResources().getXml(R.xml.kanji));
  
        // Construct a box if not already done
        if (_box == null)
            _box = new CardBox(CardBox.ORDER_FREQUENCY);

        // we start with the card view
        setContentView(R.layout.card);

        // Initialize layout
        _card_webview = (WebView)findViewById(R.id.card_webview);
        _yes_button = (Button)findViewById(R.id.yes_button);
        _no_button = (Button)findViewById(R.id.no_button);
        _overall_score = (ProgressBar)findViewById(R.id.overall_score);
        _kanji_number = (TextView)findViewById(R.id.kanji_number);

        _card_webview.setOnTouchListener(screenTouchListener);

        _overall_score.setVisibility(View.INVISIBLE);
        _kanji_number.setVisibility(View.INVISIBLE);

        // and display the next card on startup
        showQuestion();
    
        _no_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _box.answer(false);
                showQuestion();
            }
        });

        _yes_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _box.answer(true);
                showQuestion();
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

    private boolean showQuestion()
    {
        _no_button.setVisibility(View.INVISIBLE);
        _yes_button.setVisibility(View.INVISIBLE);
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

  private boolean showCard(String str, boolean show_japanese, boolean show_explanation) {
    StringBuilder card_html = new StringBuilder();

    Card card = _cardstore.get(str);

    if (card == null)
    {
        Log.i(TAG, "Card cannot be found.");
        return false;
    }

    card_html.append("<html>")
        .append("<head><title>Lalala</title>");

    if (_cardstore.getStyle() != null) {
      card_html.append("<style type=\"text/css\">")
          .append(TextUtils.htmlEncode(_cardstore.getStyle()))
          .append("</style>");
    }

    card_html.append("</head>")
        .append("<body>");

    card_html.append("<div class=\"status\">")
        .append(TextUtils.htmlEncode(_box.getStatus()))
        .append("</div><br>");

    if (show_japanese)
    {
        card_html.append("<div class=\"info\">R: ")
            .append(card.getRadical())
            .append(" / F: ")
            .append(card.getFrequency())
            .append(" / H: ")
            .append(card.getHadamitzkyNumber())
            .append("</div>");

        if (card.getJapanese() != null)
            card_html.append("<div class=\"japanese\">")
                .append(TextUtils.htmlEncode(card.getJapanese()))
                .append("</div>");
    } else {
        card_html.append("<div class=\"info\">&nbsp;</div>")
            .append("<div class=\"japanese\">&nbsp;</div>");
    }

    if (show_explanation)
    {
        if (card.getOnReading() != null)
            card_html.append("<div class=\"reading_on\">")
                .append(TextUtils.htmlEncode(card.getOnReading()))
                .append("</div>");

        if (card.getKunReading() != null)
            card_html.append("<div class=\"reading_kun\">")
                .append(TextUtils.htmlEncode(card.getKunReading()))
                .append("</div>");

        if (card.getMeaning("de") != null)
            card_html.append("<div class=\"meaning de\">")
                .append(TextUtils.htmlEncode(card.getMeaning("de")))
                .append("</div>");


        if (card.getMeaning("en") != null)
            card_html.append("<div class=\"meaning en\">")
                .append(TextUtils.htmlEncode(card.getMeaning("en")))
                .append("</div>");
    }

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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        try {
            savedInstanceState.putSerializable("Box", _box);
        } catch (Exception e) {
            Log.v(TAG, "Error saving instance state: " + e.getMessage());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            _box = (CardBox)savedInstanceState.getSerializable("Box");
        } catch (Exception e) {
            Log.v(TAG, "Error restoring instance state: " + e.getMessage());
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        // super.onConfigurationChanged(newConfig);
 
        // Set a new layout - framework will automatically choose portrait or landscape as needed
        // Reconnect all listeners for controls in the layout
    }

}

