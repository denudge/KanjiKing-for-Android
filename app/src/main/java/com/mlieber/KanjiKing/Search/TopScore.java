package com.mlieber.KanjiKing.Search;

import java.util.Vector;

/**
 * Created by nudge on 06.11.16.
 */
public class TopScore {
    private Vector scores, values;
    private final int CAPACITY;

    public TopScore(int capacity) {
        CAPACITY = capacity;
        clear();
    }

    /**
     * Adds a new value to the top score list.
     * Attention: The less score points, the better!
     * If the maximum capacity is reached, the worst element will be dropped.
     *
     * @param value
     * @param score
     */
    public void add(String value, int score) {
        int size = scores.size();

        // uninteresting
        if ((size >= CAPACITY) && (score >= ((Integer) scores.firstElement()).intValue())) {
            return;
        }

        // trivial
        if (size==0) {
            scores.addElement(new Integer(score));
            values.addElement(new String(value));
            return;
        }

        // best go to the end
        if (score <= ((Integer) scores.lastElement()).intValue()) {
            scores.addElement(new Integer(score));
            values.addElement(new String(value));
        } else {
            // something in between
            int i=0;
            while (((Integer) scores.elementAt(i)).intValue() > score) {
                i++;
            }
            scores.insertElementAt(new Integer(score), i);
            values.insertElementAt(new String(value), i);
        }

        // trim score list to capaciy size
        if (scores.size() > CAPACITY) {
            scores.removeElementAt(0);
            values.removeElementAt(0);
        }
    }

    public String[] getValues() {
        Object[] result = values.toArray();
        String[] out = new String[CAPACITY];
        for (int i = 0; i < result.length && i < CAPACITY; i++) {
            out[i] = (String) result[result.length -i -1];
        }
        return out;
    }

    public void clear() {
        scores = new Vector();
        values = new Vector(); // sorted such that the best is last
    }

    public int size() {
        return scores.size();
    }

    public int getWorstScore() {
        if ((scores == null) || (scores.size() == 0)) {
            throw new IllegalStateException("TopScore is empty, no worst score available.");
        }
        return ((Integer) scores.firstElement()).intValue();
    }

    public int getBestScore() {
        if ((scores == null) || (scores.size() == 0)) {
            throw new IllegalStateException("TopScore is empty, no best score available.");
        }
        return ((Integer) scores.lastElement()).intValue();
    }
}
