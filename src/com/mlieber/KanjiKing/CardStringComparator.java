package com.mlieber.KanjiKing;

import java.util.Comparator;

public class CardStringComparator implements Comparator<String>{
    private CardStore _cs;

    public CardStringComparator() {
        _cs = KanjiKing.getCardStore();
    }

    public int compare(String str_a, String str_b) {
        Card a = _cs.get(str_a);
        Card b = _cs.get(str_b);

        if (a.getFrequency() < b.getFrequency())
            return -1;
        if (a.getFrequency() > b.getFrequency())
            return 1;
        return 0;
    }
}


