package peersModel.implementation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import commonHelper.GlobalLogger;
import commonHelper.GlobalTools;
import commonHelper.math.RandomUtilities;
import commonHelper.math.interfaces.IVector;
import documentTreeModel.implementation.PeerBox;
import documentTreeModel.implementation.PeerBoxControlMessage;
import documentTreeModel.interfaces.IPeerBox;
import documentTreeModel.interfaces.IPeerBoxMessage;
import peersModel.interfaces.IPeer;
import peersModel.interfaces.IPeerAdress;
import peersModel.interfaces.IPeerCommunication;
import peersModel.interfaces.IPeerCommunicationBroadCastMessage;
import peersModel.interfaces.IPeerCommunicationMessage;
import randomWalk.interfaces.IRandomWalker;
import randomWalk.interfaces.IRandomWalker.RandomWalkerType;
import randomWalk.interfaces.IRandomWalkerHost;

public class Peer implements IPeer, IRandomWalkerHost, Runnable
{
	
	public Peer()
	{				
		_listFarNeighbours = new ArrayList<IPeer>();
		_listNearNeighbours = new ArrayList<IPeer>();		
		
		
		GlobalLogger.LogBroadCasts("BroadCast-Debug: Peer ( "  + this.GetPeerID() +" )");
	}
		
	private IPeerBox _containedPeerBox = null; //<-- hier weiter ... 
	private IPeerAdress _networkAdress;
	
	//todo: the peer should not reference it's neigbours, use the peer addresses or ids.
	private ArrayList<IPeer> _listNearNeighbours;
	private ArrayList<IPeer> _listFarNeighbours;
	
	private BlockingQueue<ReceivedMessage> _lstIncommingMessages = new LinkedBlockingQueue<ReceivedMessage>();
		
	//broadcast fifo , may be one of important parameters
	private BroadCastFifoQueueHandler _broadCastFifo = new BroadCastFifoQueueHandler();
	
				
	
	public IPeerBox GetPeerBox()
	{
		
		if(_containedPeerBox == null)
		{
			_containedPeerBox = new PeerBox(this);
		}
		
		return _containedPeerBox;		
	}
	
	
	private boolean IsPeerAlreadyNeighbour(IPeer other)
	{
		if(_listFarNeighbours.contains(other)) return true;
		
		if(_listNearNeighbours.contains(other)) return true;
		
		return false;
	}
	
	public boolean RemoveNeighbour(IPeer peer)
	{
		 boolean result = _listFarNeighbours.remove(peer);		 		 
		 result = result || _listNearNeighbours.remove(peer);
		 
		 return result;
	}
	
	public boolean AddNeighbour(IPeer peer, boolean isLongrangeContact)
	{
		
		if(IsPeerAlreadyNeighbour(peer)) return false;
				
		if(isLongrangeContact)
		{
			return _listFarNeighbours.add(peer);			
		}
		
		return _listNearNeighbours.add(peer);
	}
	
	
	public ArrayList<IPeer> GetNearNeighbours() 
	{
		ArrayList<IPeer> listNeighbours = new ArrayList<IPeer>();
		listNeighbours.addAll(_listNearNeighbours);
		return listNeighbours;
	}
	
	
	public ArrayList<IPeer> GetLongRangeNeighbours() 
	{
		ArrayList<IPeer> listNeighbours = new ArrayList<IPeer>();
		listNeighbours.addAll(_listFarNeighbours);
		return listNeighbours;
	}
	
	public ArrayList<IPeer> GetAllNeighbours() 
	{
		ArrayList<IPeer> listNeighbours = new ArrayList<IPeer>(_listFarNeighbours.size()+_listNearNeighbours.size());
		listNeighbours.addAll(_listFarNeighbours);
		listNeighbours.addAll(_listNearNeighbours);
		
		return listNeighbours;
	}
	
