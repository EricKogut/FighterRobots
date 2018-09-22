package FighterRobots;
import java.math.*;
import becker.robots.*;
import java.awt.Color;

/**
 * @description This program will create a fighting robot to fight against other opponents
 * @author   	Eric Kogut
 * @Version 	June 19, 2018
 */


public class AlphaBot extends FighterRobot {
	
	//Declaring the variables
	private static int attack = 5;
	private static int defence = 4;
	private static int numMoves = 1;
	private int health;
	private int attackID = -1;
	private int battleResultsAttack[];
	private boolean firstTurn = true;



	/**
	 * This is the constructor for the fighter
	 * This robot works wherever it is put, so it may spin more than an average stair bot
	 * @param c This is the city the robot is located in
	 * @param a This is the avenue the robot is located on
	 * @param s This is the street the robot is located on
	 * @param direction This is the direction the robot is facing
	 * @param maxHealh This is the health given to the robot at the beginning of the match
	 */
	public AlphaBot (City c, int a, int s, Direction d, int id, int maxHealth){
		super(c,a,s,d,id,attack,defence,numMoves);
		this.health = maxHealth;
		this.setLabel();
	}



	/**
	 * This is the turnRequest for the robot in order to return who the robot wants to attack and where it wants to move
	 * @param energy This is the energy of the robot at the beginning of the round
	 * @param data This is the oppData provided from the battleManager
	 * @return taketTurn will request who the robot wants to fight, for how many rounds and where it wants to move to
	 */
	public TurnRequest takeTurn(int energy, OppData[] data) {
		//Setting the label
		this.health = data[this.getID()].getHealth();
		this.setLabel();


		//Creating list to store player data
		KogutOppData[] kogutData = new KogutOppData[data.length];


		//If its the first turn, it will create a new array to store all the attack values
		if(firstTurn) {
			this.battleResultsAttack = new int[data.length];
			firstTurn = false;
		}


		//Creating the custom KogutOppData list and updating attack value and pile value
		createOppData(data,kogutData);
		updateAttackValue(battleResultsAttack, kogutData);
		setPileValues(kogutData);


		//Instantiating a turnRequest object
		TurnRequest myTurn;


		//movesLeft describes the amount of agility the robot has at the beggining of the round
		int movesLeft = numMoves;


		//Sorting the list by health
		sortByHealth(kogutData);


		//Finding the amount of dead bots
		int deadBotNumber  = findDeadBots(kogutData);


		//List to store the distance to each bot that is still alive
		int distances[] = new int[kogutData.length-deadBotNumber];


		//Finding the distances between each bot that is alive
		for(int i = 0; i < kogutData.length-deadBotNumber; i++) {
			distances[i] = findDistance(kogutData[deadBotNumber+i].getAvenue(), kogutData[deadBotNumber+i].getStreet());
		}


		//Finds the best robot based on their distance and health
		int bestRobot = this.findBestBot(kogutData, distances, deadBotNumber);



		//Calculates the difference in position between the attacking bot and the best selected above
		int differenceS = kogutData[bestRobot].getStreet()- this.getStreet();
		int differenceA = kogutData[bestRobot].getAvenue() - this.getAvenue();


		//ActualX describes how many times the robot is actually allowed to move in a given direction
		int actualStreetChange = streetMouvement(movesLeft, differenceS);
		movesLeft = movesLeft - (int)(java.lang.Math.abs(actualStreetChange));
		int actualAvenueChange = avenueMouvement(movesLeft, differenceA);


		//attackThisBot is originally set to -1 however it is changed if the robot can attack a robot and if that robot is not itself
		int attackThisBot = -1;

		//If its on the same tile as a robot, it will initiate a fight
		if(this.getAvenue() == kogutData[bestRobot].getAvenue() && this.getStreet() == kogutData[bestRobot].getStreet() && this.getID() != kogutData[bestRobot].getID()) {
			attackThisBot = kogutData[bestRobot].getID();
			attackID = kogutData[bestRobot].getID();
		}


		//creating the request if it needs to move
		myTurn = new TurnRequest(this.getAvenue()+actualAvenueChange, this.getStreet()+actualStreetChange, attackThisBot, attack);



		//If the robot lacks energy, it wont move
		if(energy <= 10) {
			myTurn = new TurnRequest(this.getAvenue(), this.getStreet(), -1, -1);
			//System.out.println("Robot"+ this.getID()+ "is running out of energy and wont move");

		}

		//returning The turn
		return myTurn;

	}



