package FighterRobots;
import java.math.*;
import becker.robots.*;
import java.awt.Color;

/**
 * @description This program will create a fighting robot to fight against other opponents
 * @author   	Eric Kogut
 * @Version 	June 19, 2018
 */

public class OmegaBot extends FighterRobot {
	//Setting up variables
	private static int attack =4;
	private static int defence = 3;
	private static int numMoves = 3;
	private int health = 100;

	/**
	 * This is the constructor for the fighter
	 * @param c This is the city the robot is located in
	 * @param a This is the street the robot is located on
	 * @param s This is the avenue the robot is located on
	 * @param direction This is the direction the robot is facing
	 * @param maxHealh This is the health given to the robot at the beginning of the match
	 */
	public OmegaBot (City c, int a, int s, Direction d, int id, int maxHealth){
		super(c,a,s,d,id,attack,defence,numMoves);
		this.health = maxHealth;
		this.setLabel();

	}



	/**
	 * This is the turnRequest for the robot in order to return who the robot wants to attack and where it wants to move
	 * @param energy This is the energy of the robot at the beginning of the round
	 * @param data This is the oppData provided from the battleManager
	 * @return takeTurn will request who the robot wants to fight, for how many rounds and where it wants to move to
	 */
	public TurnRequest takeTurn(int energy, OppData[] data) {
		//Setting the label
		this.health = data[this.getID()].getHealth();
		this.setLabel();

		//movesLeft describes the amount of agility the robot has at the beggining f the round
		int movesLeft = numMoves;

		//Finds the closest robot
		int lowestRobot = this.findLowestHealth(data);

		//Calculates the difference in position between the attacking bot and the closest bot
		int differenceS = data[lowestRobot].getStreet()- this.getStreet();
		int differenceA = data[lowestRobot].getAvenue() - this.getAvenue();

		//Updating the health of the current bot
		this.health = data[this.getID()].getHealth();
		this.setLabel();

		//ActualX describes how many times the robot is actually allowed to move
		int actualStreetChange = 0;
		int actualAvenueChange = 0;


		//Calculating how many times it can move along a street
		//While the number of moves left is not 0, the robot will add one to the number of moves it can make in that direction
		while(movesLeft != 0 && differenceS != 0) {
			if(differenceS < 0) {
				actualStreetChange--;
				differenceS++;
			}
			else {
				actualStreetChange++;
				differenceS--;
			}	
			movesLeft--;
		}

		//Calculating how many times the robot can move along an avenue
		//While the number of moves left is not 0, the robot will add one to the number of moves it can make in that direction
		while(movesLeft != 0 && differenceA != 0) {
			if(differenceA < 0) {
				actualAvenueChange--;
				differenceA++;
			}
			else {
				actualAvenueChange++;
				differenceA--;
			}	
			movesLeft--;
		}

		//attackThisBot is originally set to -1 however it is changed if the robot can attack a robot and if that robot is not itself
		int attackThisBot = -1;


		//If this robot is on top of the robot it wants to attack, it will set the robot it attacks to its ID
		if(this.getAvenue() == data[lowestRobot].getAvenue() && this.getStreet() == data[lowestRobot].getStreet() && this.getID() != lowestRobot) {
			attackThisBot = data[lowestRobot].getID();
		}


		//Creating the turnRequest object
		TurnRequest myTurn = new TurnRequest(this.getAvenue()+actualAvenueChange, this.getStreet()+actualStreetChange, attackThisBot, attack);

		//If the requested robot is dead it wont move, this is a failsafe
		if(data[lowestRobot].getHealth() == 0) {
			myTurn = new TurnRequest(this.getAvenue(), this.getStreet(), attackThisBot, numMoves);

		}

		//Returning the turn
		return myTurn;

	}



	/**
	 * This will retrieve the battle result in order to update the health of the bot before its turn
	 * @param healthLost This is the amount of health lost by the attacking/defending robot
	 * @param oppID This is the id of the person attacked/defended
	 * @param oppHealthLost This is the amount of health lost by the robot
	 * @param numRoundsFought This is the number of rounds of fight that the robots fought
	 */
	public void battleResult(int healthLost, int oppID, int oppHealthLost, int numRoundsFought) {
		this.health = this.health-healthLost;
		this.setLabel();
	}



	/**
	 * This sets the label of the robot by changing its health, colour and writing its ID
	 */
	public void setLabel(){
		//Setting the colour and displaying its health
		this.setColor(Color.BLUE);
		this.setLabel(Integer.toString(health) + " " + this.getID());

		//If the health is bellow 0, the robot will turn black
		if (health <= 0){
			this.setColor(Color.BLACK);
		}
	}


	/**
	 * This method moves the robot to an appropriate location
	 * @param a This is the avenue the robot wants to go to
	 * @param s This is the street the robot wants to go to 
	 */
	public void goToLocation(int a, int s) {
		//Setting up variables
		int avenue = this.getAvenue();
		int street = this.getStreet();
		int hChange = avenue - a;
		int vChange = street - s;


		//Moving South
		if(vChange < 0) {
			while(this.isFacingSouth()!= true) {
				this.turnRight();
			}
			this.move(java.lang.Math.abs(vChange));
		}

		//Moving West
		if(hChange > 0) {
			while(this.isFacingWest()!= true) {
				this.turnRight();
			}
			this.move(java.lang.Math.abs(hChange));
		}
		//Moving North
		if(vChange > 0) {
			while(this.isFacingNorth()!= true) {
				this.turnRight();
			}
			this.move(java.lang.Math.abs(vChange));
		}
		//Moving East
		if(hChange < 0) {
			while(this.isFacingEast()!= true) {
				this.turnRight();
			}
			this.move(java.lang.Math.abs(hChange));
		}
	}


	/**
	 * This is the method which finds the lowest health bot
	 * @param data This is the oppData provided from the battleManager
	 * @return lowestHealth This method will return the position in the list of the robot with the smallest health
	 */
	private int findLowestHealth(OppData[] data) {

		//Setting up the variables
		int currentIteration = 0;
		int smallestNum = data[0].getHealth();
		int smallestNumPos = 0;
		int counter = 0;

		//Will find the first robot that doesnt have a health of 0
		while(smallestNum == 0) {
			counter++;
			smallestNum = data[counter].getHealth();
		}

		//This will find the robot with the smallest health
		for(int i = 0+currentIteration; i< data.length-1; i++) {
			if(data[i].getHealth() < smallestNum && data[i].getHealth() != 0 && this.getID() != smallestNumPos) {
				smallestNum = data[i].getHealth();
				smallestNumPos = i;		
			}

		}

		//Returns the position of the robot with the smallest health
		return smallestNumPos;
	}
}