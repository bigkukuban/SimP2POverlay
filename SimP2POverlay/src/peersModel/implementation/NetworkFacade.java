package peersModel.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import commonHelper.GlobalLogger;
import commonHelper.math.EuclideanPoint;
import commonHelper.math.interfaces.IEuclideanPoint;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;


public class NetworkFacade implements INetworkFacade {

	int[] _dimensions;
	
	public NetworkFacade()
	{						
	
	}
	
	/**
	 * Returns the dimensions of the network as array
	 * [10] - one dimenstion with addresses 0-9
	 * [10][10] - two dimenstion with addresses [0-9][0-9]
	 * etc..
	 * @return
	 */
	public int[] GetDimenstions()
	{
		return _dimensions;
	}
	
	public NetworkFacade(ArrayList<IPeer> peers,int[] dimensions)
	{			
		this();
		SetPeers(peers,dimensions);		
	}
	
	private ArrayList<IPeer> _peers;	
	private Map<Long, IPeer> _peerMap;
	
	/* (non-Javadoc)
	 * @see peersModel.implementation.INetworkFacade#SetPeers(java.util.ArrayList)
	 */
	@Override
	public void SetPeers(ArrayList<IPeer> peers,int[] dimensions)
	{
		_peers = peers;
		_dimensions = dimensions;
		_peerMap = new HashMap<Long, IPeer>();
		for(IPeer peer : peers)
		{		
			_peerMap.put(peer.GetPeerID(),peer);
		}
	}

	public IPeer GetPeerById(Long peerId)
	{
		IPeer result =  _peerMap.get(peerId);
		
		if(result == null) return null;
		
		if(result.GetPeerID() != peerId)
		{
			GlobalLogger.LogForces("NetworkFacade: GetPeerById, result.GetPeerID() != peerId, failed");	
		}
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see peersModel.implementation.INetworkFacade#GetPeers()
	 */
	@Override
	public ArrayList<IPeer> GetPeers()
	{
		return _peers;
	}
	
	
	@Override
	public IEuclideanPoint GetPeerPositionWithinEuclideanSpace(long peerId) {
		IEuclideanPoint result = null;
		
		for(IPeer p : GetPeers())
		{
			if(p.GetPeerID() == peerId)
			{
				result = new EuclideanPoint(new double[]{p.GetNetworkAdress().GetPositionX(),p.GetNetworkAdress().GetPositionY()});
			}
		}
		
		return result;
	}
			
}
