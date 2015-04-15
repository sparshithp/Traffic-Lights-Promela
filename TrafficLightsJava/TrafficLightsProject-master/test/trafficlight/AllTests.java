package trafficlight;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/*
 * CHECK CONSOLE OUTPUT AFTER RUNNING THE TESTS.
 */

@RunWith(Suite.class)
@SuiteClasses({ 
	IntersectionTest.class, 
	LightSetTest.class 
})

public class AllTests {

}
