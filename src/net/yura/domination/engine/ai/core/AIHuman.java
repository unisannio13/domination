package net.yura.domination.engine.ai.core;

import net.yura.domination.engine.ai.AI;
import net.yura.domination.engine.ai.Discoverable;

/**
 * Utilizzata solo per indicare che un certo giocatore è umano
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
@Discoverable
public class AIHuman extends AI {


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
