// Yura Mamyrin, Group D

package net.yura.domination.ui.flashgui;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;
import net.yura.domination.engine.Risk;
import net.yura.domination.engine.RiskUIUtil;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.engine.guishared.AboutDialog;
import net.yura.domination.engine.translation.TranslationBundle;

/**
 * <p> Main Menu for FlashGUI </p>
 * @author Yura Mamyrin <yura@yura.net>
 * @author Christian Weiske <cweiske@cweiske.de>
 */

public class MainMenu extends JPanel implements MouseInputListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String version = "1.0.3.5";
	private final static String product;

	static {

		product = "Flash GUI for " + RiskUtil.GAME_NAME;

	}

	private BufferedImage MenuImage;
	private BufferedImage Server;
	private Risk myrisk;
	private FlashRiskAdapter fra;
	private boolean serverRunning;

	private JoinDialog joinDialog;

	private final static int BUTTON_EXIT		= 100;

	private final static int BUTTON_NEW		= 1;
	private final static int BUTTON_SERVER		= 2;
	private final static int BUTTON_LOADGAME	= 3;
	private final static int BUTTON_HELP		= 4;
	private final static int BUTTON_JOIN		= 5;
	private final static int BUTTON_ABOUT		= 6;
	private final static int BUTTON_LOBBY		= 7;
	private final static int BUTTON_DONATE		= 8;

	private JLabel lobby;

	private java.util.ResourceBundle resBundle = TranslationBundle.getBundle();

	private boolean showLobby;
	private Cursor hand;
	private Cursor defaultCursor;

	/**
	 * Creates a new MainMenu
	 * @param r the risk main program
	 */
	public MainMenu(Risk r,Frame gui) {

		myrisk = r;

		fra = new FlashRiskAdapter(this, myrisk);

		MenuImage = RiskUIUtil.getUIImage(this.getClass(),"menu.jpg");

		Server = MenuImage.getSubimage(400, 490, 60, 60);

		Dimension menuSize = new Dimension(400,550);

		addMouseListener(this);
		addMouseMotionListener(this);
		setPreferredSize(menuSize);
		setMinimumSize(menuSize);
		setMaximumSize(menuSize);

		//key control

		highlightButton=0;
		serverRunning = false;

		gui.setFocusTraversalKeysEnabled( false);
		gui.addKeyListener( this );

		setLayout(null);

		// (Risk.applet == null)?"mainmenu.online":"mainmenu.loading"

		lobby = new JLabel( resBundle.getString("mainmenu.online"), new javax.swing.ImageIcon( this.getClass().getResource("earth.gif") ),JLabel.CENTER );
		lobby.setBounds(152,409,95,95);
		lobby.setHorizontalTextPosition(JLabel.CENTER);
		lobby.setFont( new java.awt.Font("Arial", java.awt.Font.BOLD, 18) );
		lobby.setVisible(false);

		lobby.setForeground( Color.BLACK );
		add(lobby);

		hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		defaultCursor = getCursor();
	}

	/**
	 * Checks the server's state
	 * @param s The server's state
	 */
	public void setServerRunning(boolean s) {

		serverRunning = s;
		repaint();

	}

	public void hideJoinDialog(boolean a) {

		if (!a) {
			joinDialog.exitForm();
			joinDialog = null;
		}

		addMouseListener(this);
		addMouseMotionListener(this);

		//loading.setVisible(false);

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

	}


		/**
		 * Paints the panel
		 * @param g The graphics
		 */
	public void paintComponent(Graphics g) {

			g.drawImage( MenuImage ,0 ,0 ,400 ,550 ,0 ,0 ,400 ,550 ,this );



			if (highlightButton==BUTTON_NEW) {
				g.drawImage( MenuImage ,57 ,219 ,187 ,269	,400 ,0 ,530 ,50 ,this );
			}
			else if (highlightButton==BUTTON_SERVER) {
				g.drawImage( MenuImage ,212 ,219 ,342 ,269	,400 ,50 ,530 ,100 ,this );
			}
			else if (highlightButton==BUTTON_LOADGAME) {
				g.drawImage( MenuImage ,57 ,279 ,187 ,329	,400 ,100 ,530 ,150 ,this );
			}
			else if (highlightButton==BUTTON_HELP) {
				g.drawImage( MenuImage ,212 ,279 ,342 ,329	,400 ,150 ,530 ,200 ,this );
			}
			else if (highlightButton==BUTTON_JOIN) {
				g.drawImage( MenuImage ,57 ,339 ,187 ,389	,400 ,200 ,530 ,250 ,this );
			}
			else if (highlightButton==BUTTON_ABOUT) {
				g.drawImage( MenuImage ,212 ,339 ,342 ,389	,400 ,250 ,530 ,300 ,this );
			}
			//else if (highlightButton==BUTTON_LOBBY) {
			//	g.drawImage( MenuImage ,145 ,401 ,255 ,511	,400 ,300 ,510 ,410 ,this );
			//}


			else if (button==BUTTON_NEW) {
				g.drawImage( MenuImage ,57 ,219 ,187 ,269	,530 ,0 ,660 ,50 ,this );
			}
			else if (button==BUTTON_SERVER) {
				g.drawImage( MenuImage ,212 ,219 ,342 ,269	,530 ,50 ,660 ,100 ,this );
			}
			else if (button==BUTTON_LOADGAME) {
				g.drawImage( MenuImage ,57 ,279 ,187 ,329	,530 ,100 ,660 ,150 ,this );
			}
			else if (button==BUTTON_HELP) {
				g.drawImage( MenuImage ,212 ,279 ,342 ,329	,530 ,150 ,660 ,200 ,this );
			}
			else if (button==BUTTON_JOIN) {
				g.drawImage( MenuImage ,57 ,339 ,187 ,389	,530 ,200 ,660 ,250 ,this );
			}
			else if (button==BUTTON_ABOUT) {
				g.drawImage( MenuImage ,212 ,339 ,342 ,389	,530 ,250 ,660 ,300 ,this );
			}
			//else if (button==BUTTON_LOBBY) {
			//	g.drawImage( MenuImage ,145 ,401 ,255 ,511	,530 ,300 ,640 ,410 ,this );
			//}

			Graphics2D g2 = (Graphics2D)g;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			FontRenderContext frc = g2.getFontRenderContext();
			Font font = g2.getFont();
			g2.setColor( Color.black );
			TextLayout tl;

			/* This adds underlines to the strings, but it doesn't look good and is too much effort for that
			java.text.AttributedString as = new java.text.AttributedString(resBundle.getString( "mainmenu.newgame"));
			as.addAttribute(java.awt.font.TextAttribute.UNDERLINE, java.awt.font.TextAttribute.UNDERLINE_ON, 0,1);
			as.addAttribute(java.awt.font.TextAttribute.FONT, font);
			tl = new TextLayout( as.getIterator(), frc);
			*/
			
			tl = new TextLayout( resBundle.getString( "mainmenu.newgame") , font, frc);
			tl.draw( g2, (float) (122-tl.getBounds().getWidth()/2), (float)247 );

			if (serverRunning) {
				tl = new TextLayout( resBundle.getString( "mainmenu.stopserver") , font, frc);
				tl.draw( g2, (float) (277-tl.getBounds().getWidth()/2), (float)247 );

				g.drawImage( Server, 340, 490, this );

			}
			else {
				tl = new TextLayout( resBundle.getString( "mainmenu.startserver") , font, frc);
				tl.draw( g2, (float) (277-tl.getBounds().getWidth()/2), (float)247 );
			}

			tl = new TextLayout( resBundle.getString( "mainmenu.loadgame"), font, frc);
			tl.draw( g2, (float) (122-tl.getBounds().getWidth()/2), (float)309 );

			tl = new TextLayout( resBundle.getString( "mainmenu.help") , font, frc);
			tl.draw( g2, (float) (277-tl.getBounds().getWidth()/2), (float)309 );

			tl = new TextLayout( resBundle.getString( "mainmenu.joingame") , font, frc);
			tl.draw( g2, (float) (122-tl.getBounds().getWidth()/2), (float)369 );

			tl = new TextLayout( resBundle.getString( "mainmenu.about") , font, frc);
			tl.draw( g2, (float) (277-tl.getBounds().getWidth()/2), (float)369 );

			font = new java.awt.Font("Arial", java.awt.Font.BOLD, 24);

			//tl = new TextLayout( resBundle.getString( "mainmenu.quit") , font, frc);
			//tl.draw( g2, (float) (200-tl.getBounds().getWidth()/2), (float)465 );

	}


	private int button;
	private int currentButton;
	private int pressedButton;
	private int highlightButton;

	//**********************************************************************
	//                     MouseListener Interface
	//**********************************************************************

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Works out what to do when the move has been presed
	 * @param e A mouse event
	 */
	public void mousePressed(MouseEvent e) {

		highlightButton = 0;
		currentButton=insideButton(e.getX(),e.getY());

		if (currentButton != 0) {
			pressedButton = currentButton;
			button = currentButton;
			repaint();
		}

	}

	/**
	 * Works out what to do when a mouse has been released
	 * @param e A mouse event
	 */
	public void mouseReleased(MouseEvent e) {
		int thebutton=0;

		if (pressedButton == currentButton) {
			thebutton = pressedButton;
		}

		if (button != 0) {
			button=0;
		}

		highlightButton=currentButton;
		repaint();
		activateButton( thebutton);
	}//public void mouseReleased(MouseEvent e)


	/**
	 * a button shall be activated
	 */
	private void activateButton(int thebutton) {

		if (thebutton != 0) {

			switch (thebutton) {

				case MainMenu.BUTTON_NEW:{

					myrisk.parser("newgame");

					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

					removeMouseListener(this);
					removeMouseMotionListener(this);

					//loading.setVisible(true);

					break;
				}
				case MainMenu.BUTTON_SERVER: {

					if (serverRunning) {
						myrisk.parser("killserver");
					}
					else {
						myrisk.parser("startserver");
					}
					break;
				}
				case MainMenu.BUTTON_LOADGAME: {

					String name = RiskUIUtil.getLoadFileName(
						RiskUIUtil.findParentFrame(this)
						//RiskUtil.SAVES_DIR,
						//RiskFileFilter.RISK_SAVE_FILES
					);

					if (name!=null) {

						myrisk.parser("loadgame " + name );

					}

					break;

				}
				case MainMenu.BUTTON_HELP: {

					try {
						RiskUtil.openDocs( TranslationBundle.getBundle().getString( "helpfiles.flash" ) );
					}
					catch(Exception e) {
						JOptionPane.showMessageDialog( RiskUIUtil.findParentFrame(this) ,"Unable to open manual: "+e.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
					}

					break;
				}
				case MainMenu.BUTTON_JOIN: {

					Frame frame = RiskUIUtil.findParentFrame(this);

					joinDialog = new JoinDialog( frame , true, myrisk);
					Dimension frameSize = frame.getSize();
					Dimension aboutSize = joinDialog.getPreferredSize();
					int x = frame.getLocation().x + (frameSize.width - aboutSize.width) / 2;
					int y = frame.getLocation().y + (frameSize.height - aboutSize.height) / 2;
					if (x < 0) x = 0;
					if (y < 0) y = 0;
					joinDialog.setLocation(x, y+10);

					joinDialog.setVisible(true);
					break;
				}
				case MainMenu.BUTTON_LOBBY: {

					if (showLobby) {

						RiskUIUtil.runLobby(myrisk);
					}

					break;
				}
				case MainMenu.BUTTON_ABOUT: {

					Frame frame = RiskUIUtil.findParentFrame(this);

					RiskUIUtil.openAbout(frame,product, version);

					break;
				}
				case MainMenu.BUTTON_EXIT: {

					exit();

					break;
				}
				case MainMenu.BUTTON_DONATE: {

					RiskUIUtil.donate(this);

					break;
				}
			}//switch end

			currentButton=0;
			//do we need this?
//			highlightButton=currentButton;
			repaint();

		}
	}//private void activateButton(int thebutton)


	/**
	 * Checks if highlighting is needed
	 * @param e A mouse event
	 */
	public void mouseMoved(MouseEvent e) {

		int oldhighlightButton = highlightButton;
		int newhighlightButton = insideButton(e.getX(),e.getY());

		if (oldhighlightButton != newhighlightButton) {
			highlightButton = newhighlightButton;
			repaint();
		}

		if (newhighlightButton==BUTTON_DONATE) {

			if (getCursor()==defaultCursor) {

				setCursor(hand);

			}

		}
		else if (getCursor()==hand) {

			setCursor(defaultCursor);

		}

	}

	/**
	 * Works out what to do when the mouse is dragged
	 * @param e A mouse event
	 */
	public void mouseDragged(MouseEvent e) {

		currentButton = insideButton(e.getX(),e.getY());

		if (pressedButton == currentButton ) {
			if (button!=pressedButton) {
				button=pressedButton;
				repaint();
			}
		}
		else {
			if (button !=0) {
				button = 0;
				repaint();
			}
		}

	}

	/**
	 * Works out what button has been pressed
	 * @param x x co-ordinate
	 * @param y y co-ordinate
	 * @return int The type of button presssed
	 */
	public int insideButton(int x, int y) {

		int W=116;
		int H=31;

		int B=0;

		int yrel = Math.abs(455 - y);
		int xrel = (int) (Math.sqrt(2255 - yrel * yrel) * 95 / 95);

		if (x >= 65 && x < (65 + W) && y >= 228 && y < (228 + H)) {
			B=BUTTON_NEW;
		}
		else if (x >= 220 && x < (220 + W) && y >= 228 && y < (228 + H)) {
			B=BUTTON_SERVER;
		}
		else if (x >= 65 && x < (65 + W) && y >= 289 && y < (289 + H)) {
			B=BUTTON_LOADGAME;
		}
		else if (x >= 220 && x < (220 + W) && y >= 289 && y < (289 + H)) {
			B=BUTTON_HELP;
		}
		else if (x >= 65 && x < (65 + W) && y >= 350 && y < (350 + H)) {
			B=BUTTON_JOIN;
		}
		else if (x >= 220 && x < (220 + W) && y >= 350 && y < (350 + H)) {
			B=BUTTON_ABOUT;
		}
		else if (x >= 200 - xrel && x < 200 + xrel) {
			B=BUTTON_LOBBY;
		}
		else if (x >= 0 && x < 115 && y >= (getHeight()-50) && y < getHeight() ) { // google: 115px � 50px
			B=BUTTON_DONATE;
		}

		return B;

	}

	/**
	 * key control
	 */

	/**
	 * the user has released a key
	 */
	public void keyReleased( KeyEvent event )
	{
		switch (event.getKeyCode()) {
			//tab to the next button
			case KeyEvent.VK_TAB:
				if (event.isShiftDown()) {
					//Shift + Tab -> backwards
					highlightButton--;
				} else {
					//tab only -> forward
					highlightButton++;
				}
				if (highlightButton > 7) {
					highlightButton = 1;
				} else if (highlightButton < 1) {
					highlightButton = 7;
				}
				repaint();
				break;

			//activate the current button
			case KeyEvent.VK_SPACE:
			case KeyEvent.VK_ENTER:
				activateButton( highlightButton);
				break;


			//new game
			case KeyEvent.VK_N:
				activateButton( MainMenu.BUTTON_NEW);
				break;

			//load game
			case KeyEvent.VK_L:
				activateButton( MainMenu.BUTTON_LOADGAME);
				break;

			//exit
			case KeyEvent.VK_Q:
			case KeyEvent.VK_ESCAPE:
				activateButton( MainMenu.BUTTON_EXIT);
				break;

			//join game
			case KeyEvent.VK_J:
				activateButton( MainMenu.BUTTON_JOIN);
				break;

			//about
			case KeyEvent.VK_A:
				activateButton( MainMenu.BUTTON_ABOUT);
				break;

			//server
			case KeyEvent.VK_S:
				activateButton( MainMenu.BUTTON_SERVER);
				break;

			//help
			case KeyEvent.VK_H:
				activateButton( MainMenu.BUTTON_HELP);
				break;

			// lobby
			case KeyEvent.VK_O:
				activateButton( MainMenu.BUTTON_LOBBY);
				break;

		}//switch keycode
	}//public void keyReleased( KeyEvent event )


	//I don't want these, but we implement the interface
	public void keyPressed( KeyEvent event ) {}
	public void keyTyped( KeyEvent event ) {}

	private void exit() {

		//Frame frame = RiskUtil.findParentFrame(this);
		//
		//if ( frame instanceof JFrame && ((JFrame)frame).getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE && RiskUtil.checkForNoSandbox()) {
		//
		//	// not actually needed as it will auto be done
		//	System.exit(0);
		//}

		myrisk.deleteRiskListener(fra);

		//frame.setVisible(false);
		//frame.dispose();

	}

	public void addLobbyButton() {

		if (RiskUIUtil.getAddLobby(myrisk)) {

			lobby.setVisible( true );

			showLobby = true;

			repaint();
		}

	}

	/**
	 * This runs the program
	 * @param argv
	 */
	public static void main(String[] argv) {

		RiskUIUtil.parseArgs(argv);
		
		MainMenu mm = newMainMenuFrame( new Risk(),JFrame.EXIT_ON_CLOSE );

		mm.addLobbyButton();

	}

	public static MainMenu newMainMenuFrame(Risk r,int a) {

		JFrame gui = new JFrame();

		final MainMenu mm = new MainMenu( r,gui );

		gui.setContentPane( mm );
		gui.setIconImage(Toolkit.getDefaultToolkit().getImage( AboutDialog.class.getResource("icon.gif") ));
		gui.setTitle( TranslationBundle.getBundle().getString( "mainmenu.title"));
		gui.setResizable(false);
		gui.pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = gui.getSize();
		frameSize.height = ((frameSize.height > screenSize.height) ? screenSize.height : frameSize.height);
		frameSize.width = ((frameSize.width > screenSize.width) ? screenSize.width : frameSize.width);
		gui.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

		gui.addWindowListener(new java.awt.event.WindowAdapter() {

			public void windowClosing(java.awt.event.WindowEvent evt) {

				mm.exit();
			}
		});

		gui.setDefaultCloseOperation(a);

		gui.setVisible(true);

		return mm;

	}

}
