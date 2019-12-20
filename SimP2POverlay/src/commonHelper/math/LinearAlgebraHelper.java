package commonHelper.math;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;

import commonHelper.math.interfaces.IEuclideanPoint;
import commonHelper.math.interfaces.IVector;

public class LinearAlgebraHelper {

	/*
	 * Calucaltes for given 2 points the bx+c - polynom parameters
	 * returns [a, b]
	 * 
	 * First calculate the inverse-matrix, then use it to solve the equatation, 
	 * 
	 * limited to polynoms of 1st degree
	 * 
	 * p1, p2 : points of the curve
	 */
	public static double[] CalculateLinearEquation_ABParams(double[] p1, double[] p2)
	{
		double firstRow[] = new double[2];
		double secondRow[] = new double[2];		
		
		firstRow[0] = p1[0];
		firstRow[1] = 1;
						
		secondRow[0] = p2[0];
		secondRow[1] = 1;
										
		double[] result = new double [2];
		
		result[0] = p1[1];
		result[1] = p2[1];		
		
		RealMatrix coefficients = new Array2DRowRealMatrix(new double[][] { firstRow, secondRow},false);
		
		DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
		
		RealVector constants = new ArrayRealVector(result, false);
		
		try{
		
			RealVector solution = solver.solve(constants);		
			return solution.toArray();		
			
		}catch(SingularMatrixException exp)
		{
			return new double[]{};
		}
	}
	
	/*
	 * Calucaltes for given 3 points the ax^2+bx+c - polynom parameters
	 * returns [a, b, c]
	 * 
	 * First calculate the inverse-matrix, then use it to solve the equatation, 
	 * 
	 * limited to polynoms of 2nd degree
	 * 
	 * p1, p2, p3 : points of the curve
	 */
	public static double[] CalculateQuadraticEquation_ABCParams(double[] p1, double[] p2, double[] p3)
	{
		
		double firstRow[] = new double[3];
		double secondRow[] = new double[3];
		double thirdRow[] = new double[3];
		
		firstRow[0] = Math.pow(p1[0], 2);
		firstRow[1] = p1[0];
		firstRow[2] = 1;
		
		secondRow[0] = Math.pow(p2[0], 2);
		secondRow[1] = p2[0];
		secondRow[2] = 1;
		
		
		thirdRow[0] = Math.pow(p3[0], 2);
		thirdRow[1] = p3[0];
		thirdRow[2] = 1;
		
		double[] result = new double [3];
		
		result[0] = p1[1];
		result[1] = p2[1];
		result[2] = p3[1];
		
		RealMatrix coefficients = new Array2DRowRealMatrix(new double[][] { firstRow, secondRow, thirdRow},false);
		
		DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
		
		RealVector constants = new ArrayRealVector(result, false);
		
		try{
		
			RealVector solution = solver.solve(constants);		
			return solution.toArray();		
			
		}catch(SingularMatrixException exp)
		{
			return new double[]{};
		}
	}
	
	/**
	 * Calculates the agle of the vector to the x-coordinates 
	 * @param xComp
	 * @param yComp
	 * @return
	 */
	public static double CalculateAngleForVector2D(double xComp, double yComp)
	{
		double angleResult =  0;				
						
				
		if(xComp>= 0 && yComp >=0)
		{
			//q1
			angleResult = Math.atan(yComp/xComp)/(2*Math.PI)*360.0;;
		}
		
		if(xComp <= 0 && yComp>=0)
		{
			//q2			
			angleResult = Math.atan(xComp/yComp)/(2*Math.PI)*360.0;;
			angleResult = -angleResult + 90;
		}
		
		if(xComp <= 0 && yComp<=0)
		{
			//q3
			angleResult = Math.atan(yComp/xComp)/(2*Math.PI)*360.0;;
			angleResult = angleResult + 180;
		}
		
		if(xComp >= 0 && yComp<=0)
		{
			//q4
			angleResult = Math.atan(xComp/yComp)/(2*Math.PI)*360.0;;
			angleResult = -angleResult + 270;
		
		}
		return angleResult;
	}
	
	public class LineDistanceResult 
	{
		public double DistanceFromLine;
		public boolean IsInTheSameDirectionAsDirectionPointer; 
	}
	
	/**
	 * Should work for any dimensional spaces
	 * @param pointPu - first point of the line
	 * @param lineDirection - direction of the line 
	 * @param pointPd - Point of interest
	 * @return shortest distance between line (Pu, lineDir) and point Pd,
	 * 			 the return value is <0 if the point Pd is in the opposite direction for line Direction
	 */
	public static LineDistanceResult CalculateDistanceBetwennLineAndPoint(IEuclideanPoint pointPu, IVector lineDirection, IEuclideanPoint pointPd )
	{
		
		// the used approach is described here: http://www.mathematik-oberstufe.de/vektoren/a/abstand-punkt-gerade-lfdpkt.html
		double factorR;
		
		Vector_XD vecPu = new  Vector_XD(pointPu.GetComponents());
		Vector_XD vecPd = new  Vector_XD(pointPd.GetComponents());
		
		IVector pud = vecPu.SubstractVector(vecPd);
				
		factorR = -1*(pud.ScalaProduct(lineDirection))/(lineDirection.ScalaProduct(lineDirection)); 
								
		IVector  lineScaled= lineDirection.MultiplicateWithScalar(factorR);
				
		IVector PdDf = pud.AddVector(lineScaled); 
		
		
		LineDistanceResult result = new LinearAlgebraHelper().new LineDistanceResult();
		
		result.DistanceFromLine = PdDf.GetLength();
		
		if(factorR >= 0)
		{
			result.IsInTheSameDirectionAsDirectionPointer = true;
		}
		
		return result;
	}
	
}
