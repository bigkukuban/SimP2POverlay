package commonHelper;

import java.util.ArrayList;

import commonHelper.interfaces.IRandomSelecterDoubleInputObject;

public class RandomSelecterDouble {
	
	private class ProbabilityDensityFunctionEntry
	{
		public Object ReferencedObject = null;
		public double IssuedValue =0;
		public double Probability = 0;
		public double MinDensityValue = 0;
		public double MaxDensityValue = 0;
	}
	
	ArrayList<IRandomSelecterDoubleInputObject> _inputObjectsCollection;
	ArrayList<ProbabilityDensityFunctionEntry> _densityFunction;
	
	private double NegotiationFunction(double value)
	{
		/**
		 * General model Exp1:
     		f(x) = a*exp(b*x)
			Coefficients (with 95% confidence bounds):
			       a =       815.3  (493.6, 1137)
			       b =       -1.16  (-2.549, 0.2295)			
			Goodness of fit:
			  SSE: 8.818e+004
			  R-square: 0.8772
			  Adjusted R-square: 0.8465
			  RMSE: 148.5			
		 */
		
		//function was approximated by matlab curve fitting tool 
		return 1000*Math.exp(GlobalSimulationParameters.FactorProbabilityReduction*value);
	}
	
	public void AssignObjectlist(ArrayList<IRandomSelecterDoubleInputObject> objectsCollection)
	{
		_densityFunction = new ArrayList<ProbabilityDensityFunctionEntry>();
		_inputObjectsCollection = new  ArrayList<IRandomSelecterDoubleInputObject>();
		
		double sumOverAll= 0;
		for(IRandomSelecterDoubleInputObject pr: objectsCollection)
		{
			ProbabilityDensityFunctionEntry entry = new ProbabilityDensityFunctionEntry();
			entry.ReferencedObject = pr.GetReferencedObject();
			
			_densityFunction.add(entry);						
			// minimize value
			entry.IssuedValue  = NegotiationFunction(pr.GetIssuedValue());			
			sumOverAll = sumOverAll + entry.IssuedValue;
		}							
		// now calculate the density function ... 		
		for(int i=0; i<_densityFunction.size(); i++)
		{
			ProbabilityDensityFunctionEntry entry = _densityFunction.get(i);
			
			//dengerous: if issued value is 0, then the probability is 0? Yes, it is, so correct
			entry.Probability = entry.IssuedValue / sumOverAll;	
			
			if(i > 0)
			{
				ProbabilityDensityFunctionEntry prevEntry = _densityFunction.get(i-1);
				entry.MinDensityValue = prevEntry.MaxDensityValue;
			}
			entry.MaxDensityValue = entry.MinDensityValue + entry.Probability;					
		}		
	}
	
	public Object GetRandomObjectFromCurrentDensity()
	{						
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
