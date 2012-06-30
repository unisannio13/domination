package net.yura.domination.engine.ai.commands;

import net.yura.domination.engine.core.AbstractPlayer;
import net.yura.domination.engine.core.AbstractRiskGame;
import net.yura.domination.engine.core.Country;

public class Move implements Command {
	private final Country origin;
	private final Country destination;
	private final int armies;
	
	public Move(Country origin, Country destination, int armies) {
		this.origin = origin;
		this.destination = destination;
		this.armies = armies;
		
	}
	
	public Country getOrigin() {
		return this.origin;
	}
	
	public Country getDestination() {
		return this.destination;
	}
	
	public int getArmies() {
		return this.armies;
	}
	
	@Override
	public String toCommand(AbstractRiskGame<?, ?, ?> game,
			AbstractPlayer<?> player) {

		if(origin == null || destination == null)
			throw new NullPointerException("Origin or destination countries cannot be null!");
		
		if(origin.getOwner() != player || destination.getOwner() != player)
			throw new IllegalArgumentException("You can move only between your countries (was " + origin.getName() + "-->" + destination.getName() + ")");
		
		if(!origin.getNeighbours().contains(destination))
			throw new IllegalArgumentException("Nations " + origin + " and " + destination + " are not neighbours: cannot move armies!");
		if(armies < origin.getArmies() - 1)
			throw new IllegalArgumentException("You can move at most " + (origin.getArmies() - 1) + " armies from " + origin.getName() + " (was " + armies + ")");
			
		return toString();
	}
	
	@Override
	public String toString() {
		return String.format("movearmies %d %d %d", origin.getColor(), destination.getColor(), armies);
	}
}