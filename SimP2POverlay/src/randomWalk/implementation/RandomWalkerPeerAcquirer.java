package randomWalk.implementation;

import java.util.ArrayList;
import java.util.UUID;

import commonHelper.GlobalLogger;
import randomWalk.interfaces.IRandomWalker;
import randomWalk.interfaces.IRandomWalkerHost;

public class RandomWalkerPeerAcquirer implements IRandomWalker 
{
	ArrayList<Long> _visitedPeers = new  ArrayList<Long>(); 
	
	long _senderPeer;
	int _ttlValue;
	long _deathDateTime = 0;
	UUID _walkerUUID;
	RandomWalkerType _walkerType;

	public RandomWalkerPeerAcquirer(int ttlValue, long deathDateTime , long senderPeer, UUID walkerUUID, RandomWalkerType type)
	{
		_senderPeer = senderPeer;
		_ttlValue = ttlValue;
		_deathDateTime = deathDateTime;
		_walkerUUID = walkerUUID;
		_walkerType = type;
	}
	
	
	
	@Override
	public void Visit(IRandomWalkerHost host) 		
	{				
		
		if(host.GetPeerID() == _senderPeer) return; //ignore sender peer
						
		if(!_visitedPeers.contains(host.GetPeerID()))
		{						
			if(host.BeginActionOnReceivedRandomWalkerType(_walkerType, _walkerUUID, _senderPeer ))
			{
				_ttlValue--;
				_visitedPeers.add(host.GetPeerID());
			}			
		}
		
		GlobalLogger.LogWalkerState(" Besuche Peer : "+host.GetPeerID() +  " WalkerSender "+_senderPeer+" _ttlValue "+_ttlValue + " Walker UUID "+_walkerUUID);
		
		if(_deathDateTime <= host.GetCurrentDateTimeStamp())
		{
			GlobalLogger.LogWalkerState("Walker now dead : "+" WalkerSender "+_senderPeer+" Besuche Peer : "+host.GetPeerID() + " _ttlValue "+_ttlValue + " Walker UUID "+_walkerUUID);
						
						
			_isKilled = true;
		}
							
	}

	volatile boolean _isKilled = false;
	public boolean IsKilled()
	{		
		
		return _isKilled || _ttlValue <=0;				
	}
	
	@Override
	public void KillWalker() {		
		_isKilled = true;		
	}
	
	public boolean IsLongHaulWalker()
	{
		//tries to use only short range connections
		return false;
	}


}
