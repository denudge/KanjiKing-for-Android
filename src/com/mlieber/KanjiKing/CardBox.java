package com.mlieber.KanjiKing;

import java.util.ArrayList;
import android.util.Log;

public class CardBox {
    private static final String TAG = "CardStore";

    public static final int ORDER_NONE       = 0;
    public static final int ORDER_RANDOM     = 1;
    public static final int ORDER_FREQUENCY  = 2;
    public static final int ORDER_HADAMITZKY = 3;
    
	public static final int N_LISTS         = 10;
	public static final int BASE_FACTOR     = 5;

    private int _order = ORDER_FREQUENCY;

    // The card lists that hold the cards
    private CardList _pool;
    private CardList _done;
    private int _nLists;
    private CardList[] _lists;

    public CardBox(int order) {
        _pool = new CardList(0);
        _done = new CardList(0);
        _nLists = N_LISTS;
        _order = order;
        initializeLists();
        fillPool();
    }



    /********************* CONSTRUCTOR HELPERS *****************************/

	private void initializeLists()
    {
        _lists = new CardList[_nLists];
        for (int c =0; c < _nLists; c++)
            _lists[c] = new CardList(calculateListSize(c));

    }

    private int calculateListSize(int index)
	{
		if (index < 0)
			return 0;

		if (index >= _nLists)
			return 0;

		return BASE_FACTOR * (2 << (index-1));
	}

    private void fillPool()
    {
        _pool.clear();
        CardStore _cs = new CardStore();
        Object[] _cards = _cs.getCards();
        for (Object _card : _cards)
            _pool.add((String) _card);
    }

    /*********************** MANAGEMENT FUNCTIONS ***************************/

	private void refill()
	{
        String _card;
        while (! _lists[0].isFilled()) {
            _card = _pool.pop();
            if (_card == null)
                return;
            _lists[0].add(_card);
            Log.i(TAG, "card " + _card + " has been popped from the pool.");
        }
	}

    private boolean orderPool()
    {
        return false;
    }


    /********************** LEARNING FUNCTIONS *****************************/

    private int findNextList()
    {
        refill();
        
        // At first, use the uppest full list to prevent overpollution
        for (int c = (_nLists-1); c >= 0; c--)
            if (_lists[c].isFull())
                return c;

        // Second, see what queue we have something that we can learn from
        for (int c = 0; c < _nLists; c++)
            if (! _lists[c].isEmpty())
                return c;

        // return -1 if we have nothing usefull
        return -1;
    }

    public String getNextCard()
    {
        int _currentList = findNextList();

        if (_currentList == -1)
            return null;

        return _lists[_currentList].get();
    }

    public boolean answer(boolean correct)
    {

        // find current list
        int _currentList = findNextList();
        if (_currentList == -1)
            return false;

        // retrieve current card
        String _currentCard = null;
        _currentCard = _lists[_currentList].pop();
        if (_currentCard == null)
            return false;

        if (correct)
        {
            // Lets ascend the card to the next level
            _currentList++;

            // But should we throw this card out?
            if (_currentList >= _nLists)
            {
                _done.add(_currentCard);
                return true;
            }
        }
        else // otherwise we put it back to level zero (learning list)
            _currentList = 0;

        _lists[_currentList].add(_currentCard);
        return true;
    }

}

