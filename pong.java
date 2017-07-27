import javax.swing.*; // timer
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class pong implements ActionListener{
	/* TODO
current: pong instance as class variable - gameInstance -> game plz

		TIMING
		start with button press
		pause with space bar
		stop game freezing when holding keys, yet make keys holdable / adjust sensitivity
		maybe even only let point start when starting player moves
		
		GUI
		make players stop at edge
		point displaying with Jlabels as its not symmetrical :/

		GAME
		player names
		player point score function?
		point scoring

		TIDYING
		sort the net function out, that shit is shameful
		should the moving parts be taking gui into their constructors?
		- sort the statics
		dont like how it starts with Frame

		FUTURE FEATURES
		p2 AI
		playable on a webpage
	*/

	static Player p1,p2;
	static Ball ball;
	static pongGUI gui;
	static Timer t;
	static pong gameInstance;
	static boolean gameStarted = false;
	
	public static void main(String[] args){
		System.out.println("pong, on git");
		pong game = new pong();
		//gameInstance = game;
		t = new Timer(50, game);
		//gui needs game for players
		startGUI(game);
	}

	public static void startGUI(pong p){
		Frame f = new Frame(p);
	}

	//starts timer and changes the game state
	public static void startGame(){
		//gameInstance is the actionlistener
		gameStarted = true;
		t.start();
		print("game started");
	}

	public static void setupGame(){
		p1 = new Player(1, gui);
		p2 = new Player(2, gui);
		ball = new Ball(gui);
		//why isn't the background engaged at this point!?
	}

	public static void setGUIandSetupGame(pongGUI ggui){
		gui = ggui;
		setupGame();
		startGame();
	}
	
	//called per timestep
	public void actionPerformed(ActionEvent e) {
		ball.moveBall();
		
		//check if hit player
		checkBallPlayer();
		//is point over
		checkPointOver();
		
		gui.repaint(); //somehow this gives the background
	}

	//calls ball.hitPlayer(x) where -1 means it hit top third, 0 centre third and 1 bottom third of player
	public void checkBallPlayer(){
		
		//coming at player on left, p1
		if(ball.dx < 0){
			if(ball.xPos <= p1.xPos && ball.xPos >= p1.xPos-gui.widthUnit){
				if(ball.yPos + ball.diameter > p1.yPos - gui.playerRadius && ball.yPos < p1.yPos + gui.playerRadius){
					//definitely hit the player now decide where
					//top quarter
					if(ball.yPos + ball.diameter <= p1.yPos - gui.playerSixth) ball.hitPlayer(-1);
					//bottom quarter
					else if(ball.yPos >= p1.yPos + gui.playerSixth) ball.hitPlayer(1);
					//centre half
					else ball.hitPlayer(0);
				}
			}
		}

		//coming at player on right, p2
		if(ball.dx > 0){
			if(ball.xPos + ball.diameter >= p2.xPos && ball.xPos+ball.diameter <= p2.xPos+gui.widthUnit){
				if(ball.yPos + ball.diameter > p2.yPos - gui.playerRadius && ball.yPos < p2.yPos + gui.playerRadius){
					//definitely hit the player now decide where
					//top quarter
					if(ball.yPos + ball.diameter <= p2.yPos - gui.playerSixth) ball.hitPlayer(-1);
					//bottom quarter
					else if(ball.yPos >= p2.yPos + gui.playerSixth) ball.hitPlayer(1);
					//centre half
					else ball.hitPlayer(0);
				}
			}
		}
	}

	//checks if ball out of x bounds, and calls resetPoint accordingly
	public void checkPointOver(){
		//ball out of bounds p1 score
		if(ball.xPos > gui.windowWidth - gui.widthUnit){
			resetPoint(1);

		} //p2 scored
		else if (ball.xPos < gui.widthUnit){
			resetPoint(2);
		}
	}

	public void quitGame(){
		int winner =  p1.points > p2.points ? 1 : 2;
		if(p1.points == p2.points) winner = 0;
		
		print("game over with score: " + p1.points + "-" + p2.points + ". " + 
			(winner == 0 ? "draw." : "Player " + winner + " won!"));
		//die
		System.exit(0);
	}

	//calls reset functions for ball and players, increments player score
	public void resetPoint(int winner){
		ball.reset();
		p1.reset(); p2.reset();
		if(winner == 1) p1.points ++;
		else if (winner == 2) p2.points++;
		else print("who won that point?!");

		print("RESET POINT after player " + winner + " scored");
	}

	static public void print(String s){
		System.out.println(s);
	}
}

