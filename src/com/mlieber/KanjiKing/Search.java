package com.mlieber.KanjiKing;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.util.Log;
import java.util.Vector;

public class Search extends Activity
{
    private EditText _search_input;
    private Button _search_button;
    private TextView _search_result;
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

        _search_input = (EditText) findViewById(R.id.search_input);
        _search_button = (Button)   findViewById(R.id.search_button);
        _search_result = (TextView) findViewById(R.id.search_result);
   
        _search_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String input = (((TextView) _search_input).getText()).toString();
                _search_result.setText(formatSearchResult(search(input)));
            }
        });
    }


    private Card[] search(String search)
    {
        if (null == _cardstore)
            return null;
        
        Vector<Card> rv = new Vector<Card>();

        // It is a kanji-specific search
        for (int i=0; i < search.length(); i++) {
            String ch = search.charAt(i) + "";
            Card card = _cardstore.get(ch);

            if (null != card)
                rv.add(card);
        }

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

