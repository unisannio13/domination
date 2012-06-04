package net.yura.domination.engine.ai;

import java.util.LinkedList;
import java.util.List;

import net.yura.domination.engine.core.Player;

public class EnemyCommandsEventSource {
	private static List<EnemyCommandsListener> listeners = new LinkedList<EnemyCommandsListener>();


	public static void addEnemyCommandsListener(EnemyCommandsListener listener){
		listeners.add(listener);
	}


	public static void removeEnemyCommandsListener(EnemyCommandsListener listener){
			listeners.remove(listener);
	}

	public static void fireEnemyCommandsEvent(Player enemy, String command){
		for(EnemyCommandsListener listener: listeners){
			if(listener instanceof AI){
				AI ai = (AI) listener;
				if(!ai.getPlayer().equals(enemy))
					listener.onEnemyCommand(enemy, command);
			}else
				listener.onEnemyCommand(enemy, command);
		}
	}
}