	/**
	 * This is the battle result of a defence or attack
	 * @param healthLost Describes how much health was lost in the given fight
	 * @param oppID This is the ID of the player you attacked or you had to defend against.
	 * @param oppHealthLost This is the amount of energy the robot that was attacking/defending lost
	 * @numRoundsFought This is the number of rounds a fight was initiated for
	 */
	public void battleResult(int healthLost, int oppID, int oppHealthLost, int numRoundsFought) {
		//Updating the health of the robot
		this.health = this.health-healthLost;

		//If the oppID provided is equal to the previous attack, it means the battleResult is returning the id of the previous bot it fought
		if(this.attackID == -1 && oppID != -1) {
			//System.out.println("Robot"+ oppID+ " fought "+ numRoundsFought+" rounds");
			this.battleResultsAttack[oppID] = numRoundsFought;
			//System.out.println("This robot lost defended and lost"+ healthLost);
		}
		else{
			this.attackID = -1;
			//System.out.println("This robot attacked and lost"+ healthLost);
		}
	}



	/**
	 * This sets the label of the robot by changing its health and writting its ID
	 */
	public void setLabel(){
		//Setting the colour
		this.setColor(Color.YELLOW);
		//Setting the colour and displaying its health
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
	private void sortByHealth(KogutOppData[] kogutData) {
		//Creating the variables
		KogutOppData currentNumber = kogutData[1];
		int sortedListLength = 1;
		KogutOppData oldNum = null;
		int oldNumPos = 0;
		int counter = 1;

		//The loop will run for how many items there are in the array of numbers
		for(int i =0; i < kogutData.length-1; i++) {

			//Until the current number is not in the appropriate location the sorter will continue to move it
			while(sortedListLength-counter>=0 && kogutData[sortedListLength-counter].getHealth() > currentNumber.getHealth()) {

				//Moving the old number to the appropriate location
				oldNum = kogutData[sortedListLength-counter];
				oldNumPos = sortedListLength - counter;

				//The number that had to be move is now the new, smaller number
				kogutData[sortedListLength-counter] = currentNumber;

				//The old number is put in the position that the smaller number was in
				kogutData[sortedListLength-counter+1  ] = oldNum;

				//Adding 1 to the counter in order to check the next adjacent number to the left of the "small" number
				counter+=1;

			}

			//Once the new small number is added to the array, the sorted list grows by 1
			sortedListLength+=1;

			//As long as the sorted list is not equal to the length of the current list, the nre number will be upadted to be the one in front of the sorted lsit
			if(sortedListLength != kogutData.length) {
				currentNumber = kogutData[sortedListLength];
			}

			//Counter is reset
			counter = 1;
		}
	}


	/**
	 * This will find the dead bots in a given oppdata array
	 * @param kogutData the given KogutOppData[] provided
	 * @return counter This is the number of bots that are dead
	 */
	private int findDeadBots(KogutOppData[] kogutData) {
		//Creating variables
		int counter = 0;

		//For how many items there are in the kogutData the list will iterate through
		for(int i = 0; i < kogutData.length; i++) {
			if(kogutData[i].getHealth() <= 0 ) {
				//System.out.println("Bot"+ kogutData[i].getID() + " is dead");
				counter++;
			}
		}

		//Returning the amount of dead bots
		return counter;

	}
	/**
	 * This will find the best robot to attack
	 * @param kogutData The give KogutOppData[] of the robots
	 * @param distances The list of the distances away of all the robots
	 * @param deadBotAmount The number of dead bots dead in the match
	 * @return Will return the position of the best bot in the sorted array
	 */
	private int findBestBot(KogutOppData[] kogutData, int[] distances, int deadBotAmount) {

		//Creating the variables
		int DISTANCEFACTOR = 10;
		int HEALTHFACTOR = 4;
		int PILEFACTOR = 15;
		int ATTACKFACTOR = -5;

		//Variable to store how worth it it would be to attack a bot
		int pointCounter[] = new int[kogutData.length-deadBotAmount];

		//Adds the points accordingly
		for(int i = 0 ; i < pointCounter.length; i++) {
			pointCounter[i] += DISTANCEFACTOR*distances[i];
			pointCounter[i] += HEALTHFACTOR*kogutData[deadBotAmount+i].getHealth();
			pointCounter[i] += PILEFACTOR*kogutData[deadBotAmount+i].getPileValue();
			pointCounter[i] += ATTACKFACTOR*kogutData[deadBotAmount+i].getAttack();
			//System.out.println(pointCounter[i]);
		}

		//Initiating variables for sorting which number of points is the smallest
		int smallestNum = pointCounter[0];
		int smallestNumPos = 0;
		int counter = 0;

		//This is used in case bot0 determines itself to have the smallest value. If it will be comparing to itself, it changes value
		while(this.getID() == kogutData[deadBotAmount + counter].getID()) {
			counter++;
			smallestNum = pointCounter[counter];
		}	

		//This will find the smallest number of points in the array
		for(int i = 0; i < pointCounter.length ; i++) {
			//System.out.println("currentSmallest" + smallestNum + " vs " + pointCounter[i]);
			if(pointCounter[i] < smallestNum && pointCounter[i] != 0 && kogutData[i + deadBotAmount].getID()!= this.getID()) {
				smallestNum = pointCounter[i];
				smallestNumPos = i;		
			}
		}

		//Returning the smallest number positions
		return smallestNumPos + deadBotAmount;
	}



	/**
	 * This will create the custom KogutOppData[]
	 * @param data The OppData provided
	 * @param kogutData the data that you would like to create
	 */
	private void createOppData(OppData[] data, KogutOppData[] kogutData) {
		//Running through all the elements in the oppdata list
		for(int i = 0;i < data.length;i++) {

			//Creating new InsullOppData with parameters from the OppData list
			//	System.out.println(data[i].getID());
			//			System.out.println(data[]);
			kogutData[i] = new KogutOppData(data[i].getID(), data[i].getAvenue(), data[i].getStreet(), data[i].getHealth());
		}
	}

	/**
	 * This will create attack values of each robot for the KogutOppData[]
	 * @param attackValues the attack values of the robot
	 * @param data The OppData provided
	 */
	private void updateAttackValue(int[] attackValues,KogutOppData[] kogutData ) {
		//For all the attack values provided in the array, assign attack values to the kogutData list
		for(int i = 0; i< attackValues.length; i++) {
			kogutData[i].setAttack(attackValues[i]);

		}
	}


	/**
	 * This will create the pile values for each robot in the KogutOppData[]
	 * @param kogutData the list of KogutOppData information
	 */
	private void setPileValues(KogutOppData[] kogutData) {
		//for every robot
		for(int i = 0 ; i< kogutData.length; i++) {
			int counter = 0;
			//Check if any other robot is on top of it
			for(int j = 0; j <kogutData.length;j++) {
				if(kogutData[i].getAvenue() == kogutData[j].getAvenue() && kogutData[i].getStreet() == kogutData[j].getStreet() && kogutData[i].getID() != kogutData[j].getID()) {
					counter ++;
				}
				//Actually setting the pile value
				kogutData[i].setPileValue(counter);	
			}
		}

	}

	/**
	 * This will determine how many times a robot is actually allowed to move along a street
	 * @param movesLeft1 The number of moves left for the robot to do
	 * @param differenceS1 The requested number of moves the robot wants to do
	 * @return allowedStreetChange How many time the robot will actually move along a street
	 */
	public int streetMouvement(int movesLeft1, int differenceS1) {

		//Setting up the return variable
		int allowedStreetChange = 0;
		//System.out.println("The number of moves allowed is "+ movesLeft1);
		//System.out.println("Requested change is "+ differenceS1);

		//While the robot still has moves left, it will move
		while(movesLeft1 != 0 && differenceS1 != 0) {
			if(differenceS1 < 0) {
				allowedStreetChange--;
				differenceS1++;
			}
			else {
				allowedStreetChange++;
				differenceS1--;
			}	
			movesLeft1--;
		}
		//System.out.println("Allowed change"+ allowedStreetChange);

		//Return the number of times it can move along a street
		return allowedStreetChange;
	}

	/**
	 * This will determine how many times a robot will move along an avenue
	 * @param movesLeft1 The number of moves left for the robot to do
	 * @param differenceA1 The requested number of moves the robot wants to do
	 * @return allowedAvenueChange How many time the robot will actually move along an avenue
	 */
	public int avenueMouvement(int movesLeft1, int differenceA1) {

		//Setting up variables
		int allowedAvenueChange = 0 ;
		//System.out.println("The number of moves allowed is "+ movesLeft1);
		//System.out.println("Requested change is "+ differenceA1);


		//While the robot still has moves left, it will move
		while(movesLeft1 != 0 && differenceA1 != 0) {
			if(differenceA1 < 0) {
				allowedAvenueChange--;
				differenceA1++;
			}
			else {
				allowedAvenueChange++;
				differenceA1--;
			}	
			movesLeft1--;
		}
		//System.out.println("Allowed change"+ allowedAvenueChange);

		//Returning actual allowed change in avenue
		return allowedAvenueChange;
	}
}