// Yura Mamyrin, Group D

package net.yura.domination.engine.core;

//import java.awt.Color; // not on android
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import net.yura.domination.engine.Risk;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.engine.ai.api.AI;
import net.yura.domination.engine.ai.api.EnemyCommandsEventSource;
import net.yura.domination.engine.ai.api.EnemyCommandsListener;
import net.yura.domination.engine.translation.MapTranslator;
import net.yura.domination.logger.RiskLogger;

/**
 * <p> Risk Game Main Class </p>
 * @author Yura Mamyrin
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class RiskGame implements Serializable, AbstractRiskGame<Player, Country, Continent> { // transient

	private static final long serialVersionUID = 7L;

	public final static String SAVE_VERSION = String.valueOf(serialVersionUID);
	public final static String NETWORK_VERSION = "8";

	public final static int MAX_PLAYERS = 6;
	public final static Continent ANY_CONTINENT = new Continent("any","any", 0, 0);

	public final static int STATE_NEW_GAME        = 0;
	public final static int STATE_TRADE_CARDS     = 1;
	public final static int STATE_PLACE_ARMIES    = 2;
	public final static int STATE_ATTACKING       = 3;
	public final static int STATE_ROLLING         = 4;
	public final static int STATE_BATTLE_WON      = 5;
	public final static int STATE_FORTIFYING      = 6;
	public final static int STATE_END_TURN        = 7;
	public final static int STATE_GAME_OVER       = 8;
	public final static int STATE_SELECT_CAPITAL  = 9;
	public final static int STATE_DEFEND_YOURSELF = 10;


	public final static int MODE_DOMINATION     = 0;
	public final static int MODE_CAPITAL        = 2;
	public final static int MODE_SECRET_MISSION = 3;


	public final static int CARD_INCREASING_SET = 0;
	public final static int CARD_FIXED_SET = 1;
	public final static int CARD_ITALIANLIKE_SET = 2;

/*

//	public final static int MODE_DOMINATION_2   = 1;

gameState:

nogame	(-1 in gui)		(current possible commands are: newgame, loadgame, closegame, savegame)

9 - select capital		(current possible commands are: capital)
10 - defend yourself!		(current possible commands are: roll)
0 - new game just created	(current possible commands are: newplayer, delplayer, startgame)
1 - trade cards			(current possible commands are: showcards, trade, notrade)
2 - placing new armies		(current possible commands are: placearmies, autoplace)
3 - attacking 			(current possible commands are: attack endattack)
4 - rolling			(current possible commands are: roll retreat)
5 - you have won!		(current possible commands are: move)
6 - fortifying			(current possible commands are: movearmy nomove)
7 - endturn			(current possible commands are: endgo)
8 - game is over		(current possible commands are: continue)

gameMode:
0 - WORLD DOMINATION RISK	- 3 to 6 players
//1 - WORLD DOMINATION RISK	- 2 player
2 - CAPITAL RISK		- 3 to 6 players
3 - SECRET MISSION RISK	- 3 to 6 players

playerType:
0 - human
1 - AI (Easy)
2 - AI (Hard)
3 - AI (Crap)

transient - A keyword in the Java programming language that indicates that a field is not part of the serialized form of an object. When an object is serialized, the values of its transient fields are not included in the serial representation, while the values of its non-transient fields are included.

*/

	private static String defaultMap;
	private static String defaultCards;












	// ---------------------------------------
	// THIS IS THE GAME INFO FOR Serialization
	// ---------------------------------------

	private Random r; // mmm, not sure where this should go, may stop cheeting when its here

	// cant use URL as that stores full URL to the map file on the disk,
	// and if the risk install dir changes the saves dont work
	private String mapfile;
	private String cardsfile;
	private int setup;

	private Vector Players;
	private Country[] Countries;
	private Continent[] Continents;
	private Vector Cards;
	private Vector Missions;

	private Player currentPlayer;
	private int gameState;
	private int cardState;
	private int mustmove;
	private boolean capturedCountry;
	private boolean tradeCap;
	private int gameMode;

	private Country attacker;
	private Country defender;

	private int attackerDice;
	private int defenderDice;

	private String ImagePic;
	private String ImageMap;
	private String previewPic;
        
	private String mapName;
        private int ver;

	private Vector replayCommands;
	private int maxDefendDice;
	private int cardMode;

	private boolean runmaptest=false;
	private boolean recycleCards=false;


	/**
	 * Creates a new RiskGame
	 */
	public RiskGame() throws Exception {

		//try {

			setMapfile("default");
			setCardsfile("default");
		//}
		//catch (Exception e) {
		//	RiskUtil.printStackTrace(e);
		//}

		setup=0; // when setup reaches the number of players it goes into normal mode

		Players = new Vector();

		currentPlayer=null;
		gameState=STATE_NEW_GAME;
		cardState=0;

		replayCommands = new Vector();

		//System.out.print("New Game created\n"); // testing

		//simone=true;//false;

		r = new Random();

	}

	public void addCommand(String a) {

		replayCommands.add(a);

	}

	public Vector getCommands() {

		return replayCommands;

	}

	public int getMaxDefendDice() {
		return maxDefendDice;
	}

	/**
	 * This adds a player to the game
	 * @param ai Type of game (i.e World Domination, Secret Mission, Capital)
	 * @param name Name of player
	 * @param color Color of player
	 * @return boolean Returns true if the player is added, returns false if the player can't be added.
	 */
	public boolean addPlayer(AI ai, String name, int color, String a) {
		if (gameState==STATE_NEW_GAME ) { // && !name.equals("neutral") && !(color==Color.gray)

			for (int c=0; c< Players.size() ; c++) {
				if (( name.equals(((Player)Players.elementAt(c)).getName() )) || (color ==  ((Player)Players.elementAt(c)).getColor() )) return false;
			}

			//System.out.print("Player added. Type: " +type+ "\n"); // testing
			Player player = new Player(ai, name, color , a);
			Players.add(player);
			return true;
		}
		else return false;
	}

	/**
	 * This deletes a player in the game
	 * @param name Name of the player
	 * @return boolean Returns true if the player is deleted, returns false if the player cannot be deleted
	 */
	public boolean delPlayer(String name) {
		if (gameState==STATE_NEW_GAME) {

			int n=-1;

			for (int c=0; c< Players.size() ; c++) {
				if (name.equals( ((Player)Players.elementAt(c)).getName() )) n=c;
			}
			if (n==-1) {
				//System.out.print("Error: No player found\n"); // testing
				return false;
			}
			else {
				Players.removeElementAt(n);
				Players.trimToSize();
				//System.out.print("Player removed\n"); // testing
				return true;
			}
		}
		else return false;

	}

	/**
	 * Starts the game Risk
	 * @param mode This represents the moce of the game: normal, 2 player, capital or mission
	 */
	public void startGame(int mode, int card, boolean recycle, boolean threeDice) throws Exception {

		if (gameState==STATE_NEW_GAME) { //  && ((mapfile !=null && cardsfile !=null) || () )

			gameMode=mode;
			cardMode=card;

			recycleCards = recycle;
                        maxDefendDice = threeDice?3:2;

			// 2 player human crap
			//if ( gameMode==1 && ( !(((Player)Players.elementAt(0)).getType()==0) || !(((Player)Players.elementAt(1)).getType()==0) ) ) { return; }


			// check if things need to be loaded, maybe already loaded, then these will be null
			if (mapfile!=null && cardsfile!=null) {


				//try {

					loadMap();

				//}
				//catch (Exception e) {
				//	RiskUtil.printStackTrace(e);
				//	return;
				//}

				try {

					loadCards();

				}
				catch (Exception e) {

					if (runmaptest) {

						//System.out.println("LOAD FILE ERROR: " + e.getMessage() + "\n(This normally means you have selected the wrong set of cards for this map)"); // testing
						//RiskUtil.printStackTrace(e);
						throw new Exception("LOAD FILE ERROR: " + e.getMessage() + "\n(This normally means you have selected the wrong set of cards for this map)",e);

					}

					return;
				}


				if (runmaptest) {

					//try {
						testMap(); // testing maps
					//}
					//catch (Exception e) {
					//	RiskUtil.printStackTrace(e);
					//	return;
					//}
				}

			}

			if (Countries==null) { return; }

			if (gameMode==MODE_SECRET_MISSION && Missions.size() < Players.size() ) { return; }

			int armies = ( 10 - Players.size() ) *  Math.round( Countries.length * 0.12f );

			// System.out.print("armies="+ armies +"\n");
			//
			//if (gameMode==1) { // 2 player mode
			//	Player player = new Player(3, "neutral", Color.gray , "all" );
			//	Players.add(player);
			//}
			//
			//System.out.print("Game Started\n"); // testing

			for (int c=0; c< Players.size() ; c++) {
				((Player)Players.elementAt(c)).addArmies(armies);
			}

			gameState=STATE_PLACE_ARMIES;
			capturedCountry=false;
			tradeCap=false;

		}

	}

	/**
	 * this code is used to check if the borders in the map file are ok
	 */
	public void testMap() throws Exception {

		//System.out.print("Starting map test...\n");

		for (int c=0; c< Countries.length ; c++) {

			Country c1 = Countries[c];
			Vector c1neighbours = (Vector)c1.getNeighbours();

			if (c1neighbours.contains(c1)) { throw new Exception("Error: "+c1.getName()+" neighbours with itself"); }

			for (int a=0; a< c1neighbours.size() ; a++) {

				Country c2 = (Country)c1neighbours.elementAt(a);
				Vector c2neighbours = (Vector)c2.getNeighbours();

				boolean ok=false;

				for (int b=0; b< c2neighbours.size() ; b++) {

					Country c3 = (Country)c2neighbours.elementAt(b);

					if ( c1 == c3 ) { ok=true; }

				}

				if (ok==false) {
					throw new Exception("Borders error with: " + Countries[c].getName() + " ("+Countries[c].getColor()+") and " + ((Country)c1neighbours.elementAt(a)).getName() +" ("+((Country)c1neighbours.elementAt(a)).getColor()+")" ); // Display
				}

			}
		}

		//System.out.print("End map test.\n");

	}

	/**
	 * Sets the current player in the game
	 * @param name The name of the current player
	 * @return Player Returns the current player in the game
	 */
	public Player setCurrentPlayer(String name) {

		for (int c=0; c< Players.size() ; c++) {
			if (  ((Player)Players.elementAt( c )).getName().equals(name)  ) { currentPlayer=((Player)Players.elementAt( c )) ; }
		}

		return currentPlayer;

	}

	/**
	 * Gets the current player in the game
	 * @return String Returns the name of a randomly picked player from the set of players
	 */
	public String getRandomPlayer() {

		return ((Player)Players.elementAt( r.nextInt( Players.size() ) )).getName();

	}

	/**
	 * Checks whether the player deserves a card during at the end of their go
	 * @return String Returns the name of the card if deserves a card, else else returns empty speech-marks
	 */
	public String getDeservedCard() {
		//check to see if the player deserves a new risk card
		if (capturedCountry==true && Cards.size() > 0) {

			Card c = (Card)Cards.elementAt( r.nextInt(Cards.size()) );
			if (c.getCountry() == null) return Card.WILDCARD;
			else return ( (Country)c.getCountry() ).getColor()+"";
		}
		else {
			return "";
		}
	}

	/**
	 * Ends a player's go
	 * @return Player Returns the next player
	 */
	public Player endGo() {

		if (gameState==STATE_END_TURN) {
			
			
			//System.out.print("go ended\n"); // testing

			// work out who is the next player

			while (true) {

				for (int c=0; c< Players.size() ; c++) {
					if ( currentPlayer==((Player)Players.elementAt(c)) && Players.size()==(c+1) ) {
						currentPlayer=(Player)Players.elementAt(0);
						c=Players.size();
					}
					else if ( currentPlayer==((Player)Players.elementAt(c)) && Players.size() !=(c+1) ) {
						currentPlayer=(Player)Players.elementAt(c+1);
						c=Players.size();
					}
				}

				if ((setup != Players.size()) ) { break; }

											// && (currentPlayer.getType() != 3)

				else if ( currentPlayer.getTerritoriesOwnedSize() > 0       ) {break; }

			}

			//System.out.print("Curent Player: " + currentPlayer.getName() + "\n"); // testing

			if ( setup == Players.size() && !(gameMode==2 && currentPlayer.getCapital() == null) ) { // ie the initial setup has been compleated

				workOutEndGoStats( currentPlayer );
				currentPlayer.nextTurn();

				// add new armies for the Territories Owned
				if ( currentPlayer.getTerritoriesOwnedSize() < 9 ) {
					currentPlayer.addArmies(3);
				}
				else {
					currentPlayer.addArmies( currentPlayer.getTerritoriesOwnedSize() / 3 );
				}

				// add new armies for the Continents Owned
				for (int c=0; c< Continents.length ; c++) {

					if ( Continents[c].isOwned(currentPlayer) ) {
						currentPlayer.addArmies( Continents[c].getArmyValue() );
					}

				}

			}

			if (setup == Players.size() && gameMode==2 && currentPlayer.getCapital() == null) { // capital risk setup not finished
				gameState=STATE_SELECT_CAPITAL;
			}
			else if ( canTrade()==false ) { // ie the initial setup has not been compleated or there are no cards that can be traded
				gameState=STATE_PLACE_ARMIES;
			}
			else { // there are cards that can be traded
				gameState=STATE_TRADE_CARDS;
			}

			capturedCountry=false;
			tradeCap=false;
			
//			System.out.println("Players");
//			for (int i = 0; i<Players.size(); i++){
//				Player p = (Player) Players.elementAt(i);
//				System.out.println("  "+ i+") "+p.getColor() + " "+ p.getName());
//			}
//			System.out.println("Current: " + currentPlayer.getColor() + " "+ currentPlayer.getName());
			
			

			return currentPlayer;

		}
		else {

			//System.out.println("lala "+gameState);

			return null;
		}
	}

	/**
	 * Trades a set of cards
	 * @param card1 First card to trade
	 * @param card2 Second card to trade
	 * @param card3 Third card to trade
	 * @return int Returns the number of armies gained from the trade, returning 0 if the trade is unsuccessful
	 */
    public int trade(Card card1, Card card2, Card card3) {
        if (gameState!=STATE_TRADE_CARDS) return 0;

        if (tradeCap==true && ((Vector)currentPlayer.getCards()).size() < 5 )
            return 0;

        int armies = getTradeAbsValue( card1.getName(), card2.getName(), card3.getName(), cardMode);

        if (armies <= 0) return 0;

        if (cardMode==CARD_INCREASING_SET) {

            cardState=armies;

        }

        currentPlayer.tradeInCards(card1, card2, card3);

        if (recycleCards) {

            Cards.add(card1);
            Cards.add(card2);
            Cards.add(card3);
            //Return the cards to the deck

        }

        currentPlayer.addArmies(armies);

        if ( canTrade()==false || (tradeCap==true && ((Vector)currentPlayer.getCards()).size() < 5 ) ) {
            gameState=STATE_PLACE_ARMIES;
            tradeCap=false;
        }

        return cardState;
    }

    /**
     * Returns the trading value of the given cards, without taking into account
     * the territories associated to the cards.
     * @param c1 The name of the type of the first card.
     * @param c2 The name of the type of the second card.
     * @param c3 The name of the type of the third card.
     * @return 0 in case of invalid combination of cards.
     */
    public int getTradeAbsValue(String c1, String c2, String c3,int cardMode) {
        int armies=0;

        // we shift all wildcards to the front
        if (!c1.equals(Card.WILDCARD)) { String n4 = c3; c3 = c1; c1 = n4; }
        if (!c2.equals(Card.WILDCARD)) { String n4 = c3; c3 = c2; c2 = n4; }
        if (!c1.equals(Card.WILDCARD)) { String n4 = c2; c2 = c1; c1 = n4; }

        if (cardMode == CARD_INCREASING_SET) {
            if (
                    c1.equals(Card.WILDCARD) ||
                    (c1.equals(c2) && c1.equals(c3)) ||
                    (!c1.equals(c2) && !c1.equals(c3) && !c2.equals(c3))
                ) {
                armies = getNewCardState();
            }
        }
        else if (cardMode == CARD_FIXED_SET) {
            // ALL THE SAME or 'have 1 wildcard and 2 the same'
            if ((c1.equals(c2) || c1.equals(Card.WILDCARD)) && c2.equals(c3)) {
                if (c3.equals(Card.INFANTRY)) {
                    armies = 4;
                }
                else if (c3.equals(Card.CAVALRY)) {
                    armies = 6;
                }
                else if (c3.equals(Card.CANNON)) {
                    armies = 8;
                }
                else { // (c1.equals( Card.WILDCARD ))
                    armies = 12; // Incase someone puts 3 wildcards into his set
                }
            }
            // ALL CARDS ARE DIFFERENT (can have 1 wildcard) or 2 wildcards and a 3rd card
            else if (
                    (c1.equals(Card.WILDCARD) && c2.equals(Card.WILDCARD)) ||
                    (!c1.equals(c2) && !c2.equals(c3) && !c1.equals(c3))
                    ) {
                armies = 10;
            }
        }
        else { // (cardMode==CARD_ITALIANLIKE_SET)
            if (c1.equals(c2) && c1.equals(c3)) {
                // All equal
                if (c1.equals(Card.CAVALRY)) {
                    armies = 8;
                }
                else if (c1.equals(Card.INFANTRY)) {
                    armies = 6;
                }
                else if (c1.equals(Card.CANNON)) {
                    armies = 4;
                }
                else { // (c1.equals( Card.WILDCARD ))
                    armies = 0; // Incase someone puts 3 wildcards into his set
                }
            }
            else if (!c1.equals(c2) && !c2.equals(c3) && !c1.equals(c3) && !c1.equals(Card.WILDCARD)) {
                armies = 10;
            }
            //All the same w/1 wildcard
            else if (c1.equals(Card.WILDCARD) && c2.equals(c3)) {
                armies = 12;
            }
            //2 wildcards, or a wildcard and two different
            else {
                armies = 0;
            }
        }
        return armies;
    }

	public boolean canTrade() {

		Vector cards = currentPlayer.getCards();
		Card card1=null, card2=null, card3=null;

		if (setup == Players.size() && cards.size() >= 3 ) { // ie the initial setup has been compleated and there are 3 cards or more

			for (int a=0; a< cards.size() ; a++) {
				if (card1 != null && card2 != null && card3 != null) { break; }
				card1 = (Card)cards.elementAt(a);

				for (int b=(a+1); b< cards.size() ; b++) {
					if (card1 != null && card2 != null && card3 != null) { break; }
					card2 = (Card)cards.elementAt(b);

					for (int c=(b+1); c< cards.size() ; c++) {
						if (card1 != null && card2 != null && card3 != null) { break; }
						card3 = (Card)cards.elementAt(c);

						if ( checkTrade(card1, card2, card3) ) { break; }
						else { card3=null; }

					}
				}
			}
		}

		if (card3 == null) {
			return false;
		}
		else {
			return true;
		}

	}

	public int getNewCardState() {

		if (cardState < 4) {
			return cardState+4;
		}
		else if (cardState < 12) {
			return cardState+2;
		}
		else if (cardState < 15) {
			return cardState+3;
		}
		else {
			return cardState+5;
		}

	}

	/**
	 * checks if a set of cards can be traded
	 * @param card1 First card to trade
	 * @param card2 Second card to trade
	 * @param card3 Third card to trade
	 * @return boolean true if they can be traded false if they can not
	 */
	public boolean checkTrade(Card card1, Card card2, Card card3) {
            return getTradeAbsValue( card1.getName(), card2.getName(), card3.getName(), cardMode) > 0;
	}

        /**
	 * Ends the trading phase by checking if the player has less than 5 cards
	 * @return boolean Returns true if the player has ended the trade phase, returns false if the player cannot end the trade phase
	 */
	public boolean endTrade() {

		if (gameState==STATE_TRADE_CARDS) {

			if (tradeCap==true && ((Vector)currentPlayer.getCards()).size() > 5 ) {
				return false;
			}

			if (((Vector)currentPlayer.getCards()).size() < 5) {

				gameState=STATE_PLACE_ARMIES;
				tradeCap=false;
				return true;
			}

		}
		return false;

	}

	/**
	 * Places an army on the Country
	 * @param t Country that the player wants to add armies to
	 * @param n Number of armies the player wants to add to the country
	 * @return boolean Returns true if the number of armies are added the country, returns false if the armies cannot be added to the territory
	 */
	public int placeArmy(Country t, int n) {

		int done=0;

		if ( gameState==STATE_PLACE_ARMIES ) {

			if ( setup != Players.size() ) { // ie the initial setup has not been compleated
				if (n != 1) return 0;
				// if it has the player as a owner
				if ( t.getOwner()==currentPlayer ) {

					if ( NoEmptyCountries() ) { // no empty country are found
						t.addArmy();
						currentPlayer.loseExtraArmy(1);
						done=1;
						//System.out.print("army placed in: " + t.getName() + "\n"); // testing
					}

				}
				// if there is no owner
				else if ( t.getOwner()==null ) {

					t.setOwner(currentPlayer);
					currentPlayer.newCountry(t);
					t.addArmy();
					currentPlayer.loseExtraArmy(1);
					done=1;
					//System.out.print("country taken and army placed in: " + t.getName() + "\n"); // testing
				}

			}
			else { // initial setup is completed

				// if it has the player as a owner
				if ( t.getOwner()==currentPlayer && currentPlayer.getExtraArmies() >=n ) {

					currentPlayer.currentStatistic.addReinforcements(n);

					t.addArmies(n);
					currentPlayer.loseExtraArmy(n);
					//System.out.print("army placed in: " + t.getName() + "\n"); // testing
					done=1;

				}
			}

			if (done==1) {

				if (setup == Players.size() ) { // ie the initial setup has been compleated
					if ( currentPlayer.getExtraArmies()==0 ) { gameState=STATE_ATTACKING; }
					else { gameState=STATE_PLACE_ARMIES; }
				}
				else { // initial setup is not compleated
					if (currentPlayer.getExtraArmies()==0) {
						setup++; // another player has finished initial setup

					}

					gameState=STATE_END_TURN;

				}

				if ( checkPlayerWon() ) {
					done=2;
				}

			}

		}
		return done;

	}

	/**
	 * Automatically places an army on an unoccupied country
	 * @return int Returns the country id which an army was added to
	 */
	public int getEmptyCountry() {

		// find a empty country
		int nEmpty = -1;

		if (gameState==STATE_PLACE_ARMIES) {

			int a = r.nextInt(Countries.length);
			boolean done = false;

			for (int c=a; c < Countries.length ; c++) {

				if ( Countries[c].getOwner() == null ) {
					nEmpty = Countries[c].getColor();
					break;
				}
				else if ( c == Countries.length-1 && done == false ) {
					c = -1;
					done = true;
				}
				else if ( c == Countries.length-1 && done == true ) {
					break;
				}

			}

		}

		return nEmpty;

	}//public int getEmptyCountry()

	/**
	 * Attacks one country and another
	 * @param t1 Attacking country
	 * @param t2 Defending country
	 * @return int[] Returns an array which determines if the player is allowed to roll dice
	 */
	public int[] attack(Country t1, Country t2) {

		int[] result = new int[3];
		result[0]=0;
		result[1]=0;
		result[2]=0;

		if (gameState==STATE_ATTACKING) {

			if (
					t1!=null &&
					t2!=null &&
					t1.getOwner()==currentPlayer &&
					t2.getOwner()!=currentPlayer &&
					t1.isNeighbours(t2) &&
					// t2.isNeighbours(t1) && // not needed as there is code to check this
					t1.getArmies() > 1
			) {

				currentPlayer.currentStatistic.addAttack();
				((Player)t2.getOwner()).currentStatistic.addAttacked();

				result[0]=1;

				if ( t1.getArmies() > 4 ) { result[1]=3; }
				else { result[1]=t1.getArmies()-1; }

				if ( t2.getArmies() > maxDefendDice ) { result[2]=maxDefendDice; }
				else { result[2]=t2.getArmies(); }

				attacker=t1;
				defender=t2;

				gameState=STATE_ROLLING;
				//System.out.print("Attacking "+t2.getName()+" ("+t2.getArmies()+") with "+t1.getName()+" ("+t1.getArmies()+").\n"); // testing

			}

		}
		return result;
	}

	/**
	 * Ends the attacking phase
	 * @return boolean Returns true if the player ended the attacking phase, returns false if the player cannot end the attacking phase
	 */
	public boolean endAttack() {

		if (gameState==STATE_ATTACKING) { // if we were in the attack phase

			// YURA:TODO check if there are any countries with more then 1 amy, maybe even check that a move can be made

			gameState=STATE_FORTIFYING; // go to move phase
			//System.out.print("Attack phase ended\n");
			return true;
		}
		return false;
	}

	/**
	 * Rolls the attackersdice
	 * @param dice1 Number of dice to be used by the attacker
	 * @return boolean Return if the roll was successful
	 */
	public boolean rollA(int dice1) {

		if (gameState==STATE_ROLLING) { // if we were in the attacking phase

			if ( attacker.getArmies() > 4 ) {
				if (dice1<=0 || dice1>3) return false;
			}
			else {
				if (dice1<=0 || dice1> (attacker.getArmies()-1) ) return false;
			}

			attackerDice = dice1; // 5 2 0

			// System.out.print("NUMBER OF DICE: " + dice1 + " or " + attackerDice.length + "\n");

			currentPlayer=defender.getOwner();
			gameState=STATE_DEFEND_YOURSELF;
			return true;

		}
		else return false;

	}

	public boolean rollD(int dice2) {

		if (gameState==STATE_DEFEND_YOURSELF) { // if we were in the defending phase

			if ( defender.getArmies() > maxDefendDice ) {
				if (dice2<=0 || dice2>maxDefendDice) return false;
			}
			else {
				if (dice2<=0 || dice2> (defender.getArmies()) ) return false;
			}

			currentPlayer=attacker.getOwner();

			defenderDice = dice2; // 4 3

			return true;

		}
		else return false;

	}

	public int getAttackerDice() {
		return attackerDice;
	}

	public int getDefenderDice() {
		return defenderDice;
	}

	/**
	 * Rolls the defenders dice
	 * @param attackerResults The results for the attacker
	 * @param defenderResults The results for the defender
	 * @return int[] Returns an array which will determine the results of the attack
	 */
	public int[] battle(int[] attackerResults, int[] defenderResults) {
		Logger logger = Logger.getLogger(RiskLogger.LOGGER);

		int[] result = new int[6];
		result[0]=0; // worked or not
		result[1]=0; // no of armies attacker lost
		result[2]=0; // no of armies defender lost
		result[3]=0; // did you win
		result[4]=0; // min move
		result[5]=0; // max move

		if (gameState==STATE_DEFEND_YOURSELF) { // if we were in the defending phase

			// battle away!
			for (int c=0; c< Math.min(attackerResults.length, defenderResults.length) ; c++) {

				if (attackerResults[c] > defenderResults[c]) {
					defender.looseArmy();
					((Player)defender.getOwner()).currentStatistic.addCasualty();
					((Player)attacker.getOwner()).currentStatistic.addKill();
					result[2]++;
				}
				else {
					attacker.looseArmy();
					((Player)attacker.getOwner()).currentStatistic.addCasualty();
					((Player)defender.getOwner()).currentStatistic.addKill();
					result[1]++;
				}

			}

			// if all the armies have been defeated
			if (defender.getArmies() == 0) {

				((Player)attacker.getOwner()).currentStatistic.addCountriesWon();
				((Player)defender.getOwner()).currentStatistic.addCountriesLost();

				result[5]=attacker.getArmies()-1;

				capturedCountry=true;

				Player lostPlayer=(Player)defender.getOwner();

				lostPlayer.lostCountry(defender);
				currentPlayer.newCountry(defender);

				defender.setOwner( (Player)attacker.getOwner() );
				result[3]=1;
				gameState=STATE_BATTLE_WON;
				mustmove=attackerResults.length;

				result[4]=mustmove;
				
				
				if((Risk.isLogAttacks() && attacker.getOwner().isLogged()) || (Risk.isLogReceivedAttacks() && lostPlayer.isLogged())){
					StringBuilder log = new StringBuilder();
					log.append("       Esito Battaglia: Attaccante: "+ attacker.getArmies() +", Difensore: 0\n");
					log.append("    -- "+attacker.getOwner().getName()+"("+attacker.getOwner().getAI().getName()+") conquista "+ defender.getName()+"\n");
					logger.info(log.toString());
				}
				// if the player has been eliminated
				if ( lostPlayer.getTerritoriesOwnedSize() == 0) {

					result[3]=2;

					currentPlayer.addPlayersEliminated(lostPlayer);

					while (((Vector)lostPlayer.getCards()).size() > 0) {

						//System.out.print("Hes got a card .. i must take it!\n");
						currentPlayer.giveCard( lostPlayer.takeCard() );

					}

					if ( ((Vector)currentPlayer.getCards()).size() > 5) {
						// gameState=STATE_BATTLE_WON;
						tradeCap=true;
					}
					
					if(lostPlayer.getAI() instanceof EnemyCommandsListener)
						EnemyCommandsEventSource.removeEnemyCommandsListener((EnemyCommandsListener) lostPlayer.getAI());
					
					if(Risk.isLogLosersWinner())
						logger.info("\n!!! "+lostPlayer.getName()+"("+lostPlayer.getAI().getName()+") ELIMINATO!!!\n\n");

				}

			}
			else if (attacker.getArmies() == 1) {
				gameState=STATE_ATTACKING;
				//System.out.print("Retreating (FORCED)\n");
				currentPlayer.currentStatistic.addRetreat();
				
				if((Risk.isLogAttacks() && attacker.getOwner().isLogged()) || (Risk.isLogReceivedAttacks() && defender.getOwner().isLogged()))
					logger.info("       Esito Battaglia: Attaccante: 1, Difensore: "+defender.getArmies()+"\n");
			}
			else { gameState=STATE_ROLLING; }

			defenderDice = 0;
			attackerDice = 0;
			result[0]=1;

		}

		return result;
	}

	/**
	 * Moves a number of armies from the attacking country to defending country
	 * @param noa Number of armies to be moved
	 * @return boolean Return trues if you can move the number of armies across, returns false if you cannot
	 */
	public int moveArmies(int noa) {

		int result=0;

		if (gameState==STATE_BATTLE_WON && mustmove>0 && noa>= mustmove && noa<attacker.getArmies() ) {

			attacker.removeArmies(noa);
			defender.addArmies(noa);

			attacker=null;
			defender=null;

			mustmove=0;

			if (tradeCap==true) {
				gameState=STATE_TRADE_CARDS;
			}
			else {
				gameState=STATE_ATTACKING;
			}

			result=1;

			if ( checkPlayerWon() ) {
				result=2;
			}

			return result;

		}
		return result;

	}

	/**
	 * Moves all of armies from the attacking country to defending country
	 * @return int Return trues if you can move the number of armies across, returns false if you cannot
	 */
	public int moveAll() {

		if (gameState==STATE_BATTLE_WON && mustmove>0) {

			return attacker.getArmies() - 1;

		}
		return -1;

	}

	public int getMustMove() {
		return mustmove;
	}

	/**
	 * Retreats from attacking a country
	 * @return boolean Returns true if you can retreat, returns false if you cannot
	 */
	public boolean retreat() {

		if (gameState==STATE_ROLLING) { // if we were in the attacking phase

			currentPlayer.currentStatistic.addRetreat();

			attacker=null;
			defender=null;

			gameState=STATE_ATTACKING; // go to attack phase
			//System.out.print("Retreating\n");
			return true;
		}
		return false;
	}

	/**
	 * Moves armies from one country to an adjacent country and goes to the end phase
	 * @param t1 Country where the armies are moving from
	 * @param t2 Country where the armies are moving to
	 * @param noa Number of Armies to move
	 * @return boolean Returns true if the tactical move is allowed, returns false if the tactical move is not allowed
	 */
	public boolean moveArmy(Country t1, Country t2, int noa) {
		if (gameState==STATE_FORTIFYING) {

			// do they exist //check if they belong to the player //check if they are neighbours //check if there are enough troops in country1
			if (
					t1!=null &&
					t2!=null &&
					t1.getOwner()==currentPlayer &&
					t2.getOwner()==currentPlayer &&
					t1.isNeighbours(t2) &&
					// t2.isNeighbours(t1) && // not needed as there is code to check this
					t1.getArmies() > noa &&
					noa > 0
			) {

				t1.removeArmies(noa);
				t2.addArmies(noa);
				gameState=STATE_END_TURN;

				checkPlayerWon();

				//System.out.println("Armies Moved. "+gameState); // testing
				return true;

			}
		}
		return false;

	}

	/**
	 * Choosing not to use the tactical move and moves to the end phase
	 * @return boolean Returns true if you are in the right phase to use the tactical move and returns false otherwise
	 */
	public boolean noMove() {

		if (gameState==STATE_FORTIFYING) { // if we were in the move phase
			gameState=STATE_END_TURN; // go to end phase

			//System.out.print("No Move.\n"); // testing
			return true;
		}
		else return false;

	}

	public void workOutEndGoStats(Player p) {

		int countries = p.getTerritoriesOwnedSize();
		int armies = p.getNoArmies();
		int continents = getNumberContinentsOwned(p);
		int conectedEmpire = ((Vector)getConnectedEmpire(p)).size() ;

		p.currentStatistic.endGoStatistics(countries, armies, continents, conectedEmpire);

	}

	public Vector<Country> getConnectedEmpire(Player p) {

		Vector t = (Vector)p.getTerritoriesOwned().clone();

		Vector a = new Vector();
		Vector b = new Vector();

		while ( t.isEmpty() == false ) {

			Country country = ((Country)t.remove(0));

			a.add( country );

			getConnectedEmpire( t, a, country.getNeighbours() , p );

			if (a.size() > b.size() ) {
				b = a;
			}

			a = new Vector();

		}

		return b;

	}

	/**
	 * Finds the largest number of connected territories owned by a single player
	 * @param t Vector of territories owned by a single player (volatile)
	 * @param a Vector of adjacent territories
	 * @param n Vector of territories owned by a single player (non-volatile)
	 * @param p The current player
	 */
	public void getConnectedEmpire(Vector t, Vector a, Vector n, Player p) {

		for (int i = 0; i < n.size() ; i++) {

			if ( ((Country)n.elementAt(i)).getOwner() == p && t.contains( n.elementAt(i) ) ) {

				Country country = (Country)n.elementAt(i);
				t.remove( country );
				a.add( country );

				getConnectedEmpire( t, a, country.getNeighbours(), p);


			}
		}

	}

	/**
	 * Sets the capital for a player - ONLY FOR CAPITAL RISK
	 * @param c The capital country
	 * @return boolean Returns true if the country is set as the capital, returns false otherwise
	 */
	public boolean setCapital(Country c) {

		if (gameState== STATE_SELECT_CAPITAL && gameMode == 2 && c.getOwner()==currentPlayer && currentPlayer.getCapital()==null) {

			currentPlayer.setCapital(c);

			for (int b=0; b< Cards.size() ; b++) {

				if ( c== ((Card)Cards.elementAt(b)).getCountry() ) {
					Cards.removeElementAt(b);
					//System.out.print("card removed because it is a capital\n");
				}

			}

			gameState=STATE_END_TURN;

			return true;

		}
		return false;

	}

	/**
	 * Check if a player has won the game
	 * @return boolean Returns true if the player has won the game, returns false otherwise
	 */
	public boolean checkPlayerWon() {

		boolean result=false;

		// check if the player has won
		int won=0;
		for (int c=0; c< Continents.length ; c++) {

			if ( Continents[c].isOwned(currentPlayer) ) {
				won++;
			}

		}
		if (won == Continents.length ) {

			result=true;
			//System.out.print("The Game Is Over, "+currentPlayer.getName()+" has won!\n");

		}

		// check if the player has won 2 player risk

/* @todo: maybe add this back, as crap player can never win

		else if (setup == Players.size() && gameMode==1) {

			Player target=null;

			for (int c=0; c< Players.size() ; c++) {


					// ((Player)Players.elementAt(c)).getType() !=3 &&

				if (        (Player)Players.elementAt(c) != currentPlayer ) {
					target = (Player)Players.elementAt(c);
				}

			}

			if ( target.getNoTerritoriesOwned()==0 ) {

				result=true;

			}

		}
*/

		// check if the player has won capital risk!
		else if (setup == Players.size() && gameMode==MODE_CAPITAL && currentPlayer.getCapital() !=null ) {

			int capitalcount=0;

			if ( currentPlayer==((Country)currentPlayer.getCapital()).getOwner() ) {

				for (int c=0; c< Players.size() ; c++) {

					if ( ((Vector)currentPlayer.getTerritoriesOwned()).contains((Country)((Player)Players.elementAt(c)).getCapital()) ) {
						capitalcount++;
					}

				}

			}

			if ( capitalcount==Players.size() ) {
				result=true;
			}

		}
		// check if the player has won mission risk!
		else if (setup == Players.size() && gameMode==MODE_SECRET_MISSION ) {

			Mission m = currentPlayer.getMission();

			if (
					m.getPlayer() !=null && // check is this is indeed a Elim Player card
					m.getPlayer() != currentPlayer && // check if its not the current player u need to eliminate
					((Player)m.getPlayer()).getTerritoriesOwnedSize()==0 && // chack if that player has been eliminated
					((Vector)currentPlayer.getPlayersEliminated()).contains( m.getPlayer() ) //check if it was you who eliminated them
			) {

				// yay you have won
				result=true;


			}
			else if (
					m.getNoofcountries() != 0 && m.getNoofarmies() != 0 && // check if this card has a value for capture teretories
					( m.getPlayer() == null || ((Player)m.getPlayer()).getTerritoriesOwnedSize()==0 || (Player)m.getPlayer() == currentPlayer ) &&
					m.getNoofcountries() <= currentPlayer.getTerritoriesOwnedSize() // do you have that number of countries captured
			) {

				int n=0;

				for (int c=0; c< currentPlayer.getTerritoriesOwnedSize() ; c++) {
					if ( ((Country)((Vector)currentPlayer.getTerritoriesOwned()).elementAt(c)).getArmies() >= m.getNoofarmies() ) n++;

				}
				if (n >= m.getNoofcountries() ) {

					// yay you have won
					result=true;

				}

			}
			else if (
					(m.getContinent1() !=null) && // this means its a continent mission

					checkPlayerOwnesContinentForMission(m.getContinent1(),1) &&
					checkPlayerOwnesContinentForMission(m.getContinent2(),2) &&
					checkPlayerOwnesContinentForMission(m.getContinent3(),3)

			) {

				// yay you have won
				result=true;

			}

		}

		if (result==true) {
			gameState=STATE_GAME_OVER;
			if(Risk.isLogLosersWinner())
				Logger.getLogger(RiskLogger.LOGGER).info("\n!!! "+currentPlayer.getName() + "("+currentPlayer.getAI().getName()+") HA VINTO!!!" );
			if(RiskLogger.isMultipleGameLogger())
				RiskLogger.newVictory(currentPlayer.getName());			
		}

		return result;

	}

	private boolean checkPlayerOwnesContinentForMission(Continent c,int n) {

		if ( ANY_CONTINENT.equals(c) ) {

			return (getNumberContinentsOwned(currentPlayer) >=n);

		}
		else if (c!=null) {

			return c.isOwned(currentPlayer);

		}
		else {

			return true;

		}

	}

	public boolean continuePlay() {

		if (gameState==STATE_GAME_OVER && gameMode != 0 && gameMode != 1) {

			int oldGameMode=gameMode;

			gameMode=0;

			if ( checkPlayerWon() ) {
				gameMode=oldGameMode;
				return false;
			}

			if (tradeCap==true) { gameState=STATE_TRADE_CARDS; }
			else if ( currentPlayer.getExtraArmies()==0 ) { gameState=STATE_ATTACKING; }
			else { gameState=STATE_PLACE_ARMIES; }

			return true;

		}
		return false;
	}

	//private URL getURL(String a) throws Exception {
	//	return new URL(net.yura.domination.engine.Risk.mapsdir,a);
	//}

	/**
	 * Loads the map
	 * @param filename The map filename
	 * @throws Exception There was a error
	 */
	public void loadMap() throws Exception {

		StringTokenizer st=null;

		Vector Countries = new Vector();
		Vector Continents = new Vector();

		//System.out.print("Starting Load Map...\n");

		BufferedReader bufferin=RiskUtil.readMap( RiskUtil.openMapStream(mapfile) );

		String input = bufferin.readLine();
		String mode = "none";

		while(input != null) {

			if (input.equals("") || input.charAt(0)==';') {
				// do nothing
				//System.out.print("Nothing\n"); // testing
			}
			else {

				//System.out.print("Something found\n"); // testing

				if (input.charAt(0)=='[' && input.charAt( input.length()-1 )==']') {
					//System.out.print("Something beggining with [ and ending with ] found\n"); // testing
					mode="newsection";
				}
				else { st = new StringTokenizer(input); }

				if (mode.equals("files")) {
					//System.out.print("Adding files\n"); // testing

					if ( input.startsWith("pic ") )  { ImagePic = input.substring(4); } //System.out.print("file: ImagePic added!\n"); // testing
					else if ( input.startsWith("map ") ) { ImageMap = input.substring(4); } //System.out.print("file: ImageMap added!\n"); // testing
					else if ( input.startsWith("crd ") ) { }
					else if ( input.startsWith("prv ") ) { }
					else { throw new Exception("error with files section in map file: "+input); }

				}
				else if (mode.equals("continents")) {
					//System.out.print("Adding continents\n"); // testing

					String id=st.nextToken(); //System.out.print(name+"\n"); // testing

					// get translation
					String name = MapTranslator.getTranslatedMapName(id).replaceAll( "_", " ");

					int noa=Integer.parseInt( st.nextToken() ); //System.out.print(noa+"\n"); // testing
					int color=RiskUtil.getColor( st.nextToken() ); //System.out.print(color.toString()+"\n"); // testing

					if (color==0) {

						// there was no check for null b4 here, but now we need this for the map editor
						color = getRandomColor();

					}

					if ( st.hasMoreTokens() ) { throw new Exception("unknown item found in map file: "+ st.nextToken() ); }

					Continent continent = new Continent(id, name, noa, color);
					Continents.add(continent);

				}
				else if (mode.equals("countries")) {
					//System.out.print("Adding countries\n"); // testing

					int color = Integer.parseInt(st.nextToken());
					String id=st.nextToken(); //System.out.print(name+"\n"); // testing

					// get translation
					String name = MapTranslator.getTranslatedMapName(id).replaceAll( "_", " ");

					int continent = Integer.parseInt(st.nextToken());
					int x = Integer.parseInt(st.nextToken());
					int y = Integer.parseInt(st.nextToken());

					if ( st.hasMoreTokens() ) { throw new Exception("unknown item found in map file: "+ st.nextToken() ); }
					if ( Countries.size() != (color-1) ) { throw new Exception("unexpected number found in map file: "+color ); }

					Country country = new Country(color, id, name, (Continent)Continents.elementAt( continent - 1 ), x, y);
					Countries.add(country);

					((Continent)Continents.elementAt( continent - 1 )).addTerritoriesContained(country);

				}
				else if (mode.equals("borders")) {
					//System.out.print("Adding borders\n"); // testing

					int country=Integer.parseInt( st.nextToken() ); //System.out.print(country+"\n"); // testing
					while (st.hasMoreElements()) {
						((Country)Countries.elementAt( country - 1 )).addNeighbour( ((Country)Countries.elementAt( Integer.parseInt(st.nextToken()) - 1 )) );
					}


				}
				else if (mode.equals("newsection")) {

					mode = input.substring(1, input.length()-1); // set mode to the name of the section

					if (mode.equals("files") ) {
						//System.out.print("Section: files found\n"); // testing
						ImagePic=null;
						ImageMap=null;
					}
					else if (mode.equals("continents") ) {
						//System.out.print("Section: continents found\n"); // testing
					}
					else if (mode.equals("countries") ) {
						//System.out.print("Section: countries found\n"); // testing
					}
					else if (mode.equals("borders") ) {
						//System.out.print("Section: borders found\n"); // testing
					}
					else {
						throw new Exception("unknown section found in map file: "+mode);
					}

				}
//				else {
//
//					if (input.equals("test")) {
//
//					}
//					else if (input.startsWith("name ")) {
//
//					}
//                                      else if (input.startsWith("ver ")) {
//
//					}
//					else {
//
//						throw new Exception("unknown item found in map file: "+input);
//
//					}
//				}


			}


			input = bufferin.readLine(); // get next line

		}
		bufferin.close();

		this.Countries = (Country[])Countries.toArray( new Country[Countries.size()] );
		this.Continents = (Continent[])Continents.toArray( new Continent[Continents.size()] );

		//System.out.print("Map Loaded\n");

	}

	/**
	 * Sets the filename of the map file
	 * @param f The name of the new file
	 * @return boolean Return trues if missions are supported
	 * @throws Exception The file cannot be found
	 */
	public boolean setMapfile(String f) throws Exception {

		if (f.equals("default")) {
			f = defaultMap;
		}

		BufferedReader bufferin=RiskUtil.readMap( RiskUtil.openMapStream(f) );


/*
		File file;

		if (f.equals("default")) {
			file = new File("maps/" + defaultMap);
		}
		else {
			file = new File("maps/" + f);
		}

		FileReader filein = new FileReader(file);

		BufferedReader bufferin = new BufferedReader(filein);
*/

		runmaptest = false;
		previewPic = null;
		mapName = null;
                ver=1; // if there is no version then version is 1

		String input = bufferin.readLine();
		String mode = null;

		boolean yesmap = false;
		boolean returnvalue = false;
		boolean yescards = false;

		while(input != null) {

			if (input.equals("") || input.charAt(0)==';') {

			}
			else {

				if (input.charAt(0)=='[' && input.charAt( input.length()-1 )==']') {
					mode="newsection";
				}

				if ("files".equals(mode)) {

					if ( input.startsWith("pic ") ) { ImagePic = input.substring(4); }

					else if ( input.startsWith("prv ") ) { previewPic = input.substring(4); }

					else if ( input.startsWith("crd ") ) { yescards=true; returnvalue = setCardsfile( input.substring(4) ); }

				}
				else if ("borders".equals(mode)) {

					yesmap=true;

				}
				else if ("newsection".equals(mode)) {

					mode = input.substring(1, input.length()-1); // set mode to the name of the section

				}
				else if (mode == null) {

                                        if (input.equals("test")) {

						runmaptest = true;

					}
                                        else if (input.startsWith("name ")) {

						mapName = input.substring(5,input.length());

					}
                                        else if (input.startsWith("ver ")) {
                                            
                                                ver = Integer.parseInt( input.substring(4,input.length()) );
                                            
                                        }
                                        // else unknown section

				}

			}


			input = bufferin.readLine(); // get next line

		}

		if ( yesmap==false ) { throw new Exception("error with map file"); }
		if ( yescards==false ) { throw new Exception("cards file not specified in map file"); }

		mapfile = f;
		bufferin.close();

		MapTranslator.setMap( f );

		return returnvalue;

	}

        /**
         * we need to call this if we do not want to reload data from disk when we start a game
         */
	public void setMemoryLoad() {

		mapfile = null;
		cardsfile = null;

		ImagePic = null;
		ImageMap = null;
	}

	public void setupNewMap() {

		Countries = new Country[0];
		Continents = new Continent[0];

		Cards = new Vector();
		Missions = new Vector();

                ver=1;
                mapName = null;
                
                runmaptest = false;
                previewPic=null;
                
		setMemoryLoad();

	}

	public void setCountries(Country[] a) {

		Countries = a;

	}
	public void setContinents(Continent[] a) {

		Continents = a;

	}

	/**
	 * Loads the cards
	 * @param filename The cards filename
	 * @throws Exception There was a error
	 */
	public void loadCards() throws Exception {

		StringTokenizer st=null;

		Cards = new Vector();
		Missions = new Vector();

		//System.out.print("Starting load cards and missions...\n");

		BufferedReader bufferin=RiskUtil.readMap( RiskUtil.openMapStream(cardsfile) );

		String input = bufferin.readLine();
		String mode = "none";

		while(input != null) {

			if (input.equals("") || input.charAt(0)==';') {
				// do nothing
				//System.out.print("Nothing\n"); // testing
			}
			else {

				//System.out.print("Something found\n"); // testing

				if (input.charAt(0)=='[' && input.charAt( input.length()-1 )==']') {
					//System.out.print("Something beggining with [ and ending with ] found\n"); // testing
					mode="newsection";
				}
				else { st = new StringTokenizer(input); }

				if (mode.equals("cards")) {
					//System.out.print("Adding cards\n"); // testing

					String name=st.nextToken(); //System.out.print(name+"\n"); // testing

					if (name.equals(Card.WILDCARD)) {
						Card card = new Card(name, null);
						Cards.add(card);
					}
					else if ( name.equals(Card.CAVALRY) || name.equals(Card.INFANTRY) || name.equals(Card.CANNON) ) {
						int country=Integer.parseInt( st.nextToken() );

						//System.out.print( Countries[ country - 1 ].getName() +"\n"); // testing
						Card card = new Card(name, Countries[ country - 1 ]);
						Cards.add(card);
					}
					else {
						throw new Exception("unknown item found in cards file: "+name);
					}

					if ( st.hasMoreTokens() ) { throw new Exception("unknown item found in cards file: "+ st.nextToken() ); }

				}
				else if (mode.equals("missions")) {
					//System.out.print("Adding Mission\n"); // testing

					//boolean add=true;

					int s1 = Integer.parseInt(st.nextToken());
					Player p;

					if (s1==0 || s1>Players.size() ) {
						p = null;
					}
					else {
						p = (Player)Players.elementAt( s1-1 );
					}

					int noc = Integer.parseInt(st.nextToken());
					int noa = Integer.parseInt(st.nextToken());

					String s4 = st.nextToken();
					String s5 = st.nextToken();
					String s6 = st.nextToken();

					Continent c1 = getMissionContinentfromString( s4 );
					Continent c2 = getMissionContinentfromString( s5 );
					Continent c3 = getMissionContinentfromString( s6 );

					String missioncode=s1+"-"+noc+"-"+noa+"-"+s4+"-"+s5+"-"+s6;
					String description=MapTranslator.getTranslatedMissionName(missioncode);

					if (description==null) {

					    description="";
					    while (st.hasMoreElements()) {
							description = description +("".equals(description)?"":" ")+ st.nextToken();
					    }

					}

					if (p !=null) description = description.replaceAll( "PLAYER"+s1, p.getName() );

					if ( s1 <= Players.size() ) { // || Players.size()==0 null but there for the map editor

						//System.out.print(description+"\n"); // testing
						Mission mission = new Mission(p, noc, noa, c1, c2, c3, description);
						Missions.add(mission);
					}
					else {
						//System.out.print("NOT adding this mission as it refures to an unused player\n"); // testing
					}

				}
				else if (mode.equals("newsection")) {

					mode = input.substring(1, input.length()-1); // set mode to the name of the section

					if (mode.equals("cards") ) {
						//System.out.print("Section: cards found\n"); // testing
					}
					else if (mode.equals("missions") ) {
						//System.out.print("Section: missions found\n"); // testing
					}
					else {
						throw new Exception("unknown section found in cards file: "+mode);
					}

				}
				else {

					throw new Exception("unknown item found in cards file: "+input);

				}

			}

			input = bufferin.readLine(); // get next line

		}
		bufferin.close();


		//System.out.print("Cards and missions loaded.\n");

	}

	private Continent getMissionContinentfromString(String a) {

		if (a.equals("*")) {
			return ANY_CONTINENT;
		}
		else {
			int s = Integer.parseInt(a);
			if (s==0) {
				return null;
			}
			else {
				return Continents[ s-1 ];
			}

		}
	}


	/**
	 * Sets the filename of the cards file
	 * @param f The name of the new file
	 * @return boolean Return trues if missions are supported
	 * @throws Exception The file cannot be found
	 */
	@SuppressWarnings("unused")
	public boolean setCardsfile(String f) throws Exception {


		StringTokenizer st=null;


		if (f.equals("default")) {
			f = defaultCards;
		}

		BufferedReader bufferin=RiskUtil.readMap(RiskUtil.openMapStream(f) );


/*

		File file;

		if (f.equals("default")) {
			file = new File("maps/" + defaultCards);
		}
		else {
			file = new File("maps/" + f);
		}


		FileReader filein = new FileReader(file);

		BufferedReader bufferin = new BufferedReader(filein);


*/

		String input = bufferin.readLine();
		String mode = "none";

		boolean yesmissions=false;
		boolean yescards=false;

		while(input != null) {

			if (input.equals("") || input.charAt(0)==';') {

			}
			else {

				if (input.charAt(0)=='[' && input.charAt( input.length()-1 )==']') {
					mode="newsection";
				}
				else { st = new StringTokenizer(input); }



				if (mode.equals("newsection")) {

					mode = input.substring(1, input.length()-1); // set mode to the name of the section

					if (mode.equals("cards")) {

						yescards=true;

					}
					else if (mode.equals("missions")) {

						yesmissions=true;

					}
				}


			}


			input = bufferin.readLine(); // get next line

		}


		if ( yescards==false ) { throw new Exception("error with cards file"); }

		cardsfile = f;
		bufferin.close();

		MapTranslator.setCards( f );

		return yesmissions;

	}

	/**
	 * Shuffles the countries
	 */
	public Vector<Country> shuffleCountries() {

		Vector oldCountries = new Vector(Arrays.asList( Countries ));

		//Vector newCountries = new Vector();
		//while(oldCountries.size() > 0) {
		//	int a = r.nextInt(oldCountries.size()) ;
		//	newCountries.add ( oldCountries.remove(a) );
		//}
		//return newCountries;

		Collections.shuffle(oldCountries);
		return oldCountries;

	}

	/**
	 * Creates a new game
	 * @return RiskGame Returns the new game created

	public static RiskGame newGame() {
		RiskGame game = new RiskGame();
		//System.out.print("Game State: "+game.getState()+"\n"); // testing
		return game;
	}
	 */

	/**
	 * Loads a saved game
	 * @param file The saved game's filename
	 * @return Riskgame Return the saved game object if it loads, returns null if it doe not load
	 */
	public static RiskGame loadGame(String file) throws Exception {
		RiskGame game = null;
		//try {
			InputStream filein = RiskUtil.getLoadFileInputStream(file);
			ObjectInputStream objectin = new ObjectInputStream(filein);
			game = (RiskGame) objectin.readObject();
			objectin.close();

			//XMLDecoder d = new XMLDecoder( new BufferedInputStream( new FileInputStream(file)));
			//game = (RiskGame)d.readObject();
			//d.close();

		//}
		//catch (Exception e) {
			//System.out.println(e.getMessage());
		//}
		return game;
	}



	/**
	 * Closes the current game
	 * @return Riskgame Returns the game, which is already set to null
	 * /
	public static RiskGame closeGame() {
		RiskGame game = null;
		return game;
	}
         */

	/**
	 * Saves the current game to a file
	 * @param file The filename of the save
	 * @return boolean Return trues if you saved, returns false if you cannot
	 */
	public void saveGame(String file) throws Exception { //added RiskGame parameter g, so remember to change in parser

            RiskUtil.saveFile(file,this);

            //XMLEncoder e = new XMLEncoder( new BufferedOutputStream( new FileOutputStream(file)));
            //e.writeObject(this);
            //e.close();
	}

	/**
	 * Gets the state of the game
	 * @return int Returns the game state
	 */
	public int getState() {
		return gameState;
	}

	/**
	 * Checks if there are any empty countries
	 * @return boolean Return trues if no empty countries, returns false otherwise
	 */
	public boolean NoEmptyCountries() {

		// find out if there are any empty countries

		Country empty=null;


		for (int c=0; c< Countries.length ; c++) {

			if ( Countries[c].getOwner() == null ) {
				empty = Countries[c];
				c=Countries.length;
			}

		}
		if (empty != null ) {
			return false;
		}
		else {
			return true;
		}

	}

	/**
	 * Checks if the set up is completely
	 * @return boolean Return trues if the set up is complete, returns false otherwise
	 */
	public boolean getSetup() {

		return ( setup == Players.size() );

		//if (setup != Players.size() ) {
		//	return false;
		//}
		//else {
		//	return true;
		//}

	}

	/**
	 * get the value od the trade-cap
	 * @return boolean Return trues if tradecap is true and false otherwise
	 */
	public boolean getTradeCap() {
		return tradeCap;
	}

	/**
	 * Gets the game mode
	 * @return int Return the game mode
	 */
	public int getGameMode() {
		return gameMode;
	}

	/**
	 * Gets the current player
	 * @return player Return the current player
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Gets all the players
	 * @return Vector Return all the players
	 */
	public Vector<Player> getPlayers() {
		return Players;
	}

	/**
	 * Gets all the players
	 * @return Vector Return all the players
	 */
	public Vector<Player> getPlayersStats() {

		for (int c=0; c< Players.size() ; c++) {
			workOutEndGoStats( (Player)Players.elementAt(c) );
		}

		return Players;
	}

	/**
	 * Gets the attacking country
	 * @return Country the attacking country
	 */
	public Country getAttacker() {
		return attacker;
	}

	/**
	 * Gets the defending country
	 * @return Country the defending country
	 */
	public Country getDefender() {
		return defender;
	}

	/**
	 * Gets the ImagePic
	 * @return URL ImagePic
	 */
	public String getImagePic() {
		return ImagePic;
	}

	public String getPreviewPic() {
		return previewPic;
	}

	public String getMapName() {
		return mapName;
	}
        public int getVersion() {
            return ver;
        }
        public void setVersion(int newVersion) {
            ver = newVersion;
        }

	/**
	 * Gets the ImageMap
	 * @return URL ImageMap
	 */
	public String getImageMap() {
		return ImageMap;
	}

	public String getCardsFile() {

		return cardsfile; //.getFile().substring( cardsfile.getFile().lastIndexOf("/")+1 );
	}

	public String getMapFile() {

		return mapfile; //.getFile().substring( mapfile.getFile().lastIndexOf("/")+1 );
	}

	public Vector<Card> getCards() {
		return Cards;
	}

	/**
	 * Rolls a certain number of dice
	 * @param nod Number of dice you want to roll
	 * @return int[] Returns an array which was the results of the roll, ordered from highest to lowest
	 */
	public int[] rollDice(int nod) {

		int[] dice = new int[nod];

		for (int j=0; j<nod; j++) {
			dice[j]=r.nextInt( 6 );
		}

		// NOW SORT THEM, biggest at the beggining
		for (int i=0; i<nod-1; i++) {
			int temp, pos=i;

			for(int j=i+1; j<nod; j++)
				if(dice[j]>dice[pos])
					pos=j;
			temp = dice[i];
			dice[i] = dice[pos];
			dice[pos] = temp;
		}

/*
System.out.print("After sorting, the dice are:\n");

String str="[";
if(dice.length>0) {
str+=(dice[0]+1);
for(int i=1; i<dice.length; i++)
str+="|"+(dice[i]+1);
}
System.out.print(str+"]\n");
*/
		return dice;

	}

	/**
	 * Gets the number of continents which are owned by a player
	 * @param p The player you want to find continents for
	 * @return int Return the number of continents a player owns
	 */
	public int getNumberContinentsOwned(Player p) {

		int total=0;

		for (int c=0; c< Continents.length ; c++) {

			if ( Continents[c].isOwned(p) ) {
				total++;
			}

		}
		return total;
	}

	/**
	 * Gets a country
	 * @param name The name of the country
	 * @return Country Return the country you are looking for, if it exists. Otherwise returns null
	 *
	// * @deprecated

	public Country getCountry(String name) {

		for (int c=0; c< Countries.length ; c++) {

			if ( name.equals(Countries.[c].getName()) ) {
				return Countries[c];
			}

		}
		System.out.println( "ERROR: Country not found: " + name );
		return null;

	}
	 */


	/**
	 * Tries to find a country by its name.
	 * This function should only be used if a user has entered the name manually!
	 *
	 * @param name The name of the country
	 * @return Country Return the country you are looking for, if it exists. Otherwise returns null

	public Country getCountryByName(String name) {

		for (int c=0; c< Countries.length ; c++) {

			if ( name.equals(Countries[c].getName()) ) {
				return Countries[c];
			}

		}
		System.out.println( "ERROR: Country not found: " + name );
		return null;

	}//public Country getCountryByName(String name)
	 */


	/**
	 * returns the country with the given color (ID)
	 */
	public Country getCountryInt(int color) {

		if (color <= 0 || color > Countries.length ) { return null; }
		else return Countries[color-1];

	}



	/**
	 * returns the country with the given color (ID)
	 * the string is converted to an int value

	public Country getCountryInt(String strId)
	{
		int nId = -1;
		try {
			nId = Integer.parseInt( strId);
		} catch( NumberFormatException e) {
			System.out.println( "ERROR: Can't convert number \"" + strId + "\" to a number." );
			return null;
		}

		return getCountryInt(nId);
	}//public Country getCountryInt(String nId)
	 */


	/**
	 * Gets a cards
	 * @param name
	 * @return Card Return the card you are looking for, if it exists. Otherwise returns null
	 */
	public Card[] getCards(String name1,String name2,String name3) {

		Card[] c = new Card[3];

		Vector playersCards = new Vector( currentPlayer.getCards() );

		jumppoint: for (int a=0;a<3;a++) {

			String name;

			if (a==0) { name = name1; }
			else if (a==1) { name = name2; }
			else { name = name3; } // if (a==2)

			for (int b=0; b< playersCards.size(); b++) {

				if (name.equals(Card.WILDCARD) && name.equals( ((Card)playersCards.elementAt(b)).getName() ) ) {
					c[a] = (Card) playersCards.remove(b);
					continue jumppoint;
				}
				else if ( (Country)((Card)playersCards.elementAt(b)).getCountry() != null && name.equals( ((Country)((Card)playersCards.elementAt(b)).getCountry()).getColor()+"" ) ) {
					c[a] = (Card) playersCards.remove(b);
					continue jumppoint;
				}

			}

		}

		return c;

	}

	public Card findCard(String name) {

		for (int c=0; c< Cards.size() ; c++) {

			if (name.equals(Card.WILDCARD) && name.equals( ((Card)Cards.elementAt(c)).getName() ) ) {
				return ((Card)Cards.elementAt(c));
			}
			else if ( (Country)((Card)Cards.elementAt(c)).getCountry() != null && name.equals( ((Country)((Card)Cards.elementAt(c)).getCountry()).getColor()+"" ) ) {
				return ((Card)Cards.elementAt(c));
			}

		}
		return null;

	}

	/**
	 * Gets a cards
	 * @param s The number you want to parse
	 * @return int The number you wanted
	 * @throws NumberFormatException You cannot parse the string
	 */
	public static int getNumber(String s) {
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Gets the number of players in the game
	 * @return int Return the number of number of players
	 */
	public int getNoPlayers() {
		return Players.size();
	}

	/**
	 * Gets the countries in the game
	 * @return Vector Return the Countries in the current game
	 */
	public Country[] getCountries() {

		return Countries;
	}

	/**
	 * Gets the continents in the game
	 * @return Vector Return the Continents in the current game
	 */
	public Continent[] getContinents() {
		return Continents;
	}

	/**
	 * Gets the number of countries in the game
	 * @return int Return the number of countries in the current game
	 */
	public int getNoCountries() {
		return Countries.length;
	}

	public int getNoContinents() {

		return Continents.length;

	}

	/**
	 * Gets the allocated Missions in the game
	 * @return Vector Return the Missions in the current game
	 */
	public Vector getMissions() {
		return Missions;
	}

	/**
	 * Gets the number of Missions in the game
	 * @return int Return the number of Missions in the game
	 */
	public int getNoMissions() {
		return Missions.size();
	}

	public int getNoCards() {
		return Cards.size();
	}

	/**
	 * Set the Default Map and Cards File
	 */
	public static void setDefaultMapAndCards(String a,String b) {

		defaultMap=a;
		defaultCards=b;

		// not needed as is reset each time a new RiskGame object is created
		//net.yura.domination.engine.translation.MapTranslator.setMap( a );
		//net.yura.domination.engine.translation.MapTranslator.setCards( b );

	}


	public static String getDefaultMap() {
		return defaultMap;
	}
	public static String getDefaultCards() {
		return defaultCards;
	}

	/**
	 * @return the current Card Mode
	 */
	public int getCardMode()
	{
	 return cardMode;
	}

	public static int getRandomColor() {

		return HSBtoRGB( (float)Math.random(), 0.5F, 1.0F );

	}

     /**
      * copy and paste from
      * @see java.awt.Color#HSBtoRGB(float, float, float)
      */
    public static int HSBtoRGB(float hue, float saturation, float brightness) {
	int r = 0, g = 0, b = 0;
    	if (saturation == 0) {
	    r = g = b = (int) (brightness * 255.0f + 0.5f);
	} else {
	    float h = (hue - (float)Math.floor(hue)) * 6.0f;
	    float f = h - (float)java.lang.Math.floor(h);
	    float p = brightness * (1.0f - saturation);
	    float q = brightness * (1.0f - saturation * f);
	    float t = brightness * (1.0f - (saturation * (1.0f - f)));
	    switch ((int) h) {
	    case 0:
		r = (int) (brightness * 255.0f + 0.5f);
		g = (int) (t * 255.0f + 0.5f);
		b = (int) (p * 255.0f + 0.5f);
		break;
	    case 1:
		r = (int) (q * 255.0f + 0.5f);
		g = (int) (brightness * 255.0f + 0.5f);
		b = (int) (p * 255.0f + 0.5f);
		break;
	    case 2:
		r = (int) (p * 255.0f + 0.5f);
		g = (int) (brightness * 255.0f + 0.5f);
		b = (int) (t * 255.0f + 0.5f);
		break;
	    case 3:
		r = (int) (p * 255.0f + 0.5f);
		g = (int) (q * 255.0f + 0.5f);
		b = (int) (brightness * 255.0f + 0.5f);
		break;
	    case 4:
		r = (int) (t * 255.0f + 0.5f);
		g = (int) (p * 255.0f + 0.5f);
		b = (int) (brightness * 255.0f + 0.5f);
		break;
	    case 5:
		r = (int) (brightness * 255.0f + 0.5f);
		g = (int) (p * 255.0f + 0.5f);
		b = (int) (q * 255.0f + 0.5f);
		break;
	    }
	}
	return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
    }
   
}
