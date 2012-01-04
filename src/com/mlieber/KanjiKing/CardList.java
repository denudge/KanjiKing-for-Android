package com.mlieber.KanjiKing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CardList implements java.io.Serializable {
    private final int _maxSize;
    private ArrayList<String> _list = null;

    public CardList(int size) {
        _maxSize = size;
        _list = new ArrayList<String>();
    }

    public boolean isFull() {
        if (_maxSize == 0)
            return false;

        if (_list.size() >= _maxSize)
            return true;

        return false;
    }

    public boolean isFilled() {
        if (_maxSize == 0)
            return false;

        if (_list.size() >= (_maxSize-1))
            return true;

        return false;
    }

    public int size() {
        return _list.size();
    }

    public boolean isEmpty() {
        return _list.isEmpty();
    }

    public String get() {
        if (isEmpty())
            return null;
        return (String) _list.get(0);
    }

    public String pop() {
        if (isEmpty())
            return null;
        return (String) _list.remove(0);
    }

    public boolean add(String str) {
        if (isFull())
            return false;

        _list.add(str);
        return true;
    }

    public void clear() {
        _list.clear();
    }

    public void sort() {
        Comparator<String> _cc = new CardComparator();
        java.util.Collections.sort(_list, _cc);
        
    }
}

