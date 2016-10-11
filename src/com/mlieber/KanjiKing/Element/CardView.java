package com.mlieber.KanjiKing.Element;

import android.util.Log;
import android.text.TextUtils;

import com.mlieber.KanjiKing.CardBox.Card;
import com.mlieber.KanjiKing.CardBox.CardBox;

/**
 * Created by nudge on 11.10.16.
 */
public class CardView
{
    private String _html;

    private static final String STYLE = "body {text-align: center; color: white; }\n"
        + "div.info, div.status { color: #888888; font-size:90%; }\n"
        + "div.japanese {font-size: 500%; color: #99ff99; }\n"
        + "div.reading_on {font-size: 230%; color: #9090ff; font-variant: small-caps; }\n"
        + "div.reading_kun {font-size: 200%; color: #9090ff; }\n"
        + "div.meaning {color: #e0e0e0;}\n";


    public CardView(Card card, CardBox box, String language, boolean show_japanese, boolean show_explanation) {
        _html = formatView(card, box, language, show_japanese, show_explanation);
    }

    public String toString() {
        return _html;
    }

    private String formatView(Card card, CardBox box, String language, boolean show_japanese, boolean show_explanation) {
        StringBuilder sb = new StringBuilder();

        sb.append("<html>")
                .append("<head>")
                .append("<title>KanjiKing</title>")
                .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");

        sb.append("<style type=\"text/css\">")
                .append(STYLE)
                .append("</style>");

        sb.append("</head>")
                .append("<body>");

        sb.append("<div class=\"status\">")
                .append(TextUtils.htmlEncode(box.getStatus()))
                .append("</div><br>");

        // ableiten, ob wir im Wort- oder Kanji-Modus sind
        boolean show_reading = false;
        if (card.getType() == Card.TYPE_KANJI)
            show_reading = show_explanation;
        else
            show_reading = (show_japanese && show_explanation);

        if (show_japanese) {
            sb.append("<div class=\"info\">")
                    .append(TextUtils.htmlEncode(new KanjiInfo(card).toString()))
                    .append("</div><br>");

            if (card.getJapanese() != null)
                sb.append("<div class=\"japanese\">")
                        .append(TextUtils.htmlEncode(card.getJapanese()))
                        .append("</div>");
        } else {
            sb.append("<div class=\"info\">&nbsp;</div><br>")
                    .append("<div class=\"japanese\">&nbsp;</div>");
        }

        if (show_reading) {
            if (card.getOnReading() != null)
                sb.append("<div class=\"reading_on\">")
                        .append(TextUtils.htmlEncode(card.getOnReading()))
                        .append("</div>");

            if (card.getKunReading() != null)
                sb.append("<div class=\"reading_kun\">")
                        .append(TextUtils.htmlEncode(card.getKunReading()))
                        .append("</div>");
        } else
            sb.append("<div class=\"reading_kun\">&nbsp;</div>");

        if (show_explanation) {
            if (card.getMeaning(language) != null) {
                sb.append("<div class=\"meaning\">")
                        .append(TextUtils.htmlEncode(card.getMeaning(language)))
                        .append("</div>");
            } else {
                sb.append("<div class=\"meaning\">(no meaning available, switch language)</div>");
            }

        } else
            sb.append("<div class=\"meaning\">&nbsp;</div>");

        if ((show_japanese) && (show_explanation)) {
            sb.append("<div class=\"words\">");
            int i = 0;
            for (String word : card.getWords()) {
                // _word[i].setText(word);
                // _word[i].setVisibility(View.VISIBLE);

                i++;
                if (i >= 5)
                    break;
            }
            sb.append("</div>");
        }

        sb.append("</body>");

        return sb.toString();
    }
}
