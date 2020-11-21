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
	private int samplecount = 0;
	private double[] averageThroughput;
	private double[] urgentThroughput;
	private double[] nonUrgentThroughput;
	private double[] totalSurgeryTime;
	//private double[] utilized;
	//private double[] blocked;
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
		//utilized = new double[numSamples];
		//blocked = new double[numSamples];
		averageQueueLength = new double[numSamples];
		patientsOperated = new int[numSamples];
		this.theater = theater;
	}


    /**
     * The running process. Run in infinite loop and collect stats.
     */
    @Override
	public void run() {
		for (;;)
		{
			try {
				hold(interval);
			} catch (SimulationException | RestartException e ){
			    //
			}
			averageThroughput[samplecount] = Recovery.averageThroughput();
			urgentThroughput[samplecount] = Recovery.urgentThroughput();
			nonUrgentThroughput[samplecount] = Recovery.nonUrgentThroughput();
			totalSurgeryTime[samplecount] = theater.totalSurgeryTime();
			//utilized[samplecount] = 100*theater.utilizationTime()/totalTime;
			//blocked[samplecount] = 100*theater.blockedTime()/totalTime;
			averageQueueLength[samplecount] = Preparation.averageQueueLength();
			patientsOperated[samplecount] = theater.patientsOperated();
			samplecount++;
		}
	}


    /**
     * Print collected stats. TODO: calculate means, etc.
     * @param numSamples number of samples
     */
	public void report(int numSamples) {
		for (int i = 0; i < numSamples; i++) {
			System.out.println((i+1)*interval + " "
					+ averageThroughput[i] + " "
					+ urgentThroughput[i] + " "
					+ nonUrgentThroughput[i] + " "
					+ totalSurgeryTime[i] + " "
					+ averageQueueLength[i] + " "
					+ patientsOperated[i]);
		}
        System.out.println("The mean time patient spent in the hospital was: " + arrayMean(averageThroughput));
        System.out.println("The interval estimate for patient time in hospital was: " + Arrays.toString(arrayConfidence(averageThroughput,1.96)));
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