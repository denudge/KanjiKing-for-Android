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
    private String _style = null;

    CardStore() {
        if (this._map == null)
            this._map = new HashMap<String, Card>();
    }

    public void clear()
    {
		_map.clear();
    }

	public boolean loadFromXMLFile(
      					XmlResourceParser decks_xml) {

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
          			if (decks_xml.getName().equals("mean"))
						lang = decks_xml.getAttributeValue(null, "lang");
				}

            	if (next_tag == XmlResourceParser.END_TAG) {

					if (decks_xml.getName().equals("jap"))
						if (card != null)
							card.setJapanese(text);

					if (decks_xml.getName().equals("on"))
						if (card != null)
							card.setOnReading(text);

					if (decks_xml.getName().equals("kun"))
						if (card != null)
							card.setKunReading(text);

					if (decks_xml.getName().equals("grd"))
						if (card != null)
							card.setGrade(Integer.parseInt(text));

					if (decks_xml.getName().equals("style"))
							_style = text;

					if (decks_xml.getName().equals("str"))
						if (card != null)
							card.setStrokesCount(Integer.parseInt(text));

					if (decks_xml.getName().equals("freq"))
						if (card != null)
							card.setFrequency(Integer.parseInt(text));

					if (decks_xml.getName().equals("had"))
						if (card != null)
							card.setHadamitzky(Integer.parseInt(text));

					if (decks_xml.getName().equals("hal"))
						if (card != null)
							card.setHalpern(Integer.parseInt(text));

					if (decks_xml.getName().equals("rad"))
						if (card != null)
							card.setRadical(Integer.parseInt(text));

					if (decks_xml.getName().equals("mean"))
					if (decks_xml.getName().equals("mean"))
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

    public String getStyle() {
        return _style;
    }

}

