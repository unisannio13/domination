package net.yura.domination.engine.ai;

import java.util.*;

import net.yura.domination.engine.core.Card;
import net.yura.domination.engine.core.Continent;
import net.yura.domination.engine.core.Country;

public class AISimple extends AI{
	private static final long serialVersionUID = -5026514597239780686L;

	@SuppressWarnings("unchecked")
	@Override
	public String getTrade() {
		Vector<Card> cards = player.getCards();
		if(cards.size()<3) return "endtrade";

		List<Card> wildcards = new LinkedList<Card>();
		List<Card> cannons = new LinkedList<Card>();
		List<Card> infantry = new LinkedList<Card>();
		List<Card> cavalry = new LinkedList<Card>();
		for(Card c:cards){
			if(c.getName().equals(Card.WILDCARD)) wildcards.add(c);
			else if(c.getName().equals(Card.CANNON)) cannons.add(c);
			else if(c.getName().equals(Card.CAVALRY)) cavalry.add(c);
			else if(c.getName().equals(Card.INFANTRY)) infantry.add(c);
			
		}
		if (wildcards.size()>0){
			if(cannons.size()>1) return "trade wildcard "+ cannons.get(0).getCountry().getColor() + " " + cannons.get(1).getCountry().getColor();
			if(cavalry.size()>1) return "trade wildcard "+ cavalry.get(0).getCountry().getColor() + " " + cavalry.get(1).getCountry().getColor();
			if(infantry.size()>1) return "trade wildcard "+ infantry.get(0).getCountry().getColor() + " " + infantry.get(1).getCountry().getColor();
		}
		if(cannons.size()>0 && cavalry.size()>0 && infantry.size()>0)
			return "trade "                                	
				+ cannons.get(0).getCountry().getColor()+" "+ cavalry.get(0).getCountry().getColor()+" " + infantry.get(0).getCountry().getColor();
		if(cavalry.size()>2) 
			return "trade "+cavalry.get(0).getCountry().getColor() + " " + cavalry.get(1).getCountry().getColor()+ " " + cavalry.get(2).getCountry().getColor();
		if(infantry.size()>2)
			return "trade "+infantry.get(0).getCountry().getColor() + " " + infantry.get(1).getCountry().getColor()+ " "+infantry.get(2).getCountry().getColor();
		if(cannons.size()>2)
			return "trade "+cannons.get(0).getCountry().getColor() + " " + cannons.get(1).getCountry().getColor()+" "+cannons.get(2).getCountry().getColor();
		return "endtrade";
	}
	
	@SuppressWarnings("unchecked")
	public String getInitialArmyPlacement() {
		Continent[] continents = game.getContinents();
		
		Country[] candidates = new Country[continents.length];
		int[] candidateScores = new int[continents.length];
		int[] freeCountries = new int[continents.length];
		
		for(int i = 0; i < continents.length; ++i){
			for(Country country:(Vector<Country>)continents[i].getTerritoriesContained()) {
				if(country.getOwner() == null) {
					freeCountries[i]++;
					
					int score = 0;
					for(Country neightbour: (Vector<Country>) country.getNeighbours()) {
						if(neightbour.getOwner() == player || neightbour.getContinent() != continents[i])
							score++;
					}
					
					if(score > candidateScores[i] || candidates[i] == null) {
						candidateScores[i] = score;
						candidates[i] = country;
					}	
				}
			}
		}
		
		int lowestCount = 100;
		int bestContinent = 0;
		for(int i = 0; i < freeCountries.length; ++i) {
			if(freeCountries[i] > 0 && freeCountries[i] < lowestCount) {
				lowestCount = freeCountries[i];
				bestContinent = i;
			}
		}
		
		return "placearmies " + candidates[bestContinent].getColor() + " 1";
	}

	@Override
	public String getPlaceArmies() {
		return game.NoEmptyCountries() ? getArmyPlacement() : getInitialArmyPlacement();

	}

