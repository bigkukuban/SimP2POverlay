package peersModel.implementation;
import java.util.ArrayList;
import commonHelper.GlobalLogger;
import peersModel.interfaces.IPeer;
import peersModel.interfaces.IPeerAdress;

public class Peer implements IPeer
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

		
	@Override
	public IPeerAdress GetNetworkAdress() {
 
		return _networkAdress;
	}
	@Override
	public void SetNetworkAdress(IPeerAdress newAdress) {

		_networkAdress = newAdress;
	}


	long _myID = 0;
	public void SetPeerID(long id) {
		_myID =  id;
	}
	
	@Override
	public long GetPeerID() {
		return _myID;
	}

	
	@Override
	public void OnFinalizedInitialization() {
 	
	}
	
}

