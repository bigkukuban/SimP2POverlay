package commonHelper.math;

import java.util.ArrayList;

public class RandomUtilities<T> 
{
	
	
	public static boolean p05ProbablityTrial()
	{
		if(Math.random() > 0.5) return true;
		return false;
	}
	
	public static int SelectRandomInteger(int maxValue)
	{
		return (int)(Math.random()*((double)maxValue - 1.0 ));
	}
	

	public static <T> T SelectOneByRandomFromList(ArrayList<T> items)
	{			
		int sizePeers = items.size();
		
		if(sizePeers == 0) return null;
		
		Double randomNumber = Math.random();
		
		Double entry  = ((double)(sizePeers* randomNumber));						
		
		return items.get(entry.intValue());	
	}	
}