	/**
	 * Real random walk independent of the distance
	 */
	public IRandomWalkerHost SelectByRandomOneNeighbour()
	{
		ArrayList<IPeer> allNeighbours = GetAllNeighbours();

		if(allNeighbours.size() == 0) return null;
		
		IPeer result = null;
		
		do{
			result = RandomUtilities.SelectOneByRandomFromList(allNeighbours);
		}while(!(result instanceof IRandomWalkerHost));
		
		
		return (IRandomWalkerHost)result;
	}
	
	
	/**
	 * Consider distance
	 * @throws Exception 
	 */
	public IRandomWalkerHost SelectByRandomConsiderDistance(Double proprtionalityConstant) throws Exception
	{
	
		if(proprtionalityConstant >= 0)
		{
			throw new Exception("wrong constant given, this should be smaler than 0, e.g. -2");			
		}
		
		
		//get avaialbe distances
		ArrayList<IPeer> allNeighbours = GetAllNeighbours();
		
		Map<Integer, ArrayList<IPeer>> distances = new HashMap<Integer, ArrayList<IPeer>>(); 
		Map<Integer, Double> distancesProbability = new HashMap<Integer, Double>();
		Map<Integer, Double> distancesProbabilityDistribution = new HashMap<Integer, Double>();
		Double sumProportionality = 0.0;
		for(IPeer peer : allNeighbours)
		{
			int distance = peer.GetNetworkAdress().GetDistance(this.GetNetworkAdress());
			if(!distances.containsKey(distance))
			{
				distances.put(distance, new ArrayList<IPeer>());
			}			
			distances.get(distance).add(peer);		
			//collect all distances
			distancesProbability.put(distance, 0.0);
		}
		
		//calculate not normalized distances
		for( Integer distance: distancesProbability.keySet())
		{			
			Double proportionality = Math.pow(distance, proprtionalityConstant);
			distancesProbability.put(distance, proportionality);
			sumProportionality = sumProportionality + proportionality;	
		}
		
		//normalize to 0..1
		for( Integer distance: distancesProbability.keySet()){
			
			Double val = distancesProbability.get(distance) / sumProportionality;
			distancesProbability.put(distance, val);
		}
		
		Double initial =0.0;
		for( Integer distance: distancesProbability.keySet()){
			initial = initial + distancesProbability.get(distance);			
			distancesProbabilityDistribution.put(distance,initial );
		}
						
		
		//now calculate a random number and select the most near distance by its probability
		Double randVariable = Math.random();
		int iSelectedDistance = (int) distancesProbability.keySet().toArray()[0];
		for( Integer distance: distancesProbabilityDistribution.keySet()){
			if( Math.abs( distancesProbabilityDistribution.get(distance) - randVariable) < Math.abs( distancesProbabilityDistribution.get(iSelectedDistance) - randVariable))
			{
				iSelectedDistance = distance;
			}
		}
		//now by next var select the according target-peer					
		return (IRandomWalkerHost) RandomUtilities.SelectOneByRandomFromList(distances.get(iSelectedDistance));

	}
	
	public void ResetWalkerChanges()
	{
		SetHostColor(0xFFFFFF);
	}
	
	
	@Override
	public IPeerAdress GetNetworkAdress() {
 
		return _networkAdress;
	}
	@Override
	public void SetNetworkAdress(IPeerAdress newAdress) {

		_networkAdress = newAdress;
	}

	
	/**
	 * Interface provides a service for PeerBox to send point-to-point messages
	 * @param iTargetPeerId
	 * @param internalData
	 * @return
	 */	
	public boolean SendMessageService(long iTargetPeerId, IPeerBoxMessage internalData)
	{
					
		PeerMessageForPeerBox peerMessage = new PeerMessageForPeerBox(internalData);				
		ReceivedMessageFromPeerBox msg = new ReceivedMessageFromPeerBox(peerMessage,iTargetPeerId);
		
		_lstIncommingMessages.add(msg);	
		
		return true;						
	}
	
