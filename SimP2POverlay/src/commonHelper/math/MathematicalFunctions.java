package commonHelper.math;

public class MathematicalFunctions {
	/*
	 * 
	 * Calculates interpolated linear function for capacity
	 */
	public static long LinearFunctionApproximationForCapacityReduction(long forStep, long ValueX0)
	{
		
		//p1
		long N1 = ValueX0; 
		long S1 = 0;
					
		//p2
		long N2 = (long) (ValueX0*0.5); 
		long S2 = 30;
					
		//p3
		long N3 = (long) (ValueX0*0.01); 
		long S3 = 70;
					
		//p4
		long N4 = 1; 
		long S4 = 100;
		
		double [] result = null;
		
		if(forStep >=S1 && forStep <=S2)
		{
			result = LinearAlgebraHelper.CalculateLinearEquation_ABParams(new double[]{S1,N1},  new double[]{S2,N2});
											
		} else if(forStep >=S2 && forStep <=S3)
		{
			result = LinearAlgebraHelper.CalculateLinearEquation_ABParams(new double[]{S2,N2},  new double[]{S3,N3});
			
		} else if(forStep >=S3 && forStep <S4)
		{
			
			result = LinearAlgebraHelper.CalculateLinearEquation_ABParams(new double[]{S3,N3},  new double[]{S4,N4});
			
		} else
		{ 
			result = new double[]{0,1};				
		}
		return (long) (result[0]*forStep + result[1]);
					
	}
	
}
