package FighterRobots;
import java.math.*;
import becker.robots.*;
import java.awt.Color;

/**
 * @description This program will create a fighting robot to fight against other opponents
 * @author   	Eric Kogut
 * @Version 	June 19, 2018
 */

public class TestFighterRobot extends FighterRobot {

	//Declaring the variables
	private static int attack = 5;
	private static int defence = 2;
	private static int numMoves = 3;
	private int health = 100;

	/**
	 * This is the constructor for the fighter
	 * @param c This is the city the robot is located in
	 * @param a This is the street the robot is located on
	 * @param s This is the avenue the robot is located on
	 * @param direction This is the direction the robot is facing
	 * @param maxHealh This is the health given to the robot at the beginning of the match
	 * 
	 */
	public TestFighterRobot (City c, int a, int s, Direction d, int id, int maxHealth){
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

		//movesLeft describes the amount of agility the robot has at the beggining f the round
		int movesLeft = numMoves;

		//Finds the closest robot
		int closestRobot = this.FindClosestRobot(data);

		//Calculates the difference in position between the attacking bot and the closest bot
		int differenceS = data[closestRobot].getStreet()- this.getStreet();
		int differenceA = data[closestRobot].getAvenue() - this.getAvenue();

		//Getting the health of the current bot and setting the label
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
		if(this.getAvenue() == data[closestRobot].getAvenue() && this.getStreet() == data[closestRobot].getStreet() && this.getID() != closestRobot) {
			attackThisBot = data[closestRobot].getID();
		}

		//Creating the turnRequest object
		TurnRequest myTurn = new TurnRequest(this.getAvenue()+actualAvenueChange, this.getStreet()+actualStreetChange, attackThisBot, numMoves);

		//If the requested robot is dead it wont move, this is a fail safe
		if(data[closestRobot].getHealth() == 0) {
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
	 * This method will find the closest robot
	 * @param data This is the oppData provided from the battleManager
	 * @return closestRobotID this is the ID of the closest Robot
	 */
	private int FindClosestRobot(OppData[] data) {

		//Sets the first robot in the list to be the original bot that the distance is being calculated to 
		int opponentAvenue = data[0].getAvenue();
		int opponentStreet = data[0].getStreet();
		int closestRobotID = 0;

		//Looks for the next bot that is not dead
		int x=0;
		while(data[x].getHealth() <= 0 && this.getID() != data[x].getID()) {
			opponentAvenue = data[x].getAvenue();
			opponentStreet = data[x].getStreet();
			//System.out.println("This is the health of the currentBot "+ data[x].getID()+" "+data[x].getHealth());
			closestRobotID = x;
			x++;
		}

		//If the first robot is the one that is currently being compared, the list starts comparing from the second one
		if(this.getID() == 0) {
			opponentAvenue = data[1].getAvenue();
			opponentStreet = data[1].getStreet();
			closestRobotID = 1;
		}

		//The first distance it calculates is used as a baseline for further comparisons
		int smallestDistance = findDistance(opponentAvenue, opponentStreet);

		//Compares all elements of data in order to determine the closest btot
		for(int i = 0; i < data.length; i++) {
			opponentAvenue = data[i].getAvenue();
			opponentStreet = data[i].getStreet();


			//System.out.println("This is the smallestDistance "+smallestDistance);
			//System.out.println("This is the current bot"+findDistance(opponentAvenue, opponentStreet) );

			//If the distance is smaller than the one previous and the ID is not equal to one being compared, te new smallest distance is established
			if(findDistance(opponentAvenue, opponentStreet) < smallestDistance && this.getID() != i) {
				smallestDistance = findDistance(opponentAvenue, opponentStreet);
				closestRobotID = i;
			}
		}

		//System.out.println("The closest robot to "+ this.getID()+ " is "+closestRobotID);
		//System.out.println("It is located at "+data[closestRobotID].getAvenue() + data[closestRobotID].getStreet() );

		//Returning the closest robot
		return closestRobotID;
	}


	/**
	 * This sets the label of the robot by changing its health, colour and writting its ID
	 */
	public void setLabel(){

		//Setting the colour and displaying its health
		this.setColor(Color.RED);
		this.setLabel(Integer.toString(health) + " " + this.getID());
		
		//If the health is bellow 0, the robot will turn black
		if (health <= 0){
			//System.out.println("This robot is dead :(");
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
		//System.out.println("The vertival change = " +vChange);
		//System.out.println("The horizontal change = "+ hChange);


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
	 * This will determine the distance between 2 robots
	 * @param avenue The avenue of the other bot
	 * @param street The street of the other bot
	 * @return Returns the distance a robot is away
	 */
	private int findDistance(int avenue, int street) {
		//Setting up variables
		int robotAvenue = this.getAvenue();
		int robotStreet = this.getStreet();


		//In order to find the distance you find the absolute value of the difference between the street and avenue
		int streetDifference =  (int)(java.lang.Math.abs(robotStreet - street));
		int avenueDifference = (int)(java.lang.Math.abs(robotAvenue - avenue));
		int totalDistance =  streetDifference+avenueDifference;
		//System.out.println("The distance for this robot is "+ totalDistance);

		//Returning the distance
		return totalDistance;
	}
}
