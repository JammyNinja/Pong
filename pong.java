import javax.swing.*; // timer
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class pong implements ActionListener{
	/* TODO
current: 	

		TIMING
		serve again with space - or by moving I see
		stop game freezing when holding keys, yet make keys holdable / adjust sensitivity
		^why even does it freeze?!

		GUI
		point displaying with Jlabels as its not symmetrical :/
		^why does it lag at opening when painting score with text :(?

		GAME
		players not moving fast enough/ hold buttons plz
		auto serve or nah?
		play a match? score limit etc
		player point score function?
		some issue with the ball collision detection , sometimes doesnt register something that looks liek a hit, maybe should do something about player width?
		^demonstratable from start move player 1 down 3, player 2 down 2 or 3
		TIDYING
		- sort the statics

		FUTURE FEATURES
		p2 AI - doable i reckon in a greedy zombie player way
		playable on a webpage
	*/

	static Player p1,p2;
	static Ball ball;
	static pongGUI gui;
	static pong game;
	static Timer t;
	static boolean pause = false;
	
	public static void main(String[] args){
		System.out.println("Pong, by Louis. Enjoy!");
		game = new pong();
		gui = new pongGUI(game);

		setupGame();
		startGame();
	}

	//starts timer -> calls action performed
	public static void startGame(){
		t.start();
		ball.serve(1);
		print("game started");
	}
	//instantiates movable parts and timer
	public static void setupGame(){
		p1 = new Player(1, gui);
		p2 = new Player(2, gui);
		ball = new Ball(gui);
		t = new Timer(50, game);

		print("game setup done, all moving parts initialised");
	}
	
	//called per timestep
	public void actionPerformed(ActionEvent e) {
		if(!pause) ball.moveBall();
		
		//check if hit player
		checkBallPlayer();
		//is point over
		checkPointOver();
		
		gui.repaint(); //somehow this gives the background
	}

	//calls ball.hitPlayer(x) where -1 means it hit top third, 0 centre third and 1 bottom third of player
	//player now divisible by 6 (-3:3) going for centre third = straight(-1:1), middle sides (-2,2) = a little bent
	//(-3,3) = bent af -- NOTE center = 0, top sixth = -2, middle top sixth = -1
	//only 3s change dy unless its straight
	public void checkBallPlayer(){
		
		//coming at player on left, p1
		if(ball.dx < 0){
			//did it hit the player at all
			if(p1.xPos <= ball.xPos &&
				p1.xPos + gui.playerWidth >= ball.xPos){
				if(p1.yPos - gui.playerRadius < ball.yPos + gui.ballDiameter &&
					p1.yPos + gui.playerRadius > ball.yPos ){
					//yes it hit, but where:

					//top sixth
					if(ball.yPos + gui.ballDiameter <= p1.yPos - 2*gui.playerSixth) ball.hitPlayer(-2);
					//2nd from top sixth
					else if(ball.yPos + gui.ballDiameter <= p1.yPos - gui.playerSixth) ball.hitPlayer(-1);
					
					//bottom sixth
					else if(ball.yPos >= p1.yPos + 2*gui.playerSixth) ball.hitPlayer(2);
					//2nd from bottom sixth
					else if(ball.yPos >= p1.yPos + gui.playerSixth) ball.hitPlayer(1);
					//centre third
					else ball.hitPlayer(0);
				}
			}
		}

		//coming at player on right, p2
		if(ball.dx > 0){
			//did it hit the player at all
			if(p2.xPos <= ball.xPos + gui.ballDiameter && 
				p2.xPos + gui.playerWidth >= ball.xPos + gui.ballDiameter){
				if(p2.yPos - gui.playerRadius < ball.yPos + gui.ballDiameter &&
					p2.yPos + gui.playerRadius > ball.yPos ){
					//yes it hit, but where:

					//top sixth
					if(ball.yPos + gui.ballDiameter <= p2.yPos - 2*gui.playerSixth) ball.hitPlayer(-2);
					//bottom sixth
					else if(ball.yPos >= p2.yPos + 2*gui.playerSixth) ball.hitPlayer(2);
					
					//2nd from top sixth
					else if(ball.yPos + gui.ballDiameter <= p2.yPos - gui.playerSixth) ball.hitPlayer(-1);
					//2nd from bottom sixth
					else if (ball.yPos >= p2.yPos + gui.playerSixth) ball.hitPlayer(1);
					//centre third
					else ball.hitPlayer(0);
				}
			}
		}
	}

	//checks if ball out of x bounds, and calls endPoint accordingly
	public void checkPointOver(){
		//p1 score
		if(ball.xPos > gui.windowWidth - gui.goalDepth){
			endPoint(1);
		} 
		//p2 scored
		else if (ball.xPos < gui.goalDepth){
			endPoint(2);
		}
	}

	public void endPoint(int winner){
		//points to the winner
		if(winner == 1) p1.points ++;
		else if (winner == 2) p2.points++;
		else print("who won that point?!");
		//Officially mark the score
		print("Point scored by player " + winner +". " + p1.points +"-" + p2.points);
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

	public void pauseGame(){
		pause = !pause;
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

//moveBall is called per timestep
//hitPlayer is called when games decides that a player collided with the balll
//and resetBall stops and centers the ball
class Ball {
	int xPos;
	int yPos;
	
	int dy; //I propose a number from a set of 6, -3 -> +3, where +-1 = straight
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

	//called per timestep, unless game paused
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
	//called by game.scheckBallPlayer()
	//change angle (dy) depending on where it hit on the player (hitSpot)
	//hitspot -1 top, 0 mid, 1 bottom
	public void hitPlayer(int hitSpot){
		pong.print("hit player on spot: " + hitSpot + ", incoming dy " + dy);

		//bounce ball horizontally
		dx = -dx;

		//bounce vertically, depending on incoming angle and hit location:
		//coming straight
		if(dy == 0){
			dy = hitSpot * yUnit; //when coming straight dy is a function of hitSpot
			/* might leave this as comment to justify the line above
			switch(hitSpot){
				case -2:	 dy = -2*yUnit;		break;
				case -1:	 dy = -yUnit; 		break;
				case  0:	 dy = 0; 			break;
				case  1:	 dy = yUnit;		break;
				case  2:	 dy = 2*yUnit;		break; 
				default: pong.print ("somethings up with ball hit player dy 0"); break;
			}*/
 		}

 		//coming in north to south
 		else if(dy > 0){
 			//coming in 'smoothly'
 			if(dy == yUnit){
 				switch(hitSpot){
 					case -2:	 dy = dy*-2;	break; //send it back with more angle
 					case -1:	 dy = -dy; 		break; //send it back the way it came
 					case  0:	 dy = dy; 		break; //ordinary bounce
 					case  1:	 dy = 0; 		break; //'cancels out' result horizontal ball
 					case  2:	 dy = dy*2;		break; //add more to the angle
 					default: pong.print ("somethings up with ball hit player dy > 0");
 					break; 
 				}
 			}
 			//coming in hard
 			else if(dy == 2*yUnit){
 				switch(hitSpot){
 					case -2:	 dy = -dy;		break; //send it back the way it came
 					case -1:	 dy = dy/2;		break; //smooth the angle
 					case  0:	 dy = dy/2; 	break; //smooth the angle
 					case  1:	 dy = dy; 		break; //ordinary bounce
 					case  2:	 dy = 0;		break; //'cancels out' the momentum = horizontal
 					default: pong.print ("somethings up with ball hit player dy >> 0");
 					break; 
 				}
 			}

 			else pong.print("ball angle north -> south problemz");
 		}

 		//coming in south to north
 		else if(dy < 0){
 			//coming in 'smoothly'
 			if(dy == -yUnit){
 				switch(hitSpot){
					case -2:	 dy = dy*2;		break; //add more to the angle
					case -1:	 dy = 0; 		break; //neutralise the ball send it horizontal
					case  0:	 dy = dy; 		break; //ordinary bounce conserve momentum
					case  1:	 dy = -dy; 		break; //send it back the way it came
					case  2:	 dy = dy*-2;	break; //send it back with interest
 					default: pong.print ("somethings up with ball hit player dy > 0");
 					break; 
 				}
 			}
 			//coming in hard
 			else if(dy == -2*yUnit){
 				switch(hitSpot){
 					case -2:	 dy = 0;		break; //'cancels out' the momentum
 					case -1:	 dy = dy;	 	break; //maintain momentum
 					case  0:	 dy = dy/2;		break; //calm that shit
 					case  1:	 dy = dy/2; 	break; //also calm that shit
 					case  2:	 dy = -dy;		break; //send it back the way it came
 					default: pong.print ("somethings up with ball hit player dy >> 0");
 					break; 
 				}
 			}
 			else pong.print("ball angle problemz");
 		}

 		else pong.print("wtf player hit");
 		//pong.print(" newDy: " + dy);
	}
}


