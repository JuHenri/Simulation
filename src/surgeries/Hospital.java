package surgeries;

import org.javasim.Simulation;
import org.javasim.SimulationProcess;


/**
 * The process coordinating the whole simulation
 * @author Ilari Kauko
 * @author Henri Jussila
 * @author Tuomas Kontio
 * @version 13.11.2020
 */
public class Hospital extends SimulationProcess {
	
	// the questionable parameters about the hospital capacity and its service times
	private int patientInterval = 25;
	private int preparationTime = 40;
	private int numOperationUnits = 1; // not in use (yet)
	private int operationTime = 20;
	private int recoveryTime = 40;
	private double urgentPercentage = 10;
	private int numSamples = 20;
	private int samplingInterval = 1000;
	
	/**
	 * run process
	 */
	@Override
	public void run() {
		try {
			int[] preparationCapacities = new int[] {3,3,4};
			int[] recoveryCapacities = new int[] {4,5,5};
			Operation op = new Operation(operationTime);
			// Preparation and recovery facilities are stored to arrays.
			Simulation.start();
			Arrivals generator = new Arrivals(patientInterval, urgentPercentage/100);
			Reporter monitor = new Reporter(preparationCapacities.length, numSamples, samplingInterval, op);
			generator.activate();
			for (int j = 0; j < preparationCapacities.length; j++) {
				int numPreparationUnits = preparationCapacities[j];
				int numRecoveryUnits = recoveryCapacities[j];
				System.out.println("Experiment "+(j+1)+": "+numPreparationUnits+" preparation units and "+numRecoveryUnits+" recovery units.");
				for (int i = 0; i < numSamples; i++) {
					Preparation[] preparations = new Preparation[numPreparationUnits];
					for (int k = 0; k < numPreparationUnits; k++) preparations[k] = new Preparation(preparationTime, op);
					Recovery[] recoveries = new Recovery[numRecoveryUnits];
					for (int k = 0; k < numRecoveryUnits; k++) recoveries[k] = new Recovery(recoveryTime, op);
					hold(samplingInterval);
					monitor.update(j, i);
					Preparation.reset();
					Recovery.reset();
					op.reset();
				}
				monitor.report(j, numSamples);
				System.out.println();
			}
			monitor.reportSignificance();
			generator.terminate();
			op.terminate();
            Simulation.stop();
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