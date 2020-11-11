package surgeries;


/**
 * The main class beginning the simulation.
 * @author Ilari Kauko
 */
public class Main {
	
    public static void main (String[] args) {
        Hospital m = new Hospital();
        m.await();
        System.exit(0);
    }
    
}