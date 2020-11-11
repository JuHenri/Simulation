package surgeries;


/**
 * The main class beginning the simulation.
 * @author Ilari Kauko
 */
public class Main {
	
    public static void main (String[] args) {
        MachineShop m = new MachineShop();
        m.await();
        System.exit(0);
    }
    
}