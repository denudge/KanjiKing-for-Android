package com.mlieber.KanjiKing.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.util.Log;

import com.mlieber.KanjiKing.*;
import com.mlieber.KanjiKing.CardBox.Card;
import com.mlieber.KanjiKing.CardBox.CardBox;
import com.mlieber.KanjiKing.CardBox.CardStore;
import com.mlieber.KanjiKing.Element.SearchResultEntry;
import com.mlieber.KanjiKing.Search.Criteria;

public class Search extends Activity
{
    private static final String TAG = "KanjiKing/Search";

    private LinearLayout _search_form;
    private LinearLayout _search_result_area;

    private EditText _search_phrase;
    private EditText _search_reading;
    private EditText _search_meaning;
    private SeekBar _search_radical;
    private SeekBar _search_strokes;
    private Button _search_button;
    private Button _search_edit_button;
    private TextView _search_result;
    private TextView _search_radical_preview;
    private TextView _search_strokes_preview;

    private CardBox _cardbox;
    private CardStore _cardstore;
    private String _language;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _cardbox = KanjiKing.getCardBox();
        _cardstore = KanjiKing.getCardStore();
        _language = KanjiKing.getLanguage();

        // use the search view here
        setContentView(R.layout.search);

        // Areas
        _search_form = (LinearLayout) findViewById(R.id.search_form);
        _search_result_area = (LinearLayout) findViewById(R.id.search_result_area);

        // Elements
        _search_phrase = (EditText) findViewById(R.id.search_word);
        _search_reading = (EditText) findViewById(R.id.search_reading);
        _search_meaning = (EditText) findViewById(R.id.search_meaning);
        _search_radical = (SeekBar) findViewById(R.id.search_radical);
        _search_radical_preview = (TextView) findViewById(R.id.search_radical_preview);
        _search_strokes = (SeekBar) findViewById(R.id.search_strokes);
        _search_strokes_preview = (TextView) findViewById(R.id.abcde123);
        _search_button = (Button)   findViewById(R.id.search_button);
        _search_result = (TextView) findViewById(R.id.search_result);
        _search_edit_button = (Button) findViewById(R.id.search_edit_button);

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

        // TODO: Implement value extraction
        View.OnClickListener radicalClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                v.setBackgroundColor(0xffffff00);
                int radicalId = v.getId() - 10000;
                Card radical = _cardstore.get(((int) radicalId + 3000) + "");
                _search_radical_preview.setText(radicalId + ": " + radical.getOnReading());
            }
        };

        // Create radical button grid - programmatically
        TableLayout radicalTable = (TableLayout) findViewById(R.id.search_radical_grid);
        for (int row = 0; row < 22; row++) {
            TableRow newRow = new TableRow(this);
            newRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            for (int c = 0; c < 10; c++) {
                int sum = (10 * row + c);
                if (sum >= 214) break;
                Button radicalButton = new Button(this);
                radicalButton.setText(_cardstore.get(((sum + 1) + 3000) + "").getOnReading());
                radicalButton.setBackgroundResource(R.drawable.radical);
                radicalButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 10.0f));
                newRow.addView(radicalButton);

                radicalButton.setFocusable(false);
                radicalButton.setId(10000 + sum + 1);
                radicalButton.setOnClickListener(radicalClickListener);
            }
            radicalTable.addView(newRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }

        // connect search button
        _search_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                _search_form.setVisibility(View.GONE);
                _search_result.setText("Searching...");
                _search_result_area.setVisibility(View.VISIBLE);

                Criteria criteria = new Criteria();
                criteria.setSearchPhrase((((TextView) _search_phrase).getText()).toString())
                        .setReading((((TextView) _search_reading).getText()).toString())
                        .setMeaning((((TextView) _search_meaning).getText()).toString())
                        .setRadical(_search_radical.getProgress())
                        .setStrokes(_search_strokes.getProgress());

                Card[] result = search(criteria);

                _search_result.setText(
                    formatSearchResult(result)
                );

            }
        });

        _search_edit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _search_result_area.setVisibility(View.GONE);
                _search_form.setVisibility(View.VISIBLE);
            }
        });

        _search_result_area.setVisibility(View.GONE);
        _search_form.setVisibility(View.VISIBLE);
    }

    private Card[] search(Criteria criteria) {
        return _cardstore.fetchByCriteria(criteria);
    }

    private String formatSearchResult(Card[] result) {
        StringBuilder sb = new StringBuilder();

        sb.append(result.length + " Search Result(s)");
        if (0 == result.length)
            return sb.toString();
        else sb.append(": \n----------------\n");

        // Display kanji infos for each kanji
        for (int i = 0; i < result.length; i++) {
            SearchResultEntry entry = new SearchResultEntry(result[i], _language, _cardbox);
            sb.append(entry.toString())
                .append("\n\n");
        }
        
        return sb.toString();
    }
}
