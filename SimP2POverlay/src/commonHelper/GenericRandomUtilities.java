package commonHelper;

import java.util.List;

public class GenericRandomUtilities<T> {

	public T SelectOneByRandom(List<T> items)
	{
		int sizeItems = items.size();
		
		if(sizeItems == 0) return null;
		
		Double randomNumber = Math.random();
		
		Double entry  = ((double)(sizeItems* randomNumber));						
		
		return items.get(entry.intValue());	
	}
	
}
