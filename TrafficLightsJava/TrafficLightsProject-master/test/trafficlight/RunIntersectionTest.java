package trafficlight;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import trafficlight.Intersection;
import trafficlight.Operator;

/*
 * CHECK CONSOLE OUTPUT AFTER RUNNING THE TESTS.
 */

public class RunIntersectionTest extends ObservableTest {
	
	// represent the client of an intersection
	Operator operator = new Operator();
	
	// milliseconds for running the runIntersection test
	final static long TIME_LIMIT = 5000;
	
	Intersection intersection;
	
	@Before
	public void doNotCreateIntersectionBeforeTests() {
		observer = operator;
		// otherwise light sets start running and cause log output to be interleaved
	}
	
	@Test
	/* 
	 * This test simply runs the intersection for TIME_LIMIT (milliseconds).
	 * Comment this test out to speed up tests 
	 * */
	public void runIntersection() {
		try {
			log();
			intersection = new Intersection(operator);
			intersection.enable();
			Thread.sleep(TIME_LIMIT);
			intersection.disable();
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}
	
}
