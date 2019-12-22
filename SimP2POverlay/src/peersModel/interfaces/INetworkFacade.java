package peersModel.interfaces;

import java.util.ArrayList;

import commonHelper.interfaces.IPeerIdIntoEuclideanAdressConverter;


public interface INetworkFacade extends IPeerIdIntoEuclideanAdressConverter {

	void SetPeers(ArrayList<IPeer> peers,int[] dimensions);

	ArrayList<IPeer> GetPeers();
	
	int[] GetDimenstions();
	
	IPeer GetPeerById(Long peerId);
		 						
}