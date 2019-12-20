package peersModel.implementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import commonHelper.GlobalLogger;
import commonHelper.math.EuclideanPoint;
import commonHelper.math.interfaces.IEuclideanPoint;
import documentTreeModel.implementation.PeerBoxRequestChangeNumberOfHoldDocumentBoxesMessage;
import documentTreeModel.implementation.PeerBoxRequestMigrateDocumentBoxMessage;
import documentTreeModel.interfaces.IDocumentBox;
import documentTreeModel.interfaces.IPeerBoxEvaluation;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;
import peersModel.interfaces.IPeerCommunication;

public class NetworkFacade implements INetworkFacade {

	int[] _dimensions;
	IPeerCommunication _peerCommunication;
	public NetworkFacade()
	{						
		_peerCommunication = new PeerCommunication(this);
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
			peer.SetCommunicationInterface(_peerCommunication);
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
	
	

	public void InitializeDocumentBoxesOnPeers(ArrayList<IDocumentBox> docBoxes,IPeer peer)
	{										
		for(IDocumentBox bx : docBoxes)
		{					
			//Create message migrating documentBox 			
			PeerBoxRequestMigrateDocumentBoxMessage peerBoxMsg = new PeerBoxRequestMigrateDocumentBoxMessage(UUID.randomUUID(),bx,bx.GetDocumentBoxUUID());									
			PeerMessageForPeerBox peerMsg = new  PeerMessageForPeerBox(peerBoxMsg);		
			
			_peerCommunication.SendMessageToPeer(peer.GetPeerID(), peerMsg, peer.GetPeerID());						
		}
	}
	
	public void ChangeCapacityOfPeerBox(long value, long peerBoxId)
	{
		PeerBoxRequestChangeNumberOfHoldDocumentBoxesMessage peerBoxMsg = new PeerBoxRequestChangeNumberOfHoldDocumentBoxesMessage(value);	
		PeerMessageForPeerBox peerMsg = new  PeerMessageForPeerBox(peerBoxMsg);										
		_peerCommunication.SendMessageToPeer(peerBoxId, peerMsg, peerBoxId);
	}
	
	public void ChangeCapacityOfPeerBoxes(long value)
	{				
		for(IPeer p : GetPeers())
		{						
			PeerBoxRequestChangeNumberOfHoldDocumentBoxesMessage peerBoxMsg = new PeerBoxRequestChangeNumberOfHoldDocumentBoxesMessage(value);									
			PeerMessageForPeerBox peerMsg = new  PeerMessageForPeerBox(peerBoxMsg);								
			//p.PostMessageFromOtherPeer(p.GetPeerID(), peerMsg);	
			
			_peerCommunication.SendMessageToPeer(p.GetPeerID(), peerMsg, p.GetPeerID());
		}
	}

	@Override
	public void CleanUp() 
	{
		_peerCommunication.CleanUp();
	}

	@Override
	public Collection<IDocumentBox> GetAllDocumentBoxes() 
	{
		ArrayList<IDocumentBox> resultingList =new ArrayList<IDocumentBox>(); 		
		
		for(IPeer p : GetPeers())
		{
			IPeerBoxEvaluation ipbEval = (IPeerBoxEvaluation) p.GetPeerBox();
			
			for(IDocumentBox bx : ipbEval.GetListOfAssignedDocumentBoxes())
			{			
				resultingList.add(bx);
			}	
		}		
						
		return resultingList;
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
