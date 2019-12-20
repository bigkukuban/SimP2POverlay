package commonHelper.math;

import commonHelper.math.interfaces.IEuclideanPoint;
import commonHelper.math.interfaces.IVector;

public class Vector_XD extends EuclideanPoint implements IVector 
{
	public Vector_XD(double [] components) 
	{
		super(components);
	}
	
	public Vector_XD(IEuclideanPoint ep) 
	{
		super(ep.GetComponents());
	}	
	
	@Override
	public double GetLength() 
	{
		double sum =0;
		
		double [] components = super.GetComponents();
		
		for(int z =0; z<GetDimensions();z++)
		{
			sum = sum  + Math.pow(components[z] , 2); 
		}		
		return Math.sqrt(sum);
	}

	@Override
	public double[] GetUnit() {

		double length= GetLength(); 				
		double[] dest = new double[(int) GetDimensions()];		
		
		if(length < Double.MIN_VALUE) return dest;
		
		double [] components = super.GetComponents();
		
		for(int z =0; z<GetDimensions();z++)
		{
			dest[z] = components[z] / length;						
		}						
		return dest;
	}

	
	@Override
	public IVector AddVector(IVector other) {

		if(other.GetDimensions() != this.GetDimensions()) return null;
		
		double [] result  = new double[(int) GetDimensions()];
		
		for(int z =0; z<GetDimensions();z++)
		{
			result[z]  = _components[z] + other.GetComponents()[z];
		}		
		
		Vector_XD resVec = new Vector_XD(result); 
		
		return resVec;
	}
	@Override
	public IVector SubstractVector(IVector other) {

		if(other.GetDimensions() != this.GetDimensions()) return null;
					
		double [] result  = new double[(int) GetDimensions()];
		
		for(int z =0; z<GetDimensions();z++)
		{
			result[z]  = _components[z] -other.GetComponents()[z];
		}	
		
		Vector_XD resVec = new Vector_XD(result); 
		return resVec;
	}
	
	
	@Override
	public IVector CloneMe() {
				
		return new Vector_XD(_components);
	}

	@Override
	public IVector MultiplicateWithScalar(double value) {
		
		double [] result  = new double[(int) GetDimensions()];
		
		for(int z =0; z<GetDimensions();z++)
		{
			result[z]  = value * _components[z];
		}
		
		Vector_XD resVec = new Vector_XD(result); 
		return resVec;
		
	}	
	
	@Override
	public double ScalaProduct(IVector other){
		double result=0;
		
		for(int z =0; z<GetDimensions();z++)
		{
			result = result + _components[z]*other.GetComponents()[z];
		}		
		return result;
		
	}

	@Override
	public IVector GetUnitVector() {

		return new Vector_XD(GetUnit());
	}

}
