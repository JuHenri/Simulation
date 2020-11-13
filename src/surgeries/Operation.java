package surgeries;

import java.io.IOException;
import java.util.LinkedList;

import org.javasim.RestartException;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;
import org.javasim.streams.ExponentialStream;


/**
 * @author Ilari Kauko
 */
public class Operation extends SimulationProcess {
	
	private static final LinkedList<Patient> QUEUE = new LinkedList<Patient>();
    private ExponentialStream operationTime;
    private int surgeriesCompleted = 0;
    private double totalTime = 0;
    private Patient underOperation;
    private boolean blocked;
    private double utilizationTime = 0;
    
    
    /**
     * constructor
     * @param mean the average operation duration
     */
    public Operation(double mean) {
    	this.operationTime = new ExponentialStream(mean);
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
		if (!blocked && underOperation == null)
			try {
				activate();
			} catch (SimulationException | RestartException e) {
				e.printStackTrace();
			}
    }
    
	/**
	 * @return index of first non-urgent patient in the queue
	 */
	public static int getFirstNonUrgentIndex() {
		int nonUrgentIndex = 0;
		for (int i = 0; i < QUEUE.size(); i++) {
			if (!QUEUE.get(i).urgent()) {
				nonUrgentIndex = i;
				break;
			}
		}
		return nonUrgentIndex;
	}
    
    /**
     * The running process. Interpretation: the "service time" includes waiting for free recovery facility.
     */
    @Override
    public void run() {
        while (!terminated()) {
        	double utilizationBegin = currentTime();
        	// If blocked is true, operation was activated because a recovery facility was freed. 
        	// Otherwise, there is a new patient alone in the queue.
        	if (blocked) underOperation.setOperationEndTime(currentTime());
        	blocked = false;
            while (!blocked && !QUEUE.isEmpty()) {
                underOperation = QUEUE.poll();
                try {
                    double t = operationTime.getNumber();
                    hold(t);
                    totalTime += t;
                    surgeriesCompleted++;
                    blocked = !Recovery.free();
                    if (!blocked) Recovery.push(underOperation);
                } catch (ArithmeticException | SimulationException | RestartException | IOException e) {
                    e.printStackTrace();
                }
            }
            // If the loop ended because the operation queue drained and not because all recovery facilities were blocked, there is no patient in the theater.
            if (!blocked) {
            	underOperation.setOperationEndTime(currentTime());
            	underOperation = null;
            }
            utilizationTime += currentTime() - utilizationBegin;
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
    	return utilizationTime;
    }
    
    
    /**
     * @return is the theater blocked because all the recovery facilities are reserved
     */
    public boolean blocked() {
    	return blocked;
    }
}