//there will be two instances of this class
class Player {
	int yPos;
	int xPos; 			//used for collision detection with ball

	int playerNumber;
	int dy; 			//effectively the sensitivity
	int points; 		//score
	pongGUI gui;
	int northBound,southBound;

	public Player(int pNum, pongGUI gui){
		this.playerNumber = pNum;
		this.points = 0;
		this.yPos = gui.windowYCentre;
		this.dy = gui.heightUnit;
		this.southBound = gui.windowHeight;// - gui.playerRadius;
		this.northBound = 0;

		this.gui = gui;

		if(pNum == 1) this.xPos = gui.p1x;
		else if (pNum == 2) this.xPos = gui.p2x;
		else pong.print("totally an error with player number and depth in player constructor");
	
		pong.print("Player " + pNum + " initialised.");
	}

	public void moveUp(){
		//if not already at upper wall
		if (yPos > northBound) {
			if(yPos-dy <= northBound) yPos = northBound;
			else yPos-= dy;
		}
	}

	public void moveDown(){
		if (yPos < southBound) {
			if(yPos + dy >= southBound) yPos = southBound;
			else yPos += dy;
		}
	}

	public void reset() {
		this.yPos = gui.windowYCentre;
	}
}

//game only needs three of the functions, moveBall, called per timestep
//and hitPlayer, obviously called when a player collides with the ball
//and reset for when the point is over
class Ball {
	int xPos;
	int yPos;
	
	int dy; //I propose a number from a set of 5, where 0 = straight, +2 = max south, -2 = max north
	int dx; //how far the ball moves to the side with each timestep, positive = right, neg left

	pongGUI gui;
	int diameter;

	public Ball(pongGUI gui){
		this.dy = 0;//gui.heightUnit;
		this.dx = gui.widthUnit;
		this.xPos = gui.windowXCentre;
		this.yPos = gui.windowYCentre;
		this.diameter = gui.windowWidth / 50;
		this.gui = gui;

		pong.print("Ball initialised");
	}

	//called per timestep
	public void moveBall(){
		xPos += dx;
		//yPos += dy;
		updateYPos();
	}
	//move the ball in the vertical space, including bounce on wall
	private void updateYPos(){
		yPos += dy;

		//check if ball overlaps with wall (if y is out of bounds) if so, hit wall => change angle
		if(yPos <= 0 ){
			dy *= -1;
			yPos = 0;
		}
		else if (yPos + diameter >= gui.windowHeight) {
			yPos = gui.windowHeight - diameter;
			dy *= -1;
		}

	}
	//put the ball in the middle, send it towards the winner
	public void reset(){
		dx = -dx; //winner restarts
		dy = 0;
		xPos = gui.windowXCentre;
		yPos = gui.windowYCentre;
	}

	//change angle (dy) depending on where it hit on the player (hitSpot)
	//hitspot -1 top, 0 mid, 1 bottom
	public void hitPlayer(int hitSpot){
		dx = -dx;
		//pong.print("hit spot: " + hitSpot + " dy " + dy);

		//coming straight
		if(dy == 0){
			if(hitSpot == 0) {dy = 0;}
			else if (hitSpot > 0) {dy = gui.heightUnit;}
			else if (hitSpot < 0) {dy = -gui.heightUnit;}
			else pong.print ("somethings up with dy 0");
 		}

 		//coming in north to south
 		else if(dy > 0){
 			if(hitSpot < 0) {dy *= -1;} //top of player send it back where it came
 			else if(hitSpot > 0) {dy =0;}
 			else dy=dy;
 			//else centre player, doesnt change angle 
 		}

 		//coming in south to north
 		else if(dy < 0){
 			if(hitSpot < 0) {dy = 0;}
 			else if (hitSpot > 0) {dy *= -1;}
 			else dy=dy;
 		}
 		else pong.print("wtf");
 		//pong.print(" newDy: " + dy);
	}
}


