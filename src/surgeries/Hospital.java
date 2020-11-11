package surgeries;

import org.javasim.Simulation;
import org.javasim.SimulationProcess;


/**
 * The process coordinating the whole simulation
 * @author Ilari Kauko
 */
public class Hospital extends SimulationProcess {
	
	private int patientInterval = 25;
	private int numPrerationUnits = 3;
	private int prerationTime = 80;
	private int numOperationUnits = 1;
	private int operationTime = 20;
	private int numRecoveryUnits = 3;
	private int recoveryTime = 40;
	
	/**
	 * run process
	 */
	@Override
	public void run() {
		try {
			double startTime = currentTime();
			Arrivals generator = new Arrivals(patientInterval, 0.1);
			generator.activate();
			// Preparation facilities are stored to an array.
			Preparation[] preparations = new Preparation[numPrerationUnits];
			for (int i = 0; i < numPrerationUnits; i++) {
				preparations[i] = new Preparation(prerationTime);
			}
			Operation op = new Operation();
			Simulation.start();
			while (Preparation.prepared() < 10000) {
				hold(1);
                op.activate();
			}
			System.out.println("Time: "+(currentTime() - startTime));
			System.out.println("Average time in queue and preparation: "+Preparation.averageTime());
			System.out.println("Total time spent in surgery for all patients: "+op.totalSurgeryTime());
			System.out.println("Patients operated: "+op.PatientsOperated());
            Simulation.stop();
			generator.terminate();
			for (Preparation p : preparations) p.terminate();
            SimulationProcess.mainResume();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Starts the simulation process.
	 */
    public void await() {
        this.resumeProcess();
        SimulationProcess.mainSuspend();
    }
}
