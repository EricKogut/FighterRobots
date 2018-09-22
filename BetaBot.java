package FighterRobots;
import java.math.*;
import becker.robots.*;
import java.awt.Color;
/**
 * @description This program will create a fighting robot to fight against other opponents
 * @author   	Eric Kogut
 * @Version 	June 19, 2018
 */

public class BetaBot extends FighterRobot {




	/**
	 * This is the constructor for the fighter
	 * @param city This is the city the robot is located in
	 * @param street This is the street the robot is located on
	 * @param avenue This is the avenue the robot is located on
	 * @param direction This is the direction the robot is facing
	 * @param maxHealh This is the health given to the robot at the beginning of the match
	 */

	//Setting up the variables
	private static int attack = 4;
	private static int defence = 4;
	private static int numMoves = 2;
	private int health = 100;


	public BetaBot (City c, int a, int s, Direction d, int id, int maxHealth){
		super(c,a,s,d,id,attack,defence,numMoves);
		this.setLabel();

	}

	/**
	 * This is the turnRequest for the robot in order to return who the robot wants to attack and where it wants to move
	 * @param energy This is the energy of the robot at the beginning of the round
	 * @param data This is the oppData provided from the battleManager
	 */
	
	public TurnRequest takeTurn(int energy, OppData[] data) {
		//Setting the label
		this.health = data[this.getID()].getHealth();
		this.setLabel();
		
		//Instantiating a turnRequest object
		TurnRequest myTurn;
		
		//movesLeft describes the amount of agility the robot has at the beggining of the round
		int movesLeft = numMoves;

		
		//Sorting the list by health
		sortByHealth(data);

		//Finding the amount of dead bots
		int deadBotNumber  = findDeadBots(data);
		//System.out.println("THERE ARE CURRENTLY THIS MANY DEAD"+ deadBotNumber);
		
		//List to store the distance to each bot that is still alive
		int distances[] = new int[data.length-deadBotNumber];

		//Finding the distances between each bot that is alive
		for(int i = 0; i < data.length-deadBotNumber; i++) {
			distances[i] = findDistance(data[deadBotNumber+i].getAvenue(), data[deadBotNumber+i].getStreet());
		}

		//Finds the best robot based on their distance and health
		int bestRobot = this.findBestBot(data, distances, deadBotNumber);



		//Calculates the difference in position between the attacking bot and the best selected above
		int differenceS = data[bestRobot].getStreet()- this.getStreet();
		int differenceA = data[bestRobot].getAvenue() - this.getAvenue();

	
			
		//ActualX describes how many times the robot is actually allowed to move
		int actualStreetChange = 0;
		int actualAvenueChange = 0;


		//Calculating how many times it can move along a street
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

		//System.out.println("Will move "+actualStreetChange);
		//System.out.println("Will move "+actualAvenueChange);
		//System.out.println();

		
		
		//attackThisBot is originally set to -1 however it is changed if the robot can attack a robot and if that robot is not itself
		int attackThisBot = -1;
		
		//If its on the same tile as a robot, it will initiate a fight
		if(this.getAvenue() == data[bestRobot].getAvenue() && this.getStreet() == data[bestRobot].getStreet() && this.getID() != data[bestRobot].getID()) {
			attackThisBot = data[bestRobot].getID();
		}

		//returning the request if it needs to move
		myTurn = new TurnRequest(this.getAvenue()+actualAvenueChange, this.getStreet()+actualStreetChange, attackThisBot, attack);




		//If the requested robot lacks energy, it wont move
		//System.out.println("The robots energy is "+energy);
		if(energy <= 10) {

			//If the robot is running out of energy, it wont start  fight or move
			myTurn = new TurnRequest(this.getAvenue(), this.getStreet(), -1, -1);
			//System.out.println("Robot"+ this.getID()+ "is running out of energy and wont move");
		}

	
		return myTurn;

	}




	public void battleResult(int healthLost, int oppID, int oppHealthLost, int numRoundsFought) {
		this.health = this.health-healthLost;
		setLabel();
	}




	//The setLabel method sets the colour of the robot and what the robot displays overs its body
	public void setLabel(){

		//Setting the colour and displaying its health
		this.setColor(Color.PINK);
		this.setLabel(Integer.toString(health) + " " + this.getID());
		//If the health is bellow 0, the robot will turn black
		if (health <= 0){
			System.out.println("This robot is dead :(");
			this.setColor(Color.BLACK);
		}
	}


	/**
	 * This method moves the robot to an appropriate location
	 * @param a This is the avenue the robot wants to go to
	 * @param s This is the street the robot wants to go to 
	 */

