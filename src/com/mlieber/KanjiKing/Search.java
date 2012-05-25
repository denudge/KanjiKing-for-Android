package com.mlieber.KanjiKing;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.util.Log;
import java.util.Vector;

public class Search extends Activity
{
    private static final String TAG = "KanjiKing/Search";
    private EditText _search_word;
    private SeekBar _search_radical;
    private SeekBar _search_strokes;
    private Button _search_button;
    private TextView _search_result;
    private TextView _search_radical_preview;
    private TextView _search_strokes_preview;
    private CardBox _cardbox;
    private CardStore _cardstore;
    private CardStore _radicals;
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

        _search_word = (EditText) findViewById(R.id.search_word);
        _search_radical = (SeekBar) findViewById(R.id.search_radical);
        _search_radical_preview = (TextView) findViewById(R.id.search_radical_preview);
        _search_strokes = (SeekBar) findViewById(R.id.search_strokes);
        _search_strokes_preview = (TextView) findViewById(R.id.abcde123);
        _search_button = (Button)   findViewById(R.id.search_button);
        _search_result = (TextView) findViewById(R.id.search_result);
  
        //load radicals
        _radicals = new CardStore();
        _radicals.clear();
        _radicals.loadFromXMLFile(getResources().getXml(R.xml.radicals));

        // animate strokes slider
        _search_strokes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar b, int progress, boolean fromUser) {
                    if (!fromUser) return;
                    Log.i(TAG, "Moving strokes bar to " + progress);
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

                Card radical = _radicals.get(progress + "");
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
                String input = (((TextView) _search_word).getText()).toString();
                _search_result.setText(formatSearchResult(
                        search(_cardstore, input, _search_radical.getProgress(), _search_strokes.getProgress())
                ));
            }
        });
    }


    private Card[] search(CardStore base, String search, int radical, int strokes)
    {
        boolean init = false;
        Object[] obj = null;
        Card card = null;

        if (null == base)
            return null;
        
        if (((search == null) || (search.equals(""))) && (radical < 1) && (strokes < 1))
            return new Card[0];

        Vector<Card> rv = new Vector<Card>();

        // phrase search
        if ((null != search) && (!search.equals(""))) {
            init = true;
            for (int i=0; i < search.length(); i++) {
                String ch = search.charAt(i) + "";
                card = base.get(ch);

                if (null != card)
                    rv.add(card);
            }
        }

        // radical search
        if (radical > 0) {
            if (!init) {
                Log.i(TAG, "Search by radical " + radical);
               init = true;
                if (null == obj)
                    obj = base.getCards();

                for (int i=0; i < obj.length; i++) {
                    card = (Card) base.get((String) obj[i]);
                    if (radical == card.getRadical())
                        rv.add(card);
                }
            } else {
                Log.i(TAG, "Filtering search by radical " + radical);
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
               init = true;
                if (null == obj)
                    obj = base.getCards();

                for (int i=0; i < obj.length; i++) {
                    card = (Card) base.get((String) obj[i]);
                    if (strokes == card.getStrokesCount())
                        rv.add(card);
                }
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

        Card[] ra = new Card[rv.size()];
        return rv.toArray(ra);
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

            int nbox = _cardbox.getBoxListNumber(result[i].getJapanese());
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

