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
   
   
   /**
    * @param s array containing the value of a given variable on regular time intervals
    * @return an unbiased estimate of the autocorrelation on the variable on corresponding time intervals
    */
   public static double[] autocorrelation(double[] s) {
	   double m = mean(s);
	   double v = variance(s);
	   double[] r = new double[s.length];
	   if (v == 0) return r;
	   for (int i = 0; i < r.length; i++) {
		   for (int j = 0; j < r.length - i; j++) r[i] += (s[j] - m)*(s[j + i] - m);
		   r[i] /= (r.length - i)*v;
	   }
	   return r;
   }
   
   
   public static double covariance(double[] a, double[] b) {
	   double meanA = mean(a);
	   double meanB = mean(b);
	   double sum = 0;
	   for (int i = 0; i < a.length; i++) sum += (a[i] - meanA)*(b[i] - meanB);
	   return sum/a.length;
   }
}