package net.yura.domination.engine.core;

import java.util.Vector;

public interface AbstractCountry<P extends AbstractPlayer<C>, C extends AbstractCountry<P,C,N>, N extends AbstractContinent<P, C>> {

	/**
	 * gets the countries neighbours
	 * @return a vector of the countries ceighbours
	 */
	public abstract Vector<C> getNeighbours();

	/**
	 * Returns the country name
	 * @return name
	 */
	public abstract String getName();

	/**
	 * Returns the armies stationed within the country
	 * @return armies
	 */
	public abstract int getArmies();

	/**
	 * Returns the Continent of the country
	 * @return continent
	 */
	public abstract N getContinent();

	/**
	 * Gets and returns the owner of the country
	 * @return owner
	 */
	public abstract P getOwner();

	/**
	 * Returns the colour (unique) of the country
	 * starting from 1, NOT FROM ZERO
	 * @return color
	 */
	public abstract int getColor();

}