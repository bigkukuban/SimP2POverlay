package spikes;

import static org.junit.Assert.*;
import org.junit.Test;

import commonHelper.LRUMap;

public class CachingStrategies 
{

	class CachedItem 
	{
		public int Key = 0;
						
	}
	
	@Test
	public final void LRDMapOwnImpl()
	{
		
	}
	
	@Test
	public final void LRUMapApache()
	{
		org.apache.commons.collections4.map.LRUMap<Integer,CachedItem> mapLru = new org.apache.commons.collections4.map.LRUMap<Integer,CachedItem>(5);
									
		for(int i = 0; i<100; i++)
		{
			CachedItem itm = new CachedItem();		
			itm.Key = i;
		
			mapLru.put(itm.Key, itm);
			if(i > 5)
			{	
				mapLru.get(5);
			}
		}
		
		assertTrue(mapLru.get(0) == null);
		assertTrue(mapLru.get(95) == null);
		assertTrue(mapLru.get(96) != null);
		assertTrue(mapLru.get(97) != null);
		assertTrue(mapLru.get(98) != null);
		assertTrue(mapLru.get(99) != null);
		assertTrue(mapLru.get(5) != null);
		assertTrue(mapLru.size() == 5);
	}
	
	
	@Test
	public final void LRUMapApache1()
	{
		org.apache.commons.collections4.map.LRUMap<Integer,CachedItem> mapLru = new org.apache.commons.collections4.map.LRUMap<Integer,CachedItem>(2);
					
		
		
		for(int i = 0; i<100; i++)
		{
			CachedItem itm = new CachedItem();		
			itm.Key = i;
		
			mapLru.put(itm.Key, itm);
			if(i > 0)
			{	
				mapLru.get(0);
			}
		}
		
		assertTrue(mapLru.get(0) != null);
		assertTrue(mapLru.get(95) == null);
		assertTrue(mapLru.get(96) == null);
		assertTrue(mapLru.get(97) == null);
		assertTrue(mapLru.get(98) == null);
		assertTrue(mapLru.get(99) != null);
		assertTrue(mapLru.get(5) == null);
		assertTrue(mapLru.size() == 2);
	}

	
	
	@Test
	public final void LRUMapApache2()
	{
		LRUMap<Integer,CachedItem> mapLru = new LRUMap<Integer,CachedItem>(2);
									
		for(int i = 0; i<100; i++)
		{
			CachedItem itm = new CachedItem();		
			itm.Key = i;
		
			mapLru.put(itm.Key, itm);
			if(i > 0)
			{	
				mapLru.get(0);
			}
		}
		
		assertTrue(mapLru.get(0) != null);
		assertTrue(mapLru.get(95) == null);
		assertTrue(mapLru.get(96) == null);
		assertTrue(mapLru.get(97) == null);
		assertTrue(mapLru.get(98) == null);
		assertTrue(mapLru.get(99) != null);
		assertTrue(mapLru.get(5) == null);
		assertTrue(mapLru.size() == 2);
	}
	

}
