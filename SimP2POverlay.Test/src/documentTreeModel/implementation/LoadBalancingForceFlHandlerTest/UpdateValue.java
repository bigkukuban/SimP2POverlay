package documentTreeModel.implementation.LoadBalancingForceFlHandlerTest;

import static org.junit.Assert.*;

import org.junit.Test;

import commonHelper.math.EuclideanPoint;
import documentTreeModel.implementation.LoadBalancingForceFlHandler;

public class UpdateValue {

	@Test
	public final void ShouldUpdateDirection() 
	{
		EuclideanPoint pn = new EuclideanPoint(new double[]{2,2});
		
		LoadBalancingForceFlHandler flhandler = new LoadBalancingForceFlHandler(pn,1);
		
		assertTrue(flhandler.GetForceVector().GetLength() < 0.001);
				
		flhandler.UpdateValue(new double[]{1,1}, 10 , 0);
		
		
		assertTrue(flhandler.GetForceVector().GetLength() > 0.66);
		
		assertTrue(flhandler.GetForceVector().GetComponents()[0] <= 1.0);
		assertTrue(flhandler.GetForceVector().GetComponents()[1] <= 1.0);
		
	}
}
