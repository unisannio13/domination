// Yura Mamyrin

package net.yura.domination.engine.ai;

import java.util.Vector;

import net.yura.domination.engine.ai.api.BaseAI;
import net.yura.domination.engine.ai.api.Discoverable;
import net.yura.domination.engine.ai.commands.Attack;
import net.yura.domination.engine.ai.commands.Fortification;
import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Player;

/**
 * <p> Class for AIEasyPlayer </p>
 * @author Yura Mamyrin
 */

@Discoverable
@SuppressWarnings({ "unchecked", "rawtypes" })
public class AIEasy extends BaseAI {

	protected class OwnAttack {
	public Country source;
	public Country destination;

	public OwnAttack(Country s, Country d){
	    source=s;
	    destination=d;
	}
	public String toString(){
	    if (source == null || destination == null) { return ""; }
	    return "attack " + source.getColor() + " " + destination.getColor();
	}

    }

    @Override
    protected Attack onAttack() {
	//Vector t = player.getTerritoriesOwned();
	Vector outputs = new Vector();
	OwnAttack move;

	/*  // Extract method: findAttackableNeighbors() 
	Vector n;
	for (int a=0; a< t.size() ; a++) {
	    if ( ((Country)t.elementAt(a)).getArmies() > 1 ) {
		n = ((Country)t.elementAt(a)).getNeighbours();
		for (int b=0; b< n.size() ; b++) {
		    if ( ((Country)n.elementAt(b)).getOwner() != player ) {
			outputs.add( "attack " + ((Country)t.elementAt(a)).getColor() + " " + ((Country)n.elementAt(b)).getColor() );
		    }
		}
	    }
	}  */
	outputs = findAttackableNeighbors(player.getTerritoriesOwned(),0);
	if (outputs.size() > 0) {
		move = (OwnAttack) outputs.elementAt( (int)Math.round(Math.random() * (outputs.size()-1) ) );
		//System.out.println(player.getName() + ": "+ move.toString());    //TESTING
		return new Attack(move.source, move.destination);
		//return (String)outputs.elementAt( (int)Math.round(Math.random() * (outputs.size()-1) ) );
	}
	return null;
    }


    /******************
     * Helper Methods *
     ******************/

    /************
     * @name findAttackableNeighbors
     * @param t Vector of teritories
     * @param ratio - threshold of attack to defence armies to filter out
     * @return a Vector of possible attacks for a given list of territories
     * 	where the ratio of source/target armies is above ratio
     **************/
    public Vector findAttackableNeighbors(Vector t, double ratio){
	Vector output = new Vector();
	Vector n=new Vector();
    	Country source,target;
	if (ratio<0) { ratio = 0;}
	for (int a=0; a< t.size() ; a++) {
	    source=(Country)t.elementAt(a);
	    if ( source.getOwner() == player && source.getArmies() > 1 ) {
		n = source.getNeighbours();
		for (int b=0; b< n.size() ; b++) {
		    target=(Country)n.elementAt(b);
		    if ( target.getOwner() != player && 
			( (double)(source.getArmies()/target.getArmies()) > ratio) 
		      	) {     // simplify logic
			//output.add( "attack " + source.getColor() + " " + target.getColor() );
			output.add(new OwnAttack(source,target));
		    }
		}
	    }
	}
	return output;
    }

    /************
     * @name findAttackableNeighbors
     * @param t Vector of teritories
     * @param ratio - threshold of attack to defence armies to filter out
     * @return a Vector of possible attacks for a given list of territories
     * 	where the ratio of source/target armies is above ratio
     **************/
    public Vector getPossibleAttacks(Vector t){
	Vector output = new Vector();
	Vector n=new Vector();
    	Country source,target;
	for (int a=0; a< t.size() ; a++) {
	    source=(Country)t.elementAt(a);
	    if ( source.getOwner() == player && source.getArmies() > 1 ) {
		n = source.getNeighbours();
		for (int b=0; b< n.size() ; b++) {
		    target=(Country)n.elementAt(b);
		    if ( target.getOwner() != player ) {     // simplify logic
			//output.add( "attack " + source.getColor() + " " + target.getColor() );
			output.add(new OwnAttack(source,target));
		    }
		}
	    }
	}
	return output;
    }

    /*******************
     * @name filterAttacks
     * @param options - Vector of Attacks
     * @param advantage - how much of an absolute advantage to have
     * @return Vector of attacks with specified advantage
     *******************/

    public Vector filterAttacks(Vector options, int advantage){
	OwnAttack temp = null;
	Vector moves = new Vector();
	for(int j=0; j<options.size(); j++){
		temp=(OwnAttack)options.get(j);
		if ( ( ((Country)temp.source).getArmies() - ((Country)temp.destination).getArmies()) > advantage) {
			moves.add(temp);
		}
	}
	return moves;
    }

	@Override
	protected Country onCountryFortification() {
		Vector t = player.getTerritoriesOwned();
	    String name=null;
		name = findAttackableTerritory(player);
		if ( name == null ) {
		return ((Country)t.elementAt(0));
	    }

		return game.getCountryInt(Integer.valueOf(name)) ;
	    
	}
	
	@Override
	protected Fortification onFortification() {
		return new Fortification(onCountryFortification(), player.getExtraArmies());
	}

	public String findAttackableTerritory(Player p) {
    	Vector countries = p.getTerritoriesOwned();
    	
    	for (int i=0; i<countries.size(); i++) {
    		Vector neighbors = ((Country)countries.elementAt(i)).getNeighbours();
    		for (int j=0; j<neighbors.size(); j++) {
    			if (((Country)neighbors.elementAt(j)).getOwner() != p) {
    				if ((p.getCapital() != null && ((Country)countries.elementAt(i)).getColor() != p.getCapital().getColor()) || p.getCapital() == null)
    					return ((Country)countries.elementAt(i)).getColor()+"";
    			}
    		}
    	}
    	
    	return null;
    }
}
