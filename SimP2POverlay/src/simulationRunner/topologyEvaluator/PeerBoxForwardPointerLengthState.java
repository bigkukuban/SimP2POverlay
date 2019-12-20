package simulationRunner.topologyEvaluator;

import simulationRunner.topologyEvaluator.interfaces.IPeerBoxForwardPointerLengthState;

public class PeerBoxForwardPointerLengthState implements IPeerBoxForwardPointerLengthState 
{

	public long PeerBoxId = 0;
	public long ForwardPointerCacheId = 0;
	
	@Override
	public long GetPeerBoxId() 
	{
		return PeerBoxId;
	}

	@Override
	public long GetLengthForwardPointerCache() 
	{
		return ForwardPointerCacheId;
	}

}
