//package components; // JComponent
import javax.swing.*; //JPanel/containers/scrollpane
import java.awt.*; //Dimension/colour/graphics
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class pongGUI extends JPanel
						implements KeyListener, ActionListener
{
	Timer t = new Timer(50,this);
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

	//constuctor initialises everything and calls timer
	public pongGUI(int width, int height){
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

		//listen to the keys hit while focus is on this jframe
		addKeyListener(this);
		setFocusable(true);
		//some visual constants
		setOpaque(true);
		setBackground(Color.BLACK);

		//moveable object class constructors
		ball = new Ball(this);//give it the gui
		p1 = new Player(1, this);
		p2 = new Player(2, this);

		//set things in motion, the timer counts as an actionPerformed
		t.start();
	}

	public void keyPressed(KeyEvent e){
		//this is all the gui should be doing, this is where it passes onto game methinks
		switch(e.getKeyCode()){
			case KeyEvent.VK_W:
				p1.moveUp();
			break;

			case KeyEvent.VK_S:
				p1.moveDown();
			break;

			case KeyEvent.VK_UP:
				p2.moveUp();
			break;

			case KeyEvent.VK_DOWN:
				p2.moveDown();
			break;

			case KeyEvent.VK_ESCAPE:
				quitGame();
				System.exit(0);
			break;

			case KeyEvent.VK_SPACE:
				pong.print("PAUSE!");
			break;
		}
	}

	public void keyReleased(KeyEvent e){		
	}

	public void keyTyped(KeyEvent e){
	}

	public void actionPerformed(ActionEvent e) {
		ball.moveBall();
		//check if hit player
		checkBallPlayer();
		checkPointOver();
		repaint();
	}
	public void paint(Graphics g){
		paintNet(g);
		paintBall(g);
		paintPlayers(g);
	}

	public void quitGame(){
		int winner =  p1.points > p2.points ? 1 : 2;
		if(p1.points == p2.points) winner = 0;
		
		pong.print("game over with score: " + p1.points + "-" + p2.points + ". " + 
			(winner == 0 ? "draw." : "Player " + winner + " won!"));
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
		g.fillRect(ball.xPos, ball.yPos, ball.diameter, ball.diameter); //x,y,width,height 
	}

	public void paintPlayers(Graphics g){
		//player 1
		g.fillRect(p1.xPos, p1.yPos - playerRadius, playerWidth, playerHeight);

		//player 2
		g.fillRect(p2.xPos, p2.yPos - playerRadius, playerWidth, playerHeight);
	}

	//calls ball.hitPlayer(x) where -1 means it hit top third, 0 centre third and 1 bottom third of player
	public void checkBallPlayer(){
		
		//coming at player on left, p1
		if(ball.dx < 0){
			if(ball.xPos <= p1.xPos && ball.xPos >= p1.xPos-widthUnit){
				if(ball.yPos + ball.diameter > p1.yPos - playerRadius && ball.yPos < p1.yPos + playerRadius){
					//definitely hit the player now decide where
					//top quarter
					if(ball.yPos + ball.diameter <= p1.yPos - playerSixth) ball.hitPlayer(-1);
					//bottom quarter
					else if(ball.yPos >= p1.yPos + playerSixth) ball.hitPlayer(1);
					//centre half
					else ball.hitPlayer(0);
				}
			}
		}

		//coming at player on right, p2
		if(ball.dx > 0){
			if(ball.xPos + ball.diameter >= p2.xPos && ball.xPos+ball.diameter <= p2.xPos+widthUnit){
				if(ball.yPos + ball.diameter > p2.yPos - playerRadius && ball.yPos < p2.yPos + playerRadius){
					//definitely hit the player now decide where
					//top quarter
					if(ball.yPos + ball.diameter <= p2.yPos - playerSixth) ball.hitPlayer(-1);
					//bottom quarter
					else if(ball.yPos >= p2.yPos + playerSixth) ball.hitPlayer(1);
					//centre half
					else ball.hitPlayer(0);
				}
			}
		}
	}

	//checks if ball out of x bounds, and calls resetPoint accordingly
	public void checkPointOver(){
		//ball out of bounds p1 score
		if(ball.xPos > windowWidth - widthUnit){
			resetPoint(1);

		} //p2 scored
		else if (ball.xPos < widthUnit){
			resetPoint(2);
		}
	}

	//calls reset functions for ball and players, increments player score
	public void resetPoint(int winner){
		ball.reset();
		p1.reset(); p2.reset();
		if(winner == 1) p1.points ++;
		else if (winner == 2) p2.points++;
		else pong.print("who won that point?!");

		pong.print("RESET POINT after player " + winner + " scored");
	}
}

class Frame extends JFrame {
	int frameWidth = 1000; //windowWidth = 1000; player 1/100 of this, ball 1/50
	int frameHeight = 600; //windowHeight = 600;  //player is 1/6 of this

	public static void main(String[] args){
		Frame f = new Frame(); //=JFrame
	}

	public Frame(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(frameWidth,frameHeight);
		setTitle("PONG");
		setResizable(false);

		initialise();
	}

	public void initialise()
	{
		setLayout(new GridLayout(1,1));
		pongGUI gui = new pongGUI(frameWidth, frameHeight); //screen is a JPanel
		add(gui);

		setLocationRelativeTo(null);
		setVisible(true);
	}
}










