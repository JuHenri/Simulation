package surgeries;

import org.javasim.Simulation;

/**
 * The main class beginning the simulation.
 * @author Ilari Kauko
 * @author Henri Jussila
 * @author Tuomas Kontio
 * @version 13.11.2020
 */
public class Main {
	
	
	/**
	 * The initializer of a new hospital
	 * @param args not in use
	 */
    public static void main (String[] args) {
    	for (int i = 0; i < 1; i++) {
            Hospital m = new Hospital();
            m.await();
    	}
    	System.exit(0);
    }

}