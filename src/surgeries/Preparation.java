package surgeries;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

import org.javasim.RestartException;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;
import org.javasim.streams.ExponentialStream;


/**
 * the preparation process class
 * @author Ilari Kauko
 */
public class Preparation extends SimulationProcess {
	
	// Queue is implemented using the standard libraries of Java. Free facilities are collected to a stack.
	private static final Queue<Patient> QUEUE = new ArrayDeque<Patient>();
	private static Stack<Preparation> free = new Stack<Preparation>();
	// The number of preparated patients and (temporare) throughput time are kept in this class.
	private static int prepared = 0;
	private static double totalTime = 0;
	
	private ExponentialStream preparationTime;
	
	
	/**
	 * constructor
	 * @param mean the average time preparation takes
	 */
	public Preparation(double mean) {
		preparationTime = new ExponentialStream(mean);
		free.add(this);
	}
	
	
	/**
	 * Adds a new patient to the preparation queue. The preparation will follow immediately if free facility exists.
	 * @param p patient entering
	 */
	public static void enqueue(Patient p) {
		QUEUE.add(p);
		try {
			if (!free.empty()) free.pop().activate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * the running process
	 */
	public void run() {
		while (!terminated()) {
			while (!QUEUE.isEmpty()) {
				Patient p = QUEUE.poll();
				try {
					double t = preparationTime.getNumber();
					hold(t);
					prepared++;
					p.setPreparationEndTime(currentTime());
					totalTime += p.getPreparationTime();
				} catch (ArithmeticException | SimulationException | RestartException | IOException e) {
					e.printStackTrace();
				}
			}
			free.push(this);
			try {
				this.passivate();
			} catch (RestartException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * @return number of preparated patients
	 */
	public static int prepared() {
		return prepared;
	}
	
	
	/**
	 * @return total amount of time the preparated patients have been in the queue and the preparation
	 */
	public static double averageTime() {
		return totalTime/prepared;
	}
}
