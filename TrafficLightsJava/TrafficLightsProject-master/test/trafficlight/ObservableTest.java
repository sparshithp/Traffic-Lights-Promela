package trafficlight;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import trafficlight.ObservableEntity;
import trafficlight.ObserverEntity;

/* 
 * All test classes inherit from this class. 
 * 
 * This class allows test method names in subclasses to be observed and logged. Ultimately logs are 
 * printed on the console. This class allows logs to be clearly delimited. 
 * Turn the Observer pattern off to prevent this by setting observing variable 
 * to false in ObservableEntity. 
 */

public abstract class ObservableTest extends ObservableEntity {

	@Rule
	public TestName name = new TestName();
	
	@Before
	public void setUp() throws Exception {
		observer = new ObserverEntity();
		if (observing) addObserver(observer);
	}

	public void log() {
		send("******* " + name.getMethodName() + " *******");
	}

}
