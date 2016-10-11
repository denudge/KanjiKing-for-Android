package com.mlieber.KanjiKing.Db;

import com.mlieber.KanjiKing.CardBox.Card;
import com.mlieber.KanjiKing.Search.Criteria;

/**
 * Created by nudge on 11.10.16.
 */
public class SearchStatement
{
    private Criteria _criteria;

    private String _statement;

    public SearchStatement(Criteria criteria) {
        _criteria = criteria;
        _statement = createStatement(criteria);
    }

    public String toString() {
        return _statement;
    }

    private String createStatement(Criteria criteria) {
        String stmt = "SELECT " + Db.DB_QUERY_FIELDS
                + " FROM card"
                + ((criteria.getMeaning() != null) && (!criteria.getMeaning().equals(""))
                    ? " INNER JOIN card_lang ON card_lang.card=card._id AND card_lang.language='de'"
                    : ""
                  )
                + " WHERE type=" + Card.TYPE_KANJI;

        int nCriteria = 0;

        if ((criteria.getMeaning() != null) && (!criteria.getMeaning().equals(""))) {
            stmt += " AND meaning LIKE '%" + Db.mask(criteria.getMeaning()) + "%'";
        }

        if ((criteria.getSearchPhrase() != null) && (!criteria.getSearchPhrase().equals(""))) {
            stmt += stmt += " AND japanese IN (";
            for (int c = 0; c < criteria.getSearchPhrase().length(); c++) {
                stmt += "'" + criteria.getSearchPhrase().charAt(c) + "', ";
            }
            stmt += "'') AND japanese NOT IN ('')";
        }

        if ((criteria.getReading() != null) && (!criteria.getReading().equals(""))) {
            stmt += " AND (reading_on LIKE '"
                    + Db.mask(criteria.getReading())
                    + "' OR reading_kun LIKE '%" + Db.mask(criteria.getReading())  + "%')";
        }

        if (criteria.getRadical() > 0) {
            stmt += " AND radical=" + criteria.getRadical();
        }

        if (criteria.getStrokes() > 0) {
            stmt += " AND strokes=" + criteria.getStrokes();
        }

        stmt += " ORDER BY frequency ASC";
        stmt += " LIMIT 100;";

        return stmt;
    }
}
