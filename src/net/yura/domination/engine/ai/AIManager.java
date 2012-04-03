package net.yura.domination.engine.ai;

import java.util.Collection;
import java.util.HashMap;

/**
 * Utilizzato per integrare facilmente nuove AI nel gioco
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class AIManager {
	private static HashMap<String, AI> AIs = new HashMap<String, AI>();
	
	/**
	 * Utilizzato per integrare le Ai nel gioco
	 * Per far ci&ograve, instanziare una AI ed aggiungerla alla mappa.
	 * &Egrave; preferibile usare i metodi addAI o addAIs dato che
	 * fanno qualche controllo sulla validità dell'AI 
	 * 
	 */
	public static void setup(){
		addAIs(
			new AIHuman(),
			new AICrap().setID("ai crap").setName("AI Crap"),
			new AIEasy().setID("ai easy").setName("AI Easy"),
			new AIHard().setID("ai hard").setName("AI Hard").setCapitalAI(new AIHardCapital()).setMissionAI(new AIHardMission())
		);
	}
	
	/**
	 * Viene utilizzato quando l'engine risolve l'AI a partire dall'id
	 * @param id
	 * @return
	 */
	public static AI getAI(String id){
		return AIs.get(id);
	}
	
	/**
	 * Aggiunge una AI all'AIManager controllando che l'AI sia valida
	 * 
	 * @param ai
	 */
	public static void addAI(AI ai){
		try{
			ai.getName().toString(); //Serve solo per far scaturire un eventuale NullPointerExeption
			String id = ai.getId();
			if (AIs.containsKey(id))
				throw new IllegalArgumentException("Esiste già una AI con id: "+id);
			AIs.put(id, ai);
		}catch (NullPointerException e) {
			throw new IllegalArgumentException("L'AI deve avere nome e id non null");
		}
	}
	
	/**
	 * Aggiunge più AI
	 * 
	 * @param ais
	 */
	public static void addAIs(AI... ais){
		for (AI ai: ais)
			addAI(ai);
	}
	
	/**
	 * Viene utilizzato dall'interfaccia grafica per visualizzare le AI disponibili
	 * @return
	 */
	public static Collection<AI> getAIs(){
		return AIs.values();
	}
	
}
