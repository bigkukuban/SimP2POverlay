package commonHelper._LRDMap;

import static org.junit.Assert.*;
import java.util.UUID;
import org.junit.Test;

import commonHelper.LRDMap;

public class LRDMapTest {

	@Test
	public final void ShouldReplaceItem() {
		
		LRDMap<UUID,Object> dut = new LRDMap<UUID,Object>(2);
		
		UUID key1 = UUID.randomUUID();
		UUID key2 = UUID.randomUUID();
		UUID key3 = UUID.randomUUID();

		
		dut.put(key1,"String1");
		dut.put(key2,"String2");
		
		dut.get(key2);		
		dut.put(key3,"String2");
				
		assertTrue(dut.get(key1) == null);
		assertTrue(dut.get(key2) != null);
		assertTrue(dut.get(key3) != null);
		
	}
	
	
	@Test
	public final void ShouldHandleInterval() {
		
		LRDMap<UUID,Object> dut = new LRDMap<UUID,Object>(2);
		
		UUID key1 = UUID.randomUUID();
		UUID key2 = UUID.randomUUID();
		UUID key3 = UUID.randomUUID();

		
		dut.put(key1,"String1");
		dut.put(key2,"String2");
		
		dut.get(key1);
		
		for(int i =0; i<14; i++)
		{
			dut.get(key2);
			dut.get(key1);
		}
		
		//key2 wurde zuletzt verwendet, somit muss dieser rausfliegen
		dut.get(key2);
		
		dut.put(key3,"String3");
				
		assertTrue(dut.get(key1) == null);
		assertTrue(dut.get(key2) != null);
		assertTrue(dut.get(key3) != null);
		
	}
	
	@Test
	public final void ShouldWorkWithInteger() 
	{
		LRDMap<Integer,Object> dut = new LRDMap<Integer,Object>(2);
		
		Integer key1 = 1;
		Integer key2 =2;	
		
		dut.put(key1,"String1");
		dut.put(key2,"String2");		
				
		
		assertTrue(dut.get(key1) != null);
		assertTrue(dut.get(key2) != null);
		
		assertTrue(dut.get(key2) == "String2");
	}
	
	
	@Test
	public final void ShouldReplaceObjectForKey() {
		
		LRDMap<UUID,Object> dut = new LRDMap<UUID,Object>(2);
		
		UUID key1 = UUID.randomUUID();
		UUID key2 = UUID.randomUUID();	

		
		dut.put(key1,"String1");
		dut.put(key2,"String2");
		dut.put(key2,"String3");
				
		
		assertTrue(dut.get(key1) != null);
		assertTrue(dut.get(key2) != null);
		
		assertTrue(dut.get(key2) == "String3");
		
	}
	

}
