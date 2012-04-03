package net.yura.domination.engine.ai;

/**
 * Utilizzata solo per indicare che un certo giocatore è umano
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class AIHuman extends AI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AIHuman(){
		id = "human";
		name = "Umano";
	}
	
	@Override
	public String getTrade() {
		return null;
	}

	@Override
	public String getPlaceArmies() {
		return null;
	}

	@Override
	public String getAttack() {
		return null;
	}

	@Override
	public String getRoll() {
		return null;
	}

	@Override
	public String getBattleWon() {
		return null;
	}

	@Override
	public String getTacMove() {
		return null;
	}

	@Override
	public String getAutoDefendString() {
		return null;
	}

	@Override
	public String getCapital() {
		return null;
	}

}
