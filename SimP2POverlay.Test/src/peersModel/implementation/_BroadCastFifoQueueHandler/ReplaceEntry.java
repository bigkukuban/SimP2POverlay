package peersModel.implementation._BroadCastFifoQueueHandler;

import static org.junit.Assert.*;

import java.util.Queue;
import java.util.UUID;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.junit.Test;
import peersModel.implementation.BroadCastFifoQueueHandler;


public class ReplaceEntry {
	

	@Test
	public final void testFifoSpike() {
		 Queue<Integer> fifo = new CircularFifoQueue<Integer>(2);
		 
		 fifo.add(1);
		 fifo.add(2);
		 fifo.add(3);
		 
		 assertTrue(fifo.contains(2));
		 assertTrue(fifo.contains(3));
		 assertFalse(fifo.contains(1));
		 assertFalse(fifo.contains(10));
		 
	}
	
	

	
	
	@Test
	public final void testShouldReplaceEntryNext() 
	{
		BroadCastFifoQueueHandler dut = new BroadCastFifoQueueHandler();
		
		UUID id = UUID.fromString("8cfaba9b-fd7e-41b6-8afe-22875f74463c");
		
		double remainingDtl1 = 0.17157287525380993;
		double remainingDtl2 = 0.5857864376269049 ;
		
		assertTrue(dut.ShouldHandleMessage(id, remainingDtl1));
		assertTrue(dut.ShouldForwardMessage(id, remainingDtl1));
		dut.MessageBroadCasted(id, remainingDtl1);
		
		assertFalse(dut.ShouldHandleMessage(id, remainingDtl2));
		assertTrue(dut.ShouldForwardMessage(id, remainingDtl2));
		
	}
	
	
	@Test
	public final void testShouldReplaceEntry() {
	
		BroadCastFifoQueueHandler dut = new BroadCastFifoQueueHandler();
		
		UUID id = UUID.randomUUID();
		
		dut.MessageBroadCasted(id, 2);
		
		assertFalse(dut.ShouldForwardMessage(id, 1));		
		assertFalse(dut.ShouldForwardMessage(id, 2));				
		assertTrue(dut.ShouldForwardMessage(id, 3));
		
		assertFalse(dut.ShouldForwardMessage(id, 3));
		assertTrue(dut.ShouldForwardMessage(id, 4));				
		
		for(int z = 0; z<100; z++)
		{
			UUID newId = UUID.randomUUID();
			
			assertTrue(dut.ShouldHandleMessage(newId, 1));
			assertTrue(dut.ShouldForwardMessage(newId, 1));
			dut.MessageBroadCasted(newId, 1);
			
			assertFalse(dut.ShouldHandleMessage(newId, 1));
			assertFalse(dut.ShouldForwardMessage(newId, 1));	
			assertTrue(dut.ShouldForwardMessage(newId, 2));
			assertTrue(dut.ShouldForwardMessage(newId, 6));
			assertFalse(dut.ShouldForwardMessage(newId, 1));
			assertFalse(dut.ShouldForwardMessage(newId, -2));
		}
		
		for(int z = 0; z<100; z++)
		{
			UUID newId = UUID.randomUUID();
			
			assertTrue(dut.ShouldHandleMessage(newId, 1));
			assertTrue(dut.ShouldForwardMessage(newId, 1));
			dut.MessageBroadCasted(newId, 1);
			
			assertFalse(dut.ShouldHandleMessage(newId, 1));
			assertFalse(dut.ShouldForwardMessage(newId, 1));	
			assertTrue(dut.ShouldForwardMessage(newId, 2));
			assertTrue(dut.ShouldForwardMessage(newId, 6));
			assertFalse(dut.ShouldForwardMessage(newId, 1));
			assertFalse(dut.ShouldForwardMessage(newId, -2));
		}
								
	}
		
}
