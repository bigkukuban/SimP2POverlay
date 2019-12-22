package commonHelper._RandomSelecterDouble;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Objects;
import org.junit.Test;
import commonHelper.RandomSelecterDouble;
import commonHelper.interfaces.IRandomSelecterDoubleInputObject;


public class RandomSelecterDoubleTest {
	
	class RandomEntry implements IRandomSelecterDoubleInputObject
	{

		double _value;
		Object _referencedObject;
		public RandomEntry(double value, Object referenced)
		{
			_value = value;
			_referencedObject = referenced;
		}
		@Override
		public Object GetReferencedObject() {
			return _referencedObject;
		}

		@Override
		public double GetIssuedValue()
		{
			return _value;
		}
		
	}
	
	@Test
	public final void ShouldSelectRandomItem() 
	{
		ArrayList<IRandomSelecterDoubleInputObject> entriesInList = new ArrayList<IRandomSelecterDoubleInputObject>();
		
		
		entriesInList.add(new RandomEntry(0.01,"Value"+0));
		entriesInList.add(new RandomEntry(0.8,"Value"+0.5));
		entriesInList.add(new RandomEntry(0.9,"Value"+0.7));
		entriesInList.add(new RandomEntry(2,"Value"+10));		
				
		RandomSelecterDouble s = new RandomSelecterDouble();		
		boolean bResult = false;
		s.AssignObjectlist(entriesInList);
		int numberFindings = 0;		
		for(int i = 0; i< 10000; i++)
		{			
			Object result = s.GetRandomObjectFromCurrentDensity();
			result.getClass();
			
			if(Objects.equals(result, new String("Value0")))
			{
				numberFindings++;	
			}
		}		
		assertTrue(numberFindings > 5000);		
	}

}
