package com.mlieber.KanjiKing;

import java.util.ArrayList;
import java.io.IOException;
import android.util.Log;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParser;

public class CardBox implements java.io.Serializable {
    private static final String TAG = "CardBox";

    public static final int ORDER_NONE       = 0;
    public static final int ORDER_RANDOM     = 1;
    public static final int ORDER_FREQUENCY  = 2;
    public static final int ORDER_HADAMITZKY = 3;
    
	public static final int N_LISTS         = 8;
	public static final int BASE_FACTOR     = 5;

    private int _order = ORDER_FREQUENCY;

    // The card lists that hold the cards
    private CardList _pool = null;
    private CardList _done;

    private int _nLists;
    private CardList[] _lists;

    public CardBox(int order, boolean fill) {
        _pool = new CardList(0);
        _done = new CardList(0);
        _nLists = N_LISTS;
        _order = order;
        initializeLists();
        if (fill)
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

		return BASE_FACTOR * (1 << index) + 1;
	}

    private void fillPool()
    {
        if (_pool == null)
            _pool = new CardList(0);

        if (_pool.size() > 0)
            return;
        
        CardStore _cs = new CardStore();
        Object[] _cards = _cs.getCards();
        for (Object _card : _cards)
            _pool.add((String) _card);

        _pool.sort();
    }

    /*********************** MANAGEMENT FUNCTIONS ***************************/

    private String findNewCard() {
        // Look if the pool has a valid card
        String _card = _pool.get();
        if (_card != null) {
            // Do we need frequency check?
            if (KanjiKing.getMaxFreq() == 0)
                return _pool.pop();

            // Does the card fit?
            CardStore _cs = new CardStore();
            Card _c = _cs.get(_card);
            if ((_c.getFrequency() > 0) && (_c.getFrequency() <= KanjiKing.getMaxFreq()))
                return _pool.pop();
        }

        // Check if we have endless mode
        if (false == KanjiKing.getEndless())
            return null;

        // Do we have a done card?
        if (!_done.isEmpty())
            return _done.pop();

        // Search for the highest queue to take a card from
        for (int c = (_nLists-1); c >= 0; c--)
            if (!_lists[c].isEmpty())
                return _lists[c].pop();

        return null;
    }

	private void refill()
	{
        String _card;

        while (! _lists[0].isFilled()) {
            _card = findNewCard();
            if (_card == null)
                return;
            _lists[0].add(_card);
        }
	}

    private boolean orderPool()
    {
        return false;
    }

    public void clear()
    {
        _pool.clear();
        fillPool();
        _done.clear();
        for (int c = 0; c < _nLists; c++)
            _lists[c].clear();
    } 

    /********************** LEARNING FUNCTIONS *****************************/

    public int findNextList()
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

    public String getStatus()
    {
        StringBuilder sb = new StringBuilder();
        
        int _currentList = findNextList();
        
        sb.append("(")
            .append(_pool.size())
            .append(")");

        for (int c = 0; c < _nLists; c++)
        {
            sb.append(" ");

            if (c == _currentList)
                sb.append("[")
                    .append(_lists[c].size())
                    .append("]");
            else
                sb.append(_lists[c].size());
        }

        sb.append(" (")
            .append(_done.size())
            .append(")");

        return sb.toString();
    }



    /****************** SEARCH FUNCTIONS ***************************/

    public int getBoxListNumber(String search)
    {
        // check each card box from bottom to top (faster)
        for (int c = 0; c < _nLists; c++) {
            for (int d = 0; d < _lists[c].size(); d++) {
                if (search.equals(_lists[c].get(d)))
                    return (c+1);
            }
        }

        // check the learned kanji
        for (int d = 0; d < _done.size(); d++) {
            if (search.equals(_done.get(d)))
                return 99;
        }

        // the kanji has never been seen/learned before
        return 0;
    }


    

    /***************** IMPORT AND EXPORT FUNCTIONS **********************/

    public String asXML()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");

        sb.append("<done>\n")
            .append(_done.asXML())
            .append("</done>\n");

        for (int c = _nLists; c > 0; c--)
            sb.append("<list number=\"" + c + "\">\n")
                .append(_lists[c-1].asXML())
                .append("</list>\n");

        sb.append("<pool>\n")
            .append(_pool.asXML())
            .append("</pool>\n");

        return sb.toString();
    }

    public void loadFromXML(XmlPullParser xml) {
    	try {
	      	int next_tag = xml.next();
		    String text = null;
            int listnumber = 0;
            int i = 0;
            CardList _current_list = null;

			while (next_tag != XmlPullParser.END_DOCUMENT) {

                if (next_tag == XmlPullParser.START_TAG) {
                    text = null;

                    if (xml.getName().equals("pool"))
                        _current_list = _pool;
                    else if (xml.getName().equals("done"))
                        _current_list = _done;
                    else if (xml.getName().equals("list"))
                    {
                        _current_list = null;
                        listnumber = Integer.parseInt(xml.getAttributeValue(null, "number"));
                        if ((listnumber > 0) && (listnumber <= _nLists))
                        {
                            _current_list = _lists[listnumber-1];
                            Log.i(TAG, "Loading the following cards to list " + (listnumber-1));
                        }
                    }
                }

                if (next_tag == XmlPullParser.END_TAG) {
                    if ((xml.getName().equals("card") || (xml.getName().equals("c")))
                            && (text != null) 
                            && (text.length() > 0) 
                            && (_current_list != null)) {
                        _current_list.add(text);
                        i++;
                    }
                    else
                        Log.i(TAG, "Text or _current_list null/empty while loading");
                }

                if (next_tag == XmlPullParser.TEXT)
                    text = xml.getText();

                next_tag = xml.next();
			}
        
            Log.i(TAG, i + " cards loaded.");

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return;
		} catch (XmlPullParserException e) {
            Log.e(TAG, e.getMessage());
            return;
	    }
    }


}