	public String getArmyPlacement() {
		Continent[] continents = game.getContinents();
		final Map<Continent, Set<Country>> accessPoints = new HashMap<Continent, Set<Country>>();
		
		
		for(Continent continent : continents) {
			accessPoints.put(continent, getConfiningTerritories(continent));
		}
		
		Arrays.sort(continents, new Comparator<Continent>() {

			@Override
			public int compare(Continent arg0, Continent arg1) {
				int comparation = new Float(getForceRatio(arg0)).compareTo(getForceRatio(arg1));
				return comparation == 0
					? new Integer(accessPoints.get(arg0).size()).compareTo(accessPoints.get(arg1).size())
					: comparation;
			}
			
		});
		
		int extraArmies = player.getExtraArmies();
		int defendableContinents = continents.length;
		float defendQuota = 0;
		
		for(int i = defendableContinents; i > 0; --i) {
			float totalThreatLevel = 0;
			float totalAccessPoints = 0;
			
			for(int j = 0; j < defendableContinents; ++j) {
				totalThreatLevel += getThreatLevel(accessPoints.get(continents[j]));
				totalAccessPoints += accessPoints.get(continents[j]).size();
			}
			
			defendQuota = totalThreatLevel / totalAccessPoints;
			if(defendQuota < 1.0)
				break;
		}
		
		
		
		List<Country> borders = new LinkedList<Country>();
		for(int i = 0; i < defendableContinents; ++i) {
			borders.addAll(accessPoints.get(continents[i]));
		}
		
		Collections.sort(borders, new Comparator<Country>() {

			@Override
			public int compare(Country arg0, Country arg1) {
				return getDefenseNeeded(arg1) - getDefenseNeeded(arg0);
			}
		});
		
		int armiesForDefense = (int) Math.floor(defendQuota * extraArmies);
		if(armiesForDefense > 0) {			
			Country mostNeedful = borders.get(0);
			return "placearmies " + mostNeedful.getColor() + " 1";// + Math.min(getDefenseNeeded(mostNeedful), armiesForDefense);
		}
		
		Country strongest = borders.get(borders.size() - 1);
		return "placearmies " + strongest.getColor() + " 1";// + extraArmies;
	}
	
	private int getThreatLevel(Set<Country> countries) {
		int threatLevel = 0;
		for(Country country : countries) {
			if(getDefenseNeeded(country) > 0)
				threatLevel++;
		}
		
		return threatLevel;
	}
	
	@SuppressWarnings("unchecked")
	private int getDefenseNeeded(Country country) {
		if(country.getOwner() != player) 
			return 0;
		
		int neededArmies = 0, countryArmies = country.getArmies();
		for(Country neighbour : (Vector<Country>) country.getNeighbours()) {
			if(neighbour.getOwner() != player && neighbour.getArmies() >= countryArmies + neededArmies) 
				neededArmies = neighbour.getArmies() - countryArmies + 1;
		}
		
		return neededArmies;
	}
	
	@SuppressWarnings("unchecked")
	private Set<Country> getConfiningTerritories(Continent continent) {
		LinkedList<Country> toVisit = new LinkedList<Country>();
		Set<Country> visited = new HashSet<Country>();
		Set<Country> confiningTerritories = new HashSet<Country>();
		
		toVisit.addAll(continent.getTerritoriesContained());
		while(toVisit.size() > 0) {
			Country country = toVisit.remove();
			if(country.getOwner() != player) 
				continue;
			
			Vector<Country> neighbours = country.getNeighbours();
			for(Country neighbour : neighbours) {
				visited.add(neighbour);
				if(neighbour.getOwner() != player)
					confiningTerritories.add(country);
				else if(!visited.contains(neighbour))
					toVisit.addLast(neighbour);
			}
		}
		
		return confiningTerritories;
	}
	
	@SuppressWarnings("unchecked")
	private float getForceRatio(Continent arg0) {
		float myForces = 0.0f;
		float otherForces = 0.0f;
		for(Country c : (Vector<Country>) arg0.getTerritoriesContained()) {
			if(c.getOwner() == player) {
				myForces += c.getArmies();
			} else {
				otherForces += c.getArmies();
			}
		}
		
		return myForces / (myForces + otherForces);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getAttack() {
		Vector<Country> countries = player.getTerritoriesOwned();
		Country attacker = null;
		Country defender = null;
		for(Country c : countries){
			Vector<Country> neighbours = c.getNeighbours();
			for(Country n: neighbours){
				if(n.getOwner()!=player){
					if(attacker == null && defender == null){
						attacker = c;
						defender = n;
					}
					if (((c.getArmies()-1) - n.getArmies()) > ((attacker.getArmies()-1) - defender.getArmies())){
						attacker = c;
						defender = n;
					}
				}
			}	
		}
		if(attacker==null || ((attacker.getArmies()-1) - defender.getArmies()) < 1 )
			return "endattack";
		return "attack "+attacker.getColor() + " " + defender.getColor();
		
	}

	@Override
	public String getRoll() {
		return "roll "+ Math.min(game.getAttacker().getArmies()-1, 3);
	}

	@Override
	public String getBattleWon() {
		return "move all";
	}

	@Override
	public String getTacMove() {
		return "nomove";
	}

	@Override
	public String getAutoDefendString() {
    	return "roll "+ Math.min(game.getDefender().getArmies(), game.getMaxDefendDice());
	}

	@Override
	public String getCapital() {
		// TODO Auto-generated method stub
		return null;
	}

}
