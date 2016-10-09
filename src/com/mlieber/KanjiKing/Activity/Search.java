package com.mlieber.KanjiKing.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.util.Log;
import com.mlieber.KanjiKing.*;
import com.mlieber.KanjiKing.CardBox.Card;
import com.mlieber.KanjiKing.CardBox.CardBox;
import com.mlieber.KanjiKing.CardBox.CardFrequencyComparator;
import com.mlieber.KanjiKing.CardBox.CardStore;

import java.util.Vector;
import java.util.Comparator;

public class Search extends Activity
{
    private static final String TAG = "KanjiKing/Search";
    private EditText _search_phrase;
    private EditText _search_reading;
    private EditText _search_meaning;
    private SeekBar _search_radical;
    private SeekBar _search_strokes;
    private Button _search_button;
    private TextView _search_result;
    private TextView _search_radical_preview;
    private TextView _search_strokes_preview;
    private CardBox _cardbox;
    private CardStore _cardstore;
    private String _language;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _cardbox = KanjiKing.getCardBox();
        _cardstore = KanjiKing.getCardStore();
        _language = KanjiKing.getLanguage();

        // use the search view here
        setContentView(R.layout.search);

        _search_phrase = (EditText) findViewById(R.id.search_word);
        _search_reading = (EditText) findViewById(R.id.search_reading);
        _search_meaning = (EditText) findViewById(R.id.search_meaning);
        _search_radical = (SeekBar) findViewById(R.id.search_radical);
        _search_radical_preview = (TextView) findViewById(R.id.search_radical_preview);
        _search_strokes = (SeekBar) findViewById(R.id.search_strokes);
        _search_strokes_preview = (TextView) findViewById(R.id.abcde123);
        _search_button = (Button)   findViewById(R.id.search_button);
        _search_result = (TextView) findViewById(R.id.search_result);
  
