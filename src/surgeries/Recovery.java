package surgeries;

import java.io.IOException;
import java.util.Stack;

import org.javasim.RestartException;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;
import org.javasim.streams.ExponentialStream;

/**
 * @author Ilari Kauko
 */
public class Recovery extends SimulationProcess {
	
	// Like in the preparation class, queue is implemented with a standard library and free facilities are in a stack.
	private static final Stack<Recovery> FREE = new Stack<Recovery>();
	private static int recovered = 0;
	private static double totalThroughput = 0;
	private static Patient next;
	private static int numUrgent = 0;
	private static double urgentThroughput = 0;
	
	private ExponentialStream recoveryTime;
	private Operation theater;
	
	/**
	 * constructor
	 */
	public Recovery(double mean, Operation theater) {
		recoveryTime = new ExponentialStream(mean);
		this.theater = theater;
		FREE.add(this);
	}
	
	/**
	 * Tells if there is any facility available for new patients
	 */
	public static boolean free() {
		return !FREE.empty();
	}
	
	
	/**
	 * Starts the recovery of a given patient. The caller must first check if there is free facilities available.
	 * @param p
	 */
	public static void push(Patient p) {
		try {
			next = p;
			FREE.pop().activate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @return the average time between the arrival to the system and the recovery end of a recovered patient
	 */
	public static double averageThroughput() {
		return totalThroughput/recovered;
	}
	
	/**
	 * @return the average time between the arrival to the system and the recovery end of a recovered urgent patient
	 */
	public static double urgentThroughput() {
		return urgentThroughput/numUrgent;
	}
	
	/**
	 * @return the average time between the arrival to the system and the recovery end of a recovered non-urgent patient
	 */
	public static double nonUrgentThroughput() {
		return (totalThroughput-urgentThroughput)/(recovered-numUrgent);
	}

	/**
	 * @return number of recovered patients
	 */
	public static int recovered() {
		return recovered;
	}
	
	
	/**
	 * the run process
	 */
	public void run() {
		while (!terminated()) {
			do {
				Patient p = next;
				double startTime = currentTime();
				p.setRecoveryStartTime(startTime);
				try {
					// If the theater is blocked, there is a free facility for it.
					if (theater.blocked()) theater.activate();
					hold(recoveryTime.getNumber());
				} catch (ArithmeticException | SimulationException | RestartException | IOException e) {
					e.printStackTrace();
				}
				recovered++;
				double t = currentTime();
				totalThroughput += t - p.getPreparationStartTime();
				if (p.urgent()) {
					numUrgent++;
					urgentThroughput += t - p.getPreparationStartTime();
				}
				p.setRecoveryEndTime(t);
			} while (theater.blocked()); // If the theater is blocked, this facility can be straightly reserved for it
			FREE.push(this);
			try {
				passivate();
			} catch (RestartException e) {
				e.printStackTrace();
			}
		}
	}
}