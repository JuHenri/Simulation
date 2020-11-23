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
	private int experimentCount = 0;
	private int sampleCount = 0;
	private double[][] averageThroughput;
	private double[][] urgentThroughput;
	private double[][] nonUrgentThroughput;
	private double[][] totalSurgeryTime;
	private double[][] utilized;
	private double[][] blocked;
	private double[][] averageQueueLength;
	private int[][] patientsOperated;
	private Operation theater;


	/**
	 * constructor
	 * @param numExperiments number of experiments
	 * @param numSamples number of samples
	 * @param samplingInterval sampling interval
	 * @param theater the operation room being used.
	 */
	public Reporter(int numExperiments, int numSamples, int samplingInterval, Operation theater) {
		interval = samplingInterval;
		averageThroughput = new double[numExperiments][numSamples];
		urgentThroughput = new double[numExperiments][numSamples];
		nonUrgentThroughput = new double[numExperiments][numSamples];
		totalSurgeryTime = new double[numExperiments][numSamples];
		utilized = new double[numExperiments][numSamples];
		blocked = new double[numExperiments][numSamples];
		averageQueueLength = new double[numExperiments][numSamples];
		patientsOperated = new int[numExperiments][numSamples];
		this.theater = theater;
	}


    /**
     * Update stats.
     * @param numExperiment number of experiment
     * @param numSamples number of samples
     */
	public void update(int numExperiment, int numSamples) {
		experimentCount = numExperiment;
		sampleCount = numSamples;

		averageThroughput[experimentCount][sampleCount] = Recovery.averageThroughput();
		urgentThroughput[experimentCount][sampleCount] = Recovery.urgentThroughput();
		nonUrgentThroughput[experimentCount][sampleCount] = Recovery.nonUrgentThroughput();
		totalSurgeryTime[experimentCount][sampleCount] = theater.totalSurgeryTime();

		utilized[experimentCount][sampleCount] = 100*(theater.utilizationTime()/interval);

		blocked[experimentCount][sampleCount] = 100*(theater.blockedTime()/interval);

		averageQueueLength[experimentCount][sampleCount] = Preparation.averageQueueLength();
		patientsOperated[experimentCount][sampleCount] = theater.patientsOperated();

	}


    /**
     * Print collected stats. TODO: calculate means, etc.
     * @param numExperiment number of experiment
     * @param numSamples number of samples
     */
	public void report(int numExperiment, int numSamples) {
		for (int i = 0; i < numSamples; i++) {
			System.out.println(
					i+1 + " "
					+ averageThroughput[numExperiment][i] + " "
					+ urgentThroughput[numExperiment][i] + " "
					+ nonUrgentThroughput[numExperiment][i] + " "
					+ totalSurgeryTime[numExperiment][i] + " "
					+ utilized[numExperiment][i] + " "
					+ blocked[numExperiment][i] + " "
					+ averageQueueLength[numExperiment][i] + " "
					+ patientsOperated[numExperiment][i]
			);
		}
        System.out.println("The mean time patient spent in the hospital was: " + arrayMean(averageThroughput[numExperiment]));
        System.out.println("The interval estimate lower and upper bounds at 95% confifedence for patient time in hospital were: " + Arrays.toString(arrayConfidence(averageThroughput[numExperiment],1.96)));
        System.out.println("The mean utilization percentage was: " + arrayMean(utilized[numExperiment]));
        System.out.println("The mean blocked percentage was: " + arrayMean(blocked[numExperiment]));
	}
	
	/**
	 * Checks and prints if differences between runs were significant
	 */
	public void reportSignificance() {
	    System.out.println("Confidence level/interval 80%");
	    System.out.println("Difference in utilization between runs 1/2 was statistically significant: " + pairedTTest(utilized[0],utilized[1],1.328));
	    System.out.println("Difference in utilization between runs 2/3 was statistically significant: " + pairedTTest(utilized[1],utilized[2],1.328));
	    System.out.println("Difference in utilization between runs 1/3 was statistically significant: " + pairedTTest(utilized[1],utilized[2],1.328));
	    System.out.println("Difference in blocking between runs 1/2 was statistically significant: " + pairedTTest(blocked[0],blocked[1],1.328));
	    System.out.println("Difference in blocking between runs 2/3 was statistically significant: " + pairedTTest(blocked[1],blocked[2],1.328));
	    System.out.println("Difference in blocking between runs 1/3 was statistically significant: " + pairedTTest(blocked[1],blocked[2],1.328));
	    System.out.println("Difference in average queue length between runs 1/2 was statistically significant: " + pairedTTest(averageQueueLength[0],averageQueueLength[1],1.328));
        System.out.println("Difference in average queue length between runs 2/3 was statistically significant: " + pairedTTest(averageQueueLength[1],averageQueueLength[2],1.328));
        System.out.println("Difference in average queue length between runs 1/3 was statistically significant: " + pairedTTest(averageQueueLength[1],averageQueueLength[2],1.328));
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
   
   /**
   * @return true if the difference is statistically significant.
   * @param array_One the first array for the calculation.
   * @param array_Two the second array for the calculation.
   * @param confLevel https://i.stack.imgur.com/PiSUh.png df value for array_One.length - 1 with the prefered confidence level.
   * @example for 20 observations at 95% confidence. confLevel/df value would be: 2.093
   */
   private boolean pairedTTest(double[] array_One,double[] array_Two, double confLevel) {
       int i = 0;
       double[] differences = new double[array_One.length];
       for(double value : array_One) {
           differences[i] = value - array_Two[i];
           i++;
       }
       double mean = arrayMean(differences);
       double s_deviation = arrayDeviation(differences);
       double s_error = s_deviation / Math.sqrt(array_One.length);
       double t = mean / s_error;
       if (t > confLevel)return true;
       return false;
       
   }


}