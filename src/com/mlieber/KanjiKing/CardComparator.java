package com.mlieber.KanjiKing;

import java.util.Comparator;

public class CardComparator implements Comparator<Card>{

    public CardComparator() {
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

