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
		return generator.nextFloat() * (max - min) - min;
	}
	
	/** Get a float uniformly distributed in the given range and its negation.
	 * 
	 * The result will be in one of the ranges [min,max), (-max,min].
	 * @param min
	 * @param max
	 * @return
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
}
