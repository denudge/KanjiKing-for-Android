package com.mlieber.KanjiKing.Activity;

import android.app.Activity;
import android.content.Intent;
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

    private Button[] kanji;
    private DrawPanel draw_area;
    private Button clear_button;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // use the search view here
        setContentView(R.layout.draw);

        kanji = new Button[] {
                (Button) findViewById(R.id.drawResult1),
                (Button) findViewById(R.id.drawResult2),
                (Button) findViewById(R.id.drawResult3),
                (Button) findViewById(R.id.drawResult4),
                (Button) findViewById(R.id.drawResult5),
                (Button) findViewById(R.id.drawResult6),
                (Button) findViewById(R.id.drawResult7)
        };

        draw_area = (DrawPanel) findViewById(R.id.draw_area);
        clear_button = (Button) findViewById(R.id.btn_clear);

        View.OnClickListener finisher = new View.OnClickListener() {
            public void onClick(View arg0) {
                if (arg0 instanceof TextView) {
                    TextView tv = (TextView) arg0;

                    // only act on filled buttons
                    if ((tv.getText() == null) || ("".equals(tv.getText()))) {
                        return;
                    }

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", tv.getText());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        };

        for (int i = 0; i < kanji.length; i++) {
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
