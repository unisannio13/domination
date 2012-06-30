package net.yura.domination.engine.ai.commands;

import net.yura.domination.engine.core.AbstractPlayer;
import net.yura.domination.engine.core.AbstractRiskGame;

public interface Command {
	String toCommand(AbstractRiskGame<?, ?, ?> game, AbstractPlayer<?> player);
}