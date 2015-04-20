package safety;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import trafficlight.Intersection;
import trafficlight.Operator;

public class Safety1 {
	
	// represent the client of an intersection
		Operator operator = new Operator();
		
		// milliseconds for running the runIntersection test
		final static long TIME_LIMIT = 1000;
		
		Intersection intersection;
	
		
		public void runIntersectionForAWhileThenInterrupt() {
			try {				
				intersection = new Intersection(operator);
				intersection.enable();
				Thread.sleep(TIME_LIMIT);	
				//intersection.disable();
				//intersection.interrupt();
				//System.out.println("Successfully iinterrupted without deadlock");
				//intersection.exit();
			} catch (Exception e) {
				fail(e.getMessage());
			}
		}
		
		
		public void runIntersectionForAWhileThenDisabled() {
			try {					
				intersection = new Intersection(operator);
				Thread thread = intersection.enable();
				intersection.interrupt();
				thread.join();
				System.out.println("Successfully disabled without deadlock");
				intersection.exit();
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		
		public static void main(String[] args){
			Safety1 o=new Safety1();
			o.runIntersectionForAWhileThenInterrupt();
			//new NoDeadlockUntilInterruptedOrDisabled().runIntersectionForAWhileThenDisabled();			
		}
	

}
