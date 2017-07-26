import java.awt.event.KeyListener;
public class pong {
	/* TODO
		pause with space bar
		stop game freezing when holding keys, yet make keys holdable / adjust sensitivity
		point scoring
		point displaying
		maybe even only let point start when starting player moves
		work out which functions can/should go into pong class as opposed to gui
		player point score function?
		make players stop at edge
	*/
	public static void main(String[] args){
		System.out.println("pong, on git");
		Frame f = new Frame();
		
		/*
		while(thisGameNotOver){

			takeinputs();
			updateGUI(){
				if(moveplayer1) moveplayer1();
				if(moveplayer2) moveplayer2();
				moveBall();
				)}

			//checkGameNotOver()

		}
		
		public movePlayer(int player, int direction){
			//up or down by 1, unless on edge
		}*/
	}

	static public void print(String s){
		System.out.println(s);
	}

}

class Player {
	//there will be two instances of this class
	int yPos;
	int xPos; //used for collision detection with ball

	int playerNumber;
	int dy; //effetively the sensitivity
	int points; //score
	pongGUI gui;
	int northBound,southBound;

	public Player(int pNum, pongGUI gui){
		this.playerNumber = pNum;
		this.yPos = gui.windowYCentre;
		this.points = 0;
		this.dy = gui.heightCell;
		this.northBound = 0;
		this.southBound = gui.windowHeight;// - gui.playerRadius;

		this.gui = gui;

		if(pNum == 1) this.xPos = gui.p1x;
		else if (pNum == 2) this.xPos = gui.p2x;
		else pong.print("totally an error with player number and depth in player constructor");
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

//game only needs two of the functions, moveBall, called per timestep
//and hitPlayer, obviously called when a player hits the ball -- not sure how that's being decided yet
//and resetBall for when the point is over
class Ball {
	int xPos;
	int yPos;
	
	int dy; //I propose a number from a set of 5, where 0 = straight, +2 = max south, -2 = max north
	//so that it can be directly added to y value


	int dx; //how far the ball moves to the side with each timestep, positive = right, neg left
	//should merge dx and direction!
	//ySpeed is angle
	int diameter;
	pongGUI gui;

	public Ball(pongGUI gui){
		this.dy = 0;//gui.heightCell;
		this.dx = gui.widthCell;
		this.xPos = gui.windowXCentre;
		this.yPos = gui.windowYCentre;
		this.diameter = gui.windowWidth / 50;
		this.gui = gui;
	}

	//called per timestep
	public void moveBall(){
		xPos += dx;
		//yPos += dy;
		updateYPos();
	}

	public void updateYPos(){
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

	public void reset(){
		dx = -dx; //winner restarts
		dy = 0;
		xPos = gui.windowXCentre;
		yPos = gui.windowYCentre;
	}
	//the game will check if the ball touched a player and will call hitPlayer
	//-1 top, 0 mid, 1 bottom
	public void hitPlayer(int hitSpot){
		//updateAngle();
		dx = -dx;
		//pong.print("hit spot: " + hitSpot + " dy " + dy);

		//coming straight
		if(dy == 0){
			if(hitSpot == 0) {dy = 0;}
			else if (hitSpot > 0) {dy = gui.heightCell;}
			else if (hitSpot < 0) {dy = -gui.heightCell;}
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
	
/*
	private updateYPos(){
		yPos += dy;

		//check if ball overlaps with wall (if y is out of bounds) if so, hit wall => change angle
		if(y>= upperWall || y<= lowerWall){
			hitWall();
		}
	}

	private hitWall(){
		dy = dy*-1;
	}

	public updateAngle(){
		//this is where it gets complex
		//depending on previous angle and where on the player that it hit, change the angle
		//I propose splitting the player into 3, and having done enough of the game to be able to play it and feel what I want
		//this only happens when the ball hits a player, so GAME must check where on the player it hit
		//and call change direction separately

	}
*/	
}


