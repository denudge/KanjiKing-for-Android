package de.bitnetic.KanjiKing.Element;

import de.bitnetic.KanjiKing.CardBox.Card;

/**
 * Created by nudge on 11.10.16.
 */
public class KanjiInfo
{
    private String _info;

    public KanjiInfo(Card card) {
        _info = formatInfo(card);
    }

    public String toString() {
        return _info;
    }

    private String formatInfo(Card card) {
        StringBuilder sb = new StringBuilder();

        sb.append("F: ")
                .append(card.getFrequency())
                .append(" / G: ")
                .append(card.getGrade())
                .append(" / S: ")
                .append(card.getStrokesCount())
                .append(" / R: ")
                .append(card.getRadical())
                .append(" / H: ")
                .append(card.getHadamitzkyNumber());

        return sb.toString();
    }
}
