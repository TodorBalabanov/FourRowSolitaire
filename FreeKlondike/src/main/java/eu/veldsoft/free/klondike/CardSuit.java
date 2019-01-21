/*
 This file is a part of Free Klondike

 Copyright (C) 2010-2014 by Matt Stephen, Todor Balabanov, Konstantin Tsanov, Ventsislav Medarov, Vanya Gyaurova, Plamena Popova, Hristiana Kalcheva, Yana Genova

 Free Klondike is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Free Klondike is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with FreeKlondike.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.veldsoft.free.klondike;

/**
 * Manages the card suit.
 * 
 * @author Todor Balabanov
 */
enum CardSuit {

	/**
	 * Spades enum constant.
	 */
	SPADES(0, "Spades"),

	/**
	 * Clubs enum constant.
	 */
	CLUBS(1, "Clubs"),

	/**
	 * Diamonds enum constant.
	 */
	DIAMONDS(2, "Diamonds"),

	/**
	 * Hearts enum constant.
	 */
	HEARTS(3, "Hearts");

	/**
	 * Suit name.
	 */
	private int index;

	/**
	 * Suit name.
	 */
	private String name;

	/**
	 * Sets the card suit.
	 * 
	 * @param index
	 *            Index to be used for the suit.
	 * 
	 * @param name
	 *            Name to be used for the suit.
	 * 
	 * @author Todor Balabanov
	 */
	private CardSuit(int index, String name) {
		this.index = index;
		this.name = name;
	}

	/**
	 * Index of the suit.
	 * 
	 * @return Suit index.
	 * 
	 * @author Todor Balabanov
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the card suit.
	 * 
	 * @return Suit name.
	 * 
	 * @author Todor Balabanov
	 */
	public String getName() {
		return name;
	}
}