	public void goToLocation(int a, int s) {
		//Setting up variales
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
	 * This is the method which finds the lowest health bot
	 * @param data This is the oppData provided from the battleManager
	 * @return lowestHealth This method will return the position in the list of the robot with the smallest health
	 */
	public int findLowestHealth(OppData[] data) {
		//Setting up variables
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
		
		//System.out.println("The bot, "+smallestNumPos+" has the lowest health");
		return smallestNumPos;
	}


	/**
	 * This will determine the distance between 2 robots
	 * @param avenue The avenue of the other bot
	 * @param street The street of the other bot
	 */
	private int findDistance(int avenue, int street) {
		//RobotAvenue and robot street are the location of my robot
		int robotAvenue = this.getAvenue();
		int robotStreet = this.getStreet();

		//In order to find the distance you find the absolute value of the difference between the street and avenue
		int streetDifference =  (int)(java.lang.Math.abs(robotStreet - street));
		int avenueDifference = (int)(java.lang.Math.abs(robotAvenue - avenue));
		int totalDistance =  streetDifference+avenueDifference;

		//Returning the distance
		return totalDistance;
	}



	
	/**
	 * This will sort a list of oppData by health
	 * @param data The OppData provided
	 */
	private void sortByHealth(OppData[] data) {
		OppData currentNumber = data[1];
		int sortedListLength = 1;
		OppData oldNum = null;
		int oldNumPos = 0;
		int counter = 1;

		//The loop will run for how many items there are in the array of numbers
		for(int i =0; i < data.length-1; i++) {

			//Until the current number is not in the appropriate location the sorter will continue to move it
			while(sortedListLength-counter>=0 && data[sortedListLength-counter].getHealth() > currentNumber.getHealth()) {

				//Moving the old number to the appropriate location
				oldNum = data[sortedListLength-counter];
				oldNumPos = sortedListLength - counter;

				//The number that had to be move is now the new, smaller number
				data[sortedListLength-counter] = currentNumber;

				//The old number is put in the position that the smaller number was in
				data[sortedListLength-counter+1  ] = oldNum;

				//Printing out the array


				//Adding 1 to the counter in order to check the next adjacent number to the left of the "small" number
				counter+=1;


			}

			//Once the new small number is added to the array, the sorted list grows by 1
			sortedListLength+=1;
			//As long as the sorted list is not equal to the length of the current list, the nwe number will be upadted to be the one in front of the sorted lsit
			if(sortedListLength != data.length) {
				currentNumber = data[sortedListLength];
			}

			//Counter is reset
			counter = 1;
		}

		for(int j = 0; j <= data.length-1; j++) {
			//System.out.print(data[j].getHealth()+ " ");
		}

	}
	/**
	 * This will find the dead bots in a given oppdata array
	 * @param kogutData the given KogutOppData[] provided
	 * @return counter This is the number of bots
	 */
	private int findDeadBots(OppData[] data) {
		int counter = 0;
		for(int i = 0; i < data.length; i++) {
			if(data[i].getHealth() <= 0 ) {
				//System.out.println("Bot"+ data[i].getID() + " is dead");
				counter++;
			}
		}
		
		//Returning the counter
		return counter;

	}

	/**
	 * This will find the best robot to attack
	 * @param dataata The OppData[] of the robots
	 * @param distances The list of the distances away of all the robots
	 * @param deadBotAmount The number of dead bots dead in the match
	 * @return Will return the position of the best bot in the sorted array
	 */
	private int findBestBot(OppData[] data, int[] distances, int deadBotAmount) {
		//Setting up variables
		int DISTANCEFACTOR = 10;
		int HEALTHFACTOR = 4;

		//Variable to store how worth it it would be to attack a bot
		int pointCounter[] = new int[data.length-deadBotAmount];
		
		//Adds the points accordingly
		for(int i = 0 ; i < pointCounter.length; i++) {
			pointCounter[i] += DISTANCEFACTOR*distances[i];
			pointCounter[i] += HEALTHFACTOR*data[deadBotAmount+i].getHealth();
		//	System.out.println(pointCounter[i]);

		}

		
		
		//Initiating variables for sorting which number of points is the smallest
		int smallestNum = pointCounter[0];
		int smallestNumPos = 0;
		int counter = 0;

		//Used when this robot at the front of the list point counter and is the robot that is using this method
		while(this.getID() == data[deadBotAmount + counter].getID()) {
			counter++;
			smallestNum = pointCounter[counter];
		}
		
		//Finding the smallest value position
		for(int i = 0; i < pointCounter.length ; i++) {
			//System.out.println("currentSmallest" + smallestNum + " vs " + pointCounter[i]);
			if(pointCounter[i] < smallestNum && pointCounter[i] != 0 && data[i + deadBotAmount].getID()!= this.getID()) {
				smallestNum = pointCounter[i];
				smallestNumPos = i;		
				
			}

		}

		//Returning the location of the best bot in the array
		return smallestNumPos + deadBotAmount;
	}
}
