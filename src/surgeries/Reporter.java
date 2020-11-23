
package surgeries;

import java.util.Arrays;

import org.javasim.*;

/**
 * @author Tuomas Kontio
 * @version 21.11.2020
 */
public class Reporter extends SimulationProcess
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
	private Operation theater;


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


    /**
     * Update stats.
     */
	public void update() {
		averageThroughput[sampleCount] = Recovery.averageThroughput();
		urgentThroughput[sampleCount] = Recovery.urgentThroughput();
		nonUrgentThroughput[sampleCount] = Recovery.nonUrgentThroughput();
		totalSurgeryTime[sampleCount] = theater.totalSurgeryTime();

		utilized[sampleCount] = 100*(theater.utilizationTime()/ interval);

		blocked[sampleCount] = 100*(theater.blockedTime()/ interval);

		averageQueueLength[sampleCount] = Preparation.averageQueueLength();
		patientsOperated[sampleCount] = theater.patientsOperated();

		sampleCount++;
	}


    /**
     * Print collected stats. TODO: calculate means, etc.
     * @param numSamples number of samples
     */
	public void report(int numSamples) {
		/*
		for (int i = 0; i < numSamples; i++) {
			System.out.println(
					i+1 + " "
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

		System.out.println("The mean utilization was: " + arrayMean(utilized));
		System.out.println("The mean queue length was: " + arrayMean(averageQueueLength) + " patients");
        System.out.println("The interval estimate lower and upper bounds at 95% confidence for queue length were: " + Arrays.toString(arrayConfidence(averageQueueLength,1.96)));
        System.out.println("The mean blocked percentage was: " + arrayMean(blocked));
        System.out.println("The interval estimate lower and upper bounds at 95% confidence for blocking were: " + Arrays.toString(arrayConfidence(blocked,1.96)));

	}

    /**
     * @return returns the mean of the array as a double.
     * @param array the array from which the mean is calculated.
     */
	private double arrayMean(double[] array) {
	    double total = 0;
	    for(double value : array){
	        total += value;
	    }
        return total/array.length;

	}
	
	 /**
     * @return returns the standard deviation as a double.
     * @param array the array from which the standard deviation is calculated.
     */
    private double arrayDeviation(double[] array) {
        Double mean = arrayMean(array);
        double difference = 0;
        for (double value : array) {
            difference += (value - mean) * (value - mean);
        }
        double variance = difference / array.length;
        return Math.sqrt(variance);

    }

    /**
    * @return returns the interval estimates upper and lower bound in a double array.
    * @param array the array from which the interval estimate is calculated.
    * @param confLevel https://i.stack.imgur.com/PiSUh.png find z value for the percentage you are looking for.
    */
   private double[] arrayConfidence(double[] array,double confLevel) {
       double mean = arrayMean(array);
       double deviation = arrayDeviation(array);
       double confInterval = confLevel * deviation / Math.sqrt(array.length);
       return new double[]{mean - confInterval, mean + confInterval};

   }

}
