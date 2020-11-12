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
	private double operationEndTime;
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
	 * set time when operation started
	 */
	public void setOperationStartTime(double time) {
		this.operationStartTime = time;
	}
	
	/**
	 * set time when operation ended
	 */
	public void setOperationEndTime(double time) {
		this.operationEndTime = time;
	}

	/**
	 * set time when recovery started
	 */
	public void setRecoveryStartTime(double time) {
		this.recoveryStartTime = time;
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
	 * @return time preparation started
	 */
	public double getPreparationStartTime() {
		return preparationStartTime;
	}
	
	/**
	 * @return time in operation queue, operation and waiting for recovery
	 */
	public double getOperationTime() {
		return this.operationEndTime - this.operationStartTime;
	}
	
	/**
	 * @return time in recovery
	 */
	public double getRecoveryTime() {
		return this.recoveryEndTime - this.recoveryStartTime;
	}
}