	/**
	 * Interface provides a service for PeerBox to send broad cast messages
	 * @param internalData
	 * @return
	 */
	public boolean SendBroadCastMessageService(IPeerBoxMessage internalData, long initialTTL)
	{					
		UUID newMessageId = UUID.randomUUID();				
		PeerBroadCastMessage broadCastMessage = new PeerBroadCastMessage(internalData,initialTTL,newMessageId); 						
		ReceivedBroadCastMessageFromPeerBox msg = new ReceivedBroadCastMessageFromPeerBox(broadCastMessage);
								
		_lstIncommingMessages.add(msg);			
		return true;
	}
	
	//Interface for Communication for placing of incomming messages
	@Override
	public void PostMessageFromOtherPeer(long SourcePeerId,IPeerCommunicationMessage peerMsg) 
	{
		
		if(_lstIncommingMessages.remainingCapacity() <=0)
		{
			GlobalLogger.LogCapacityChange("BroadCast-Debug: Peer Blockign queue has reached it's max. capacity ....:"+_lstIncommingMessages.remainingCapacity());	
		}				
		
		if (peerMsg instanceof PeerMessageForPeerBox)
		{		
			ReceivedPeerMessageFromOtherPeer msgIn = new ReceivedPeerMessageFromOtherPeer(SourcePeerId,peerMsg);			
			_lstIncommingMessages.add(msgIn);					
		}
					
				
		if(peerMsg instanceof PeerBoxControlMessage )
		{
			PeerBoxControlMessage msg = (PeerBoxControlMessage)peerMsg;			
			ReceivedPeerControllMessageFromOtherPeer msgIn = new ReceivedPeerControllMessageFromOtherPeer(msg); 			
			_lstIncommingMessages.add(msgIn);			
		}
		
		
		if (peerMsg instanceof IPeerCommunicationBroadCastMessage)
		{		
			ReceivedPeerMessageFromOtherPeer msgIn = new ReceivedPeerMessageFromOtherPeer(SourcePeerId,peerMsg); 			
			_lstIncommingMessages.add(msgIn);					
		}
		  				
	}
	
	
	/**
	 * This method should not block. 
	 * It receives an random walker, starts new Thread 
	 * for RandomWalker-Execution and after execution forwards the 
	 * walker to the next randomly choosen peer 
	 */
	@Override	
	public synchronized void AcceptRandomWalker(IRandomWalker walker) 
	{
		//this function will be always entered from a foreign thread		
				
		WalkerExecuter walkerExecuter = new WalkerExecuter(walker, this);		
		(new Thread(walkerExecuter)).start();
	}

	
	Object synchronizedWalker = new Object();  
	int _iColor = 0xFFFFFF;
	
	@Override
	public void SetHostColor(int iColor) {		
		synchronized(synchronizedWalker)
		{			
			_iColor = iColor;	
		}					
	}

	@Override
	public int GetHostColorSetByWalker()
	{
		return _iColor;
	}
	
	@Override
	public int GetHostColor() 
	{
		return (int) (0xFFFF00 + 255*GetPeerBox().GetDocumentBoxCapacity()); 			
	}

	
	long _myID = 0;
	public void SetPeerID(long id) {
		_myID =  id;
	}
	
	@Override
	public long GetPeerID() {
		return _myID;
	}

	boolean _bPeerBoxStarted = false;
	boolean _bPeerStarted = false;

	
	
	public void StartExecution() 
	{
		GlobalTools.GetTaskExecutor().RegisterNewExecutable(this);	
	}

	public void StopExecution() 
	{
		GlobalTools.GetTaskExecutor().UnRegisterNewExecutable(this);
	}


	class ReceivedMessage {
		// either from a document - box or other peer-box
	

	}
	
	class ReceivedMessageFromPeerBox extends ReceivedMessage
	{
		public ReceivedMessageFromPeerBox(PeerMessageForPeerBox msg, long targetPeerId)
		{
			_message = msg;
			_targetPeerId = targetPeerId;
		}
		
		long _targetPeerId;
		PeerMessageForPeerBox _message;
		
		public long TargetPeerId(){
			return _targetPeerId; 
		}
		
		public PeerMessageForPeerBox ContainedMessage(){
			return _message; 
		}
			
	}
	

