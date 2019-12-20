package commonHelper._LinearHelper;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import commonHelper.math.EuclideanPoint;
import commonHelper.math.LinearAlgebraHelper;
import commonHelper.math.LinearAlgebraHelper.LineDistanceResult;
import commonHelper.math.Vector_XD;

public class CalculateDistanceBetwennLineAndPoint {

	
	@Test
	public final void CalculateDistanceFor2Dimensions() 
	{
		Vector_XD fl = new Vector_XD(new double[]{2,1}); 
		EuclideanPoint pu = new EuclideanPoint(new double[]{1,1});
		EuclideanPoint pd = new EuclideanPoint(new double[]{1,3});
		
		LineDistanceResult result = LinearAlgebraHelper.CalculateDistanceBetwennLineAndPoint(pu, fl,pd);
		
		assertTrue(result.DistanceFromLine > 1.788);
		assertTrue(result.DistanceFromLine < 1.789);
		assertTrue(result.IsInTheSameDirectionAsDirectionPointer);
	}
	
	
	@Test
	public final void CalculateDistanceFor2Dimensions2() 
	{
		Vector_XD fl = new Vector_XD(new double[]{2,1}); 
		EuclideanPoint pu = new EuclideanPoint(new double[]{1,1});
		EuclideanPoint pd = new EuclideanPoint(new double[]{1,2});
		
		LineDistanceResult result = LinearAlgebraHelper.CalculateDistanceBetwennLineAndPoint(pu, fl,pd);
		
		assertTrue(result.DistanceFromLine  > 0.894);
		assertTrue(result.DistanceFromLine  < 0.895);
		assertTrue(result.IsInTheSameDirectionAsDirectionPointer);
	}
	
	@Test
	public final void CalculateDistanceFor2Dimensions3() 
	{
		Vector_XD fl = new Vector_XD(new double[]{2,1}); 
		EuclideanPoint pu = new EuclideanPoint(new double[]{1,1});
		EuclideanPoint pd = new EuclideanPoint(new double[]{3,1});
		
		LineDistanceResult result = LinearAlgebraHelper.CalculateDistanceBetwennLineAndPoint(pu, fl,pd);
		
		assertTrue(result.DistanceFromLine  > 0.894);
		assertTrue(result.DistanceFromLine  < 0.895);
		assertTrue(result.IsInTheSameDirectionAsDirectionPointer);
	}
	
	@Test
	public final void CalculateDistanceFor2Dimensions4() 
	{
		Vector_XD fl = new Vector_XD(new double[]{2,1}); 
		EuclideanPoint pu = new EuclideanPoint(new double[]{1,1});
		EuclideanPoint pd = new EuclideanPoint(new double[]{0,0.5});
		
		LineDistanceResult result = LinearAlgebraHelper.CalculateDistanceBetwennLineAndPoint(pu, fl,pd);
		
		assertTrue(result.DistanceFromLine  > -0.001);
		assertTrue(result.DistanceFromLine  < 0.001);
		assertTrue(result.IsInTheSameDirectionAsDirectionPointer == false);
	}
	
	
	@Test
	public final void CalculateDistanceFor2Dimensions5() 
	{
		Vector_XD fl = new Vector_XD(new double[]{2,1}); 
		EuclideanPoint pu = new EuclideanPoint(new double[]{1,1});
		EuclideanPoint pd = new EuclideanPoint(new double[]{0,0.0});
		
		LineDistanceResult result = LinearAlgebraHelper.CalculateDistanceBetwennLineAndPoint(pu, fl,pd);
		
		assertTrue(result.DistanceFromLine > 0.35 && result.DistanceFromLine < 0.45);
		assertTrue(result.IsInTheSameDirectionAsDirectionPointer == false);
		
	}
	
	@Test
	public final void CalculateDistanceFor3Dimensions1() 
	{
		Vector_XD fl = new Vector_XD(new double[]{2,1,1}); 
		EuclideanPoint pu = new EuclideanPoint(new double[]{1,1,1});
		EuclideanPoint pd = new EuclideanPoint(new double[]{0,0.5,1});
		
		LineDistanceResult result = LinearAlgebraHelper.CalculateDistanceBetwennLineAndPoint(pu, fl,pd);
		
		assertTrue(result.DistanceFromLine > 0.44);
		assertTrue(result.DistanceFromLine < 0.46);
		assertTrue(result.IsInTheSameDirectionAsDirectionPointer == false);
	}
	
	
	@Test
	public final void CalculateDistanceFor3Dimensions2() 
	{
		Vector_XD fl = new Vector_XD(new double[]{2,1,1}); 
		EuclideanPoint pu = new EuclideanPoint(new double[]{1,1,1});
		EuclideanPoint pd = new EuclideanPoint(new double[]{0,0.0,1});
		
		LineDistanceResult result = LinearAlgebraHelper.CalculateDistanceBetwennLineAndPoint(pu, fl,pd);
		
		assertTrue(result.DistanceFromLine > 0.706 && result.DistanceFromLine < 0.708);
		assertTrue(result.IsInTheSameDirectionAsDirectionPointer == false);
		
	}
	
	
	
	@Test
	public final void CalculateDistanceFor3Dimensions3() 
	{
		Vector_XD fl = new Vector_XD(new double[]{-1242.674090720811 ,1242.674090720811 ,1}); 
		EuclideanPoint pu = new EuclideanPoint(new double[]{2,2,1});
		EuclideanPoint pd = new EuclideanPoint(new double[]{1,1,1});
		
		LineDistanceResult result = LinearAlgebraHelper.CalculateDistanceBetwennLineAndPoint(pu, fl,pd);
		
		assertTrue(result.DistanceFromLine > 1.39 && result.DistanceFromLine < 1.42);
		assertTrue(result.IsInTheSameDirectionAsDirectionPointer);
		
	}
	
	@Test
	public final void CalculateDistanceFor3Dimensions4() 
	{
		Vector_XD fl = new Vector_XD(new double[]{-1242 ,1243 ,1}); 
		EuclideanPoint pu = new EuclideanPoint(new double[]{2,2,1});
		EuclideanPoint pd = new EuclideanPoint(new double[]{1,1,1});
		
		LineDistanceResult result = LinearAlgebraHelper.CalculateDistanceBetwennLineAndPoint(pu, fl,pd);
		
		assertTrue(result.DistanceFromLine > 1.4 && result.DistanceFromLine  < 1.42);
		assertTrue(result.IsInTheSameDirectionAsDirectionPointer == false);
		
	}
	
	
	@Test
	public final void CalculateDistanceFor3Dimensions5() 
	{
		Vector_XD fl = new Vector_XD(new double[]{0 ,1401 ,1}); 
		EuclideanPoint pu = new EuclideanPoint(new double[]{1,2,1});
		EuclideanPoint pd = new EuclideanPoint(new double[]{1,1,1});
		
		LineDistanceResult result = LinearAlgebraHelper.CalculateDistanceBetwennLineAndPoint(pu, fl,pd);
		
		assertTrue(result.DistanceFromLine  < 0.001 && result.DistanceFromLine  > 0.0001);
		assertTrue(result.IsInTheSameDirectionAsDirectionPointer == false);
		
	}
}
