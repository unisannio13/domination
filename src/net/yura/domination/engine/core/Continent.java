// Yura Mamyrin, Group D

package net.yura.domination.engine.core;

import java.io.Serializable;
import java.util.Vector;


/**
 * <p> Continent </p>
 * @author Yura Mamyrin
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Continent implements Serializable, AbstractContinent<Player, Country> {

	private static final long serialVersionUID = 1L;

	private String idString; // used by the map editor

	private String name;
	private int color;
	private int armyValue;
	private Vector territoriesContained = new Vector();

	/**
	 * Creates a continent object
	 * @param n the continent name
	 * @param c the continent colour
	 * @param noa the number of armies recieved when the whole continent is owned
	 */
	public Continent(String id, String n, int noa, int c) {

		idString = id;

		name		= n;
		color		= c;
		armyValue	= noa;

	}

	public String toString() {

		if (armyValue!=0) {

			return idString+" ["+armyValue+"]";
		}

		return idString;

	}

	public String getIdString() {

		return idString;

	}

	public void setIdString(String a) {

		idString = a;

	}

	/**
	 * Returns the name of the continent
	 * @return name
	 */
	public String getName() {
		return name;
	}

	public void setName(String a) {
		name = a;
	}

	/**
	 * Returns the colour of the continent
	 * @return color
	 */
	public int getColor() {
		return color;
	}

	public void setColor(int a) {
		color = a;
	}

	/**
	 * Returns the number of armies the continent is worth
	 * @return armyValue
	 */
	public int getArmyValue() {
		return armyValue;
	}

	public void setArmyValue(int a) {
		armyValue = a;
	}

	/**
	 * Checks if the player owns all the territories within a continent
	 * @param p player object
	 * @return true if the player owns all the territories within a continent,
	 * otherwise false if the player does not own the all the territories
	 */
	public boolean isOwned(Player p) {

		int ownedByPlayer=0;

		for (int c=0; c< territoriesContained.size() ; c++) {

			if ( ((Country)territoriesContained.elementAt(c)).getOwner() == p ) {
				ownedByPlayer++;
			}

		}

		if ( ownedByPlayer==territoriesContained.size() ) {
			return true;
		}
		else {
			return false;
		}

	}

	/*****
	 * @name getOwner
	 * @return player who owns the continent
	 *  else null if no one owns the continent
	 ****/
	public Player getOwner(){
		Player owner;
		owner = ((Country)territoriesContained.elementAt(0)).getOwner();
		for (int c=1; c< territoriesContained.size() ; c++) {
			if ( ((Country)territoriesContained.elementAt(c)).getOwner() != owner ) {
				owner = null;
				break;
			}
		}
		return owner;
	}

	/**
	 * Returns a vector of the territories contained within a continent
	 * @return territoriesContained
	 */
	public Vector<Country> getTerritoriesContained() {
		return territoriesContained;
	}

	/**
	 * Adds country to a continent
	 * @param t the country object
	 */
	public void addTerritoriesContained(Country t) {
		territoriesContained.add(t);
	}

	public boolean equals(Object o) {

		return (o != null &&
			o instanceof Continent &&
			((Continent)o).name.equals(name) &&
			((Continent)o).idString.equals(idString) &&
			((Continent)o).armyValue == armyValue
		);

	}

}
