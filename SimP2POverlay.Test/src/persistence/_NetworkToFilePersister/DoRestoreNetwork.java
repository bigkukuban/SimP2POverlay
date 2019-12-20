package persistence._NetworkToFilePersister;

import static org.junit.Assert.*;
import networkInitializer.smallWorldKleinberg.NetworkSettingsSmallWorldKleinberg;
import peersModel.implementation.NetworkFacade;
import persistence.NetworkToFilePersister;

public class DoRestoreNetwork {

	
	//@Test
	public final void testRestoreNetwork() 
	{		
		
							
		NetworkFacade facade = new 	NetworkFacade();
		
		NetworkToFilePersister persister = new  NetworkToFilePersister();
						
		persister.InitializeTargetFile("H:\\output.network");		
		boolean result = persister.DoRestoreNetwork(facade);
		NetworkSettingsSmallWorldKleinberg kleinBergSettings = (NetworkSettingsSmallWorldKleinberg)persister.GetLastRestoredNetworkSettings(); 
					
		assertTrue(result);
		
		assertTrue("Available Size: "+facade.GetPeers().size(),facade.GetPeers().size() == 1600 );
		assertTrue(kleinBergSettings._xLength == 40);
		assertTrue(kleinBergSettings._yLength == 40);
	}
	
}
