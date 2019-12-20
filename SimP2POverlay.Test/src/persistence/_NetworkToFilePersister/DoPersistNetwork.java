package persistence._NetworkToFilePersister;

import static org.junit.Assert.*;
import networkInitializer.smallWorldKleinberg.NetworkSettingsSmallWorldKleinberg;
import networkInitializer.smallWorldKleinberg.SmallWorldKleinbergInitializer;
import peersModel.interfaces.INetworkFacade;
import persistence.NetworkToFilePersister;

public class DoPersistNetwork {

	//@ignore
	//@Test
	public final void testPersistNetworkSimple() 
	{		
				
		NetworkSettingsSmallWorldKleinberg settings = new NetworkSettingsSmallWorldKleinberg();
		
		settings._qParameter = 2;
		settings._pPParameter = 1;
		settings._rParameter = 2.0;
		settings._xLength = 40;
		settings._yLength = 40;
		
			
		SmallWorldKleinbergInitializer creator = new SmallWorldKleinbergInitializer(settings);		
		INetworkFacade facade = creator.GetInitializedNetwork();
					
		assertTrue("Available Size: "+facade.GetPeers().size(),facade.GetPeers().size() == settings._xLength *settings._yLength );
		
		NetworkToFilePersister persister = new  NetworkToFilePersister();
						
		persister.InitializeTargetFile("H:\\Networks\\output.network");		
		boolean result = persister.DoPersistNetwork(facade, settings);
		
		assertTrue(result);
	}
	
	
	//@Ignore
	//@Test
	public final void testPersistNetwork100x100() 
	{		
				
		NetworkSettingsSmallWorldKleinberg settings = new NetworkSettingsSmallWorldKleinberg();
		
		settings._qParameter = 2;
		settings._pPParameter = 1;
		settings._rParameter = 2.0;
		settings._xLength = 100;
		settings._yLength = 100;
		
		SmallWorldKleinbergInitializer creator = new SmallWorldKleinbergInitializer(settings);		
		INetworkFacade facade = creator.GetInitializedNetwork();
						
		NetworkToFilePersister persister = new  NetworkToFilePersister();
						
		persister.InitializeTargetFile("H:\\Networks\\output100x100.network");		
		boolean result = persister.DoPersistNetwork(facade,settings);
		
		assertTrue(result);
	}
	
	//@Test
	//@Ignore
	public final void testPersistNetwork333x333Q1P1R2() 
	{		
				
		
		NetworkSettingsSmallWorldKleinberg settings = new NetworkSettingsSmallWorldKleinberg();
		
		settings._qParameter = 1;
		settings._pPParameter = 1;
		settings._rParameter = 2.0;
		settings._xLength = 333;
		settings._yLength = 333;
		
		SmallWorldKleinbergInitializer creator = new SmallWorldKleinbergInitializer(settings);		
		INetworkFacade facade = creator.GetInitializedNetwork();
						
		NetworkToFilePersister persister = new  NetworkToFilePersister();
						
		persister.InitializeTargetFile("H:\\Networks\\output333x333Q1P1R2.network");		
		boolean result = persister.DoPersistNetwork(facade,settings);
		
		assertTrue(result);
	}
	
	//@Test
	//@Ignore
	public final void testPersistNetwork330x330Q2P1R2() 
	{		
		
		NetworkSettingsSmallWorldKleinberg settings = new NetworkSettingsSmallWorldKleinberg();
		
		settings._qParameter = 2;
		settings._pPParameter = 1;
		settings._rParameter = 2.0;
		settings._xLength = 330;
		settings._yLength = 330;
		
				
		SmallWorldKleinbergInitializer creator = new SmallWorldKleinbergInitializer(settings);		
		INetworkFacade facade = creator.GetInitializedNetwork();
						
		NetworkToFilePersister persister = new  NetworkToFilePersister();
						
		persister.InitializeTargetFile("H:\\Networks\\330x330Q2P1R2.network");		
		boolean result = persister.DoPersistNetwork(facade,settings);
		
		assertTrue(result);
	}
	
	//@Test
	//@Ignore
	public final void testPersistNetwork330x330Q1P1R2() 
	{		
		
		NetworkSettingsSmallWorldKleinberg settings = new NetworkSettingsSmallWorldKleinberg();
		
		settings._qParameter = 1;
		settings._pPParameter = 1;
		settings._rParameter = 2.0;
		settings._xLength = 330;
		settings._yLength = 330;
		
				
		SmallWorldKleinbergInitializer creator = new SmallWorldKleinbergInitializer(settings);		
		INetworkFacade facade = creator.GetInitializedNetwork();
						
		NetworkToFilePersister persister = new  NetworkToFilePersister();
						
		persister.InitializeTargetFile("H:\\Networks\\330x330Q1P1R2.network");		
		boolean result = persister.DoPersistNetwork(facade,settings);
		
		assertTrue(result);
	}
	
	//@Test
	//@Ignore
	public final void testPersistNetwork200x200Q1P1R2() 
	{		
		
		NetworkSettingsSmallWorldKleinberg settings = new NetworkSettingsSmallWorldKleinberg();
		
		settings._qParameter = 1;
		settings._pPParameter = 1;
		settings._rParameter = 2.0;
		settings._xLength = 200;
		settings._yLength = 200;
		
				
		SmallWorldKleinbergInitializer creator = new SmallWorldKleinbergInitializer(settings);		
		INetworkFacade facade = creator.GetInitializedNetwork();
						
		NetworkToFilePersister persister = new  NetworkToFilePersister();
						
		persister.InitializeTargetFile("H:\\Networks\\200x200Q1P1R2.network");		
		boolean result = persister.DoPersistNetwork(facade,settings);
		
		assertTrue(result);
	}
	
}
