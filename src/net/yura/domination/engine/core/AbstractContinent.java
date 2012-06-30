package net.yura.domination.engine.core;

import java.util.Vector;

public interface AbstractContinent<P extends AbstractPlayer, C extends AbstractCountry> {

	public abstract String getIdString();

	/**
	 * Returns the name of the continent
	 * @return name
	 */
	public abstract String getName();

	/**
	 * Returns the colour of the continent
	 * @return color
	 */
	public abstract int getColor();

	/**
	 * Returns the number of armies the continent is worth
	 * @return armyValue
	 */
	public abstract int getArmyValue();

	/**
	 * Checks if the player owns all the territories within a continent
	 * @param p player object
	 * @return true if the player owns all the territories within a continent,
	 * otherwise false if the player does not own the all the territories
	 */
	public abstract boolean isOwned(P player);

	/*****
	 * @name getOwner
	 * @return player who owns the continent
	 *  else null if no one owns the continent
	 ****/
	public abstract Player getOwner();

	/**
	 * Returns a vector of the territories contained within a continent
	 * @return territoriesContained
	 */
	public abstract Vector<C> getTerritoriesContained();

}