package trafficlight;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import trafficlight.AbstractLightSet;
import trafficlight.LinearLightSet;
import trafficlight.Signal;
import trafficlight.TurnLightSet;

/*
 * CHECK CONSOLE OUTPUT AFTER RUNNING THE TESTS.
 */

public class LightSetTest extends ObservableTest {

    LinearLightSet lightSet;
    // Used to read console output
    private final ByteArrayOutputStream consoleOut = new ByteArrayOutputStream();

    @Before
    public void doNotCreateLightSetBeforeTests() {
        // otherwise they start running before tests and mess up the console log
    }

    /*
     * Try-catch clauses in tests help report unexpected/uncaught runtime
     * exceptions that may be masked by console log. These may be caused by
     * minor, recoverable synchronization issues.
     */

    @Test
    public void canCreate() {
        try {
            log();
            lightSet = new LinearLightSet(0, observer);
            Thread thread = lightSet.getSignalCycle();
            assertTrue(thread.isAlive());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void canTurnOn() {
        try {
            log();
            lightSet = new LinearLightSet(0, observer);
            synchronized (lightSet) {
                lightSet.turnOn();
            }
            assertEquals(Signal.RED, lightSet.getStatus());
            checkAllLights(lightSet, Signal.RED, Signal.WALK);
            assertTrue(lightSet.getSignalCycle().isAlive());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void isRedAfterTurningOn() {
        try {
            log();
            lightSet = new LinearLightSet(0, observer);
            synchronized (lightSet) {
                lightSet.turnOn();
            }
            assertEquals(Signal.RED, lightSet.getStatus());
            checkAllLights(lightSet, Signal.RED, Signal.WALK);
            assertTrue(lightSet.getSignalCycle().isAlive());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void isRedAfterOneAdvance() {
        try {
            log();
            lightSet = new LinearLightSet(0, observer);
            synchronized (lightSet) {
                lightSet.turnOn();
                lightSet.advance();
            }
            assertEquals(Signal.RED, lightSet.getStatus());
            checkAllLights(lightSet, Signal.RED, Signal.WALK);
            assertTrue(lightSet.getSignalCycle().isAlive());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void isRedAfterMultipleAdvances() {
        try {
            log();
            lightSet = new LinearLightSet(0, observer);
            synchronized (lightSet) {
                lightSet.turnOn();
                lightSet.advance();
                lightSet.advance();
            }
            assertEquals(Signal.RED, lightSet.getStatus());
            checkAllLights(lightSet, Signal.RED, Signal.WALK);
            assertTrue(lightSet.getSignalCycle().isAlive());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void canNotBeTurnedOnMultipleTimesInARow() {
        try {
            log();
            lightSet = new LinearLightSet(0, observer);
            synchronized (lightSet) {
                lightSet.turnOn();
                lightSet.turnOn();
            }
            assertEquals(Signal.OFF, lightSet.getStatus());
            checkAllLights(lightSet, Signal.OFF, Signal.OFF);
            assertTrue(lightSet.getSignalCycle().isAlive());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void canReset() {
        try {
            log();
            lightSet = new LinearLightSet(0, observer);
            synchronized (lightSet) {
                lightSet.turnOn();
                lightSet.advance();
                lightSet.reset();
            }
            assertEquals(Signal.OFF, lightSet.getStatus());
            checkAllLights(lightSet, Signal.OFF, Signal.OFF);
            assertTrue(lightSet.isEventQueueEmpty());
            assertTrue(lightSet.getSignalCycle().isAlive());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void canStopPedestrians() {
        log();
        try {
            lightSet = new LinearLightSet(0, observer);
            synchronized (lightSet) {
                lightSet.turnOn();
                lightSet.advance();
                lightSet.forbidPedestrians();
            }
            Thread.sleep(100);
            assertEquals(Signal.RED, lightSet.getStatus());
            checkAllLights(lightSet, Signal.RED, Signal.DONT_WALK);
            assertTrue(lightSet.getSignalCycle().isAlive());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void canControlTurnLights() {
        try {
            log();
            lightSet = new LinearLightSet(0, observer);
            TurnLightSet lightSet = new TurnLightSet(1, observer);
            synchronized (lightSet) {
                lightSet.turnOn();
                lightSet.advance();
            }
            assertEquals(Signal.RED, lightSet.getStatus());
            checkAllLights(lightSet, Signal.RED);
            assertTrue(lightSet.getSignalCycle().isAlive());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // helper methods

    private void checkAllLights(AbstractLightSet StopLightSet, Signal stopLightStatus, Signal pedestrianLightStatus) {
        checkAllLights(lightSet, stopLightStatus);
        for (int i = 0; i < 2; i++) {
            assertEquals(pedestrianLightStatus, lightSet.getPedestrianLight(i).getStatus());
        }
    }

    private void checkAllLights(AbstractLightSet lightSet, Signal lightStatus) {
        for (int i = 0; i < 2; i++) {
            assertEquals(lightStatus, lightSet.getTriColorLight(i).getStatus());
        }
    }

    /*
     * Begin new methods added as part of promela testing
     */

    @Test
    public void returnToOffStateWhenSystemDisabled() {
        try {
            log();
            lightSet = new LinearLightSet(0, observer);
            synchronized (lightSet) {
                lightSet.turnOn();
                lightSet.reset();
            }
            assertEquals(Signal.OFF, lightSet.getStatus());
            checkAllLights(lightSet, Signal.OFF, Signal.OFF);
            assertTrue(lightSet.getSignalCycle().isAlive());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void switchToWalkAfterVehicleLightsAreRed() {
        System.setOut(new PrintStream(consoleOut));
        try {
            log();
            lightSet = new LinearLightSet(0, observer);
            synchronized (lightSet) {
                lightSet.turnOn();
                lightSet.advance();
            }
            assertThat(
                    consoleOut.toString(),
                    containsString("> trafficlight.LinearLightSet: 0 RED--switching pedestrian lights\r\n> trafficlight.PedestrianLight: 0 WALK"));
            assertTrue(lightSet.getSignalCycle().isAlive());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        System.setOut(System.out);
    }

    @Test
    public void swichToDontWalkBeforeVehicleLightsAreGreen() {
        System.setOut(new PrintStream(consoleOut));
        try {
            log();
            lightSet = new LinearLightSet(0, observer);
            synchronized (lightSet) {
                lightSet.turnOn();
                lightSet.advance();
            }
            assertThat(
                    consoleOut.toString(),
                    containsString("> trafficlight.PedestrianLight: 0 DONT_WALK\r\n> trafficlight.LinearLightSet: 0 GREEN"));
            assertTrue(lightSet.getSignalCycle().isAlive());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        System.setOut(System.out);
    }

    @Test
    public void anyVehicleLightEventuallyTurnsOrange() {
        System.setOut(new PrintStream(consoleOut));
        try {
            log();
            lightSet = new LinearLightSet(0, observer);
            synchronized (lightSet) {
                lightSet.turnOn();
                lightSet.advance();
            }
            assertThat(consoleOut.toString(), containsString("trafficlight.VehicleLight: 0 ORANGE"));
            assertThat(consoleOut.toString(), containsString("trafficlight.LinearLightSet: 0 ORANGE"));
            assertTrue(lightSet.getSignalCycle().isAlive());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        System.setOut(System.out);
    }

    @Test
    public void anyVehicleLightStaysGreenUntilTurningOrange() {
        System.setOut(new PrintStream(consoleOut));
        try {
            log();
            lightSet = new LinearLightSet(0, observer);
            synchronized (lightSet) {
                lightSet.turnOn();
                lightSet.advance();
            }
            assertThat(consoleOut.toString(),
                    containsString("trafficlight.LinearLightSet: 0 GREEN--switching to ORANGE"));
            assertTrue(lightSet.getSignalCycle().isAlive());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        System.setOut(System.out);
    }

    @Test
    public void anyVehicleLightStaysRedUntilTurningGreen() {
        System.setOut(new PrintStream(consoleOut));
        try {
            log();
            lightSet = new LinearLightSet(0, observer);
            synchronized (lightSet) {
                lightSet.turnOn();
                lightSet.advance();
            }
            assertThat(consoleOut.toString(), containsString("trafficlight.LinearLightSet: 0 RED--switching to GREEN"));
            assertTrue(lightSet.getSignalCycle().isAlive());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        System.setOut(System.out);
    }
}
