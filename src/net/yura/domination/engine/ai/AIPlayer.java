package net.yura.domination.engine.ai;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import net.yura.domination.engine.Risk;
import net.yura.domination.engine.ai.core.AICrap;
import net.yura.domination.engine.core.RiskGame;

public class AIPlayer {

	private static int wait=500;
	private static int timeout = 30;

	public static int getWait() {
		return wait;

	}
	public static void setWait(int w) {
		wait = w;
	}

	public static void play(Risk risk) {

		final RiskGame game = risk.getGame();
		
		AI thisAI = null;
		AI ai = game.getCurrentPlayer().getAI();
		int mode = game.getGameMode();
		if (mode == RiskGame.MODE_DOMINATION)
			thisAI = ai;
		else if (mode == RiskGame.MODE_CAPITAL && ai.getCapitalAI() != null)
			thisAI = ai.getCapitalAI();
		else if (mode == RiskGame.MODE_SECRET_MISSION && ai.getMissionAI() != null)
			thisAI = ai.getMissionAI();

		final AI usethisAI = thisAI;
		

		FutureTask<String> future = new FutureTask<String>(new Callable<String>() {
			   public String call() {
			       return getOutput(game,usethisAI);
			   }
			});
		
		new Thread(future).start();		
		
		String output = null;
		try{
			output = future.get(timeout, TimeUnit.SECONDS);
		} catch (Exception ex) {
			output = getOutput(game, new AICrap());
		}

		try { Thread.sleep(wait); }
		catch(InterruptedException e) {}

		risk.parser(output);

	}


	public static int getTimeout() {
		return timeout;
	}
	public static void setTimout(int timeout) {
		AIPlayer.timeout = timeout;
	}
	public static String getOutput(RiskGame game,AI usethisAI) {
		game.NoEmptyCountries();
		usethisAI.setGame(game);
		usethisAI.setPlayer(game.getCurrentPlayer());

		String output=null;

		switch ( game.getState() ) {
			case RiskGame.STATE_TRADE_CARDS:	output = usethisAI.getTrade(); break;
			case RiskGame.STATE_PLACE_ARMIES:	output = usethisAI.getPlaceArmies(); break;
			case RiskGame.STATE_ATTACKING:		output = usethisAI.getAttack(); break;
			case RiskGame.STATE_ROLLING:		output = usethisAI.getRoll(); break;
			case RiskGame.STATE_BATTLE_WON:		output = usethisAI.getBattleWon(); break;
			case RiskGame.STATE_FORTIFYING:		output = usethisAI.getTacMove(); break;
			case RiskGame.STATE_SELECT_CAPITAL:	output = usethisAI.getCapital(); break;

			case RiskGame.STATE_END_TURN:		output = "endgo"; break;
			case RiskGame.STATE_GAME_OVER:		/* output="closegame"; */ break;
			case RiskGame.STATE_DEFEND_YOURSELF:	output = usethisAI.getAutoDefendString(); break;

			default: throw new RuntimeException("AI error: unknown state "+ game.getState() );
		}

		if (output==null) { throw new NullPointerException("AI ERROR!"); }

		return output;

	}



}
