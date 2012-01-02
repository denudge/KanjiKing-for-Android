package com.mlieber.KanjiKing;

import android.app.Activity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.util.Log;
import android.text.TextUtils;
import org.apache.http.protocol.HTTP;
import android.webkit.WebView;
import android.widget.Button;
import android.view.View;
import android.view.MotionEvent;

public class KanjiKing extends Activity
{
    private CardStore _cardstore;
    private static final String TAG = "KanjiKing";
    private CardBox _box;
  
    // The card view
    private Button no_button_, yes_button_;
    private WebView _card_webview;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _cardstore = new CardStore();
        _cardstore.loadFromXMLFile(getResources().getXml(R.xml.kanji));
        Log.i(TAG, _cardstore.size() + " cards loaded.");
    
        // Initialize layout
        _card_webview = (WebView)findViewById(R.id.card_webview);
        yes_button_ = (Button)findViewById(R.id.yes_button);
        no_button_ = (Button)findViewById(R.id.no_button);

        _box = new CardBox(CardBox.ORDER_FREQUENCY);

        // Start the display
        // setContentView(R.layout.main);
        showCard(_box.getNextCard(), true, true);
        setContentView(R.layout.card);
    }

  private final View.OnTouchListener screenTouchListener
      = new View.OnTouchListener() {
    public boolean onTouch(View v, MotionEvent event) {
      if (event.getAction() == MotionEvent.ACTION_UP) {
        /*if (study_mode_) {
          int width = card_layout_.getWidth();
          move_backwards_ = event.getX() < (width / 2.0);
          showNextCard();
        } else if (no_button_.getVisibility() == View.INVISIBLE) {
          showCard(current_card_.sides(), true);
          no_button_.setVisibility(View.VISIBLE);
          yes_button_.setVisibility(View.VISIBLE);
        }
      */
      }
      return true;
    }
  };

  private void findViews() {
    _card_webview = (WebView)findViewById(R.id.card_webview);
  }

  private boolean showCard(String str, boolean show_japanese, boolean show_explanation) {
    StringBuilder card_html = new StringBuilder();

    Card card = _cardstore.get(str);

    Log.i(TAG, str + " shall be displayed.");

    if (card == null)
    {
        Log.i(TAG, "Card cannot be found.");
        return false;
    }

    card_html.append("<html>")
        .append("<head>");
/*
    if (_cardstore.style() != null) {
      card_html.append("<style type=\"text/css\">")
          .append(TextUtils.htmlEncode(_cardstore.style()))
          .append("</style>");
    }
*/
    card_html.append("</head>")
        .append("<body>");

    if (show_japanese)
    {
        if (card.getJapanese().length() > 0)
            card_html.append("<div class=\"japanese\">")
                .append(TextUtils.htmlEncode(card.getJapanese()))
                .append("</div>");
    }

    if (show_explanation)
    {
        if (card.getOnReading().length() > 0)
            card_html.append("<div class=\"reading_on\">")
                .append(TextUtils.htmlEncode(card.getOnReading()))
                .append("</div>");

        if (card.getKunReading().length() > 0)
            card_html.append("<div class=\"reading_kun\">")
                .append(TextUtils.htmlEncode(card.getKunReading()))
                .append("</div>");

        if (card.getMeaning("de").length() > 0)
            card_html.append("<div class=\"meaning de\">")
                .append(TextUtils.htmlEncode(card.getMeaning("de")))
                .append("</div>");

        if (card.getMeaning("en").length() > 0)
            card_html.append("<div class=\"meaning en\">")
                .append(TextUtils.htmlEncode(card.getMeaning("en")))
                .append("</div>");
    }

    card_html.append("</body>");

    if (_card_webview == null)
    {
        Log.i(TAG, "Webview is null!");
    }
    
    // _card_webview.loadDataWithBaseURL(null, card_html.toString(), "text/html", HTTP.UTF_8, null);
    _card_webview.loadData(card_html.toString(), "text/html", HTTP.UTF_8);
    _card_webview.setBackgroundColor(0xff880000);
    _card_webview.setVisibility(View.VISIBLE);
    return true;
  }


}

