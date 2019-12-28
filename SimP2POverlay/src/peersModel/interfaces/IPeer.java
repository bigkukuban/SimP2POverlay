package peersModel.interfaces;

import java.util.ArrayList;

public interface IPeer 
{

	//every peer has an unique id, id == 0 is an invalid value 
	long GetPeerID(); 	
	ArrayList<IPeer> GetAllNeighbours();
	
	boolean RemoveNeighbour(IPeer peer);
	boolean AddNeighbour(IPeer peer);
	
	IPeerAdress GetNetworkAdress();
	void SetNetworkAdress(IPeerAdress  newAdress);
						
	//called after all connection initializations are done 
	void OnFinalizedInitialization();
		
}
