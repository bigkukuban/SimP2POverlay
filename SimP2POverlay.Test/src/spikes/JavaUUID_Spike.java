package spikes;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.Test;

public class JavaUUID_Spike {

	@Test
	public final void testCreateUUID()
	{
		UUID uuid = UUID.randomUUID();
		String result= uuid.toString();
		
						
		assertTrue(result.length() > 0);
	}
	
	@Test
	public final void testCreateUUIDFast()
	{
		//  for (;;) {		        		        		       
		        
			  long t0 = System.currentTimeMillis();
			  
		        for (int i = 0; i < 100000000; i++) {		        	
		            Math.random();		           
		        }
		       
		        
		        System.out.println(System.currentTimeMillis() - t0);
		   // }
	}
	
	@Test
	public final void testTimeToItertaOverBigArray()
	{
		long iMax = 1000000;
		Map<Long,Long> myMap = new HashMap<Long,Long>();
		 long startTime = System.currentTimeMillis();
		
		for(long i =0; i<iMax;i++)
		{
			myMap.put(i, iMax);
		}
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println("Elapsted Time [ms]:"+elapsedTime);
	}
}
