package net.yura.domination.engine.core;

import java.util.Vector;

import net.yura.domination.engine.ai.api.AI;

public interface AbstractPlayer<C extends AbstractCountry<? extends AbstractPlayer<C>, C, ? extends AbstractContinent<? extends AbstractPlayer<C>, C>>> {

	public abstract int getNoArmies();

	/**
	 * Gets the name of the player
	 * @return String The name of the player
	 */
	public abstract String getName();

	/**
	 * Gets the color of the player
	 * @return Color Returns the color of the player
	 */
	public abstract int getColor();

	/**
	 * Gets the number of extra armies that the player has
	 * @return int Returns the number of extra armies that the player has
	 */
	public abstract int getExtraArmies();

	/**
	 * Gets the cards that the player owns
	 * @return Vector Returns the vector of all the cards the player owns
	 */
	public abstract Vector<Card> getCards();

	/**
	 * Gets the captical that the player chose
	 * @return Country Returns the player's capital country
	 */
	public abstract C getCapital();

	/**
	 * Gets the player's mission
	 * @return Mission Returns player's mission
	 */
	public abstract Mission getMission();

	/**
	 * Gets the countries that the player owns
	 * @return Vector Returns a vector of all the countries the player owns
	 */
	public abstract Vector<C> getTerritoriesOwned();

	/**
	 * Gets the type of player
	 * @return int Returns the type of player
	 */
	public abstract AI getAI();

	public abstract boolean getAutoEndGo();

	public abstract boolean getAutoDefend();

	public abstract int getTerritoriesOwnedSize();

}