package com.mlieber.KanjiKing;

import java.util.Map;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParserException;
import android.content.res.XmlResourceParser;
import java.io.IOException;
import android.util.Log;

/**
 * A Card store holds all information of kanji or vocabulary cards.
 * So the cardbox just holds the kanji or Japanese vocabulary as
 * reference to a card stored here.
 * Downside is that you need to have the full card store loaded
 * when opening a cardbox, e.g. with personal words
 */
public class CardStore {

    private static HashMap<String, Card> _map;
    private static final String TAG = "CardStore";

    CardStore() {
        if (this._map == null)
            this._map = new HashMap<String, Card>();
    }

    public void clear()
    {
		_map.clear();
    }


    /**
     * Reads an XML resource into a card store
     */
	public boolean loadFromXMLFile(
      					XmlResourceParser xml) {

    	try {
	      	int next_tag = xml.next();
            int _ncards = 0;
			String text = null;
			Card card = null;
			String lang = null, type = null;
		    Style _style = null;

            // Iterate all xml tags
			while (next_tag != XmlResourceParser.END_DOCUMENT) {

        		if (next_tag == XmlResourceParser.START_TAG) {
					if (xml.getName().equals("card"))
                    {
						card = new Card("");
                        card.setStyle(_style);
                        type = xml.getAttributeValue(null, "type");
                        if ((type != null) && type.equals("word"))
                            card.setType(Card.TYPE_WORD);
                        else
                            card.setType(Card.TYPE_KANJI);
                    }

                    // only meaning and hint are language-dependent
                    lang = null;
          			if ((xml.getName().equals("mean"))
                        || (xml.getName().equals("hint"))) {
						lang = xml.getAttributeValue(null, "lang");
                    }
				}

            	if (next_tag == XmlResourceParser.END_TAG) {

					if (xml.getName().equals("style"))
							_style = new Style(text);

					if (xml.getName().equals("jap"))
						if (card != null)
							card.setJapanese(text);

					if (xml.getName().equals("hint"))
						if (card != null)
							card.setHint(text);

					if (xml.getName().equals("on"))
						if (card != null)
							card.setOnReading(text);

					if (xml.getName().equals("kun"))
						if (card != null)
							card.setKunReading(text);

					if (xml.getName().equals("grd"))
						if (card != null)
							card.setGrade(Integer.parseInt(text));

					if (xml.getName().equals("str"))
						if (card != null)
							card.setStrokesCount(Integer.parseInt(text));

					if (xml.getName().equals("freq"))
						if (card != null)
							card.setFrequency(Integer.parseInt(text));

					if (xml.getName().equals("had"))
						if (card != null)
							card.setHadamitzky(Integer.parseInt(text));

					if (xml.getName().equals("hal"))
						if (card != null)
							card.setHalpern(Integer.parseInt(text));

					if (xml.getName().equals("rad"))
						if (card != null)
							card.setRadical(Integer.parseInt(text));

					if (xml.getName().equals("mean"))
						if ((card != null)&&(lang != null))
							card.setMeaning(lang, text);

          			if (xml.getName().equals("card")) {
						if ((card != null)&&(card.getJapanese() != "")) {
							_map.put(card.getJapanese(), card);
                            _ncards++;
                        }
						card = null;
					}
				}

            	if (next_tag == XmlResourceParser.TEXT)
					text = xml.getText();

				next_tag = xml.next();
			}

            Log.i(TAG, _ncards + " cards loaded.");
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

