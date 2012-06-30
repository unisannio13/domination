package net.yura.domination.engine.ai.commands;

import net.yura.domination.engine.core.AbstractPlayer;
import net.yura.domination.engine.core.AbstractRiskGame;
import net.yura.domination.engine.core.Card;
import net.yura.domination.engine.core.Country;

public class Trade implements Comparable<Trade>, Command {	
	private final Card card1;
	private final Card card2;
	private final Card card3;
	
	private final AbstractRiskGame<?,?,?> game;
	private final AbstractPlayer<?> player;
	
	public Trade(AbstractRiskGame<?,?,?> game, AbstractPlayer<?> player, Card card1, Card card2, Card card3) {
		this.card1 = card1;
		this.card2 = card2;
		this.card3 = card3;
		
		this.game = game;
		this.player = player;
		
		if(card1 == null || card2 == null || card3 == null)
			throw new NullPointerException("Cards cannot be null!");
		
		if(!game.checkTrade(card1, card2, card3))
			throw new IllegalArgumentException("Invalid trade: " + card1 + ", " + card2 + ", " + card3);
		
		if(card1 == card2 || card2 == card3 || card1 == card3)
			throw new IllegalArgumentException("Cannot trade the same card twice (was " + card1 + ", " + card2 + ", " + card3 + ")!");
	}
	
	public Card[] getCards() {
		return new Card[] {card1, card2, card3};
	}
	
	@Override
	public String toCommand(AbstractRiskGame<?, ?, ?> game,
			AbstractPlayer<?> player) {
		return toString();
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		for(Card c : getCards()) {
			if(c.getName().equals(Card.WILDCARD))
				buf.insert(0, " wildcard");
			else
				buf.append(' ').append(c.getCountry().getColor());
		}
		
		return buf.insert(0, "trade").toString();
	}

	public int getValue() {
		int value = game.getTradeAbsValue(card1.getName(), card2.getName(), card3.getName(), game.getCardMode());
		for(Card c : getCards()) {
			Country country = c.getCountry();
			if(country != null && country.getOwner() == player)
				value += 2;
		}
		
		return value;
	}
	
	@Override
	public int compareTo(Trade other) {
		return getValue() - other.getValue();
	}
}