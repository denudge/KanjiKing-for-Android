package com.mlieber.KanjiKing;

public class CardBox {
	public static final int BOXES 		= 10;
	public static final int BASE_FACTOR = 5;

	private int getSize(int box)
	{
		if (box <= 0)
			return 0;

		if (box >= BOXES)
			return 0;

		return BASE_FACTOR * (2 << box);
	}

	private boolean refill()
	{
        return false;
	}
}

