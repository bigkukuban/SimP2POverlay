package peersModel.interfaces;

import java.util.ArrayList;
import java.util.Collection;

import commonHelper.interfaces.IPeerIdIntoEuclideanAdressConverter;
import documentTreeModel.interfaces.IDocumentBox;

public interface INetworkFacade extends IPeerIdIntoEuclideanAdressConverter {

	void SetPeers(ArrayList<IPeer> peers,int[] dimensions);

	ArrayList<IPeer> GetPeers();
	
	int[] GetDimenstions();
	
	IPeer GetPeerById(Long peerId);
	
	Collection<IDocumentBox> GetAllDocumentBoxes(); 
				
	void InitializeDocumentBoxesOnPeers(ArrayList<IDocumentBox> docBoxes,IPeer peer);
	
	void ChangeCapacityOfPeerBoxes(long value);
	void ChangeCapacityOfPeerBox(long value, long peerId);
	
	void CleanUp();
			
}