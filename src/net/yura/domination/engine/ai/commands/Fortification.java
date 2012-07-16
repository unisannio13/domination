package net.yura.domination.engine.ai.commands;

import net.yura.domination.engine.core.AbstractCountry;
import net.yura.domination.engine.core.AbstractPlayer;
import net.yura.domination.engine.core.AbstractRiskGame;
import net.yura.domination.engine.core.Country;

public class Fortification implements Command {
	private final AbstractCountry<?,?,?> country;
	private final int armies;
	
	public Fortification(AbstractCountry<?, ?, ?> country2, int armies) {
		this.country = country2;
		this.armies = armies; 
	}
	
	@Override
	public String toCommand(AbstractRiskGame<?, ?, ?> game,
			AbstractPlayer<?> player) {

		if(country == null)
			throw new NullPointerException("Country cannot be null!");
		
		if(country.getOwner() != player)
			throw new IllegalArgumentException("Cannot fortify a nation not owned by player: " + country.getName() + " [#" + country.getColor() + "]");
		
		if(country.getArmies() < 1)
			throw new IllegalArgumentException("At least one army must be placed on the country " + country.getName() + " [#" + country.getColor() + "] (was " + armies + ")");
	
		if(armies > player.getExtraArmies())
			throw new IllegalArgumentException("Trying to place " + armies + " armies on " + country.getName() + " while player has only " + player.getExtraArmies() + " left");
	
		return String.format("placearmies %d %d", country.getColor(), armies);
	}
	
	public AbstractCountry<?,?,?> getCountry() {
		return country;
	}
	
	public int getArmies() {
		return armies;
	}
	
	@Override
	public String toString() {
		return String.format("placearmies %d %d", country.getColor(), armies);
	}
}