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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Class: SolitaireBoard
 * 
 * Description: The SolitaireBoard class manages the entire playing field.
 * 
 * @author Matt Stephen
 */
class SolitaireBoard {

	/**
	 * 
	 */
	private static final int INITIAL_CARDS_NUMBER_IN_COLUMN = 5;

	/**
	 * Can be 1 or 3. Should be only here!
	 */
	static int drawCount = 1;

	/**
	 * To store new option selection for next new game, otherwise the count
	 * would be changed at next click of the deck (in the middle of the game).
	 */
	private int newDrawCount = drawCount;

	/**
	 * 1 = easy, 2 = medium, 3 = hard Should be only here!
	 */
	private GameDifficulty difficulty = GameDifficulty.EASY;

	/**
	 * Game difficulty.
	 */
	private GameDifficulty newDifficulty = difficulty;

	/**
	 * Card numbers.
	 */
	// TODO Should be private.
	LinkedList<Integer> numCards = new LinkedList<Integer>();

	/**
	 * The cards from the discard pile.
	 */
	LinkedList<Integer> numCardsInDiscardView = new LinkedList<Integer>();

	/**
	 * The four columns for the main playing field.
	 */
	// TODO Should be private.
	Column[] columns = { null, null, null, null };

	/**
	 * The discard pile.
	 */
	// TODO Should be private.
	DiscardPile discardPile = new DiscardPile();

	/**
	 * The deal pile.
	 */
	// TODO Should be private.
	DealDeck dealDeck = new DealDeck(discardPile);

	/**
	 * The four ace piles (to stack Ace - King of a single suit).
	 */
	// TODO Should be private.
	AcePile[] acePiles = { null, null, null, null };

	/**
	 * The four top individual cells.
	 */
	// TODO Should be private.
	SingleCell[] cells = { null, null, null, null };

	/**
	 * Source.
	 */
	// TODO Should be private.
	LinkedList<CardStack> sourceList = new LinkedList<CardStack>();

	/**
	 * Destination.
	 */
	// TODO Should be private.
	LinkedList<CardStack> destinationList = new LinkedList<CardStack>();

	/**
	 * Sets the board's window name, size, location, close button option, makes
	 * it unresizable and puts the logo on it.
	 * 
	 * @author Todor Balabanov
	 */
	public SolitaireBoard() {
	}

	/**
	 * Check for highlighted cards in the board.
	 * 
	 * @return True if there is a card or bunch of cards highlighted and false
	 *         otherwise.
	 * 
	 * @author Todor Balabanov
	 */
	public int highlightedCards() {
		int counter = 0;

		for (int i = 0; i < columns.length; i++) {
			for (int j = 0; j < columns[i].length(); j++) {
				if (columns[i].getCardAtLocation(j).isHighlighted() == true) {
					counter++;
				}
			}
		}

		for (int i = 0; i < cells.length; i++) {
			if (cells[i].isEmpty() == false && cells[i].peek().isHighlighted() == true) {
				counter++;
			}
		}

		for (int i = 0; i < acePiles.length; i++) {
			if (acePiles[i].isEmpty() == false && acePiles[i].peek().isHighlighted() == true) {
				counter++;
			}
		}

		if (discardPile.isEmpty() == false && discardPile.peek().isHighlighted() == true) {
			counter++;
		}

		return (counter);
	}

	/**
	 * Mark all cards on the board as non highlighted.
	 * 
	 * @author Todor Balabanov
	 */
	public void clearHighlighting() {
		for (int n = 1; n <= 52; n++) {
			Card.valueBy(n).unhighlight();
		}
	}

	/**
	 * Creates the solitaire board.
	 * 
	 * @param cards
	 *            List of cards.
	 * 
	 * @param numViewableCards
	 *            Number of viewable cards.
	 * 
	 * @author Todor Balabanov
	 */
	public void createBoard(LinkedList<Integer> cards, int numViewableCards) {
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new Column();
		}

		for (int i = 0; i < cells.length; i++) {
			cells[i] = new SingleCell();
		}

