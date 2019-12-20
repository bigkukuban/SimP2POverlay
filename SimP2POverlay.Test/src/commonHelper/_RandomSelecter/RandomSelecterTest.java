package commonHelper._RandomSelecter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Objects;

import org.junit.Test;

import commonHelper.RandomSelecter;
import commonHelper.interfaces.IRandomSelecterInputObject;

public class RandomSelecterTest {
	
	class RandomEntry implements IRandomSelecterInputObject
	{

		long _value;
		Object _referencedObject;
		public RandomEntry(long value, Object referenced)
		{
			_value = value;
			_referencedObject = referenced;
		}
		@Override
		public Object GetReferencedObject() {
			return _referencedObject;
		}

		@Override
		public long GetIssuedValue()
		{
			return _value;
		}
		
	}
	
	@Test
	public final void ShouldSelectRandomItem() 
	{
		ArrayList<IRandomSelecterInputObject> entriesInList = new ArrayList<IRandomSelecterInputObject>();
		
		
		entriesInList.add(new RandomEntry(10,"Value"+10));
		entriesInList.add(new RandomEntry(30,"Value"+30));
		entriesInList.add(new RandomEntry(50,"Value"+50));
		entriesInList.add(new RandomEntry(90,"Value"+90));
		entriesInList.add(new RandomEntry(500,"Value"+500));
				
		RandomSelecter s = new RandomSelecter();		
		boolean bResult = false;
		
		for(int i = 0; i< 1; i++)
		{
			Object result = s.CalculateRandomFromObjects(entriesInList);
			result.getClass();
			
			if(Objects.equals(result, new String("Value500")))
			{
				bResult = true;	
			}
		}		
		assertTrue(bResult);		
	}

}
