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
	int ballDiameter;
	int ballX, ballY;
		
	int windowXCentre;
	int windowYCentre;

	pong game;
	Frame f;

	public pongGUI(pong game) {
		pong.print("constructing gui...");

		//needs game for passing keyboard actions to game
		this.game = game;
		f = new Frame();
		initGUI(f);
		
		pong.print("gui constructed.");
	}

	public void initGUI(Frame f){
		pong.print("initialising gui...");

		//some visual constants
		setOpaque(true);
		setBackground(Color.BLACK);
		//not just set size, works with pack and solved frame border problem!
		setPreferredSize(new Dimension(f.frameWidth,f.frameHeight));
		
		//get wet for input
		addKeyListener(this);
		setFocusable(true);

		//initalise gui values, based on the frame size
		this.windowWidth 	= f.frameWidth;
		this.windowHeight 	= f.frameHeight;
		//units to gridify things
		this.widthUnit 		= windowWidth 	/ 100; // s=100
		this.heightUnit 	= windowHeight 	/ 30;  // =20

		this.playerHeight	= heightUnit 	* 6 ;  //must be divisble by 6
		this.playerRadius 	= playerHeight	/ 2;

		this.playerSixth	= playerHeight 	/ 6;
		this.playerWidth	= windowWidth	/ 100;
		this.playerDepth	= windowWidth 	/ 20;

		this.windowXCentre	= windowWidth 	/ 2;
		this.windowYCentre 	= windowHeight 	/ 2;

		this.ballDiameter = windowWidth		/ 50;
		this.p1x = playerDepth;
		this.p2x = windowWidth - playerDepth;

		

		f.initialise(this);
		game.print("GUI initialised.");

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
				game.print("ESCAPE!");
				game.quitGame();
			break;

			case KeyEvent.VK_SPACE:
				game.print("SPACE!");
				game.startPoint(1);
			break;
		}
	}

	public void keyReleased(KeyEvent e){}
	
	public void keyTyped(KeyEvent e){}

	//called per timestep from pong.actionPerformed
	public void paint(Graphics g){
		g.setColor(Color.WHITE);
		//if(game.gameStarted) {
			paintNet(g);
			paintBall(g);
			paintPlayers(g);

			g.drawLine(0,windowHeight-1,windowWidth,windowHeight-1);
			paintScore(g);
		//}
	}

	//black and white should be set to call from a global foreground/background colour
	public void paintNet(Graphics g){
				
		int netGaps = ballDiameter;
		int netX = windowWidth/2;
		
		boolean flip = true;
		//gui stops drawing players if <= in the stop condition, apprently you cant draw out of bounds...
		for(int brushY =0; brushY< windowHeight; brushY+=netGaps){
			g.drawLine(netX, brushY, netX, brushY+netGaps);
			//switch colour
			if(g.getColor() == Color.WHITE) g.setColor(Color.BLACK);
			else g.setColor(Color.WHITE);
		}
		g.setColor(Color.WHITE);
	}
	//paints ball from its top left
	public void paintBall(Graphics g) {
		
		g.fillRect(game.ball.xPos, game.ball.yPos, ballDiameter, ballDiameter); //x,y,width,height 
	}
	//paints players from their centre 
	public void paintPlayers(Graphics g){
		//player 1
		g.fillRect(game.p1.xPos, game.p1.yPos - playerRadius, playerWidth, playerHeight);

		//player 2
		g.fillRect(game.p2.xPos, game.p2.yPos - playerRadius, playerWidth, playerHeight);
	}

	public void paintScore(Graphics g){
		int fontSize = 25;
		int scoreGap = 10;
		g.setFont(new Font("Comic Sans MS", Font.PLAIN, fontSize));
		g.drawString("" + game.p1.points, windowXCentre - fontSize, 50);
		g.drawString(""+ game.p2.points, windowXCentre + scoreGap, 50);
	}
}

class Frame extends JFrame {
	static int frameWidth = 1000; //windowWidth = 1000; player 1/100 of this, ball 1/50
	static int frameHeight = 600; //windowHeight = 600;  //player is 1/6 of this

	public Frame(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("PONG");

		//this size includes border!!
		setSize(frameWidth,frameHeight);
		setResizable(false);
	}

	public void initialise(pongGUI guiPanel)
	{
		setLayout(new GridLayout(1,1));
		add(guiPanel);
		//pack();
		setLocationRelativeTo(null);
		pack();
		pong.print("frame insets now known as " + getInsets());
		setVisible(true);
	}
}










