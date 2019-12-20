package peersModel.implementation;

import java.util.Queue;
import java.util.UUID;

import org.apache.commons.collections4.queue.CircularFifoQueue;


public class BroadCastFifoQueueHandler
{
	//Achtung: ab der Grösse 10000 Elemente scheint der Buffer nicht korrekt zu arbeiten. Mit 20 wird ein sehr gutes gleichmäßiges BroadCast erreicht.
	//
	private Queue<BroadCastFifoEntry> _broadCastFifo = new CircularFifoQueue<BroadCastFifoEntry>(20);
			
	class BroadCastFifoEntry
	{
		public UUID ForwarededMessageId;
		public double ForwardedWithTTL;
	}
	
	private BroadCastFifoEntry GetEntry(UUID id)
	{
		BroadCastFifoEntry result = null;
		
		for(BroadCastFifoEntry entry: _broadCastFifo)
		{
			if(entry.ForwarededMessageId.equals(id))
			{
				result = entry;
			}
		}
		
		return result;
	}
	
	private  void ReplaceEntry(BroadCastFifoEntry newEntry)
	{
		for(BroadCastFifoEntry entry: _broadCastFifo)
		{
			if(entry.ForwarededMessageId.equals(newEntry.ForwarededMessageId))
			{
				_broadCastFifo.remove(entry);
				break;
			}
		}
		_broadCastFifo.add(newEntry);
		
	}
	
	public void MessageBroadCasted(UUID id, double dtlValue)
	{
		BroadCastFifoEntry entry = GetEntry(id);
		
		if(entry != null) return;
				
		BroadCastFifoEntry newEntry = new BroadCastFifoEntry();
		
		newEntry.ForwardedWithTTL = dtlValue;
		newEntry.ForwarededMessageId = id;
		
		_broadCastFifo.add(newEntry);
				
	}
	
	public boolean ShouldHandleMessage(UUID id, double dtlValue)
	{
		BroadCastFifoEntry entry = GetEntry(id);
		
		if(entry == null) return true;
		
		return false;
	}
	
	public boolean ShouldForwardMessage(UUID id, double dtlValue)
	{				
		BroadCastFifoEntry entry = GetEntry(id);
		
		if(entry == null)
		{
			return true;
		}
								
		
		if( dtlValue <= entry.ForwardedWithTTL )
			return false;
		
		BroadCastFifoEntry newEntry = new BroadCastFifoEntry();
		
		newEntry.ForwardedWithTTL = dtlValue;
		newEntry.ForwarededMessageId = id;
		
		ReplaceEntry(newEntry);
		return true;
	}
}