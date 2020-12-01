package surgeries;

import java.util.Arrays;

import org.javasim.Simulation;
import org.javasim.SimulationProcess;
import org.javasim.streams.ExponentialStream;
import org.javasim.streams.RandomStream;
import org.javasim.streams.UniformStream;


/**
 * The process coordinating the whole simulation
 * @author Ilari Kauko
 * @author Henri Jussila
 * @author Tuomas Kontio
 * @version 13.11.2020
 */
public class Hospital extends SimulationProcess {
	
	// the questionable parameters about the hospital capacity and its service times
	private double patientInterval = 25;
	private int preparationTime = 40;
	private int numOperationUnits = 1; // not in use (yet)
	private int operationTime = 20;
	private int recoveryTime = 40;
	private double urgentPercentage = 50;
	private int numSamples = 1000;
	private int samplingInterval = 10;
	
	/**
	 * run process
	 */
	@Override
	public void run() {
		try {
			Operation op = new Operation(operationTime);
			Simulation.start();
			Arrivals generator = new Arrivals(urgentPercentage/100, new ExponentialStream(patientInterval));
			generator.activate();
			/*
			 * assignment 3 code
			int[] preparationCapacities = new int[] {3,3,4};
			int[] recoveryCapacities = new int[] {4,5,5};
			double[] queueMean = new double[preparationCapacities.length];
			double[] blockedMean = new double[preparationCapacities.length];
			double[] queueVariance = new double[preparationCapacities.length];
			double[] blockedVariance = new double[preparationCapacities.length];
			*/
			// autocorrelation experiment
			int runs = 25;
			int numPreparationUnits = 4;
			int numRecoveryUnits = 4;
			double[] autocorrelations = new double[numSamples];
			ExponentialStream prepStream = new ExponentialStream(preparationTime, 1766);
			ExponentialStream recStream = new ExponentialStream(recoveryTime, 5155);
			Preparation[] preps = new Preparation[numPreparationUnits];
			Recovery[] recs = new Recovery[numRecoveryUnits];
			for (int j = 0; j < runs; j++) {
				/*
				 * assignment 3 code
				int numPreparationUnits = preparationCapacities[j];
				int numRecoveryUnits = recoveryCapacities[j];
				System.out.println("Experiment "+(j+1)+": "+numPreparationUnits+" preparation units and "+numRecoveryUnits+" recovery units.");
				Reporter monitor = new Reporter(numSamples, samplingInterval, op);
				double totalQueueTime = 0;
				double totalBlockedTime = 0;
				double[] blockedResults = new double[numSamples];
				*/
				double[] queueLengths = new double[numSamples];
				for (int i = 0; i < numPreparationUnits; i++) preps[i] = new Preparation(preparationTime, op, prepStream);
				for (int i = 0; i < numRecoveryUnits; i++) recs[i] = new Recovery(recoveryTime, op, recStream);
				for (int i = 0; i < numSamples; i++) {
					hold(samplingInterval);
					queueLengths[i] = Preparation.queueLength();
				}
				Preparation.reset();
				Recovery.reset();
				for (Preparation p : preps) p.cancel();
				for (Recovery r : recs) r.cancel();
				op.reset();
				double[] autocorrs = Statistics.autocorrelation(queueLengths);
				for (int i = 0; i < autocorrs.length; i++) autocorrelations[i] += autocorrs[i];
				/*
				queueMean[j] = Statistics.mean(queueResults);
				blockedMean[j] = Statistics.mean(blockedResults);
				queueVariance[j] = Statistics.variance(queueResults);
				blockedVariance[j] = Statistics.variance(blockedResults);
				monitor.report(numSamples);
				System.out.println();
				*/
			}
			// The array queueLengths stores the mean of the queue length at each moment, not sum
			for (int i = 0; i < numSamples; i++) autocorrelations[i] /= runs;
			System.out.println("TESTING AUTOCORRELATION");
			for (int i = 0; i < 100; i++) System.out.println("Autocorrelation on delay "+(i*samplingInterval)+": "+autocorrelations[i]);
			generator.cancel();
			/*
			 * assignment 3 code
			for (int i = 0; i < preparationCapacities.length; i++) {
				int p1 = preparationCapacities[i];
				int r1 = recoveryCapacities[i];
				for (int j = 0; j < i; j++) {
					int p2 = preparationCapacities[j];
					int r2 = recoveryCapacities[j];
					System.out.println("Analyzing the difference between results of "+p1+"P"+r1+"R"+" and "+p2+"P"+r2+"R.");
					double sd = Math.sqrt(queueVariance[i] + queueVariance[j]);
					double mean = Math.abs(queueMean[i] - queueMean[j]);
					double[] confInterval = new double[] {mean - 1.96*sd/Math.sqrt(numSamples), mean + 1.96*sd/Math.sqrt(numSamples)};
					System.out.println("The interval estimate lower and upper bounds at 95% confidence for the average entry queue length difference were: "+Arrays.toString(confInterval));
					sd = Math.sqrt(blockedVariance[i] + blockedVariance[j]);
					mean = Math.abs(blockedMean[i] - blockedMean[j]);
					confInterval = new double[] {mean - 1.96*sd/Math.sqrt(numSamples), mean + 1.96*sd/Math.sqrt(numSamples)};
					System.out.println("The interval estimate lower and upper bounds at 95% confidence for the difference of probability of the operating theater being blocked were: "+Arrays.toString(confInterval));
				}
			}
			*/
			// The experiment of 8 configurations. 
			// Four random streams and 2 capacity numbers can be of 2 types.
			RandomStream[][] streams = new RandomStream[][] {
					{new ExponentialStream(25,173), new ExponentialStream(22.5,674)},
					{new UniformStream(20,30,295), new UniformStream(20,25,760)},
					{new ExponentialStream(40,810),new UniformStream(30,50,166)},
					{new ExponentialStream(40,234),new UniformStream(30,50,898)},
			};
			int[][] capacities = new int[][] {
				{4,5},
				{4,5}
			};
			// The numbers in this matrix are used to refer to the indexes of the former arrays.
			// The first column tells whether exponential or uniform distributions are used in arrivals.
			// The second column tells the mean of the distribution.
			// The third and fourth tell the distribution used by preparations and recoveries.
			// The fifth and sixth tell their capacities.
			int[][] design = new int[][] {
				{1,1,1,0,0,0},
				{1,1,0,1,1,0},
				{1,0,1,1,0,0},
				{1,0,0,1,0,0},
				{0,1,1,0,0,1},
				{0,1,0,0,1,0},
				{0,0,1,1,0,1},
				{0,0,0,0,1,1},
			};
			for (int i = 0; i < design.length; i++) {
				System.out.println("Experiment "+(i+1));
				generator = new Arrivals(urgentPercentage/100, streams[design[i][0]][design[i][1]]);
				generator.activate();
				preps = new Preparation[capacities[0][design[i][4]]];
				recs = new Recovery[capacities[1][design[i][5]]];
				double averageQueueLength = 0;
				for (int j = 0; j < runs; j++) {
					for (int k = 0; k < capacities[0][design[i][4]]; k++) preps[k] = new Preparation(preparationTime, op, streams[2][design[i][0]]);
					for (int k = 0; k < capacities[1][design[i][5]]; k++) recs[k] = new Recovery(recoveryTime, op, streams[3][design[i][1]]);
					for (int k = 0; k < numSamples; k++) {
						hold(samplingInterval);
					}
					averageQueueLength += Preparation.averageQueueLength();
					Preparation.reset();
					Recovery.reset();
					for (Preparation p : preps) p.cancel();
					for (Recovery r : recs) r.cancel();
					op.reset();
				}
				generator.cancel();
				averageQueueLength /= runs;
				System.out.println("Average entry queue length: "+averageQueueLength);
			}
			op.terminate();
            Simulation.stop();
            SimulationProcess.mainResume();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Starts the simulation process.
	 */
    public void await() {
        this.resumeProcess();
        SimulationProcess.mainSuspend();
    }
}