package surgeries;

import java.io.IOException;
import java.util.Stack;

import org.javasim.RestartException;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;
import org.javasim.streams.ExponentialStream;

/**
 * @author Ilari Kauko
 * @author Henri Jussila
 * @author Tuomas Kontio
 * @version 13.11.2020
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
     * @param mean used to init the exponentialStram
     * @param theater operation theather being used.
     */
    public Recovery(double mean, Operation theater) {
        recoveryTime = new ExponentialStream(mean, 0, 123, 12345);
        this.theater = theater;
        FREE.add(this);
    }

    /**
     * Tells if there is any facility available for new patients
     * @return true if there are facilities available.
     */
    public static boolean free() {
        return !FREE.empty();
    }


    /**
     * Starts the recovery of a given patient. The caller must first check if there are free facilities available.
     * @param p the patient object being pushed.
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
     * @return the average time between the arrival to the system and the recovery end of a recovered urgent patient.
     * If there were 0 urgent patients returns a -1;
     */
    public static double urgentThroughput() {
        if (numUrgent != 0)return urgentThroughput/numUrgent;
        return -1;
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
	 * @return total throughput
	 */
	public static double totalThroughput() {
		return totalThroughput;
	}


	/**
	 * the run process
	 */
	@Override
    public void run() {
        while (!terminated() && next != null) {
            do {
                Patient p = next;
                double startTime = currentTime();
                p.setRecoveryStartTime(startTime);
                try {
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
                FREE.push(this);
                // If the theater is blocked, there is a free facility for it.
                if (theater.blocked())
                    try {
                        theater.activate();
                    } catch (SimulationException | RestartException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
            } while (theater.blocked()); // If the theater is blocked, this facility can be straightly reserved for it
            try {
                passivate();
            } catch (RestartException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Resets the static attributes to their initial value/state to enable a new simulation
     */
    public static void reset() {
        FREE.clear();
        recovered = 0;
        totalThroughput = 0;
        next = null;
        numUrgent = 0;
        urgentThroughput = 0;
    }
}
