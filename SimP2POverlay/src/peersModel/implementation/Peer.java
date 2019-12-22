package peersModel.implementation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import commonHelper.GlobalLogger;
import commonHelper.math.RandomUtilities;
import peersModel.interfaces.IPeer;
import peersModel.interfaces.IPeerAdress;
import randomWalk.interfaces.IRandomWalker;
import randomWalk.interfaces.IRandomWalker.RandomWalkerType;
import randomWalk.interfaces.IRandomWalkerHost;

public class Peer implements IPeer, IRandomWalkerHost
{
	
	public Peer()
	{				
		_listNeighbours = new ArrayList<IPeer>();		
				
		GlobalLogger.LogBroadCasts("BroadCast-Debug: Peer ( "  + this.GetPeerID() +" )");
	}
		
	
	private IPeerAdress _networkAdress;
	
	//todo: the peer should not reference it's neigbours, use the peer addresses or ids.	;
	private ArrayList<IPeer> _listNeighbours;
	
		
	
	private boolean IsPeerAlreadyNeighbour(IPeer other)
	{
		if(_listNeighbours.contains(other)) return true;
		
		
		return false;
	}
	
	public boolean RemoveNeighbour(IPeer peer)
	{
		 boolean result = _listNeighbours.remove(peer);		 		 		 		 
		 return result;
	}
	
	@Override
	public boolean AddNeighbour(IPeer peer)
	{
		
		if(IsPeerAlreadyNeighbour(peer)) return false;
							
		return _listNeighbours.add(peer);
	}
	
	
	
	public ArrayList<IPeer> GetAllNeighbours() 
	{
		ArrayList<IPeer> listNeighbours = new ArrayList<IPeer>(_listNeighbours.size());
		listNeighbours.addAll(_listNeighbours);		
		
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
		return (int) (0xFFFF00 + 255); 			
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
	public void OnFinalizedInitialization() {
 	
	}
	
}

