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
	private int numPreparationUnits = 3;
	private int preparationTime = 40;
	private int numOperationUnits = 1; // not in use (yet)
	private int operationTime = 20;
	private int numRecoveryUnits = 3;
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
			Preparation[] preparations = new Preparation[numPreparationUnits];
			for (int i = 0; i < numPreparationUnits; i++) preparations[i] = new Preparation(preparationTime, op);
			Recovery[] recoveries = new Recovery[numRecoveryUnits];
			for (int i = 0; i < numRecoveryUnits; i++) recoveries[i] = new Recovery(recoveryTime, op);
			Simulation.start();
			Arrivals generator = new Arrivals(patientInterval, urgentPercentage/100);
			generator.activate();
<<<<<<< HEAD
			for (int j = 0; j < preparationCapacities.length; j++) {
				numPreparationUnits = preparationCapacities[j];
				numRecoveryUnits = recoveryCapacities[j];
				System.out.println("Experiment "+(j+1)+": "+numPreparationUnits+" preparation units and "+numRecoveryUnits+" recovery units");
				Reporter monitor = new Reporter(numSamples, samplingInterval, op);
				for (int i = 0; i < numSamples; i++) {
					double startTime = currentTime();
					hold(samplingInterval + 1);
					double totalTime = currentTime() - startTime;
					/*
					System.out.println("Time: "+totalTime);
					System.out.println("Average time in hospital: "+Recovery.averageThroughput());
					System.out.println("Average time for urgent patients: "+Recovery.urgentThroughput());
					System.out.println("Average time for non-urgent patients: "+Recovery.nonUrgentThroughput());
					System.out.println("Total time spent in surgery for all patients: "+op.totalSurgeryTime());
					*/
					double utilized = 100*op.utilizationTime()/totalTime;
					double blocked = 100*op.blockedTime()/totalTime;
					/*
					System.out.println("The operating theater was in use "+utilized+" % of the simulation time.");
					System.out.println("The operating theater was blocked "+blocked+" % of the simulation time.");
					System.out.println("The average entry queue length was "+Preparation.averageQueueLength());
					System.out.println("Patients operated: "+op.patientsOperated());
					System.out.println("----------");
					*/
					monitor.update();
					for (Preparation p : preparations) p.reset();
					for (Recovery r : recoveries) r.reset();
					op.reset();
=======
			monitor.activate();            
			for (int i = 0; i < numSamples; i++) {
			    double startTime = currentTime();
				hold(samplingInterval + 1);
				double totalTime = currentTime() - startTime;
				System.out.println("Time: "+totalTime);
				System.out.println("Average time in hospital: "+Recovery.averageThroughput());
				System.out.println("Average time for urgent patients: "+Recovery.urgentThroughput());
				System.out.println("Average time for non-urgent patients: "+Recovery.nonUrgentThroughput());
				System.out.println("Total time spent in surgery for all patients: "+op.totalSurgeryTime());
				double utilized = 100*op.utilizationTime()/totalTime;
				double blocked = 100*op.blockedTime()/totalTime;
				System.out.println("The operating theater was in use "+utilized+" % of the simulation time.");
				System.out.println("The operating theater was blocked "+blocked+" % of the simulation time.");
				System.out.println("The average entry queue length was "+Preparation.averageQueueLength());
				System.out.println("Patients operated: "+op.patientsOperated());
				System.out.println("----------");
				for (Preparation p : preparations) {
				    p.cancel();
					p.reset();
					p.activate();
>>>>>>> branch 'Ilari' of https://github.com/JuHenri/Simulation
				}
<<<<<<< HEAD
				monitor.report(numSamples);
				System.out.println();
=======
				for (Recovery r : recoveries) {
				    r.cancel();
					r.reset();
					r.activate();
				}
				op.cancel();
				op.reset();
				op.activate();
>>>>>>> branch 'Ilari' of https://github.com/JuHenri/Simulation
			}
			generator.terminate();
            Simulation.stop();
			op.terminate();
			for (Preparation p : preparations) p.terminate();
			for (Recovery r : recoveries) r.terminate();
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