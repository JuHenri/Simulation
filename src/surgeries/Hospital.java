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
	private int numPreparationUnits = 4;
	private int preparationTime = 40;
	private int numOperationUnits = 1; // not in use (yet)
	private int operationTime = 20;
	private int numRecoveryUnits = 5;
	private int recoveryTime = 40;
	private double urgentPercentage = 10;
	private int numSamples = 20;
	private int samplingInterval = 1000;
	
	private Preparation[] preparations;
	private Recovery[] recoveries;
	
	/**
	 * run process
	 */
	@Override
	public void run() {
		try {
			Operation op = new Operation(operationTime);
			Arrivals generator = new Arrivals(patientInterval, urgentPercentage/100);
			Reporter monitor = new Reporter(numSamples, samplingInterval, op);
			setupFacilities(op);
			Simulation.start();
			generator.activate();
			monitor.activate();            
			for (int i = 0; i < numSamples; i++) {
			    double startTime = currentTime();
				hold(samplingInterval + 1);
				double totalTime = currentTime() - startTime;
				op.updateBlocking();
				System.out.println("Run: "+(i+1));
				System.out.println("Average time in hospital: "+Recovery.averageThroughput());
				System.out.println("Average time for urgent patients: "+Recovery.urgentThroughput());
				System.out.println("Average time for non-urgent patients: "+Recovery.nonUrgentThroughput());
				System.out.println("Total time spent in surgery for all patients: "+op.totalSurgeryTime());
				double utilized = 100*op.utilizationTime()/totalTime;
				double blocked = 100*op.blockedTime()/totalTime;
				System.out.println("The operating theater had a patient inside "+utilized+" % of the simulation time.");
				System.out.println("The operating theater was blocked "+blocked+" % of the simulation time.");
				System.out.println("The average entry queue length was "+Preparation.averageQueueLength());
				System.out.println("Patients operated: "+op.patientsOperated());
				System.out.println("----------");
	            Preparation.reset();
	            Recovery.reset();
	            setupFacilities(op);
				op.cancel();
				op.reset();
				op.activate();
			}
            Simulation.stop();
			monitor.report(numSamples);
			generator.terminate();
			monitor.terminate();
			for (Preparation p : preparations) p.terminate();
			for (Recovery r : recoveries) r.terminate();
            SimulationProcess.mainResume();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setupFacilities(Operation op) {
        // Preparation and recovery facilities are stored to arrays.
        preparations = new Preparation[numPreparationUnits];
        for (int i = 0; i < numPreparationUnits; i++) preparations[i] = new Preparation(preparationTime, op);
        recoveries = new Recovery[numRecoveryUnits];
        for (int i = 0; i < numRecoveryUnits; i++) recoveries[i] = new Recovery(recoveryTime, op);
	}
	
	/**
	 * Starts the simulation process.
	 */
    public void await() {
        this.resumeProcess();
        SimulationProcess.mainSuspend();
    }
}