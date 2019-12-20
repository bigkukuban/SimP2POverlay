package commonHelper._LinearHelper;

import static org.junit.Assert.*;

import org.junit.Test;

import commonHelper.math.LinearAlgebraHelper;

public class CalculateAngleForVector2D {

	@Test
	public void CalculateQ1() {
	
	double [] loadForceDirection = new double[2];			
	
		loadForceDirection[0] = 2;
		loadForceDirection[1] = 1.6;
		
		double result = LinearAlgebraHelper.CalculateAngleForVector2D(loadForceDirection[0], loadForceDirection[1] );
		
		assertTrue(result >38 && result <39);
	}

	@Test
	public void CalculateQ2() {
	
	double [] loadForceDirection = new double[2];			
	
		loadForceDirection[0] = -2;
		loadForceDirection[1] = 1.6;
		
		double result = LinearAlgebraHelper.CalculateAngleForVector2D(loadForceDirection[0], loadForceDirection[1] );
		
		assertTrue(result >141 && result <142);
	}
	
	
	@Test
	public void CalculateQ2_2() {
	//:  Load direction X-0.8944271909999159 Y: 0.4472135954999579 Length: 0.010000000149011612 angle :116.56505117707799 <-- wrong
	double [] loadForceDirection = new double[2];			
	
		loadForceDirection[0] = -0.8944271909999159;
		loadForceDirection[1] = 0.4472135954999579;
		
		double result = LinearAlgebraHelper.CalculateAngleForVector2D(loadForceDirection[0], loadForceDirection[1] );
		
		assertTrue(result >153 && result <154);
	}
	
	
	@Test
	public void CalculateQ3() {
	
	double [] loadForceDirection = new double[2];			
	
		loadForceDirection[0] = -2;
		loadForceDirection[1] = -1.6;
		
		double result = LinearAlgebraHelper.CalculateAngleForVector2D(loadForceDirection[0], loadForceDirection[1] );
		
		assertTrue(result >218 && result <219);
	}
	
	@Test
	public void CalculateQ4() {
	
		double [] loadForceDirection = new double[2];			
	
		loadForceDirection[0] = 2;
		loadForceDirection[1] = -1.6;
		
		double result = LinearAlgebraHelper.CalculateAngleForVector2D(loadForceDirection[0], loadForceDirection[1] );
		
		assertTrue(result >321 && result <322);
	}
	
}