        // animate strokes slider
        _search_strokes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar b, int progress, boolean fromUser) {
                    if (!fromUser) return;
                    if (progress == 0)
                        _search_strokes_preview.setText("");
                    else
                        _search_strokes_preview.setText(progress + "");
            }

            public void onStartTrackingTouch(SeekBar b) { }
            public void onStopTrackingTouch(SeekBar b) { }
        });
                
        // animate radicals slider
        _search_radical.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar b, int progress, boolean fromUser) {
                if (!fromUser) return;
                if (progress == 0) {
                    _search_radical_preview.setText("");
                    return;
                }

                Card radical = _cardstore.get(((int) progress + 3000) + "");
                if (null == radical)
                    _search_radical_preview.setText(progress + ": !");
                else
                    _search_radical_preview.setText(progress + ": " + radical.getOnReading());
            }

            public void onStartTrackingTouch(SeekBar b) { }
            public void onStopTrackingTouch(SeekBar b) { }
        });

        // connect search button
        _search_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String phrase = (((TextView) _search_phrase).getText()).toString();
                String reading = (((TextView) _search_reading).getText()).toString();
                String meaning = (((TextView) _search_meaning).getText()).toString();
                _search_result.setText("Searching...");
                _search_result.setText(
                    formatSearchResult(
                        search(_cardstore, phrase, reading, meaning, _search_radical.getProgress(), _search_strokes.getProgress())
                    )
                );
            }
        });
    }


    private Card[] search(CardStore base, String search, String reading, String meaning, int radical, int strokes)
    {
        boolean init = false;
        String[] obj = null;
        Card card = null;

        if (null == base)
            return null;
        
        Vector<Card> rv = new Vector<Card>();

        // phrase search
        if ((null != search) && (!search.equals(""))) {
            for (int i=0; i < search.length(); i++) {
                String ch = search.charAt(i) + "";
                card = base.search(ch);

                if (null != card)
                    rv.add(card);
            }
            init = true;
        }

        // Reading search
        if ((null != reading) && (!reading.equals(""))) {
            if (!init) {
                Log.v(TAG, "Search by reading " + reading);
                Card[] cards = base.fetchByReading(reading);
                for (int i=0; i < cards.length; i++) {
                    rv.add(cards[i]);
                }
               init = true;
            } else {
                Log.v(TAG, "Filtering search by reading " + reading);
                // radical filter
                for (int i=0; i < rv.size(); i++) {
                    card = rv.get(i);
                    if (! card.hasReading(reading, false))
                        rv.removeElement(card);
                        i--;
                }
            }
        }
        
        // Meaning search
        if ((null != meaning) && (!meaning.equals(""))) {
            if (!init) {
                Log.v(TAG, "Search by meaning " + meaning);
                if (null == obj)
                    obj = base.getKeysByType(Card.TYPE_KANJI);

                for (int i=0; i < obj.length; i++) {
                    card = (Card) base.fetchById(Integer.parseInt(obj[i]));
                    if ((card != null) && (card.hasMeaning(meaning, KanjiKing.getLanguage(), false)))
                        rv.add(card);
                }
               init = true;
            } else {
                Log.v(TAG, "Filtering search by meaning " + meaning);
                // radical filter
                for (int i=0; i < rv.size(); i++) {
                    card = rv.get(i);
                    if (! card.hasMeaning(meaning, KanjiKing.getLanguage(), false))
                        rv.removeElement(card);
                        i--;
                }
            }
        }
        
        // radical search
        if (radical > 0) {
            if (!init) {
                Log.v(TAG, "Search by radical " + radical);
                Card[] cards = base.fetchByRadical(radical);
                for (int i=0; i < cards.length; i++) {
                    rv.add(cards[i]);
                }
               init = true;
            } else {
                Log.v(TAG, "Filtering search by radical " + radical);
                // radical filter
                for (int i=0; i < rv.size(); i++) {
                    card = rv.get(i);
                    if (radical != card.getRadical()) {
                        rv.removeElement(card);
                        i--;
                    }
                }
            }
        }

        // strokes search
        if (strokes > 0) {
            if (!init) {
                Card[] cards = base.fetchByStrokes(strokes);
                for (int i=0; i < cards.length; i++) {
                    rv.add(cards[i]);
                }
                init = true;
            } else {
                // strokes filter
                for (int i=0; i < rv.size(); i++) {
                    card = rv.get(i);
                    if (strokes != card.getStrokesCount()) {
                        rv.removeElement(card);
                        i--;
                    }
                }
            }
        }
        
        if (!init)
            return new Card[0];

        Comparator<Card> _cc = new CardFrequencyComparator();
        java.util.Collections.sort(rv, _cc);

        Card[] ra = new Card[rv.size()];
        rv.toArray(ra);

        return ra;
    }

    private String formatSearchResult(Card[] result) {
        StringBuilder sb = new StringBuilder();

        sb.append(result.length + " Search Result(s)");
        if (0 == result.length)
            return sb.toString();
        else sb.append(": \n----------------\n");

        // Display kanji infos for each kanji
        for (int i = 0; i < result.length; i++) {
            sb.append(result[i].getJapanese())
                .append(" ")
                .append(result[i].getOnReading())
                .append(" ")
                .append(result[i].getKunReading())
                .append("\n")
                .append(result[i].getInfo())
                .append("\n")
                .append(result[i].getMeaning(_language))
                .append("\n");

            int nbox = _cardbox.getBoxListNumber(result[i].getId() + "");
            sb.append("Lern-Status: ");

            switch (nbox) {
                case 0:
                    sb.append("Noch nie gesehen\n");
                    break;
                case 99: 
                    sb.append("Fertig gelernt\n");
                    break;
                default:
                    sb.append("Beim Lernen in Box " + nbox + "\n");
                    break;
            }

            sb.append("\n\n");
        }
        
        return sb.toString();
    }

}

