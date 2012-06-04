package net.yura.domination.engine.ai;

import net.yura.domination.engine.core.Player;

/**
 * Interfaccia che permette di ricevere informazioni riguardo alle mosse eseguite 
 * dagli avversari
 * 
 * @author danilo
 *
 */
public interface EnemyCommandsListener {
	
	/**
	 * Chiamato quando un avversario effettua una mossa
	 * 
	 * @param enemy Il giocatore avversario che ha effettuato una mossa
	 * @param command La stringa che identifica la mossa effettuata
	 */
	public void onEnemyCommand(Player enemy, String command);
}
