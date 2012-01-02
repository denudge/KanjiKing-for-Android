package com.mlieber.KanjiKing;

import java.util.Map;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParserException;
import android.content.res.XmlResourceParser;
import java.io.IOException;
import android.util.Log;

public class CardStore {

    private static HashMap<String, Card> _map;
    private static final String TAG = "CardStore";

    CardStore() {
        if (this._map == null)
            this._map = new HashMap<String, Card>();
    }

	public boolean loadFromXMLFile(
      					XmlResourceParser decks_xml) {

		_map.clear();

    	try {
	      	int next_tag = decks_xml.next();
			String text = null;
			Card card = null;
			String lang = null;
			
			while (next_tag != XmlResourceParser.END_DOCUMENT) {

        		if (next_tag == XmlResourceParser.START_TAG) {
					if (decks_xml.getName().equals("card")) 
						card = new Card("");

					lang = null;
          			if (decks_xml.getName().equals("meaning"))
						lang = decks_xml.getAttributeValue(null, "lang");
				}

            	if (next_tag == XmlResourceParser.END_TAG) {

					if (decks_xml.getName().equals("japanese"))
						if (card != null)
							card.setJapanese(text);

					if (decks_xml.getName().equals("reading_on"))
						if (card != null)
							card.setOnReading(text);

					if (decks_xml.getName().equals("reading_kun"))
						if (card != null)
							card.setKunReading(text);

					if (decks_xml.getName().equals("grade"))
						if (card != null)
							card.setGrade(text);

					if (decks_xml.getName().equals("strokes"))
						if (card != null)
							card.setStrokesCount(Integer.parseInt(text));

					if (decks_xml.getName().equals("freq"))
						if (card != null)
							card.setFrequency(Integer.parseInt(text));

					if (decks_xml.getName().equals("hadam"))
						if (card != null)
							card.setHadamitzky(Integer.parseInt(text));

					if (decks_xml.getName().equals("halpern"))
						if (card != null)
							card.setHalpern(Integer.parseInt(text));

					if (decks_xml.getName().equals("radical"))
						if (card != null)
							card.setRadical(Integer.parseInt(text));

					if (decks_xml.getName().equals("meaning"))
					if (decks_xml.getName().equals("meaning"))
						if ((card != null)&&(lang != null))
							card.setMeaning(lang, text);

          			if (decks_xml.getName().equals("card"))
					{
						if ((card != null)&&(card.getJapanese() != ""))
							_map.put(card.getJapanese(), card);
						card = null;
					}
				}

            	if (next_tag == XmlResourceParser.TEXT)
					text = decks_xml.getText();

				next_tag = decks_xml.next();
			}

            Log.i(TAG, _map.size() + " cards loaded.");
            return true; 
		} catch (IOException e) {
		  throw new RuntimeException(e);
		} catch (XmlPullParserException e) {
		  throw new RuntimeException(e);
	    }
    }

    public Object[] getCards() {
        return _map.keySet().toArray(); 
    }

    public Card get(String str) {
        return _map.get(str);
    }

    public int size() {
        return _map.size();
    }
}

