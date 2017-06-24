import java.util.Random;


/**
 * Random Generator
 *
 * @author Oscar Downing (z5114817)
 */
public class RandomGenerator {

	private long seed;
	private Random rand;

	public RandomGenerator() {
		/* Random setup */
		this.seed =  System.nanoTime();
		this.rand = new Random(this.seed);
	}

	/**
	 * @return the seed used to instantiate Random object
	 */
	public long getSeed() {
		return this.seed;
	}

	/**
	 * 
	 * @param x - limit of random
	 * 
	 * @return a psuedo-random int between 0 (inclusive) and x (non-inclusive)
	 */
	public int getRandInt(int x) {
		return this.rand.nextInt(x);
	}

	/**
	 * 
	 * @param x - start of range
	 *		  y - end of range
	 * 
	 * @return a psuedo-random int between x (inclusive) and y (inclusive)
	 */
	public int getRandIntRange(int x, int y) {

	    long range = (long) y - (long) x + 1;
	    // compute a fraction of the range, 0 <= frac < range
	    long fraction = (long)(range * this.rand.nextDouble() );
	    int randomNumber =  (int)(fraction + x);    
	    return randomNumber;
  	}

}

		