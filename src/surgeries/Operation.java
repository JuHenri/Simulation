package surgeries;

import java.io.IOException;

import org.javasim.RestartException;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;
import org.javasim.streams.ExponentialStream;


/**
 * @author Ilari Kauko
 * TODO: Write This!
 */
public class Operation extends SimulationProcess {
	
    private int surgeriesCompleted = 0;
    private ExponentialStream operationTime = new ExponentialStream(30);
    private boolean free = true;
    private double totalTime = 0;
    
    /**
     * the running process
     */
    public void run() {
        while (!terminated()) {
            while (free & Preparation.hasNextPatient()) {
                Patient p = Preparation.getPatientForSurgery();
                free = false;
                try {
                    double t = operationTime.getNumber();
                    hold(t);
                    totalTime+= t;
                    surgeriesCompleted++;
                    //TODO: recovery
                    free = true;
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
    
    public double totalSurgeryTime() {
        return totalTime;
    }
    
    public int PatientsOperated() {
        return surgeriesCompleted;
    }
}
