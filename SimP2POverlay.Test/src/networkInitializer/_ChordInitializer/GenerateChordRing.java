package networkInitializer._ChordInitializer;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import networkInitializer.chord.ChordAddress;
import networkInitializer.chord.ChordNetworkInitializer;
import networkInitializer.chord.NetworkSettingsChord;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;

public class GenerateChordRing {

	
	
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
	}
	
	
	public void ShouldGenerateStaticNetwork() 
	{
		NetworkSettingsChord settings = new NetworkSettingsChord(5, 31, false);
		ChordNetworkInitializer initializer;
		try {
			initializer = new ChordNetworkInitializer(settings);
			INetworkFacade network = initializer.GetInitializedNetwork();
			
			assertTrue(network.GetPeers().size() == 30);
			
			ArrayList<IPeer> peers  = network.GetPeers();
			
			IPeer pr1 = peers.get(0);			
			ChordAddress addr1 = (ChordAddress)pr1.GetNetworkAdress();
			
			IPeer pr2 = peers.get(1);
			ChordAddress addr2 = (ChordAddress)pr2.GetNetworkAdress();
			
			IPeer pr30 = peers.get(30);
			ChordAddress addr30 = (ChordAddress)pr30.GetNetworkAdress();
			
			
			assertTrue(addr1._identifier== 0);
			assertTrue(addr2._identifier== 1);
			assertTrue(addr30._identifier== 30);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

}
