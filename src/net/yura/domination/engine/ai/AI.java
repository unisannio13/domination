package net.yura.domination.engine.ai;

import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;

/**
 * Classe astratta che fa da superclasse a tutte le AI
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public abstract class AI {
	
	protected String name;
	protected String id;
	protected AI aiCapital, aiMission;
	protected RiskGame game;
	protected Player player;
	
	public AI() {
		id = "ai "+this.getClass().getSimpleName().toLowerCase().substring(2);
		name = this.getClass().getSimpleName().substring(2);	
		
		if(this instanceof EnemyCommandsListener)
			EnemyCommandsEventSource.addEnemyCommandsListener((EnemyCommandsListener) this);
	}
	
	public void onInit() {}
	
	public RiskGame getGame() {
		return game;
	}

	public void setGame(RiskGame game) {
		this.game = game;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public String getName() {
		return name;
	}

	public String getId() {		
		return id;
	}
	
	public AI getCapitalAI() {
		return aiCapital;
	}

	/**
	 * Utilizzato per fornire all'AI una implementazione apposita per la modalità "Capital"
	 * @param aiCapital
	 * @return this
	 */
	public AI setCapitalAI(AI aiCapital) {
		this.aiCapital = aiCapital;
		return this;
	}

	/**
	 * Utilizzato per fornire all'AI una implementazione apposita per la modalità "Mission"
	 * @param aiCapital
	 * @return this
	 */
	public AI getMissionAI() {
		return aiMission;
	}

	public AI setMissionAI(AI aiMission) {
		this.aiMission = aiMission;
		return this;
	}
	
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Chiamato durante la fase in cui vengono giocate le carte.
	 * 
	 * @return String che descrive l'operazione da compiere:
	 *         <ul>
	 *         <li>"<i>endtrade</i>" per non giocare alcuna carta.</li>
	 *         <li>"<i>trade [carta1] [carta2] [carta3]</i>" <br>
	 *         dove carta1, carta2 e carta3 sono strighe che descriveno le carte
	 *         da giocare e vanno fornite nel seguente modo:
	 *         <ul>
	 *         <li>"<i>wildcard</i>" per una wild card ovvero la carta jolly
	 *         contenente tutti e 3 i simboli</li>
	 *         <li>il “color” (un numero univoco) della nazione rappresentata
	 *         sulla carta.</li></li>
	 *         </ul>
	 *         </ul>
	 */
	public abstract String getTrade();

	/**
	 * Chiamato durante la fase di piazzamento delle armate. Per distinguere la
	 * fase di piazzamento iniziale da quella che avviene prima di ogni attacco,
	 * si utilizza il metodo: <br>
	 * public boolean NoEmptyCountries() da applicare sull’oggetto game.
	 * 
	 * @return String che descrive l'operazione da compiere:
	 *         <ul>
	 *         <li>Durante il piazzamento iniziale (viene piazzata un’armata
	 *         alla volta):</li>
	 *         <ul>
	 *         <li>"<i>autoplace</i>" per piazzare un’armata su di una nazione
	 *         scelta a random</li>
	 *         <li>"<i>placearmies [nazione] 1</i>" per piazzare un’armata su di
	 *         una nazione specifica</li>
	 *         </ul>
	 *         <li>Durante il piazzamento pre-combattimento:</li>
	 *         <ul>
	 *         <li>"<i>placearmies [nazione] [numeroArmate]</i>" piazza un certo
	 *         numero di armate su di una nazione specifica.</li>
	 *         </ul>
	 *         </ul> Per [nazione] si intende il “color” che identifica
	 *         univocamente la nazione.
	 */
	public abstract String getPlaceArmies();

	/**
	 * Chiamato durante la fase d’attacco per determinare la nazione da
	 * attaccare.
	 * 
	 * @return String che descrive l'operazione da compiere:
	 *         <ul>
	 *         <li>"<i>endattack</i>" per terminare la fase d’attacco;</li>
	 *         <li>"<i>attack [nazioneAttaccante] [nazioneDaAttaccare]</i>" per
	 *         attaccare una nazione;<br>
	 *         Per specificare [nazioneAttaccante] e [nazioneDaAttaccare] deve
	 *         essere fornito il “color” che identifica univocamente la nazione.
	 *         </li>
	 *         <ul>
	 */
	public abstract String getAttack();

	/**
	 * Chiamato durante la fase di attacco per definire il numero di dadi da
	 * utilizzare per l'attacco. Il numero di dadi deve essere >=1 e <=3.
	 * 
	 * @return String che descrive l'operazione da compiere:
	 *         <ul>
	 *         <li>"<i>retreat</i>" per ritirarsi dall’attacco;</li>
	 *         <li>"<i>roll [numeroDadi]</i>" per tirare un certo numero di
	 *         dadi;</li>
	 *         </ul>
	 */
	public abstract String getRoll();

	/**
	 * Chiamato successivamente alla vincita di una battaglia per determinare il
	 * numero di armate da spostare nella nazione appena conquistata.
	 * 
	 * @return String che descrive l'operazione da compiere:
	 *         <ul>
	 *         <li>"<i>move all</i>" per spostare tutte le armate (tranne una)
	 *         sulla nazione appena conquistata;</li>
	 *         <li>"<i>move [numeroArmate]” per spostare un certo numero di
	 *         armate.<br>
	 *         Bisogna aver cura del fatto che almeno una armata DEVE essere
	 *         lasciata sulla nazione attaccante.</li>
	 *         </ul>
	 */
	public abstract String getBattleWon();

	/**
	 * Chiamato nella fase di fortificazione successiva alla fase d’attacco.
	 * Prevede lo spostamento di un certo numero di truppe da una nazione ad
	 * un'altra limitrofa.
	 * 
	 * @return String che descrive l'operazione da compiere:
	 *         <ul>
	 *         <li>"<i>nomove</i>" per non spostare alcuna armata;</li>
	 *         <li>"<i>movearmies [nazioneOrigine] [nazioneDestinazione]
	 *         [numeroArmate]” per spostare un certo numero di armate da una
	 *         nazione ad un’altra.<br>
	 *         Bisogna aver cura del fatto che almeno una armata DEVE essere
	 *         lasciata sulla nazione attaccante.<br>
	 *         Per specificare [nazioneOrigine] e [nazioneDestinazione] deve
	 *         essere fornito il “color” che identifica univocamente la nazione.
	 *         </li>
	 *         </ul>
	 */
	public abstract String getTacMove();

	/**
	 * Chiamato per definire il numero di dadi da utilizzare per difendersi da
	 * un attacco. Il numero di dadi deve essere >=1 e <=2.
	 * 
	 * @return String che descrive l'operazione da compiere:
	 *         <ul>
	 *         <li>"<i>roll [numeroDadi]</i>" per tirare un certo numero di
	 *         dadi.</li>
	 *         </ul>
	 */
	public abstract String getAutoDefendString();

	public abstract String getCapital();



}
