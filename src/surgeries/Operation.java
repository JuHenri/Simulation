package surgeries;

import java.io.IOException;
import java.util.LinkedList;

import org.javasim.RestartException;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;
import org.javasim.streams.ExponentialStream;


/**
 * @author Ilari Kauko
 * @author Tuomas Kontio
 * @version 13.11.2020
 */
public class Operation extends SimulationProcess {
	
	private static final LinkedList<Patient> QUEUE = new LinkedList<Patient>();
    private ExponentialStream operationTime;
    private int surgeriesCompleted = 0;
    private double totalTime = 0;
    private Patient underOperation;
    private double blockedTime = 0;
    private double blockedStart = 0;
    
    
    /**
     * constructor
     * @param mean the average operation duration
     */
    public Operation(double mean) {
        this.operationTime = new ExponentialStream(mean, 0, 123, 12345);
    }
    
    
    /**
     * Sets a prepared patient to the operation queue.
     * @param p prepared patient
     */
    public void enqueue(Patient p) {
		if (p.urgent()) {
			// add urgent patient after previous urgent patients but before non-urgent
			QUEUE.add(getFirstNonUrgentIndex(), p);
		} else {
			// add patient to the end of the queue
			QUEUE.add(p);
		}
		if (!blocked()) {
			try {
				activate();
			} catch (SimulationException | RestartException e) {
				e.printStackTrace();
			}
		}
    }
    
	/**
	 * @return index of first non-urgent patient in the queue
	 * returns zero if there are no patients in the queue
	 * returns size of the queue if all patients in the queue are urgent
	 */
	public static int getFirstNonUrgentIndex() {
		for (int i = 0; i < QUEUE.size(); i++) {
			if (!QUEUE.get(i).urgent()) {
				return i;
			}
		}
		return QUEUE.size();
	}
    
    /**
     * The running process. Interpretation: the "service time" includes waiting for free recovery facility.
     */
    @Override
    public void run() {
        while (!terminated()) {
        	// If blocked returns true, operation was activated because a recovery facility was freed. 
        	// Otherwise, there is a new patient alone in the queue.
        	if (blocked()) blockedTime += currentTime() - blockedStart;
            while (!QUEUE.isEmpty()) {
                underOperation = QUEUE.poll();
                try {
                	double begin = currentTime();
                    double t = operationTime.getNumber();
                    hold(t);
                    totalTime += currentTime() - begin;
                    // If the sample got terminated when there was a surgery going on
                    if (underOperation == null) break; 
                    surgeriesCompleted++;
                    if (!Recovery.push(underOperation)) {
                    	blockedStart = currentTime();
                    	break;
                    }
                    underOperation = null;
                } catch (ArithmeticException | SimulationException | RestartException | IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                this.passivate();
            } catch (RestartException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    /**
     * @return total time of the surgeries
     */
    public double totalSurgeryTime() {
        return totalTime;
    }
    
    
    /**
     * @return number of operated patients
     */
    public int patientsOperated() {
        return surgeriesCompleted;
    }

    
    /**
     * @return the amount of time there has been an operation going on in the theater
     */
    public double utilizationTime() {
    	return totalTime;
    }
    
    
    /**
     * @return is the theater blocked because all the recovery facilities are reserved
     */
    public boolean blocked() {
    	return underOperation != null && !Recovery.free();
    }
    
    public void reset() {
    	QUEUE.clear();
        surgeriesCompleted = 0;
        totalTime = 0;
        underOperation = null;
        blockedTime = 0;
    }
    
    
    
    /**
     * @return the total amount of time the theater has been blocked from delivering the patient to recovery
     */
    public double blockedTime() {
        return blockedTime;
    }   
}