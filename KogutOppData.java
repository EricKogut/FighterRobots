package FighterRobots;

/**
 * @description This program will extend OppData and add more variables in order for ther robots
 * to make more informed decisions with regards to who to attack
 * @author   	Eric Kogut
 * @Version 	June 19, 2018
 */
public class KogutOppData extends OppData {
	
	//Declaring the variables
	private int pileValue =0;
	private int attackValue=0;
	
	/**
	 * The OppData constructor used to make records of this type.
	 * @param id		the ID number of the player's ID for this OppData record
	 * @param a			the player's avenue for this KogutOppData
	 * @param s			the player's street for this KogutOppData
	 * @param health	the player's health for this KogutOppData
	 */
	public KogutOppData (int id, int a, int s, int health)
	{
		super(id,a,s,health);
		
	}

	
	/**
	 * This sets the attack value for the kogutOppID
	 * @param attackValue	This sets the attack value of the KogutOppId
	 */
	public void setAttack(int attackValue) {
		//If the new one is greater, it actually changes it
		if(this.attackValue<attackValue) {
			this.attackValue = attackValue;
		}
		
	}
	
	/**
	 * This gets the attack value for the kogutOppID
	 * @return attackValue this is the attack value of the robot
	 */
	public int getAttack() {
		return this.attackValue;
	}
	
	/**
	 * This gets the pileValue for the kogutOppID
	 * @param numberOfPlayers This is the umber of player that are on top of a robot.
	 */
	public void setPileValue(int numberOfPlayers) {
		this.pileValue = numberOfPlayers;
	}
	
	/**
	 * This gets the pileValue for the kogutOppID
	 * @return pileValue This is the pile value of the robot. This is how many robots are on top of it.
	 */
	public int getPileValue() {
		return this.pileValue;
	}
	
}




