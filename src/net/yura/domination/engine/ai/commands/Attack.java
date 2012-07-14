package net.yura.domination.engine.ai.commands;

import net.yura.domination.engine.core.AbstractCountry;
import net.yura.domination.engine.core.AbstractPlayer;
import net.yura.domination.engine.core.AbstractRiskGame;

public class Attack implements Command {
	
	private final AbstractCountry<?,?,?> origin;
	private final AbstractCountry<?,?,?> destination;
	
	public Attack(AbstractCountry<?,?,?> country, AbstractCountry<?,?,?> neighbour) {
		this.origin = country;
		this.destination = neighbour;
		
		
	}
	
	public AbstractCountry<?,?,?> getOrigin() {
		return this.origin;
	}
	
	public AbstractCountry<?,?,?> getDestination() {
		return this.destination;
	}
	
	@Override
	public String toString() {
		return String.format("attack %d %d", origin.getColor(), destination.getColor());
	}

	@Override
	public String toCommand(AbstractRiskGame<?, ?, ?> game, AbstractPlayer<?> player) {
		if(origin == null || destination == null)
			throw new NullPointerException("Origin or destination countries cannot be null!");
		
		if(origin.getOwner() != player)
			throw new IllegalArgumentException("You can attack only from your own territories (was " + origin.getName() + ")");
		
		if(destination.getOwner() == player)
			throw new IllegalArgumentException("You cannot attack your own territories (was " + destination.getName() + ")");
		
		return toString();
	}
}