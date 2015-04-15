package trafficlight;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import trafficlight.Intersection;
import trafficlight.Operator;
import trafficlight.Signal;

/*
 * CHECK CONSOLE OUTPUT AFTER RUNNING THE TESTS.
 */

public class IntersectionTest extends ObservableTest {

	// represent the client of an intersection
	Operator operator = new Operator();

	Intersection intersection;

	@Before
	public void doNotCreateIntersectionBeforeTests() {
		observer = operator;
		// otherwise light sets start running and cause log output to be
		// interleaved
	}
	
	/*
	 * Try-catch clauses in tests help report unexpected/uncaught runtime exceptions 
	 * that may be masked by console log. These may be caused by minor, recoverable 
	 * synchronization issues.
	 */

	@Test
	public void isInitiallyIdle() {
		try {
			log();
			intersection = new Intersection(operator);
			assertEquals(Intersection.Status.DISABLED, intersection.getStatus());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void canBeEnabled() {
		try {
			log();
			intersection = new Intersection(operator);
			intersection.enable();
			assertEquals(Intersection.Status.ENABLED, intersection.getStatus());
			intersection.disable();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void canBeDisabled() {
		try {
			intersection = new Intersection(operator);
			intersection.enable();
			intersection.disable();
			assertEquals(Intersection.Status.DISABLED, intersection.getStatus());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void canBeInterruptedAfterBeingEnabled() throws InterruptedException {
		try {
			log();
			intersection = new Intersection(operator);
			Thread thread = intersection.enable();
			intersection.interrupt();
			thread.join();
			assertEquals(Intersection.Status.FAILED, intersection.getStatus());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void canBeInterruptedWhenDisabledAndStaysDisabledThereon() {
		try {
			log();
			intersection = new Intersection(operator);
			intersection.interrupt();
			assertEquals(Intersection.Status.DISABLED, intersection.getStatus());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void canTurnOnStopLightSet() {
		try {
			log();
			intersection = new Intersection(operator);
			intersection.turnOnStopLights();
			checkStopLightSet(intersection, Signal.RED);
			assertTrue(isReady(intersection));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void canTurnOnTurnLightSet() {
		try {
			log();
			intersection = new Intersection(operator);
			intersection.turnOnTurnLights();
			checkTurnLightSet(intersection, Signal.RED);
			assertTrue(isReady(intersection));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void canAdvanceStopLightSet() {
		try {
			log();
			intersection = new Intersection(operator);
			intersection.turnOnStopLights();
			intersection.advanceStopLights();
			checkStopLightSet(intersection, Signal.RED);
			assertTrue(isReady(intersection));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void canAdvanceTurnLightSet() {
		try {
			log();
			intersection = new Intersection(operator);
			intersection.turnOnTurnLights();
			intersection.advanceTurnLights();
			checkTurnLightSet(intersection, Signal.RED);
			assertTrue(isReady(intersection));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void canBlockPedestrians() {
		try {
			log();
			intersection = new Intersection(operator);
			intersection.turnOnStopLights();
			intersection.turnOnTurnLights();
			intersection.blockPedestrians();
			Thread.sleep(100);
			checkStopLightSet(intersection, Signal.RED);
			checkTurnLightSet(intersection, Signal.RED);
			checkPedestrianLights(intersection, Signal.DONT_WALK);
			assertTrue(isReady(intersection));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void canUnbllockPedestrians() {
		try {
			log();
			intersection = new Intersection(operator);
			intersection.turnOnStopLights();
			intersection.turnOnTurnLights();
			intersection.blockPedestrians();
			intersection.unblockPedestrians();
			intersection.advanceStopLights();
			checkPedestrianLights(intersection, Signal.WALK);
			checkStopLightSet(intersection, Signal.RED);
			checkTurnLightSet(intersection, Signal.RED);
			assertTrue(isReady(intersection));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	// helper methods

	private void checkStopLightSet(Intersection intersection, Signal signal) {
		for (int i = 0; i < 2; i++) {
			assertEquals(signal, intersection.getStopLightSet(i).getStatus());
		}
	}

	private void checkTurnLightSet(Intersection intersection, Signal signal) {
		for (int i = 0; i < 2; i++) {
			assertEquals(signal, intersection.getTurnLightSet(i).getStatus());
		}
	}

	private void checkPedestrianLights(Intersection intersection, Signal signal) {
		for (int i = 0; i < 2; i++) {
			assertEquals(signal, intersection.getStopLightSet(i)
					.getPedestrianLight(i).getStatus());
		}
	}

	private boolean isReady(Intersection intersection) {
		return (intersection.isReady(0) && intersection.isReady(0));
	}

}
