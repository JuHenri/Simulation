package surgeries;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.javasim.RestartException;
import org.javasim.Simulation;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;

/**
 * The main class beginning the simulation.
 * @author Ilari Kauko
 * @author Henri Jussila
 * @author Tuomas Kontio
 * @version 13.11.2020
 */
public class Main {
	
	
	/**
	 * The initializer of a new hospital.
	 * @param args not in use
	 * @throws InterruptedException 
	 * @throws RestartException 
	 * @throws SimulationException 
	 */
    public static void main (String[] args) throws InterruptedException, SimulationException, RestartException {
    	new Hospital().await();
    	System.exit(0);
    }

}