	class ReceivedBroadCastMessageFromPeerBox extends ReceivedMessage
	{
		PeerBroadCastMessage _message;
		
		public ReceivedBroadCastMessageFromPeerBox(PeerBroadCastMessage msg)
		{
			_message = msg;
		}
		
		public PeerBroadCastMessage ContainedMessage(){
			return _message; 
		}
			
	}

	
	class ReceivedPeerMessageFromOtherPeer extends ReceivedMessage
	{
		IPeerCommunicationMessage _message;
		long _sourcePeerId;
		public ReceivedPeerMessageFromOtherPeer(long sourcePeerId,IPeerCommunicationMessage msg)
		{
			_message = msg;
			_sourcePeerId = sourcePeerId;			
		}
		
		public long GetSourcePeerId()
		{
			return _sourcePeerId;
		}
		
		public IPeerCommunicationMessage ContainedMessage(){
			return _message; 
		}
			
	}
	
	class ReceivedPeerControllMessageFromOtherPeer extends ReceivedMessage
	{
		PeerBoxControlMessage _message;
		
		public ReceivedPeerControllMessageFromOtherPeer(PeerBoxControlMessage msg)
		{
			_message = msg;
		}
		
		public PeerBoxControlMessage ContainedMessage(){
			return _message; 
		}
			
	}
	
	
	IPeerCommunication _peerCommunication = null;
	
	@Override
	public IPeerCommunication GetCommunicationInterface()
	{
		return _peerCommunication;
	}
	
	@Override
	public void SetCommunicationInterface(IPeerCommunication peerCommunication) 
	{					
		_peerCommunication = peerCommunication;
	}

	@Override
	public boolean BeginActionOnReceivedRandomWalkerType(RandomWalkerType type, UUID walkerId, long walkerSender) 
	{
		return true;						
	}

	@Override
	public long GetCurrentDateTimeStamp() {
		return System.currentTimeMillis();				
	}


	@Override
	public void OnFinalizedInitialization() 
	{
		GetPeerBox().OnFinalizedInitialization();
	
		StartExecution();
	}


	
	
