package commonHelper;
import java.util.ArrayList;

import commonHelper.interfaces.IRandomSelecterInputObject;

public class RandomSelecter 
{
		
	private class ProbabilityDensityFunctionEntry
	{
		public Object ReferencedObject = null;
		public long IssuedValue =0;
		public double Probability = 0;
		public double MinDensityValue = 0;
		public double MaxDensityValue = 0;
	}
	
	long _power = 2;
	
	public RandomSelecter()
	{		
	}
	
	public RandomSelecter(long power)
	{
		_power = power;
	}	
	
	/**
	 * Calculates an object from the list of selected items
	 * @param objectsCollection
	 * @return
	 */
	public Object CalculateRandomFromObjects(ArrayList<IRandomSelecterInputObject> objectsCollection)
	{
		ArrayList<ProbabilityDensityFunctionEntry> _densityFunction = new ArrayList<ProbabilityDensityFunctionEntry>();
		
		// if the input value is milliseconds, we will be able to handle 49,7 days 
		// difference between DocumentBoxes with power of 2
		
		long minimalValue = 0;
		for(IRandomSelecterInputObject pr: objectsCollection)
		{
			if(minimalValue> pr.GetIssuedValue())
			{
				minimalValue = pr.GetIssuedValue();
			}
		}
		
		long nominator = 0;		
		// power the values and calculate nominator to make them more spreaded
		for(IRandomSelecterInputObject pr: objectsCollection)
		{
			ProbabilityDensityFunctionEntry entry = new ProbabilityDensityFunctionEntry();
			entry.ReferencedObject = pr.GetReferencedObject();
			
			_densityFunction.add(entry);			
			
			// minimize value
			entry.IssuedValue  = pr.GetIssuedValue() -  minimalValue;			
			// and make the values more spreaded by powerizing them
			entry.IssuedValue = (long) Math.pow(entry.IssuedValue, _power);
			nominator = nominator + entry.IssuedValue;						
		}
		
		
		// now calculate the density function ... 
		
		for(int i=0; i<_densityFunction.size(); i++)
		{
			ProbabilityDensityFunctionEntry entry = _densityFunction.get(i);
			
			//dengerous: if issued value is 0, then the probability is 0? Yes, it is, so correct
			entry.Probability = (double)entry.IssuedValue / (double)nominator;	
			
			if(i > 0)
			{
				ProbabilityDensityFunctionEntry prevEntry = _densityFunction.get(i-1);
				entry.MinDensityValue = prevEntry.MaxDensityValue;
			}
			entry.MaxDensityValue = entry.MinDensityValue + entry.Probability;					
		}		
		
		// now select the normalized probability		
		Double randomNumber = Math.random();
		Object objResult  =null;
		// and determine the according referenced object
		for(ProbabilityDensityFunctionEntry pr: _densityFunction)
		{
			if(pr.MinDensityValue <= randomNumber && randomNumber<= pr.MaxDensityValue)
			{
				objResult = pr.ReferencedObject;	
			}			
		}
		return objResult;
	}
}
