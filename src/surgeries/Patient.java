package surgeries;

import org.javasim.Scheduler;


/**
 * The patient class. The purpose is just that the patient knows it arrival time and urgency.
 * @author Ilari Kauko
 */
public class Patient {
	
	private double preparationStartTime;
	private double preparationEndTime;
	private double operationStartTime;
	private double opertationEndTime;
	private double recoveryStartTime;
	private double recoveryEndTime;
	private boolean urgent;
	
	
	/**
	 * constructor
	 * @param urgent is the patient urgent
	 */
	public Patient(boolean urgent) {
		this.urgent = urgent;
		this.preparationStartTime = Scheduler.currentTime();
	}
	
	
	/**
	 * @return is the patient urgent
	 */
	public boolean urgent() {
		return urgent;
	}
	
	/**
	 * set time when preparation ended
	 */
	public void setPreparationEndTime(double time) {
		this.preparationEndTime = time;
	}
	
	/**
	 * set time when operation ended
	 */
	public void setOperrationEndTime(double time) {
		this.opertationEndTime = time;
	}
	
	/**
	 * set time when recovery ended
	 */
	public void setRecoveryEndTime(double time) {
		this.recoveryEndTime = time;
	}
	
	/**
	 * @return time in preparation queue & preparation
	 */
	public double getPreparationTime() {
		return this.preparationEndTime - this.preparationStartTime;
	}
	
	/**
	 * @return time in operation queue & operation
	 */
	public double getOperationTime() {
		return this.opertationEndTime - this.operationStartTime;
	}
	
	/**
	 * @return time in recovery queue & recovery
	 */
	public double getRecoveryTime() {
		return this.recoveryEndTime - this.recoveryStartTime;
	}

}
