package commonHelper._LinearHelper;

import static org.junit.Assert.*;

import org.junit.Test;

import commonHelper.math.LinearAlgebraHelper;

public class CalculateLinearEquation_ABParams {

	@Test
	public final void CalculateQuadraticEquation_ABCParamsTest1() {
		
		//Test with -2x-1
		double[] p1 = new double[]{0,-1};
		
		double[] p2 = new double[]{3f,32f};
		
				
		double[] result = LinearAlgebraHelper.CalculateLinearEquation_ABParams(p1,p2);
		
		assertTrue(result[0] == 11f);
		assertTrue(result[1] == -1f);		
	}
	
		
	@Test
	public final void CalculateQuadraticEquation_ABCParamsTest2() {
		
		//Test with 2x+1
		double[] p1 = new double[]{0,1};
		
		double[] p2 = new double[]{1f,-4f};
		
				
		double[] result = LinearAlgebraHelper.CalculateLinearEquation_ABParams(p1,p2);
		
		assertTrue(result[0] == -5f);
		assertTrue(result[1] == 1f);
	
	}

	
	@Test
	public final void CalculateQuadraticEquation_ABCParamsTestFailes() {
		
		//Test with -3x^2-2x+1
		double[] p1 = new double[]{3,-1};
		
		double[] p2 = new double[]{3,1};
		
				
		double[] result = LinearAlgebraHelper.CalculateLinearEquation_ABParams(p1,p2);
		
		assertTrue(result.length == 0);
	
	}

}
