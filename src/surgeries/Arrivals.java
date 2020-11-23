package surgeries;

import java.io.IOException;

import org.javasim.RestartException;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;
import org.javasim.streams.ExponentialStream;
import org.javasim.streams.UniformStream;

/**
 * The process generating new patients.
 * @author Ilari Kauko
 * @author Henri Jussila
 * @version 13.11.2020
 */
public class Arrivals extends SimulationProcess {
	
	private ExponentialStream timeStream;
	private UniformStream urgencyStream;
	private double pUrgent;
	
	/**
	 * The constructor.
	 * @param mean the mean time between two patients arriving
	 * @param pUrgent the probability of a patient being urgent
	 */
	public Arrivals(double mean, double pUrgent) {
		timeStream = new ExponentialStream(mean, 0, 123, 12345);
		urgencyStream = new UniformStream(0, 1);
		this.pUrgent = pUrgent;
	}
	
	/**
	 * the run process
	 */
	@Override
    public void run() {
		while (!terminated()) {
			try {
				Preparation.enqueue(new Patient(urgencyStream.getNumber() < pUrgent));
				hold(timeStream.getNumber());
			} catch (ArithmeticException | RestartException | IOException | SimulationException e) {
				e.printStackTrace();
			}
		}
	}
}