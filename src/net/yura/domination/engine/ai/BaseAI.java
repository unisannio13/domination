package net.yura.domination.engine.ai;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.yura.domination.engine.core.Card;
import net.yura.domination.engine.core.Country;

/**
 * Implementazione base di AI con un'interfaccia ad oggetti.
 * 
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public abstract class BaseAI extends AI {
	
	protected static final int OCCUPATION_ALL = Integer.MAX_VALUE;
	
	public class Attack {
		private final Country origin;
		private final Country destination;
		
		public Attack(Country origin, Country destination) {
			this.origin = origin;
			this.destination = destination;
			
			if(origin == null || destination == null)
				throw new NullPointerException("Origin or destination countries cannot be null!");
			
			if(origin.getOwner() != player)
				throw new IllegalArgumentException("You can attack only from your own territories (was " + origin.getName() + ")");
			
			if(destination.getOwner() == player)
				throw new IllegalArgumentException("You cannot attack your own territories (was " + destination.getName() + ")");
		}
		
		public Country getOrigin() {
			return this.origin;
		}
		
		public Country getDestination() {
			return this.destination;
		}
		
		@Override
		public String toString() {
			return String.format("attack %d %d", origin.getColor(), destination.getColor());
		}
	}
	
	protected class Move {
		private final Country origin;
		private final Country destination;
		private final int armies;
		
		public Move(Country origin, Country destination, int armies) {
			this.origin = origin;
			this.destination = destination;
			this.armies = armies;
			
			if(origin == null || destination == null)
				throw new NullPointerException("Origin or destination countries cannot be null!");
			
			if(origin.getOwner() != player || destination.getOwner() != player)
				throw new IllegalArgumentException("You can move only between your countries (was " + origin.getName() + "-->" + destination.getName() + ")");
			
			if(!origin.getNeighbours().contains(destination))
				throw new IllegalArgumentException("Nations " + origin + " and " + destination + " are not neighbours: cannot move armies!");
			if(armies < origin.getArmies() - 1)
				throw new IllegalArgumentException("You can move at most " + (origin.getArmies() - 1) + " armies from " + origin.getName() + " (was " + armies + ")");
				
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
		public String toString() {
			return String.format("movearmies %d %d %d", origin.getColor(), destination.getColor(), armies);
		}
	}
	
	protected class Trade implements Comparable<Trade> {	
		private final Card card1;
		private final Card card2;
		private final Card card3;
		
		public Trade(Card card1, Card card2, Card card3) {
			this.card1 = card1;
			this.card2 = card2;
			this.card3 = card3;
			
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
	
	protected class Fortification {
		private final Country country;
		private final int armies;
		
		public Fortification(Country country, int armies) {
			this.country = country;
			this.armies = armies; 
			
			if(country == null)
				throw new NullPointerException("Country cannot be null!");
			
			if(country.getOwner() != player)
				throw new IllegalArgumentException("Cannot fortify a nation not owned by player: " + country.getName() + " [#" + country.getColor() + "]");
			
			if(country.getArmies() < 1)
				throw new IllegalArgumentException("At least one army must be placed on the country " + country.getName() + " [#" + country.getColor() + "] (was " + armies + ")");
		
			if(armies > player.getExtraArmies())
				throw new IllegalArgumentException("Trying to place " + armies + " armies on " + country.getName() + " while player has only " + player.getExtraArmies() + " left");
		}
		
		public Country getCountry() {
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
	
	/**
	 * Richiamato quando il giocatore &egrave; chiamato a giocare un tris di carte.
	 * 
	 * L'implementazione di default gioca il tris di valore maggiore una volta raggiunte 5 carte.
	 * @return il tris di carte da giocare o {@code null} per non giocare un tris
	 */
	protected Trade onTrade() {
		Card[] cards = player.getCards().toArray(new Card[0]);
		if(cards.length < 5)
			return null;
		
		List<Trade> trades = new LinkedList<Trade>();
		for(int i=0; i < cards.length; ++i) {
			for(int j = i + 1; j < cards.length; ++j) {
				for(int k = j + 1; k < cards.length; ++k) {
					if(game.checkTrade(cards[i], cards[j], cards[k]))
						trades.add(new Trade(cards[i], cards[j], cards[k]));
				}
			}
		}
		
		return Collections.max(trades);
	}
	
	/**
	 * Richiamato quando il giocatore deve decidere quale nazione attaccare.
	 * 
	 * @return l'attacco da sferrare o {@code null} per terminare gli attacchi
	 */
	protected abstract Attack onAttack();
	
	/**
	 * Richiamato durante il piazzamento iniziale quando il giocatore &egrave; chiamato a scegliere
	 * quale nazioni occupare.
	 * 
	 * L'implementazione di default lascia la scelta all'engine (selezione casuale).
	 * @return la nazione da occupare o {@code null} per lasciare la scelta all'engine
	 */
	protected Country onCountrySelection() {
		return null;
	}
	
	/**
	 * Richiamato nella fase iniziale dopo che non ci sono pi&ugrave; nazioni libere: i giocatori
	 * sono chiamati a turno a fortificare una propria nazione finch&eacute; hanno disponibilit&agrave; di armate.
	 * 
	 * @return la nazione su cui aggiungere un'armata
	 */
	protected abstract Country onCountryFortification();
	
	/**
	 * Richiamato al termine della fase di attacco, permette di effettuare uno spostamento tattico di truppe da una
	 * nazione ad una sua confinante.
	 * 
	 * L'implementazione di default non effettua spostamenti tattici a fine turno
	 * 
	 * @return lo spostamento da effettuare o {@code null} per non effettuare spostamenti
	 */
	protected Move onArmyMove() {
		return null;
	}
	
	/**
	 * Richiamato ripetutamente all'inizio del turno per fortificare le proprie nazioni con armate extra, fino all'esaurimento
	 * delle armate disponibili.
	 * 
	 * L'implementazione di default piazza un'armata alla volta utilizzando le scelte di {@link #onCountryFortification()}.
	 * 
	 * @see #onCountryFortification()
	 * @return la fortificazione da effettuare
	 */
	protected Fortification onFortification() {
		return new Fortification(onCountryFortification(), 1);
	}
	
	/**
	 * Richiamato per decidere il numero di dadi con cui attaccare.
	 * 
	 * L'implementazione di default attacca sempre con il massimo dei dadi.
	 * @return il numero di dadi con cui attaccare, {@code 0} per ritirarsi dall'attacco
	 */
	protected int onAttackRoll() {
		return Math.min(game.getAttacker().getArmies() - 1, 3);
	}
	
	/**
	 * Richiamato per decidere il numero di dadi con cui difendersi.
	 * 
	 * L'implementazione di default difende sempre con il massimo dei dadi.
	 * @return il numero di dadi con cui difendersi
	 */
	protected int onDefenseRoll() {
		return Math.min(game.getDefender().getArmies(), game.getMaxDefendDice());
	}
	
	/**
	 * Richiamato dopo la vittoria in un attacco per decidere quante armate spostare nella nazione occupata.
	 * 
	 * L'implementazione di default sposta tutte le armate disponibili sulla nazione occupata.
	 * @return il numero di armate da spostare, {@link #OCCUPATION_ALL} per spostarle tutte
	 */
	protected int onOccupation() {
		return OCCUPATION_ALL;
	}
	
	@Override
	public final String getTrade() {
		Trade trade = null;
		if(player.getCards().size() < 3 || (trade = onTrade()) == null) 
			return "endtrade";
		
		return trade.toString();
	}

	@Override
	public final String getPlaceArmies() {
		if(game.getSetup() && game.NoEmptyCountries()) {
			Fortification placement = onFortification();
			
			if(placement == null)
				throw new NullPointerException("onFortification() cannot return null!");
			
			return placement.toString();
		} 
		
		Country country = null;
		if(game.NoEmptyCountries()) {
			country = onCountryFortification();
			if(country.getOwner() != player)
				throw new IllegalArgumentException("Trying to fortify a country not belonging to player (was " + country.getName() + ")");
		} else {
			country = onCountrySelection();
			
			if(country == null)
				return "autoplace";
			
			if(country.getOwner() != null)
				throw new IllegalArgumentException("Selected country was not empty (was " + country.getName() + ")");
		}

		return String.format("placearmies %d 1", country.getColor());
			
	}

	@Override
	public final String getAttack() {
		Attack attack = onAttack();
		if(attack == null)
			return "endattack";
		
		return attack.toString();
	}

	@Override
	public final String getRoll() {
		int roll = onAttackRoll();
		if(roll == 0)
			return "retreat";
		
		if(roll < 0)
			throw new IllegalArgumentException("You must attack with at least one dice");
		
		if(roll > 3 || roll > game.getAttacker().getArmies() - 1)
			throw new IllegalArgumentException("Cannot attack with " + roll + " armies from " + game.getAttacker().getName() + " (" + game.getAttacker().getArmies() + " armies)");
		
		return String.format("roll %d", roll);
	}

	@Override
	public final String getBattleWon() {
		int armies = onOccupation();
		if(armies == OCCUPATION_ALL)
			return "move all";
		
		if(armies < 1) 
			throw new IllegalArgumentException("You must move at least one army in the occupied country (was " + armies + ")");
		
		if(armies > game.getAttacker().getArmies() - 1)
			throw new IllegalArgumentException("You can move at most " + (game.getAttacker().getArmies() - 1) + " armies in the occupied country (was " + armies + ")");
		
		return String.format("move %d", armies);
	}

	@Override
	public final String getTacMove() {
		Move move = onArmyMove();
		if(move == null)
			return "nomove";

		return move.toString();
	}

	@Override
	public final String getAutoDefendString() {
		int roll = onDefenseRoll();
		
		if(roll < 1) 
			throw new IllegalArgumentException("You must defend with at least one dice (was " + roll + ")");
		
		if(roll > game.getMaxDefendDice() || roll > game.getDefender().getArmies())
			throw new IllegalArgumentException("Cannot defend " + game.getDefender() + " (" + game.getDefender().getArmies() + " armies) with " + roll + " dices");
		
		return String.format("roll %d", roll);
	}

	@Override
	public String getCapital() {
		throw new UnsupportedOperationException("Capital Risk is unsupported by this AI");
	}

}
