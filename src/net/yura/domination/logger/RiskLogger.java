package net.yura.domination.logger;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import net.yura.domination.engine.Risk;
import net.yura.domination.engine.ai.AI;
import net.yura.domination.engine.core.Player;

public class RiskLogger {
	public static String LOGGER = "RiskLogger";
	private static Logger logger = Logger.getLogger(LOGGER);
	
		
	private static boolean logOwnedCards;
	private static boolean logOwnedCountries;
	private static boolean logPlaceArmies;
	private static boolean logTradeCards;
	private static boolean logAttacks;
	private static boolean logReceivedAttacks;
	private static boolean logBattleDetails;
	private static boolean logBattleWon;
	private static boolean logTacMove;
	private static boolean logLosersWinner;
	
	private static boolean multipleGameLogger;
	private static JProgressBar progressBar;
	private static JTextArea txtLog;
	private static JButton stop;
	private static int gamesNumber = 10;
	private static Risk risk;
	private static HashMap<String, StatPlayer> players = new HashMap<String, RiskLogger.StatPlayer>();
	private static int counter = 1;
	
	
	public static void setup()  {
		try{
			
			FileHandler fh = new FileHandler("RiskLog.txt");
			fh.setFormatter(new LoggerFormatter());
			logger.addHandler(fh);
			logger.addHandler(new SysOutConsoleHandler()); //Commentare questa linea se non si vuole scrivere su System.out
			
			logOwnedCards = true;
			logOwnedCountries = true;
			logPlaceArmies = true;
			logTradeCards = true;
			logAttacks = true;
			logReceivedAttacks = true;
			logBattleDetails = true;
			logBattleWon = true;
			logTacMove = true;
			logLosersWinner = true;
		
			apply();
			
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public static boolean isMultipleGameLogger() {
		return multipleGameLogger;
	}

	public static void setMultipleGameLogger(boolean multipleGameLogger) {
		RiskLogger.multipleGameLogger = multipleGameLogger;
		if (multipleGameLogger){ 
			Risk.setLogLosersWinner(false);
			Risk.setLogOwnedCountries(false);
			Risk.setLogOwnedCards(false);
			Risk.setLogTradeCards(false);
			Risk.setLogPlaceArmies(false);
			Risk.setLogAttacks(false);
			Risk.setLogReceivedAttacks(false);
			Risk.setLogBattleDetails(false);
			Risk.setLogBattleWon(false);
			Risk.setLogTacMove(false);
		}else{
			apply();
		}
	}
	
	private static void apply(){
		Risk.setLogLosersWinner(logOwnedCards);
		Risk.setLogOwnedCountries( logOwnedCountries);
		Risk.setLogOwnedCards(logPlaceArmies);
		Risk.setLogTradeCards(logTradeCards);
		Risk.setLogPlaceArmies(logAttacks);
		Risk.setLogAttacks(logReceivedAttacks);
		Risk.setLogReceivedAttacks(logBattleDetails);
		Risk.setLogBattleDetails(logBattleWon);
		Risk.setLogBattleWon(logTacMove);
		Risk.setLogTacMove(logLosersWinner);
	}

	private static class SysOutConsoleHandler extends ConsoleHandler{
		public SysOutConsoleHandler() {
			setOutputStream(System.out);
			setFormatter(new LoggerFormatter());
		}
	}
	
	private static class LoggerFormatter extends Formatter{

		@Override
		public String format(LogRecord record) {
			if(record.getLevel() == Level.INFO){
				return record.getMessage();
			}
			return record.toString();
		}
		
	}
	
	private static class StatPlayer extends Player implements Comparable<StatPlayer>{
		private static final long serialVersionUID = 1L;
		
		private int victories;

		public StatPlayer(AI ai, String n, int c, String a) {
			super(ai, n, c, a);
			victories = 0;
		}
		
		public void addVictory(){
			victories++;
		}

		public int getVictories() {
			return victories;
		}

		@Override
		public int compareTo(StatPlayer o) {
			return o.getVictories() - this.getVictories();
		}
		
		@Override
		public String toString() {
			return getName() + "("+getAI().getName()+")";
		}
	}
	
	public static void setGamesNumber(int n){
		if(n<1)
			throw new IllegalArgumentException("Numero di partite non valido");
		gamesNumber = n;
	}
	
	
	public static void newVictory(String playerId){
		StatPlayer player =  players.get(playerId);
		player.addVictory();
		String log = "- Partita "+counter+": Il vincitore è "+ player.toString()+";\n";
		txtLog.append(log);
		logger.info(log);
		
		progressBar.setValue(counter);
		counter++;
		if(counter <= gamesNumber)
			startOver();
		else{
			stop.setText("Esci");
			stop.setActionCommand("Exit");
			printFinalStats();
		}
	}


	@SuppressWarnings("unchecked")
	public static void setRisk(Risk risk) {
		RiskLogger.risk = risk;
		Vector<Player> pls = risk.getGame().getPlayers();
		for(Player p: pls){
			if(p.getAI().getId().equals("human"))
				risk.parser("delplayer " + p.getName());
			else
				players.put(p.getName(), new StatPlayer(p.getAI(), p.getName(), p.getColor(), p.getAddress()));
		}
		RiskLogger.showProgressFrame();
		startGame();
	}

	private static void startGame() {
		risk.parser("startgame domination increasing");
	}

	private static void startOver() {
		risk.parser("closegame");
		risk.parser("newgame");
		for(StatPlayer p: players.values()){
			risk.parser("newplayer "+ p.getAI().getId() +" "+ p.getColor() +" "+ p.getName() );
		}
		startGame(); 
	}
	
	
	private static void printFinalStats() {
		StringBuilder log = new StringBuilder("\nRiepilogo:\n");
		ArrayList<StatPlayer> sPlayers = new ArrayList<RiskLogger.StatPlayer>(players.values()); 
		Collections.sort(sPlayers);
		for(StatPlayer p: sPlayers){
			log.append("- "+p.toString() + ": "+p.getVictories()+"/"+(counter-1) + " ("+(p.getVictories()*100)/(counter-1)+"%) vittorie;\n");
		}
		logger.info(log.toString());
		txtLog.append(log.toString());
	}
	
	
	public static void showProgressFrame(){
		JFrame progressFrame = new JFrame("Logger");
		progressFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		progressFrame.getContentPane().setLayout(new BoxLayout(progressFrame.getContentPane(),BoxLayout.PAGE_AXIS));
		progressFrame.setPreferredSize(new Dimension(350, 300));
		progressFrame.setResizable(false);
		progressBar = new JProgressBar(0, gamesNumber);
		progressBar.setValue(0);
		progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
		progressBar.setStringPainted(true);
		txtLog = new JTextArea(10, 3);
		txtLog.setEditable(false);
		((DefaultCaret) txtLog.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		stop = new JButton("Stop");
		stop.setActionCommand("Stop");
		stop.setAlignmentX(Component.CENTER_ALIGNMENT);
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("Stop")){
					risk.parser("closegame");
					printFinalStats();
					stop.setText("Esci");
					stop.setActionCommand("Exit");
				}else if (e.getActionCommand().equals("Exit")){
					System.exit(0);
				}
			}
		});
		JLabel label =new JLabel("Progresso:");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		progressFrame.getContentPane().add(label);
		progressFrame.getContentPane().add(progressBar);
		progressFrame.getContentPane().add(new JScrollPane(txtLog));
		progressFrame.getContentPane().add(stop);
		progressFrame.pack();
		progressFrame.setLocationRelativeTo(null);
		progressFrame.setVisible(true);
	
		
	}

	
}