	@Override
	public void run() {

		ReceivedMessage msg = null;;
		do{
			
			msg = _lstIncommingMessages.poll();
			
			
			if(msg instanceof ReceivedPeerMessageFromOtherPeer)
			{
				ReceivedPeerMessageFromOtherPeer otherPeerMsg = (ReceivedPeerMessageFromOtherPeer)msg;
				
				if(!_bPeerBoxStarted)
				{
					GlobalLogger.LogBroadCasts("BroadCast-Debug Starte PeerBox");
					GetPeerBox().StartExecution();
					_bPeerBoxStarted = true;				
				}
				
				
				//check if a simple broad cast
				if(otherPeerMsg.ContainedMessage() instanceof IPeerCommunicationBroadCastMessage)
				{
					// check if the broadcast was received in prior cycles and if the TTL has expired, forward it to all neighbors
					//GlobalLogger.LogBroadCasts("BroadCast-Debug: Peer ( "  + this.GetPeerID() +" ) received broadcast");
					IPeerCommunicationBroadCastMessage broadCastMessage = (IPeerCommunicationBroadCastMessage) otherPeerMsg.ContainedMessage();
															
					if(_broadCastFifo.ShouldHandleMessage(broadCastMessage.GetBroadCastMessageId(), broadCastMessage.GetRemainingDTL()))
					{
						GlobalLogger.LogForces("BroadCast-Debug: Peer ( "  + this.GetPeerID() +" ) , HashCode: " + this.hashCode() + " DTL:"+broadCastMessage.GetRemainingDTL()+" UUID "+broadCastMessage.GetBroadCastMessageId());
						
						GetPeerBox().PlaceMessageFromOtherPeerBox((IPeerBoxMessage)broadCastMessage.GetPrivateData(), otherPeerMsg.GetSourcePeerId());												
					}
					
					if(_broadCastFifo.ShouldForwardMessage(broadCastMessage.GetBroadCastMessageId(), broadCastMessage.GetRemainingDTL()))
					{
						ArrayList<IPeer> allNeighbours = GetAllNeighbours();
																	
						GlobalLogger.LogBroadCasts("BroadCast-Debug: Peer ( "  + this.GetPeerID() +" ) , HashCode: " + this.hashCode() +"forwards the broadcast to " + allNeighbours.size() + " neighbours, remaining TTL:"+broadCastMessage.GetRemainingDTL()+" UUID+"+broadCastMessage.GetBroadCastMessageId());
						
						for(IPeer neighbour : allNeighbours)
						{
							double distance = neighbour.GetNetworkAdress().GetPoint().GetDistanceToOther(GetNetworkAdress().GetPoint());
							
							IPeerCommunicationBroadCastMessage msgToSend = broadCastMessage.CloneMe(distance);
							if(msgToSend != null)
							{
								GetCommunicationInterface().SendMessageToPeer(neighbour.GetPeerID(), msgToSend,_myID);	
							}														
						}								
						_broadCastFifo.MessageBroadCasted(broadCastMessage.GetBroadCastMessageId(), broadCastMessage.GetRemainingDTL());																			
					}													
					
				} else 
				{					
					
					GetPeerBox().PlaceMessageFromOtherPeerBox((IPeerBoxMessage)otherPeerMsg.ContainedMessage().GetPrivateData(), otherPeerMsg.GetSourcePeerId());	
				}
																	
			}
			
			if(msg instanceof ReceivedPeerControllMessageFromOtherPeer)
			{
				ReceivedPeerControllMessageFromOtherPeer otherPeerMsg = (ReceivedPeerControllMessageFromOtherPeer)msg;
				
				if((boolean)(otherPeerMsg.ContainedMessage().GetPrivateData()) == true)
				{
					GetPeerBox().StartExecution();				
				} else 
				{
					GetPeerBox().StopExecution();	
				}								
			}
						
			if(msg instanceof ReceivedMessageFromPeerBox)
			{
				ReceivedMessageFromPeerBox msgIn =  (ReceivedMessageFromPeerBox )msg;
				
				GetCommunicationInterface().SendMessageToPeer(msgIn.TargetPeerId(), msgIn.ContainedMessage(),_myID);
			}
			
			
			if(msg instanceof ReceivedBroadCastMessageFromPeerBox)
			{								
				//initial broadcast request from PeerBox, send the message to other.
				
				ReceivedBroadCastMessageFromPeerBox msgIn =  (ReceivedBroadCastMessageFromPeerBox)msg;	
				
				_broadCastFifo.MessageBroadCasted(msgIn.ContainedMessage().GetBroadCastMessageId(), msgIn.ContainedMessage().GetRemainingDTL());				
				
				ArrayList<IPeer> allNeighbours = GetAllNeighbours();
				GlobalLogger.LogBroadCasts("BroadCast-Debug: Peer ( "  + this.GetPeerID() +" ) initial broadcast forwarding to "+allNeighbours.size()+" neighbours, remaining TTL "+ msgIn.ContainedMessage().GetRemainingDTL()+" Broad cast id"+  msgIn.ContainedMessage().GetBroadCastMessageId());

				for(IPeer neighbour : allNeighbours)
				{
					
					long distance = neighbour.GetNetworkAdress().GetDistance(GetNetworkAdress());
					
					IPeerCommunicationBroadCastMessage msgToSend = msgIn.ContainedMessage().CloneMe(distance);
					if(msgToSend != null)
					{
						GetCommunicationInterface().SendMessageToPeer(neighbour.GetPeerID(), msgToSend,_myID);	
					}																								
				}																				
			}
												

		}while(msg != null);
	}


	@Override
	public long GetCurrentLoadState() {

		return GetPeerBox().GetNumberOfAllContainedDocumentBoxes();
	}


	@Override
	public IVector GetLoadForce()
	{		
		return GetPeerBox().GetLoadForce();
	}


	
	
}

