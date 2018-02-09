package de.bitnetic.KanjiKing.Search;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.bitnetic.KanjiKing.CardBox.Card;
import de.bitnetic.KanjiKing.Element.KanjiInfo;

/**
 * Created by nudge on 02.11.16.
 */
public class SearchResultItem extends LinearLayout {

    private TextView txtKanji;
    private LinearLayout rContainer;
    private TextView rFacts, rReading, rMeaning;

    public SearchResultItem(Context context, Card card) {
        super(context);
        this.setWeightSum(1.0f);

        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 2, 0, 2);
        this.setLayoutParams(params);
        this.setOrientation(LinearLayout.HORIZONTAL);

        //use a GradientDrawable with only one color set, to make it a solid color
        GradientDrawable border = new GradientDrawable();
        border.setColor(0x00FFFFFF); //white background, null opacity?
        border.setStroke(1, 0xFF444444); // red border with full opacity
        this.setBackgroundDrawable(border);
        this.setPadding(0, 0, 0, 1);

        this.addChildren(context);
        this.renderCard(card);
        this.setVisibility(View.VISIBLE);
    }

    private void addChildren(Context context) {
        this.txtKanji = new TextView(context);
        txtKanji.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT, 0.3f));
        txtKanji.setVisibility(View.VISIBLE);
        txtKanji.setTextSize(32);
        txtKanji.setBackgroundColor(Color.BLACK);
        txtKanji.setGravity(Gravity.CENTER);
        this.addView(txtKanji);

        rContainer = new LinearLayout(context);
        rContainer.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.7f));
        rContainer.setOrientation(LinearLayout.VERTICAL);
        rContainer.setBackgroundColor(Color.BLACK);
        rContainer.setVisibility(View.VISIBLE);

        rFacts = new TextView(context);
        rFacts.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        rFacts.setVisibility(View.VISIBLE);
        rContainer.addView(rFacts);

        rReading = new TextView(context);
        rReading.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        rReading.setVisibility(View.VISIBLE);
        rContainer.addView(rReading);

        rMeaning = new TextView(context);
        rMeaning.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        rMeaning.setVisibility(View.VISIBLE);
        rContainer.addView(rMeaning);

        this.addView(rContainer);
    }

    public void renderCard(Card card) {
        txtKanji.setText(card.getJapanese());
        rFacts.setText(new KanjiInfo(card).toString());
        rReading.setText(card.getOnReading()+" "+card.getKunReading());
        rMeaning.setText(card.getMeaning("de"));
    }
}
