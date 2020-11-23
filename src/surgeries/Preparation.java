package surgeries;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Stack;

import org.javasim.RestartException;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;
import org.javasim.streams.ExponentialStream;


/**
 * the preparation process class
 * @author Ilari Kauko
 * @author Henri Jussila
 * @author Tuomas Kontio
 * @version 13.11.2020
 */
public class Preparation extends SimulationProcess {
	
	// Queue is implemented using the standard libraries of Java. Free facilities are collected to a stack.
	private static final LinkedList<Patient> QUEUE = new LinkedList<Patient>();
	// The index i of QUEUETIMES tells the amount of time the length of the queue has been i.
	private static double[] queueTimes = new double[1000];
	private static double lastTimeQueueChanged = currentTime();
	private static final Stack<Preparation> FREE = new Stack<Preparation>();
	// The number of prepared patients and (temporary) throughput time are kept in this class.
	private static int prepared = 0;
	private static double totalTime = 0;
	private Operation theater;
	
	private ExponentialStream preparationTime;
	
	
	/**
	 * constructor
	 * @param mean the average time preparation takes
	 * @param theater the operation room being used.
	 */
	public Preparation(double mean, Operation theater) {
		preparationTime = new ExponentialStream(mean, 0, 123, 12345);
		FREE.add(this);
		this.theater = theater;
	}
	
	
	/**
	 * Adds a new patient to the preparation queue. The preparation will follow immediately if free facility exists.
	 * @param p patient entering
	 */
	public static void enqueue(Patient p) {
		updateQueueStatistics();
		if (p.urgent()) {
			// add urgent patient after previous urgent patients but before non-urgent
			QUEUE.add(getFirstNonUrgentIndex(), p);
		} else {
			// add patient to the end of the queue
			QUEUE.add(p);
		}
		try {
			if (!FREE.empty()) FREE.pop().activate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return index of first non-urgent patient in the queue
	 * returns zero if there are no patients in the queue
	 * returns size of the queue if all patients in the queue are urgent
	 */
	public static int getFirstNonUrgentIndex() {
		for (int i = 0; i < QUEUE.size(); i++) {
			if (!QUEUE.get(i).urgent()) {
				return i;
			}
		}
		return QUEUE.size();
	}
	
	/**
	 * the running process
	 */
	@Override
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
	 * @return number of prepared patients
	 */
	public static int prepared() {
		return prepared;
	}
	
	
	/**
	 * @return total amount of time the prepared patients have been in the queue and the preparation
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
		double totalTimeQueue = 0;
		double sumTime = 0;
		for (int i = 0; i < queueTimes.length; i++) {
			totalTimeQueue += queueTimes[i];
			sumTime += i*queueTimes[i];
		}
		return sumTime/totalTimeQueue;
	}
	
	public static  void reset() {
		QUEUE.clear();
		FREE.clear();
		queueTimes = new double[1000];
		lastTimeQueueChanged = currentTime();
		// The number of prepared patients and (temporary) throughput time are kept in this class.
		prepared = 0;
		totalTime = 0;
	}
	
	
	public static void terminateAll() {
		for (Preparation p : FREE) p.terminate();
	}
}