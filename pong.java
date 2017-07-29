import javax.swing.*; // timer
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class pong implements ActionListener{
	/* TODO
current:

		TIMING
		pause with space bar
		serve again with space - or by moving I see
		stop game freezing when holding keys, yet make keys holdable / adjust sensitivity
		
		GUI
		point displaying with Jlabels as its not symmetrical :/
		^why does it lag at opening when painting score?
		should I be painting objects from their centre? :/
		forground and background colours variable - maybe even players and ball...

		GAME
		if you press space after auto serve sometimes it switches direction
		player point score function?
		player names
		players not moving fast enough/ hold buttons plz

		TIDYING
		- sort the statics

		FUTURE FEATURES
		p2 AI
		playable on a webpage
	*/

	static Player p1,p2;
	static Ball ball;
	static pongGUI gui;
	static Timer t;
	static pong game;
	//static boolean gameStarted = false;
	
	public static void main(String[] args){
		System.out.println("Pong, by Louis. Enjoy!");
		game = new pong();
		gui = new pongGUI(game);

		//move timer into setup game, gameinstance to be solved at the same time
		setupGame();
		startGame();
	}

	//starts timer and changes the game state
	public static void startGame(){
		//gameStarted = true;
		t.start();
		print("game started");
	}

	public static void setupGame(){
		p1 = new Player(1, gui);
		p2 = new Player(2, gui);
		ball = new Ball(gui);
		t = new Timer(50, game);

		print("game setup done, all moving parts initialised");
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
			//did it hit the player at all
			if(ball.xPos <= p1.xPos && ball.xPos >= p1.xPos-gui.widthUnit){
				if(ball.yPos + gui.ballDiameter > p1.yPos - gui.playerRadius && ball.yPos < p1.yPos + gui.playerRadius){
					//yes it hit, but where:
					//top quarter
					if(ball.yPos + gui.ballDiameter <= p1.yPos - gui.playerSixth) ball.hitPlayer(-1);
					//bottom quarter
					else if(ball.yPos >= p1.yPos + gui.playerSixth) ball.hitPlayer(1);
					//centre half
					else ball.hitPlayer(0);
				}
			}
		}

		//coming at player on right, p2
		if(ball.dx > 0){
			//did it hit the player at all
			if(ball.xPos + gui.ballDiameter >= p2.xPos && ball.xPos+gui.ballDiameter<= p2.xPos+gui.widthUnit){
				if(ball.yPos + gui.ballDiameter > p2.yPos - gui.playerRadius && ball.yPos < p2.yPos + gui.playerRadius){
					//yes it hit, but where:
					//top quarter
					if(ball.yPos + gui.ballDiameter <= p2.yPos - gui.playerSixth) ball.hitPlayer(-1);
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
			endPoint(1);

		} //p2 scored
		else if (ball.xPos < gui.widthUnit){
			endPoint(2);
		}
	}

	public void endPoint(int winner){
		print("Point scored by player " + winner);
		//points to the winner
		if(winner == 1) p1.points ++;
		else if (winner == 2) p2.points++;
		else print("who won that point?!");
		//reset the ball
		ball.resetBall();
		//auto starting point for now
		startPoint(winner);
	}
	//consider letting the players be free
	public void startPoint(int server){
		//p1.reset(); p2.reset();
		ball.serve(server);
	}

	public void quitGame(){
		print("Quitting game");
		int winner =  p1.points > p2.points ? 1 : 2;
		if(p1.points == p2.points) winner = 0;
		
		print("game over with score: " + p1.points + "-" + p2.points + ". " + 
			(winner == 0 ? "draw." : "Player " + winner + " won!"));
		//die
		System.exit(0);
	}

	static public void print(String s){
		System.out.println(s);
	}
}

//there will be two instances of this class
class Player {
	//player attributes
	int playerNumber;
	int points;

	//position variables
	int xPos; 			//used for collision detection with ball
	int yPos;			//y co-ord of the player centre

	//movement variables
	int dy; 			//effectively the sensitivity
	static int northBound, southBound, centre; //static as same for all players

	public Player(int pNum, pongGUI gui){
		this.playerNumber = pNum;
		this.points = 0;
		
		//gui info necessary for player movement restriction/reset
		this.southBound = gui.windowHeight - gui.playerRadius;
		this.northBound = 0 + gui.playerRadius;
		this.centre = gui.windowYCentre;
		this.dy = gui.heightUnit;
		this.yPos = centre;

		//assign player depth calculated by gui
		if(pNum == 1) this.xPos = gui.p1x;
		else if (pNum == 2) this.xPos = gui.p2x;
		else pong.print("totally an error with player number and depth in player constructor");
	
		pong.print("Player " + pNum + " initialised.");
	}

	public void moveUp(){
		if (yPos > northBound) {
			if(yPos-dy < northBound) yPos = northBound;
			else yPos-= dy;
		}
	}

	public void moveDown(){
		if (yPos < southBound) {
			if(yPos + dy > southBound) yPos = southBound;
			else yPos += dy;
		}
	}

	public void reset() {
		this.yPos = centre;
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

	static int diameter;
	static int southWall, northWall;
	static int resetX, resetY;
	static int yUnit, xUnit;

	public Ball(pongGUI gui){
		//fixed bounds based on gui - which paints from top left of ball
		this.northWall = 0; 
		this.southWall = gui.windowHeight - gui.ballDiameter;
		this.resetX = gui.windowXCentre - gui.ballDiameter / 2;
		this.resetY = gui.windowYCentre - gui.ballDiameter / 2;
		
		//granularity of ball movement
		this.xUnit = gui.widthUnit;
		this.yUnit = gui.heightUnit;

		//place the ball
		resetBall();
		
		pong.print("Ball initialised");
	}

	//called per timestep
	public void moveBall(){
		//bounce taken care of by hitPlayer()<-game which comminicates with players
		xPos += dx;
		//seperate function to take into account walls -> bounce
		updateYPos();
	}

	//move the ball in the vertical space, including bounce on wall
	private void updateYPos(){
		yPos += dy;

		//check if ball overlaps with wall (if y is out of bounds) if so, hit wall => change angle
		if(yPos <= northWall ){
			yPos = northWall;
			dy *= -1;
		}
		else if (yPos>= southWall) {
			yPos = southWall;
			dy *= -1;
		}

	}

	//set the ball in motion
	public void serve(int server){
		dx = server == 1 ? -xUnit : xUnit ; //winner should start
	}
	//put the ball in the middle, send it towards the winner
	public void resetBall(){
		xPos = resetX;
		yPos = resetY;
		dx = 0; 
		dy = 0;
	}

	//change angle (dy) depending on where it hit on the player (hitSpot)
	//hitspot -1 top, 0 mid, 1 bottom
	public void hitPlayer(int hitSpot){
		//pong.print("hit spot: " + hitSpot + " dy " + dy);

		//bounce horizontal
		dx = -dx;

		//bounce vertical:
		//coming straight
		if(dy == 0){
			if(hitSpot == 0) {dy = 0;}
			else if (hitSpot > 0) {dy = yUnit;}
			else if (hitSpot < 0) {dy = -yUnit;}
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

 		else pong.print("wtf player hit");
 		//pong.print(" newDy: " + dy);
	}
}


