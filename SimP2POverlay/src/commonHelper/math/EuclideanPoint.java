package commonHelper.math;

import commonHelper.math.interfaces.IEuclideanPoint;

public class EuclideanPoint implements IEuclideanPoint{

	protected double []_components;

	public EuclideanPoint(double [] components)
	{
		 _components = components;
	}	
	
	public EuclideanPoint(float[] values) 
	{
		_components = new double[values.length];
		
		for(int z =0; z<values.length;z++)
		{
			_components[z] = values[z];
		}
	}

	@Override
	public double[] GetComponents() {
		return _components;
	}
	@Override
	public long GetDimensions() {
		
		return _components.length;
	}

	@Override
	public IEuclideanPoint CloneMe() {
				
		return new EuclideanPoint(_components);
	}

	@Override
	public double GetDistanceToOther(IEuclideanPoint point) 
	{		
		double result =0;		
		for(int z =0; z<point.GetDimensions(); z++)
		{
			result =result +  Math.pow(_components[z] - point.GetComponents()[z],2); 
		}
		
		return Math.sqrt(result);
	}	
	
}
