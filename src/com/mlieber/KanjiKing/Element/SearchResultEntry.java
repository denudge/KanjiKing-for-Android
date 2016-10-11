package com.mlieber.KanjiKing.Element;

import com.mlieber.KanjiKing.CardBox.Card;
import com.mlieber.KanjiKing.CardBox.CardBox;

/**
 * Created by nudge on 11.10.16.
 */
public class SearchResultEntry
{
    private String _entry;

    public SearchResultEntry(Card card, String language, CardBox box) {
        _entry = formatEntry(card, language, box);
    }

    public String toString() {
        return _entry;
    }

    private String formatEntry(Card card, String language, CardBox box) {
        StringBuilder sb = new StringBuilder();

        sb.append(card.getJapanese())
                .append(" ")
                .append(card.getOnReading())
                .append(" ")
                .append(card.getKunReading())
                .append("\n")
                .append(new KanjiInfo(card).toString())
                .append("\n")
                .append(card.getMeaning(language))
                .append("\n");

        if (box != null) {
            int nbox = box.getBoxListNumber(card.getId() + "");
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
        }

        return sb.toString();
    }

}
