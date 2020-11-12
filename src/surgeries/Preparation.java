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
	// The index i of QUEUETIMES tells the amount of time the length of the queue has been i.
	private static double[] queueTimes = new double[1000];
	private static double lastTimeQueueChanged = currentTime();
	private static final Stack<Preparation> FREE = new Stack<Preparation>();
	// The number of preparated patients and (temporare) throughput time are kept in this class.
	private static int prepared = 0;
	private static double totalTime = 0;
	private Operation theater;
	
	private ExponentialStream preparationTime;
	
	
	/**
	 * constructor
	 * @param mean the average time preparation takes
	 */
	public Preparation(double mean, Operation theater) {
		preparationTime = new ExponentialStream(mean);
		FREE.add(this);
		this.theater = theater;
	}
	
	
	/**
	 * Adds a new patient to the preparation queue. The preparation will follow immediately if free facility exists.
	 * @param p patient entering
	 */
	public static void enqueue(Patient p) {
		updateQueueStatistics();
		QUEUE.add(p);
		try {
			if (!FREE.empty()) FREE.pop().activate();
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
				updateQueueStatistics();
				Patient p = QUEUE.poll();
				try {
					double t = preparationTime.getNumber();
					hold(t);
					prepared++;
					p.setPreparationEndTime(currentTime());
					p.setOperationStartTime(currentTime());
					totalTime += p.getPreparationTime();
					theater.enqueue(p);
				} catch (ArithmeticException | SimulationException | RestartException | IOException e) {
					e.printStackTrace();
				}
			}
			FREE.push(this);
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
	
	
	/**
	 * Updates the statistics about the amount of time the queue has had any length.
	 * Must be called just before the queue changes.
	 */
	public static void updateQueueStatistics() {
		double t = currentTime();
		if (queueTimes.length <= QUEUE.size()) {
			double[] newTimes = new double[queueTimes.length*2];
			for (int i = 0; i < queueTimes.length; i++) newTimes[i] = queueTimes[i];
			queueTimes = newTimes;
		}
		queueTimes[QUEUE.size()] += t - lastTimeQueueChanged;
		lastTimeQueueChanged = t;
	}
	
	/**
	 * @return the average length of the queue
	 */
	public static double averageQueueLength() {
		double totalTime = 0;
		double sumTime = 0;
		for (int i = 0; i < queueTimes.length; i++) {
			totalTime += queueTimes[i];
			sumTime += i*queueTimes[i];
		}
		return sumTime/totalTime;
	}
}
