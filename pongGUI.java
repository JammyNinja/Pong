//package components; // JComponent
import javax.swing.*; //JPanel/containers/scrollpane
import java.awt.*; //Dimension/colour/graphics
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class pongGUI extends JPanel
						implements KeyListener//, ActionListener
{
	//Timer t = new Timer(50,this);
	Ball ball;
	Player p1,p2;

	int windowWidth;	//1000, player 1/100 of this, ball 1/50
	int widthUnit; 		//100 blocks of width, also half the ball size - for now my target ball granularity
	
	int windowHeight;	//600, player is 1/6 of this
	int heightUnit; 	//50 blocks of height, one fifth of the player height //- so that the player can be split into 5
	
	int playerHeight;
	int playerWidth;
	int playerDepth;	//player positioned 1/20 of the width of game
	int playerRadius;
	int playerSixth;
	
	int p1x,p2x;
	int player2y;
	int ballX, ballY;
		
	int windowXCentre;
	int windowYCentre;

	pong game;

	//constuctor initialises everything and calls timer
	public pongGUI(int width, int height, pong game){
		//initalise gui values, based on the frame size
		this.windowWidth 	= width;
		this.windowHeight 	= height;

		this.widthUnit 		= windowWidth 	/ 100;
		this.heightUnit 	= windowHeight 	/ 50;

		this.playerHeight	= windowHeight	/ 6;
		this.playerRadius 	= playerHeight	/ 2;
		this.playerSixth	= playerHeight 	/ 6;
		this.playerWidth	= windowWidth	/ 100;
		this.playerDepth	= windowWidth 	/ 20;

		this.windowXCentre	= windowWidth 	/ 2;
		this.windowYCentre 	= windowHeight 	/ 2;

		this.p1x = playerDepth;
		this.p2x = windowWidth - playerDepth;
		this.game = game;

		//listen to the keys hit while focus is on this jframe
		addKeyListener(this);
		setFocusable(true);
		//some visual constants
		setOpaque(true);
		setBackground(Color.BLACK);
		
		//START THE GAME FROM HERE FOR NOW
		//set things in motion, the timer counts as an actionPerformed
		game.setGUIandSetupGame(this);

		game.print("GUI constructed");
	}


	public void keyPressed(KeyEvent e){
		switch(e.getKeyCode()){
			case KeyEvent.VK_W:
				game.p1.moveUp();
			break;

			case KeyEvent.VK_S:
				game.p1.moveDown();
			break;

			case KeyEvent.VK_UP:
				game.p2.moveUp();
			break;

			case KeyEvent.VK_DOWN:
				game.p2.moveDown();
			break;

			case KeyEvent.VK_ESCAPE:
				game.quitGame();
			break;

			case KeyEvent.VK_SPACE:
				//if(!game.gameStarted) game.startGame();
				game.print("PAUSE!");
			break;
		}
	}

	public void keyReleased(KeyEvent e){		
	}

	public void keyTyped(KeyEvent e){
	}

	public void paint(Graphics g){
		if(game.gameStarted) {
			paintNet(g);
			paintBall(g);
			paintPlayers(g);
		}
	}

	//redo this better later
	public void paintNet(Graphics g){
		g.setColor(Color.WHITE);
				
		int netGaps = 25; boolean netFinished = false;
		int brushHeight = 0;
		int netLocation = windowWidth/2;
		while(!netFinished){ //probably should have been a for loop... #hungover programming
			g.drawLine(netLocation,brushHeight,netLocation,brushHeight+netGaps);
			brushHeight += netGaps;
			if(g.getColor() == Color.WHITE) g.setColor(Color.BLACK);
			else g.setColor(Color.WHITE);

			if(brushHeight >= windowHeight) netFinished = true;
		}
	}

	public void paintBall(Graphics g) {
		
		g.fillRect(game.ball.xPos, game.ball.yPos, game.ball.diameter, game.ball.diameter); //x,y,width,height 
	}

	public void paintPlayers(Graphics g){
		//player 1
		g.fillRect(game.p1.xPos, game.p1.yPos - playerRadius, playerWidth, playerHeight);

		//player 2
		g.fillRect(game.p2.xPos, game.p2.yPos - playerRadius, playerWidth, playerHeight);
	}
}

class Frame extends JFrame {
	int frameWidth = 1000; //windowWidth = 1000; player 1/100 of this, ball 1/50
	int frameHeight = 600; //windowHeight = 600;  //player is 1/6 of this
	pong game;
	//public static void main(String[] args){
	//	Frame f = new Frame(); //=JFrame
	//}

	public Frame(pong p){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(frameWidth,frameHeight);
		setTitle("PONG");
		setResizable(false);
		this.game = p;
		initialise();
	}

	public void initialise()
	{
		setLayout(new GridLayout(1,1));
		pongGUI gui = new pongGUI(frameWidth, frameHeight, game); //screen is a JPanel
		add(gui);

		setLocationRelativeTo(null);
		setVisible(true);
	}
}










