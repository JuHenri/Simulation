package surgeries;

import org.javasim.Simulation;
import org.javasim.SimulationProcess;


/**
 * The process coordinating the whole simulation
 * @author Ilari Kauko
 */
public class MachineShop extends SimulationProcess {
	
	
	/**
	 * run process
	 */
	@Override
	public void run() {
		try {
			double alku = currentTime();
			Arrivals generator = new Arrivals(1, 0.1);
			generator.activate();
			// Preparation facilities are stored to an array.
			int preparationCapacity = 5;
			Preparation[] preparations = new Preparation[preparationCapacity];
			for (int i = 0; i < preparationCapacity; i++) {
				preparations[i] = new Preparation(5);
			}
			Simulation.start();
			while (Preparation.prepared() < 10000) {
				hold(1);
			}
			System.out.println("Time: "+(currentTime() - alku));
			System.out.println("Average time in queue and preparation: "+Preparation.averageTime());
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
