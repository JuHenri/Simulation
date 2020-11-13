package surgeries;

import org.javasim.Simulation;
import org.javasim.SimulationProcess;


/**
 * The process coordinating the whole simulation
 * @author Ilari Kauko
 * @author Henri Jussila
 */
public class Hospital extends SimulationProcess {
	
	// the questionable parameters about the hospital capacity and its service times
	private int patientInterval = 25;
	private int numPreparationUnits = 3;
	private int preparationTime = 40;
	private int numOperationUnits = 1;
	private int operationTime = 20;
	private int numRecoveryUnits = 3;
	private int recoveryTime = 40;
	private double urgentPercentage = 10;
	
	/**
	 * run process
	 */
	@Override
	public void run() {
		try {
			double startTime = currentTime();
			Operation op = new Operation(operationTime);
			Arrivals generator = new Arrivals(patientInterval, urgentPercentage/100);
			generator.activate();
			// Preparation and recovery facilities are stored to arrays.
			Preparation[] preparations = new Preparation[numPreparationUnits];
			for (int i = 0; i < numPreparationUnits; i++) preparations[i] = new Preparation(preparationTime, op);
			Operation[] operations = new Operation[numOperationUnits];
			for (int i = 0; i < numOperationUnits; i++) operations[i] = new Operation(operationTime);
			Recovery[] recoveries = new Recovery[numRecoveryUnits];
			for (int i = 0; i < numRecoveryUnits; i++) recoveries[i] = new Recovery(recoveryTime, op);
			Simulation.start();
			while (Recovery.recovered() < 1000) hold(10);
			double totalTime = currentTime() - startTime;
			System.out.println("Time: "+totalTime);
			System.out.println("Average time in hospital: "+Recovery.averageThroughput());
			System.out.println("Average time for urgent patients: "+Recovery.urgentThroughput());
			System.out.println("Average time for non-urgent patients: "+Recovery.nonUrgentThroughput());
			System.out.println("Total time spent in surgery for all patients: "+op.totalSurgeryTime());
			double utilized = 100*op.utilizationTime()/totalTime;
			System.out.println("The operating theater was in use "+utilized+" % of the simulation time.");
			System.out.println("The average entry queue length was "+Preparation.averageQueueLength());
			System.out.println("Patients operated: "+op.patientsOperated());
            Simulation.stop();
			generator.terminate();
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
