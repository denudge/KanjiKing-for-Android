package com.mlieber.KanjiKing.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.mlieber.KanjiKing.Element.DrawListener;
import com.mlieber.KanjiKing.Element.DrawPanel;
import com.mlieber.KanjiKing.R;
import com.mlieber.KanjiKing.Search.DrawResult;


import java.util.Vector;

public class Draw extends Activity
{
    private static final String TAG = "KanjiKing/Draw";

    private TextView kanji[];
    private DrawPanel draw_area;
    private Button clear_button;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // use the search view here
        setContentView(R.layout.draw);

        kanji = new TextView[]{
                (TextView) findViewById(R.id.drawResult1),
                (TextView) findViewById(R.id.drawResult2),
                (TextView) findViewById(R.id.drawResult3),
                (TextView) findViewById(R.id.drawResult4),
                (TextView) findViewById(R.id.drawResult5),
                (TextView) findViewById(R.id.drawResult6),
                (TextView) findViewById(R.id.drawResult7)
        };

        draw_area = (DrawPanel) findViewById(R.id.draw_area);
        clear_button = (Button) findViewById(R.id.btn_clear);

        View.OnClickListener finisher = new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        };

        for (int i = 0; i < 7; i++) {
            kanji[i].setOnClickListener(finisher);
        }

        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                draw_area.clear();
                clearKanji();
            }
        });

        draw_area.setDrawListener(new DrawListener() {
            @Override
            public void onStrokeStart(Vector xstrokes, Vector ystrokes) { }

            @Override
            public void onStrokeFinish(Vector xstrokes, Vector ystrokes) {
                DrawResult drawResult = new DrawResult(xstrokes, ystrokes);
                setKanji(drawResult.getTopRated());
            }
        });
    }

    private void setKanji(String items[]) {
        clearKanji();
        for (int i = 0; i < kanji.length && i < items.length; i++) {
            kanji[i].setText(items[i]);
        }
    }

    private void clearKanji() {
        for (int i = 0; i < kanji.length; i++) {
            kanji[i].setText("");
        }
    }

}
