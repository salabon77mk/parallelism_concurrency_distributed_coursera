package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;
import edu.rice.pcdp.PCDP;


/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 * <p>
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
	/**
	 * {@inheritDoc}
	 * <p>
	 * TODO Use the SieveActorActor class to calculate the number of primes <=
	 * limit in parallel. You might consider how you can model the Sieve of
	 * Eratosthenes as a pipeline of actors, each corresponding to a single
	 * prime number.
	 */
	@Override
	public int countPrimes(final int limit) {
		int primes = 0;
		if(limit >= 3) {
			final SieveActorActor act = new SieveActorActor(2);
			PCDP.finish(() -> {
				for(int i = 3; i <= limit; i += 2) {
					act.send(i);
				}
			});

			SieveActorActor curr = act;

			while(curr != null) {
				primes += curr.numPrimes;
				curr = curr.nextActor;
			}
		}
		return primes;
	}

	/**
	 * An actor class that helps implement the Sieve of Eratosthenes in
	 * parallel.
	 */
	public static final class SieveActorActor extends Actor {
		private static final int MAX = 512;
		private int[] primes = new int[MAX];
		private int numPrimes = 0;
		private SieveActorActor nextActor = null;

		public SieveActorActor(int msg) {
			primes[numPrimes++] = msg;
		}
		/**
		 * Process a single message sent to this actor.
		 * <p>
		 * TODO complete this method.
		 *
		 * @param msg Received message
		 */
		@Override
		public void process(final Object msg) {
			final int cand = (Integer) msg;

			final boolean isPrime = isPrime(cand);
			if(isPrime) {
				if(numPrimes < MAX) {
					primes[numPrimes++] = cand;
				}
				else if(nextActor == null) {
					nextActor = new SieveActorActor(cand);
				}
				else {
					nextActor.send(msg);
				}
			}
		}

		private boolean isPrime(final int cand) {
			for(int i = 0; i < numPrimes; i++) {
				if(cand % primes[i] == 0) return false;
			}
			return true;
		}
	}
}
