package surgeries;

import java.util.Arrays;

import org.javasim.Simulation;
import org.javasim.SimulationProcess;
import org.javasim.streams.ExponentialStream;
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
	private int numPreparationUnits = 3;
	private int preparationTime = 40;
	private int numOperationUnits = 1; // not in use (yet)
	private int operationTime = 20;
	private int numRecoveryUnits = 3;
	private int recoveryTime = 40;
	private double urgentPercentage = 50;
	private int simulationRuns = 10;
	private int simulationTime = 10000;
	private int numSamples = 10;
	private int samplingInterval = simulationTime/numSamples;
	
	/**
	 * run process
	 */
	@Override
	public void run() {
		try {
			Operation op = new Operation(operationTime);
			Simulation.start();
			Arrivals generator = new Arrivals(patientInterval, urgentPercentage/100, new ExponentialStream(patientInterval));
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
			for (int i = 0; i < numPreparationUnits; i++) new Preparation(new ExponentialStream(preparationTime), op);
			for (int i = 0; i < numRecoveryUnits; i++) new Recovery(new ExponentialStream(recoveryTime), op);
			double[] queueLengths = new double[numSamples];
			for (int j = 0; j < simulationRuns; j++) {
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
				for (int i = 0; i < numSamples; i++) {
					hold(samplingInterval);
					queueLengths[i] += Preparation.queueLength();
				}
				Preparation.reset();
				Recovery.reset();
				op.reset();
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
			for (int i = 0; i < numSamples; i++) queueLengths[i] /= simulationRuns;
			for (int i = 0; i < numSamples; i++) System.out.println("Average queue length on delay "+((i+1)*samplingInterval)+": "+queueLengths[i]);
			double[] correlations = Statistics.autocorrelation(queueLengths);
			System.out.println("TESTING AUTOCORRELATION");
			for (int i = 0; i < correlations.length; i++) System.out.println("Autocorrelation on delay "+((i+1)*samplingInterval)+": "+correlations[i]);
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
			generator.terminate();
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