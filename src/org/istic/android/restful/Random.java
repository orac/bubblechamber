package org.istic.android.restful;

public class Random {
	private java.util.Random generator;
	public Random() {
		generator = new java.util.Random();
	}
	
	public Random(long seed) {
		generator = new java.util.Random(seed);
	}
	
	/** Get a uniformly distributed float in the given range. */
	public float getUniform(float min, float max) {
		return generator.nextFloat() * (max - min) + min;
	}
	
	/** Get a Gaussian distributed float.
	 * 
	 * @param mean The mean of the Gaussian distribution to draw from.
	 * @param sd The standard deviation of the Gaussian distribution to draw from.
	 * @return A float drawn from this distribution.
	 */
	public float getGaussian(float mean, float sd) {
		return (float)generator.nextGaussian() * sd + mean;
	}
	
	/** Get a float uniformly distributed in the given range and its negation.
	 * 
	 * The result will be in one of the ranges [min,max), (-max,min].
	 * @param min The minimum magnitude.
	 * @param max The maximum magnitude.
	 * @return A float drawn from one of the two ranges.
	 */
	public float getTwoRanges(float min, float max) {
		float result = getUniform(min, max);
		if (generator.nextBoolean())
			result = -result;
		return result;
	}
	
	/** Get a Boolean that's true with a given probability.
	 * 
	 * @param p_true The probability of getting a true result.
	 * @return True with probability p_true; false with probability (1 - p_true). 
	 */
	public boolean get_boolean(float p_true) {
		return generator.nextFloat() < p_true;
	}
	
	/** Get a randomly distributed int in the range [0,max). */
	public int get_int(int max) {
		return generator.nextInt(max);
	}
}
