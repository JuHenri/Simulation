package surgeries;


/**
 * Statistical methods.
 * @author Ilari Kauko
 *
 */
public class Statistics {
	
	
    /**
     * @return returns the mean of the array as a double.
     * @param array the array from which the mean is calculated.
     */
	public static double mean(double[] array) {
	    double total = 0;
	    for(double value : array) total += value;
        return total/array.length;
	    
	}
	

	/**
	 * @param array the array from which the variance is calculated
	 * @return the variance as a double
	 */
	public static double variance(double[] array) {
		double sum = 0;
		double mean = mean(array);
		for (double value : array) sum += (value - mean)*(value - mean);
		return sum/array.length;
	}
	
	
	 /**
     * @return returns the standard deviation as a double.
     * @param array the array from which the standard deviation is calculated.
     */
    public static double standardDeviation(double[] array) {
        return Math.sqrt(variance(array));
    }
    
    /**
    * @return returns the interval estimates upper and lower bound in a double array.
    * @param array the array from which the interval estimate is calculated.
    * @param confLevel find z value for the percentage you are looking for.
    * @return an array in which index 0 has the minimum of confidence interval and 1 the maximum
    */
   public static double[] confidence(double[] array,double confLevel) {
       double mean = mean(array);
       double confInterval = confLevel*standardDeviation(array)/Math.sqrt(array.length);
       return new double[]{mean - confInterval, mean + confInterval};
   }
}
