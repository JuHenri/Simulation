package surgeries;

import java.util.Arrays;

import org.javasim.*;

/**
 * @author Tuomas Kontio
 * @version 21.11.2020
 */
public class Reporter 
{
	private int interval;
	private int sampleCount = 0;
	private double[] averageThroughput;
	private double[] urgentThroughput;
	private double[] nonUrgentThroughput;
	private double[] totalSurgeryTime;
	private double[] utilized;
	private double[] blocked;
	private double[] averageQueueLength;
	private int[] patientsOperated;
	private static Operation theater;


	/**
	 * constructor
	 * @param numSamples number of samples
	 * @param samplingInterval sampling interval
	 * @param theater the operation room being used.
	 */
	public Reporter(int numSamples, int samplingInterval, Operation theater) {
		interval = samplingInterval;
		averageThroughput = new double[numSamples];
		urgentThroughput = new double[numSamples];
		nonUrgentThroughput = new double[numSamples];
		totalSurgeryTime = new double[numSamples];
		utilized = new double[numSamples];
		blocked = new double[numSamples];
		averageQueueLength = new double[numSamples];
		patientsOperated = new int[numSamples];
		this.theater = theater;
	}


	public void update() {
			averageThroughput[sampleCount] = Recovery.averageThroughput();
			urgentThroughput[sampleCount] = Recovery.urgentThroughput();
			nonUrgentThroughput[sampleCount] = Recovery.nonUrgentThroughput();
			totalSurgeryTime[sampleCount] = theater.totalSurgeryTime();

			utilized[sampleCount] = 100*(theater.utilizationTime()/ interval);

			blocked[sampleCount] = 100*(theater.blockedTime()/ interval);
			
			averageQueueLength[sampleCount] = Preparation.averageQueueLength();
			patientsOperated[sampleCount++] = theater.patientsOperated();
	}


    /**
     * Print collected stats.
     * @param numSamples number of samples
     */
	public void report(int numSamples) {
		/*
		for (int i = 0; i < numSamples; i++) {
			System.out.println(
					(i+1)*interval + " "
					+ averageThroughput[i] + " "
					+ urgentThroughput[i] + " "
					+ nonUrgentThroughput[i] + " "
					+ totalSurgeryTime[i] + " "
					+ utilized[i] + " "
					+ blocked[i] + " "
					+ averageQueueLength[i] + " "
					+ patientsOperated[i]
			);
		}
		*/
        System.out.println("The mean time patient spent in the hospital was: " + Statistics.mean(averageThroughput));
        System.out.println("The interval estimate lower and upper bounds at 95% confifedence for patient time in hospital were: " + Arrays.toString(Statistics.confidence(averageThroughput,1.96)));
        System.out.println("The mean utilization percentage was: " + Statistics.mean(utilized));
        System.out.println("The mean blocked percentage was: " + Statistics.mean(blocked));
	}
}