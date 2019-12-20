package peersModel.implementation;

import peersModel.interfaces.IPeer;
import randomWalk.interfaces.IRandomWalker;
import randomWalk.interfaces.IRandomWalkerHost;

public class WalkerExecuter implements Runnable
{

	IRandomWalker _walker;
	IRandomWalkerHost _host;
	public WalkerExecuter(IRandomWalker walker, IRandomWalkerHost host)
	{
		_walker = walker;
		_host = host;
	}
	
	@Override
	public void run() {
		
		_walker.Visit(_host);
				
		IRandomWalkerHost newHost = null;
		if(_walker.IsLongHaulWalker())
		{
			newHost = _host.SelectByRandomOneNeighbour();
			
		} else 
		{
			try {
				newHost = _host.SelectByRandomConsiderDistance(-50.0);
			} catch (Exception e) {
				e.printStackTrace();				
			}
		}				
		
		
		IPeer nextPeer = (IPeer) newHost;
		if(nextPeer == null)
		{
			//der Walker darf aber so nicht sterben ...
			return;
		}
					
		if(_walker.IsKilled())
		{
			//killed, 
			return;
		}
		
		nextPeer.AcceptRandomWalker(_walker);
	}

}
