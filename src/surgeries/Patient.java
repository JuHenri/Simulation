package surgeries;

import org.javasim.Scheduler;


/**
 * The patient class. The purpose is just that the patient knows it arrival time and urgency.
 * @author Ilari Kauko
 */
public class Patient {
	
	private double arrivalTime;
	private boolean urgent;
	
	
	/**
	 * constructor
	 * @param urgent is the patient urgent
	 */
	public Patient(boolean urgent) {
		this.urgent = urgent;
		this.arrivalTime = Scheduler.currentTime();
	}
	
	
	/**
	 * @return is the patient urgent
	 */
	public boolean urgent() {
		return urgent;
	}
	
	
	/**
	 * @return when the patient arrived the system
	 */
	public double arrivalTime() {
		return arrivalTime;
	}


}
