package networkInitializer._ChordInitializer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import networkInitializer.chord.ChordAddress;
import networkInitializer.chord.ChordNetworkInitializer;
import networkInitializer.chord.NetworkSettingsChord;
import peersModel.implementation.Peer;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;

public class SortPeersByIdentifier {

	@Test
	public void ShouldSortEntriesIncreasingByIdentifier() 
	{
		ArrayList<IPeer>  peerList = new ArrayList<IPeer>();
		
		Peer p1 = new Peer();
		p1.SetPeerID(1);
		p1.SetNetworkAdress(new ChordAddress(0,0,1000));
		
		Peer p2 = new Peer();
		p2.SetPeerID(2);
		p2.SetNetworkAdress(new ChordAddress(0,0,3000));
		
		Peer p3 = new Peer();
		p3.SetPeerID(3);
		p3.SetNetworkAdress(new ChordAddress(0,0,2000));
		
		peerList.add(p1);
		peerList.add(p2);
		peerList.add(p3);
		
		
		List<IPeer> sorted = ChordNetworkInitializer.SortPeersIncreasingByIdentifier(peerList);
		
		assertTrue(sorted.get(0).GetPeerID() == 1);
		assertTrue(sorted.get(1).GetPeerID() == 3);
		assertTrue(sorted.get(2).GetPeerID() == 2);
		
	}
	
	@Test
	public void ShouldGenerateNetwork() 
	{
		NetworkSettingsChord settings = new NetworkSettingsChord(12, 10, true);
		ChordNetworkInitializer initializer;
		try {
			initializer = new ChordNetworkInitializer(settings);
			INetworkFacade network = initializer.GetInitializedNetwork();
			
			assertTrue(network.GetPeers().size() == 10);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	;
	}
	

}