		for (int i = 0; i < acePiles.length; i++) {
			switch (i) {
			case 0:
				acePiles[i] = new AcePile(CardSuit.SPADES);
				break;
			case 1:
				acePiles[i] = new AcePile(CardSuit.CLUBS);
				break;
			case 2:
				acePiles[i] = new AcePile(CardSuit.DIAMONDS);
				break;
			case 3:
				acePiles[i] = new AcePile(CardSuit.HEARTS);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Deals the cards.
	 * 
	 * @author Todor Balabanov
	 */
	void dealOutBoard() {
		LinkedList<Card> cards = (LinkedList<Card>) Deck.getFullShuffledDeck();

		/*
		 * Fill five cards by column.
		 */
		for (int i = 0; i < INITIAL_CARDS_NUMBER_IN_COLUMN; i++) {
			for (int j = 0; j < columns.length; j++) {
				columns[j].addCard(cards.getLast());
				cards.removeLast();
			}
		}

		/*
		 * Fill cards in buffer area.
		 */
		for (int j = 0; j < cells.length; j++) {
			cells[j].addCard(cards.getLast());
			cards.removeLast();
		}

		dealDeck.setDeck(cards);
		dealDeck.setDrawCount(newDrawCount);
		dealDeck.setDifficulty(newDifficulty);

		if (newDrawCount != drawCount) {
			drawCount = newDrawCount;
		}

		if (newDifficulty != difficulty) {
			difficulty = newDifficulty;
		}

		clearHighlighting();
	}

	/**
	 * Used to deal the cards on the board after opening a saved game.
	 * 
	 * @param numbers
	 *            List of integers.
	 * 
	 * @param numViewableCards
	 *            Number of viewable cards.
	 * 
	 * @author Todor Balabanov
	 */
	void dealOutCustomBoard(LinkedList<Integer> numbers, int numViewableCards) {
		List<Card> cards = Deck.getDeckSubsetByCardNumbers(numbers);

		int pileNumber = 0;
		int cardNumber = -1;

		dealDeck.setDrawCount(drawCount);
		dealDeck.setDifficulty(difficulty);

		for (int i = 0; i < numbers.size(); i++) {
			if (numbers.get(i) > 0) {
				cardNumber++;
			} else {
				pileNumber++;
				continue;
			}

			if (0 <= pileNumber && pileNumber <= 3) {
				cells[pileNumber % 4].addCard(cards.get(cardNumber));
			} else if (4 <= pileNumber && pileNumber <= 7) {
				columns[pileNumber % 4].addCard(cards.get(cardNumber));
			} else if (8 <= pileNumber && pileNumber <= 11) {
				acePiles[pileNumber % 4].addCard(cards.get(cardNumber));
			} else if (pileNumber == 12) {
				Card card = cards.get(cardNumber);
				card.setFaceDown();
				dealDeck.addCard(card);
			} else if (pileNumber == 13) {
				discardPile.push(cards.get(cardNumber));
			}
		}

		discardPile.setView(numViewableCards);
	}

	/**
	 * Clears the board.
	 * 
	 * @author Todor Balabanov
	 */
	void clearBoard() {
		for (int i = 0; i < columns.length; i++) {
			while (columns[i].isEmpty() == false) {
				columns[i].pop();
			}
		}

		for (int i = 0; i < cells.length; i++) {
			while (cells[i].isEmpty() == false) {
				cells[i].pop();
			}
		}

		for (int i = 0; i < acePiles.length; i++) {
			while (acePiles[i].isEmpty() == false) {
				acePiles[i].pop();
			}
		}

		while (dealDeck.isEmpty() == false) {
			dealDeck.pop();
		}

		while (discardPile.isEmpty() == false) {
			discardPile.pop();
		}
	}

	/**
	 * For starting a new game.
	 * 
	 * @param winOrLoss
	 *            Game state.
	 * 
	 * @author Todor Balabanov
	 */
	public void newGame(GameState winOrLoss) {
		/*
		 * Remove cards from ace piles. Set numTimesThroughDeck back to 1.
		 */
		clearBoard();
		dealDeck.reset();

		sourceList.clear();
		destinationList.clear();
		numCards.clear();
		numCardsInDiscardView.clear();
	}

	/**
	 * Manages the game states.
	 * 
	 * @param winOrLoss
	 *            Game state.
	 * 
	 * @param timerCount
	 *            Timer.
	 * 
	 * @param backgroundNumber
	 *            Background number.
	 * 
	 * @param deckNumber
	 *            Card back number.
	 * 
	 * @param timerToRunNextGame
	 *            Timer to next game.
	 * 
	 * @param timerToRun
	 *            Timer to run.
	 * 
	 * @author Todor Balabanov
	 */
	void recordGame(GameState winOrLoss, int deckNumber, int backgroundNumber, int timerCount, int timerToRunNextGame,
			boolean timerToRun) {
		int count = 0, temp = 0;
		int gamesPlayed1e = 0, gamesWon1e = 0, winStreak1e = 0, lossStreak1e = 0, currentStreak1e = 0;
		int gamesPlayed1m = 0, gamesWon1m = 0, winStreak1m = 0, lossStreak1m = 0, currentStreak1m = 0;
		int gamesPlayed1h = 0, gamesWon1h = 0, winStreak1h = 0, lossStreak1h = 0, currentStreak1h = 0;
		int gamesPlayed3e = 0, gamesWon3e = 0, winStreak3e = 0, lossStreak3e = 0, currentStreak3e = 0;
		int gamesPlayed3m = 0, gamesWon3m = 0, winStreak3m = 0, lossStreak3m = 0, currentStreak3m = 0;
		int gamesPlayed3h = 0, gamesWon3h = 0, winStreak3h = 0, lossStreak3h = 0, currentStreak3h = 0;

		String fileLocation = System.getProperty("user.home") + System.getProperty("file.separator");
		File file = new File(fileLocation + "frs-statistics.dat");

		try {
			file.createNewFile();
		} catch (Exception ex) {
			System.out.println(ex);
		}

		try {
			DataInputStream input = new DataInputStream(new FileInputStream(file));

			if (input.available() > 0) {
				temp = input.readInt();
				count++;
			}

			if (temp != -1) {
				gamesPlayed1m = temp;

				while ((input.available() > 0) && count < 5) {
					temp = input.readInt();
					switch (count) {
					/*
					 * Case 0 is the format checker.
					 */
					case 1:
						gamesWon1m = temp;
						break;
					case 2:
						winStreak1m = temp;
						break;
					case 3:
						lossStreak1m = temp;
						break;
					case 4:
						currentStreak1m = temp;
						break;

					default:
						break;
					}

					count++;
				}
			} else {
				while ((input.available() > 0) && count < 31) {
					temp = input.readInt();
					switch (count) {
					/*
					 * Case 0 is the format checker.
					 */
					case 1:
						gamesPlayed1e = temp;
						break;
					case 2:
						gamesWon1e = temp;
						break;
					case 3:
						winStreak1e = temp;
						break;
					case 4:
						lossStreak1e = temp;
						break;
					case 5:
						currentStreak1e = temp;
						break;

					case 6:
						gamesPlayed1m = temp;
						break;
					case 7:
						gamesWon1m = temp;
						break;
					case 8:
						winStreak1m = temp;
						break;
					case 9:
						lossStreak1m = temp;
						break;
					case 10:
						currentStreak1m = temp;
						break;

					case 11:
						gamesPlayed1h = temp;
						break;
					case 12:
						gamesWon1h = temp;
						break;
					case 13:
						winStreak1h = temp;
						break;
					case 14:
						lossStreak1h = temp;
						break;
					case 15:
						currentStreak1h = temp;
						break;

					case 16:
						gamesPlayed3e = temp;
						break;
					case 17:
						gamesWon3e = temp;
						break;
					case 18:
						winStreak3e = temp;
						break;
					case 19:
						lossStreak3e = temp;
						break;
					case 20:
						currentStreak3e = temp;
						break;

					case 21:
						gamesPlayed3m = temp;
						break;
					case 22:
						gamesWon3m = temp;
						break;
					case 23:
						winStreak3m = temp;
						break;
					case 24:
						lossStreak3m = temp;
						break;
					case 25:
						currentStreak3m = temp;
						break;

					case 26:
						gamesPlayed3h = temp;
						break;
					case 27:
						gamesWon3h = temp;
						break;
					case 28:
						winStreak3h = temp;
						break;
					case 29:
						lossStreak3h = temp;
						break;
					case 30:
						currentStreak3h = temp;
						break;

					default:
						break;
					}

					count++;
				}
			}

			input.close();
		} catch (Exception ex) {
			System.out.println(ex);
		}

		try {
			DataOutputStream output = new DataOutputStream(new FileOutputStream(file));

			if (winOrLoss == GameState.GAME_WON) {
				if (drawCount == 3) {
					if (difficulty == GameDifficulty.EASY) {
						gamesPlayed3e++;
						gamesWon3e++;

						if (currentStreak3e >= 0) {
							currentStreak3e++;
						} else {
							currentStreak3e = 1;
						}

						if (winStreak3e < currentStreak3e) {
							winStreak3e = currentStreak3e;
						}
					} else if (difficulty == GameDifficulty.MEDIUM) {
						gamesPlayed3m++;
						gamesWon3m++;

						if (currentStreak3m >= 0) {
							currentStreak3m++;
						} else {
							currentStreak3m = 1;
						}

						if (winStreak3m < currentStreak3m) {
							winStreak3m = currentStreak3m;
						}
					} else {
						gamesPlayed3h++;
						gamesWon3h++;

						if (currentStreak3h >= 0) {
							currentStreak3h++;
						} else {
							currentStreak3h = 1;
						}

						if (winStreak3h < currentStreak3h) {
							winStreak3h = currentStreak3h;
						}
					}
				} else if (drawCount == 1) {
					if (difficulty == GameDifficulty.EASY) {
						gamesPlayed1e++;
						gamesWon1e++;

						if (currentStreak1e >= 0) {
							currentStreak1e++;
						} else {
							currentStreak1e = 1;
						}

						if (winStreak1e < currentStreak1e) {
							winStreak1e = currentStreak1e;
						}
					} else if (difficulty == GameDifficulty.MEDIUM) {
						gamesPlayed1m++;
						gamesWon1m++;

						if (currentStreak1m >= 0) {
							currentStreak1m++;
						} else {
							currentStreak1m = 1;
						}

						if (winStreak1m < currentStreak1m) {
							winStreak1m = currentStreak1m;
						}
					} else {
						gamesPlayed1h++;
						gamesWon1h++;

						if (currentStreak1h >= 0) {
							currentStreak1h++;
						} else {
							currentStreak1h = 1;
						}

						if (winStreak1h < currentStreak1h) {
							winStreak1h = currentStreak1h;
						}
					}
				}
			} else if (winOrLoss == GameState.RESET_STATS) {
				gamesWon1e = 0;
				gamesPlayed1e = 0;
				currentStreak1e = 0;
				winStreak1e = 0;
				lossStreak1e = 0;

				gamesWon1m = 0;
				gamesPlayed1m = 0;
				currentStreak1m = 0;
				winStreak1m = 0;
				lossStreak1m = 0;

				gamesWon1h = 0;
				gamesPlayed1h = 0;
				currentStreak1h = 0;
				winStreak1h = 0;
				lossStreak1h = 0;

				gamesWon3e = 0;
				gamesPlayed3e = 0;
				currentStreak3e = 0;
				winStreak3e = 0;
				lossStreak3e = 0;

				gamesWon3m = 0;
				gamesPlayed3m = 0;
				currentStreak3m = 0;
				winStreak3m = 0;
				lossStreak3m = 0;

				gamesWon3h = 0;
				gamesPlayed3h = 0;
				currentStreak3h = 0;
				winStreak3h = 0;
				lossStreak3h = 0;
			} else if (winOrLoss == GameState.DO_NOTHING || winOrLoss == GameState.GAME_SAVED) {
				/*
				 * Just updating options.
				 */
			} else {
				if (drawCount == 3) {
					if (difficulty == GameDifficulty.EASY) {
						gamesPlayed3e++;

						if (currentStreak3e <= 0) {
							currentStreak3e--;
						} else {
							currentStreak3e = -1;
						}

						if (lossStreak3e > currentStreak3e) {
							lossStreak3e = currentStreak3e;
						}
					} else if (difficulty == GameDifficulty.MEDIUM) {
						gamesPlayed3m++;

						if (currentStreak3m <= 0) {
							currentStreak3m--;
						} else {
							currentStreak3m = -1;
						}

						if (lossStreak3m > currentStreak3m) {
							lossStreak3m = currentStreak3m;
						}
					} else {
						gamesPlayed3h++;

						if (currentStreak3h <= 0) {
							currentStreak3h--;
						} else {
							currentStreak3h = -1;
						}

						if (lossStreak3h > currentStreak3h) {
							lossStreak3h = currentStreak3h;
						}
					}
				} else if (drawCount == 1) {
					if (difficulty == GameDifficulty.EASY) {
						gamesPlayed1e++;

						if (currentStreak1e <= 0) {
							currentStreak1e--;
						} else {
							currentStreak1e = -1;
						}

						if (lossStreak1e > currentStreak1e) {
							lossStreak1e = currentStreak1e;
						}
					} else if (difficulty == GameDifficulty.MEDIUM) {
						gamesPlayed1m++;

						if (currentStreak1m <= 0) {
							currentStreak1m--;
						} else {
							currentStreak1m = -1;
						}

						if (lossStreak1m > currentStreak1m) {
							lossStreak1m = currentStreak1m;
						}
					} else {
						gamesPlayed1h++;

						if (currentStreak1h <= 0) {
							currentStreak1h--;
						} else {
							currentStreak1h = -1;
						}

						if (lossStreak1h > currentStreak1h) {
							lossStreak1h = currentStreak1h;
						}
					}
				}
			}

			/*
			 * New format indicator.
			 */
			output.writeInt(-1);

			output.writeInt(gamesPlayed1e);
			output.writeInt(gamesWon1e);
			output.writeInt(winStreak1e);
			output.writeInt(lossStreak1e);
			output.writeInt(currentStreak1e);

			output.writeInt(gamesPlayed1m);
			output.writeInt(gamesWon1m);
			output.writeInt(winStreak1m);
			output.writeInt(lossStreak1m);
			output.writeInt(currentStreak1m);

			output.writeInt(gamesPlayed1h);
			output.writeInt(gamesWon1h);
			output.writeInt(winStreak1h);
			output.writeInt(lossStreak1h);
			output.writeInt(currentStreak1h);

			output.writeInt(gamesPlayed3e);
			output.writeInt(gamesWon3e);
			output.writeInt(winStreak3e);
			output.writeInt(lossStreak3e);
			output.writeInt(currentStreak3e);

			output.writeInt(gamesPlayed3m);
			output.writeInt(gamesWon3m);
			output.writeInt(winStreak3m);
			output.writeInt(lossStreak3m);
			output.writeInt(currentStreak3m);

			output.writeInt(gamesPlayed3h);
			output.writeInt(gamesWon3h);
			output.writeInt(winStreak3h);
			output.writeInt(lossStreak3h);
			output.writeInt(currentStreak3h);

			output.writeInt(drawCount);
			output.writeInt(newDrawCount);
			output.writeInt(deckNumber);
			output.writeInt(backgroundNumber);
			output.writeInt(timerToRunNextGame);

			/*
			 * Finish saving options.
			 */
			output.writeInt(0);
			output.writeInt(0);
			output.writeInt(dealDeck.getDeckThroughs());
			output.writeInt(difficulty.getValue());
			output.writeInt(newDifficulty.getValue());
			output.writeInt(discardPile.getNumViewableCards());

			File savedFile = new File(fileLocation + "frs-savedgame.dat");
			DataOutputStream saved = new DataOutputStream(new FileOutputStream(savedFile));

			if (winOrLoss == GameState.GAME_SAVED) {
				/*
				 * Saved.
				 */
				output.writeInt(1);

				for (int i = 0; i < cells.length; i++) {
					if (!cells[i].isEmpty()) {
						saved.writeInt(cells[i].peek().getFullNumber());
						saved.writeInt(-1);
					} else {
						saved.writeInt(-1);
					}
				}

				for (int i = 0; i < columns.length; i++) {
					if (!columns[i].isEmpty()) {
						for (int j = 0; j < columns[i].length(); j++) {
							saved.writeInt(columns[i].getCardAtLocation(j).getFullNumber());
						}

						saved.writeInt(-1);
					} else {
						saved.writeInt(-1);
					}
				}

				for (int i = 0; i < acePiles.length; i++) {
					if (!acePiles[i].isEmpty()) {
						for (int j = 0; j < acePiles[i].length(); j++) {
							saved.writeInt(acePiles[i].getCardAtLocation(j).getFullNumber());
						}

						saved.writeInt(-1);
					} else {
						saved.writeInt(-1);
					}
				}

				if (!dealDeck.isEmpty()) {
					for (int j = 0; j < dealDeck.length(); j++) {
						saved.writeInt(dealDeck.getCardAtLocation(j).getFullNumber());
					}

					saved.writeInt(-1);
				} else {
					saved.writeInt(-1);
				}

				if (!discardPile.isEmpty()) {
					for (int j = 0; j < discardPile.length(); j++) {
						saved.writeInt(discardPile.getCardAtLocation(j).getFullNumber());
					}

					saved.writeInt(-1);
				} else {
					saved.writeInt(-1);
				}

				if (timerToRun == true)
					saved.writeInt(timerCount);
				else
					saved.writeInt(-1);
			} else {
				/*
				 * Not saved.
				 */
				output.writeInt(0);
				savedFile.delete();
			}

			output.close();
			saved.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Returns the draw count.
	 * 
	 * @return drawCount Current draw count.
	 * 
	 * @author Todor Balabanov
	 */
	public int getDrawCount() {
		return drawCount;
	}

	/**
	 * Sets draw count.
	 * 
	 * @param draw
	 *            Sets the new draw count.
	 * 
	 * @author Todor Balabanov
	 */
	public void setDrawCount(int draw) {
		drawCount = draw;

		if (drawCount != 3 && drawCount != 1) {
			drawCount = 1;
		}
	}

	/**
	 * Returns the new draw count.
	 * 
	 * @return newDrawCount New draw count.
	 * 
	 * @author Todor Balabanov
	 */
	public int getNewDrawCount() {
		return newDrawCount;
	}

	/**
	 * Sets the new draw count.
	 * 
	 * @param draw
	 *            New draw count to be set.
	 * 
	 * @author Todor Balabanov
	 */
	public void setNewDrawCount(int draw) {
		newDrawCount = draw;

		if (newDrawCount != 3 && newDrawCount != 1) {
			newDrawCount = 1;
		}
	}

	/**
	 * Returns game difficulty.
	 * 
	 * @return difficulty Current game difficulty.
	 * 
	 * @author Todor Balabanov
	 */
	public GameDifficulty getDifficulty() {
		return difficulty;
	}

	/**
	 * Sets game difficulty.
	 * 
	 * @param difficulty
	 *            The new game difficulty.
	 * 
	 * @author Todor Balabanov
	 */
	public void setDifficulty(GameDifficulty difficulty) {
		this.difficulty = difficulty;
	}

	/**
	 * Returns the new difficulty.
	 * 
	 * @return newDifficulty New difficulty.
	 * 
	 * @author Todor Balabanov
	 */
	public GameDifficulty getNewDifficulty() {
		return newDifficulty;
	}

	/**
	 * Sets the new difficulty.
	 * 
	 * @param newDifficulty
	 *            New difficulty to be set.
	 * 
	 * @author Todor Balabanov
	 */
	public void setNewDifficulty(GameDifficulty newDifficulty) {
		this.newDifficulty = newDifficulty;
	}

	/**
	 * Sets the number of times through deck.
	 * 
	 * @param deckThroughs
	 *            Deck throughs to be set.
	 * 
	 * @author Todor Balabanov
	 */
	public void setDeckThroughs(int deckThroughs) {
		dealDeck.setDeckThroughs(deckThroughs);
	}

	/**
	 * Used to undo a move.
	 * 
	 * @author Todor Balabanov
	 */
	public synchronized void undoMove() {
		if (sourceList.isEmpty()) {
			return;
		}

		/*
		 * If player is holding on to a card.
		 */
		if (sourceList.size() > destinationList.size()) {
			CardStack tempSource = sourceList.getLast();
			sourceList.removeLast();

			int num = numCards.getLast();
			numCards.removeLast();

			int numDiscard = numCardsInDiscardView.getLast();
			numCardsInDiscardView.removeLast();

			if (num == 1) {
				discardPile.setView(numDiscard);
				tempSource.peek().unhighlight();
			} else {
				for (int i = 0; i < num; i++) {
					tempSource.getCardAtLocation(tempSource.length() - i - 1).unhighlight();
				}
			}
		} else if (!(sourceList.getLast() instanceof DealDeck)) {
			CardStack tempSource = sourceList.getLast();
			CardStack tempDest = destinationList.getLast();
			int num = numCards.getLast();
			int numDiscard = numCardsInDiscardView.getLast();

			sourceList.removeLast();
			destinationList.removeLast();
			numCards.removeLast();
			numCardsInDiscardView.removeLast();

			if (num == 1) {
				tempSource.addCard(tempDest.pop());
			} else {
				Vector<Card> temp = tempDest.undoStack(num);
				tempSource.addStack(temp);
			}

			discardPile.setView(numDiscard);
		}
		/*
		 * The last draw from the deck didn't reset the discard pile to make it
		 * an empty pile.
		 */
		else if (sourceList.getLast() instanceof DealDeck && !destinationList.getLast().isEmpty()) {
			int num = numCards.getLast();
			int numDiscard = numCardsInDiscardView.getLast();

			sourceList.removeLast();
			destinationList.removeLast();
			numCards.removeLast();
			numCardsInDiscardView.removeLast();

			for (int i = 0; i < num; i++) {
				Card card = discardPile.undoPop();
				card.setFaceDown();
				dealDeck.addCard(card);
			}

			discardPile.setView(numDiscard);
		}
		/*
		 * Last move was a reset on the discard pile.
		 */
		else if (sourceList.getLast() instanceof DealDeck) {
			dealDeck.undoPop();

			int numDiscard = numCardsInDiscardView.getLast();
			discardPile.setView(numDiscard);

			sourceList.removeLast();
			destinationList.removeLast();
			numCards.removeLast();
			numCardsInDiscardView.removeLast();
		}
	}

	/**
	 * Manages the hints.
	 * 
	 * @return Hint structure.
	 * 
	 * @author Todor Balabanov
	 */
	@SuppressWarnings("fallthrough")
	public String[] getHint() {
		CardStack source = null;
		CardStack destination = null;
		CardStack temp = null;

		LinkedList<String> hints = new LinkedList<String>();
		String sourceString = "";

		for (int i = 0; i < 9; i++) {
			switch (i) {
			case 0:
			case 1:
			case 2:
			case 3: {
				source = columns[i];
				sourceString = "Column " + (i + 1);
			}
				break;
			case 4:
			case 5:
			case 6:
			case 7: {
				source = cells[i - 4];
				sourceString = "Cell " + (i - 3);
			}
				break;
			case 8: {
				source = discardPile;
				sourceString = "the Discard Pile";
			}
				break;

			default:
				break;
			}

			if (source != null && !source.isEmpty()) {
				temp = source.getAvailableCards();
				String destinationString = "";

				for (int j = 0; j < 8; j++) {
					switch (j) {
					case 0:
					case 1:
					case 2:
					case 3: {
						destination = columns[j];
						destinationString = "Column " + (j + 1);
					}
						break;
					case 4:
					case 5:
					case 6:
					case 7: {
						destination = acePiles[j - 4];
						destinationString = "its Ace Pile";
					}
						break;

					default:
						break;
					}

					if (destination != null && !destination.isEmpty() && destination != source
							&& !(destination instanceof SingleCell)) {
						for (int k = temp.length() - 1; k >= 0; k--) {
							Card card = temp.getCardAtLocation(k);

							if (((destination instanceof AcePile)
									&& card.getSuit().equals(((AcePile) destination).getSuit())
									&& card.getRank().isLessByOneThan(((AcePile) (destination)).peek().getRank())
									&& k == 0)
									|| (!(destination instanceof AcePile)
											&& card.getColor() != ((AcePile) destination).peek().getColor()
											&& card.getRank()
													.isGreaterByOneThan((((AcePile) destination).peek()).getRank()))) {
								String hintString = "Move the ";

								if (card.getRank().equals(CardRank.JACK)) {
									hintString += "Jack";
								} else if (card.getRank().equals(CardRank.QUEEN)) {
									hintString += "Queen";
								} else if (card.getRank().equals(CardRank.KING)) {
									hintString += "King";
								} else if (card.getRank().equals(CardRank.ACE)) {
									hintString += "Ace";
								} else {
									hintString += card.getRank();
								}

								hintString += " of " + card.getSuit() + " in " + sourceString + " to the ";

								if (((AcePile) (destination)).peek().getRank().equals(CardRank.JACK)) {
									hintString += "Jack";
								} else if (((AcePile) (destination)).peek().getRank().equals(CardRank.QUEEN)) {
									hintString += "Queen";
								} else if (((AcePile) (destination)).peek().getRank().equals(CardRank.KING)) {
									hintString += "King";
								} else if (((AcePile) (destination)).peek().getRank().equals(CardRank.ACE)) {
									hintString += "Ace";
								} else {
									hintString += ((AcePile) (destination)).peek().getRank();
								}

								hintString += " of " + ((AcePile) (destination)).peek().getSuit() + " in "
										+ destinationString;

								hints.add(hintString);
								/*
								 * Once a move is found from a source to
								 * destination, stop looking for more.
								 */
								break;
							}
						}
					} else if (destination != null && destination != source && (destination instanceof Column)
							&& ((Column) destination).isEmpty()
							&& (source.getBottom().getRank().equals(CardRank.KING) == false
									|| source instanceof SingleCell)) {
						for (int k = 0; k < temp.length(); k++) {
							Card card = temp.getCardAtLocation(k);

							if (card.getRank().equals(CardRank.KING)) {
								String hintString = "Move the King of " + card.getSuit() + " in " + sourceString
										+ " to the empty " + destinationString;

								hints.add(hintString);
								/*
								 * Once a move is found from a source to
								 * destination, stop looking for more.
								 */
								break;
							}
						}
					} else if (destination != null && destination != source && (destination instanceof AcePile)
							&& ((AcePile) destination).isEmpty()) {
						Card card = temp.peek();

						if (card.getRank().equals(CardRank.ACE)
								&& card.getSuit().equals(((AcePile) destination).getSuit())) {
							String hintString = "Move the Ace of " + card.getSuit() + " in " + sourceString + " to "
									+ destinationString;

							hints.add(hintString);
							/*
							 * Once a move is found from a source to
							 * destination, stop looking for more.
							 */
							break;
						}
					}
				}
			}
		}

		for (int i = 0; i < cells.length; i++) {
			if (cells[i].isEmpty() == true) {
				String hintString = "Move any available card to Cell " + (i + 1);
				hints.add(hintString);
			}
		}

		String hint[] = { "", "" };
		if (hints.isEmpty() == false) {
			for (int i = 0; i < hints.size(); i++) {
				hint[0] += hints.get(i) + "\n";
			}
			hint[1] = "Hints Galore";
		} else {
			hint[0] = "There are no moves on the field.\n" + "Either deal more cards or start a new game";
			hint[1] = "Hints";
		}

		return (hint);
	}

	/**
	 * Try to move selected card to its ace pile.
	 * 
	 * @param index
	 *            Index of the ace pile to be used.
	 * @param numberOfSelectedCards
	 *            Number of cards to be moved.
	 * 
	 * @author Todor Balabanov
	 */
	public void moveToAces(int index, int numberOfSelectedCards) {
		if (numberOfSelectedCards > 1) {
			return;
		}

		CardStack stack = null;
		for (int i = 0; i < cells.length; i++) {
			if (cells[i].isEmpty() == true) {
				continue;
			}

			if (cells[i].peek().isHighlighted() == true) {
				stack = cells[i];
			}
		}
		for (int i = 0; i < columns.length; i++) {
			if (columns[i].isEmpty() == true) {
				continue;
			}
			if (columns[i].peek().isHighlighted() == true) {
				stack = columns[i];
			}
		}
		if (discardPile.isEmpty() == false && discardPile.peek().isHighlighted() == true) {
			stack = discardPile;
		}

		if (stack == null) {
			return;
		}
		if (stack == acePiles[index]) {
			return;
		}

		/*
		 * The card is Ace and should be collected in Ace Pile.
		 */
		if (stack.peek().getRank().equals(CardRank.ACE) == true
				&& stack.peek().getSuit().equals(acePiles[index].getSuit()) == true) {
			Card card = stack.pop();
			acePiles[index].push(card);

			return;
		}

		/*
		 * The card is not Ace and should be collected in Ace Pile only if suite
		 * and rank are matching with Ace Pile state.
		 */
		if (acePiles[index].isEmpty() == true) {
			return;
		}
		if (stack.peek().getSuit().equals(acePiles[index].getSuit()) == false) {
			return;
		}
		if (stack.peek().getRank().isLessByOneThan((acePiles[index].peek().getRank())) == false) {
			return;
		}

		Card card = stack.pop();
		acePiles[index].push(card);
	}

	/**
	 * Try to move selected card to one of the single cells.
	 * 
	 * @param index
	 *            Index of the single cell to be used.
	 * @param numberOfSelectedCards
	 *            Number of cards to be moved.
	 * 
	 * @author Todor Balabanov
	 */
	public void moveToCells(int index, int numberOfSelectedCards) {
		if (numberOfSelectedCards > 1) {
			return;
		}

		/*
		 * Single cells can hold only single card.
		 */
		if (cells[index].isEmpty() == false) {
			return;
		}

		CardStack stack = null;
		for (int i = 0; i < acePiles.length; i++) {
			if (acePiles[i].isEmpty() == true) {
				continue;
			}

			if (acePiles[i].peek().isHighlighted() == true) {
				stack = acePiles[i];
			}
		}
		for (int i = 0; i < cells.length; i++) {
			if (cells[i].isEmpty() == true) {
				continue;
			}

			if (cells[i].peek().isHighlighted() == true) {
				stack = cells[i];
			}
		}
		for (int i = 0; i < columns.length; i++) {
			if (columns[i].isEmpty() == true) {
				continue;
			}
			if (columns[i].peek().isHighlighted() == true) {
				stack = columns[i];
			}
		}
		if (discardPile.isEmpty() == false && discardPile.peek().isHighlighted() == true) {
			stack = discardPile;
		}

		if (stack == null) {
			return;
		}
		if (stack == cells[index]) {
			return;
		}

		Card card = stack.pop();
		cells[index].push(card);
	}

	/**
	 * Try to move selected card to one of the columns.
	 * 
	 * @param index
	 *            Index of the column to be used.
	 * @param numberOfSelectedCards
	 *            Number of cards to be moved.
	 * 
	 * @author Todor Balabanov
	 */
	public void moveToColumns(int index, int numberOfSelectedCards) {
		CardStack stack = null;
		for (int i = 0; i < acePiles.length; i++) {
			if (acePiles[i].isEmpty() == true) {
				continue;
			}

			if (acePiles[i].peek().isHighlighted() == true) {
				stack = acePiles[i];
			}
		}
		for (int i = 0; i < cells.length; i++) {
			if (cells[i].isEmpty() == true) {
				continue;
			}

			if (cells[i].peek().isHighlighted() == true) {
				stack = cells[i];
			}
		}
		for (int i = 0; i < columns.length; i++) {
			if (columns[i].isEmpty() == true) {
				continue;
			}
			if (columns[i].peek().isHighlighted() == true) {
				stack = columns[i];
			}
		}
		if (discardPile.isEmpty() == false && discardPile.peek().isHighlighted() == true) {
			stack = discardPile;
		}

		if (stack == null) {
			return;
		}
		if (stack == columns[index]) {
			return;
		}

		if (stack instanceof AcePile || stack instanceof SingleCell || stack instanceof DiscardPile
				|| (stack instanceof Column && numberOfSelectedCards == 1)) {
			Card card = stack.peek();

			if (columns[index].isValidMove(card) == true) {
				card = stack.pop();
				columns[index].push(card);
			}
		} else if (stack instanceof Column && numberOfSelectedCards > 1) {
			boolean valid = false;
			Column a = (Column) stack;
			Column b = columns[index];
			for (int i = 0; i < a.length(); i++) {
				if (a.getCardAtLocation(i).isHighlighted() == true) {
					if (b.isValidMove(a.getCardAtLocation(i)) == true) {
						valid = true;
					}
					break;
				}
			}
			if (valid == true) {
				for (int i = 0; i < a.length(); i++) {
					if (a.getCardAtLocation(i).isHighlighted() == true) {
						b.push(a.getCardAtLocation(i));
					}
				}
				for (int i = a.length() - 1; i >= 0; i--) {
					if (a.getCardAtLocation(i).isHighlighted() == true) {
						a.pop();
					}
				}
			}
		}
	}

	/**
	 * Check is the solitaire solved.
	 * 
	 * @return True if the solitaire is solved, false otherwise.
	 * 
	 * @author Todor Balabanov
	 */
	public boolean isSolved() {
		// TODO Check much better for final solution.
		for (int i = 0; i < acePiles.length; i++) {
			if (acePiles[i].isFull() == false) {
				return false;
			}
		}

		return true;
	}
}
