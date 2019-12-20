package commonHelper._LinearHelper;

import static org.junit.Assert.*;

import org.junit.Test;

import commonHelper.math.LinearAlgebraHelper;

public class CalculateQuadraticEquation_ABCParams {

	@Test
	public final void CalculateQuadraticEquation_ABCParamsTest1() {
		
		//Test with 3x^2+2x-1
		double[] p1 = new double[]{0,-1};
		
		double[] p2 = new double[]{3f,32f};

		double[] p3 = new double[]{4f,55f};
				
		double[] result = LinearAlgebraHelper.CalculateQuadraticEquation_ABCParams(p1,p2,p3);
		
		assertTrue(result[0] == 3f);
		assertTrue(result[1] == 2f);
		assertTrue(result[2] == -1f);

		
	}
	
		
	@Test
	public final void CalculateQuadraticEquation_ABCParamsTest2() {
		
		//Test with -3x^2-2x+1
		double[] p1 = new double[]{0,1};
		
		double[] p2 = new double[]{1f,-4f};

		double[] p3 = new double[]{-1f,0f};
				
		double[] result = LinearAlgebraHelper.CalculateQuadraticEquation_ABCParams(p1,p2,p3);
		
		assertTrue(result[0] == -3f);
		assertTrue(result[1] == -2f);
		assertTrue(result[2] == +1f);		
	}

	
	@Test
	public final void CalculateQuadraticEquation_ABCParamsTestFailes() {
		
		//Test with -3x^2-2x+1
		double[] p1 = new double[]{3,-1};
		
		double[] p2 = new double[]{3,1};

		double[] p3 = new double[]{4,1};
				
		double[] result = LinearAlgebraHelper.CalculateQuadraticEquation_ABCParams(p1,p2,p3);
		
		assertTrue(result.length == 0);
	
	}

}
