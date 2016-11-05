package com.mlieber.KanjiKing.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.mlieber.KanjiKing.Element.DrawPanel;
import com.mlieber.KanjiKing.R;

public class Draw extends Activity
{
    private static final String TAG = "KanjiKing/Draw";

    private TextView kanji1;
    private TextView kanji2;
    private TextView kanji3;
    private TextView kanji4;
    private DrawPanel draw_area;
    private Button clear_button;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // use the search view here
        setContentView(R.layout.draw);

        kanji1 = (TextView) findViewById(R.id.drawResult1);
        kanji2 = (TextView) findViewById(R.id.drawResult2);
        kanji3 = (TextView) findViewById(R.id.drawResult3);
        kanji4 = (TextView) findViewById(R.id.drawResult4);
        draw_area = (DrawPanel) findViewById(R.id.draw_area);
        clear_button = (Button) findViewById(R.id.btn_clear);

        View.OnClickListener finisher = new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        };

        kanji1.setOnClickListener(finisher);
        kanji2.setOnClickListener(finisher);
        kanji3.setOnClickListener(finisher);
        kanji4.setOnClickListener(finisher);

        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                draw_area.clear();
            }
        });

    }
}
