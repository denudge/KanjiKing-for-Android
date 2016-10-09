package com.mlieber.KanjiKing.CardBox;

import java.util.Comparator;

public class CardFrequencyComparator implements Comparator<Card>{

    public CardFrequencyComparator() {
    }

    public int compare(Card a, Card b)
    {
        if (a.getFrequency() < b.getFrequency())
            return -1;
        if (a.getFrequency() > b.getFrequency())
            return 1;
        return 0;
    }
}
