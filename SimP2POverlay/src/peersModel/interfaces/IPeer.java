package peersModel.interfaces;

import java.util.ArrayList;

import commonHelper.math.interfaces.IVector;
import documentTreeModel.interfaces.IPeerBox;
import documentTreeModel.interfaces.IPeerBoxMessage;
import randomWalk.interfaces.IRandomWalker;

public interface IPeer 
{

	//every peer has an unique id, id == 0 is an invalid value 
	long GetPeerID(); 
	
	ArrayList<IPeer> GetLongRangeNeighbours();
	ArrayList<IPeer> GetNearNeighbours();	
	ArrayList<IPeer> GetAllNeighbours();
	
	boolean RemoveNeighbour(IPeer peer);
	boolean AddNeighbour(IPeer peer, boolean isLongrangeContact);
	
	IPeerAdress GetNetworkAdress();
	void SetNetworkAdress(IPeerAdress  newAdress);
	
	void ResetWalkerChanges();
	void AcceptRandomWalker(IRandomWalker walker);
		
	
	/**
	 * Returns the current load state (e.g. number documentBoxes, )
	 * @return
	 */
	long GetCurrentLoadState();
	
	/**
	 * Direction of load force (e.g. two dimensional vector x, y, z, etc..)
	 * The vector is normalized
	 * @return
	 */
	IVector GetLoadForce();
		
	
	IPeerBox GetPeerBox();
	void SetCommunicationInterface(IPeerCommunication peerCommunication);
	IPeerCommunication GetCommunicationInterface();
	
	
	//services for hold PeerBox
	boolean SendMessageService(long iTargetPeerId, IPeerBoxMessage internalData);
	boolean SendBroadCastMessageService(IPeerBoxMessage internalData, long initialTTL);
	
	void PostMessageFromOtherPeer(long SourcePeerId, IPeerCommunicationMessage peer);

	//called after all connection initializations are done 
	void OnFinalizedInitialization();
		
}
