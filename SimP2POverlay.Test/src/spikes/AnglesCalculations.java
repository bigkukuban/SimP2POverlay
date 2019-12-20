package spikes;

import static org.junit.Assert.*;

import org.junit.Test;

public class AnglesCalculations {
	@Test
	public final void CalcAngleFromVectorQ3()
	{
		double [] loadForceDirection = new double[2];
		
		loadForceDirection[0] = -2;
		loadForceDirection[1] = -1.6;
		
		double angle = Math.atan(loadForceDirection[1]/loadForceDirection[0])/(2*Math.PI)*360.0;
				
		if(loadForceDirection[0] >= 0 && loadForceDirection[1] >=0)
		{
			
		}
		
		if(loadForceDirection[0] < 0 && loadForceDirection[1]>=0)
		{
			angle = angle + 90;
		}
		
		if(loadForceDirection[0] <= 0 && loadForceDirection[1]<=0)
		{
			angle = angle + 180;
		}
		
		if(loadForceDirection[0] > 0 && loadForceDirection[1]<=0)
		{
			angle = angle + 270;
		}
		
		assertTrue(angle >218 && angle <220);
	}
	
	@Test
	public final void CalcAngleFromVectorQ1()
	{
		double [] loadForceDirection = new double[2];
		
		loadForceDirection[0] = 2;
		loadForceDirection[1] = 1.6;
		
		double angle = Math.atan(loadForceDirection[1]/loadForceDirection[0])/(2*Math.PI)*360.0;
				
		if(loadForceDirection[0] >= 0 && loadForceDirection[1] >=0)
		{
			
		}
		
		if(loadForceDirection[0] < 0 && loadForceDirection[1]>=0)
		{
			angle = angle + 90;
		}
		
		if(loadForceDirection[0] <= 0 && loadForceDirection[1]<=0)
		{
			angle = angle + 180;
		}
		
		if(loadForceDirection[0] > 0 && loadForceDirection[1]<=0)
		{
			angle = angle + 270;
		}
		
		assertTrue(angle >38 && angle <39);
	}
	
	
}
