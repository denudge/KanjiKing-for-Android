package com.mlieber.KanjiKing;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.util.Log;

public class Search extends Activity
{
    
    private Button _search_button;
    private CardBox _cardbox;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _cardbox = KanjiKing.getCardBox();

        // use the search view here
        setContentView(R.layout.search);

        _search_button = (Button)findViewById(R.id.search_button);
    }

}

