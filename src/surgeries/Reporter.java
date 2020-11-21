package surgeries;

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
			} catch (SimulationException e){
			} catch (RestartException e){
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
	}

}