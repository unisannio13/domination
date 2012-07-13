package net.yura.domination.engine.core;

import java.util.Vector;

public interface AbstractRiskGame<P extends AbstractPlayer<C>, C extends AbstractCountry<P, C, N>, N extends AbstractContinent<P, C>> {

	/**
	 * Returns the trading value of the given cards, without taking into account
	 * the territories associated to the cards.
	 * @param c1 The name of the type of the first card.
	 * @param c2 The name of the type of the second card.
	 * @param c3 The name of the type of the third card.
	 * @return 0 in case of invalid combination of cards.
	 */
	public abstract int getTradeAbsValue(String c1, String c2, String c3,
			int cardMode);

	public abstract boolean canTrade();

	/**
	 * Checks if there are any empty countries
	 * @return boolean Return trues if no empty countries, returns false otherwise
	 */
	public abstract boolean NoEmptyCountries();

	/**
	 * get the value of the trade-cap
	 * @return boolean Return trues if tradecap is true and false otherwise
	 */
	public abstract boolean getTradeCap();

	/**
	 * Gets the current player
	 * @return player Return the current player
	 */
	public abstract P getCurrentPlayer();

	/**
	 * Gets all the players
	 * @return Vector Return all the players
	 */
	public abstract Vector<P> getPlayers();

	/**
	 * Gets the attacking country
	 * @return Country the attacking country
	 */
	public abstract C getAttacker();

	public abstract Vector<Card> getCards();

	/**
	 * Gets the number of continents which are owned by a player
	 * @param p The player you want to find continents for
	 * @return int Return the number of continents a player owns
	 */
	public abstract int getNumberContinentsOwned(P player);

	/**
	 * returns the country with the given color (ID)
	 */
	public abstract C getCountryInt(int color);

	/**
	 * Gets a cards
	 * @param name
	 * @return Card Return the card you are looking for, if it exists. Otherwise returns null
	 */
	public abstract Card[] getCards(String name1, String name2, String name3);

	public abstract Card findCard(String name);

	/**
	 * Gets the number of players in the game
	 * @return int Return the number of number of players
	 */
	public abstract int getNoPlayers();

	/**
	 * Gets the countries in the game
	 * @return Vector Return the Countries in the current game
	 */
	public abstract C[] getCountries();

	/**
	 * Gets the continents in the game
	 * @return Vector Return the Continents in the current game
	 */
	public abstract N[] getContinents();

	/**
	 * Gets the number of countries in the game
	 * @return int Return the number of countries in the current game
	 */
	public abstract int getNoCountries();

	public abstract int getNoContinents();

	/**
	 * Gets the allocated Missions in the game
	 * @return Vector Return the Missions in the current game
	 */
	public abstract Vector<Mission> getMissions();

	/**
	 * Gets the number of Missions in the game
	 * @return int Return the number of Missions in the game
	 */
	public abstract int getNoMissions();

	public abstract int getNoCards();

	/**
	 * @return the current Card Mode
	 */
	public abstract int getCardMode();

	public abstract boolean checkTrade(Card card1, Card card2, Card card3);

	public abstract C getDefender();

	public abstract int getMaxDefendDice();

	public abstract boolean getSetup();

	public abstract int getMustMove();

}