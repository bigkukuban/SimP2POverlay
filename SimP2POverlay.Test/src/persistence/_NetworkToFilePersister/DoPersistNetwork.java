package persistence._NetworkToFilePersister;

import static org.junit.Assert.*;

import org.junit.Test;

import launcher.ApplicationModelSettings;
import networkInitializer.smallWorldKleinberg.NetworkSettingsSmallWorldKleinberg;
import networkInitializer.smallWorldKleinberg.SmallWorldKleinbergInitializer;
import peersModel.interfaces.INetworkFacade;
import persistence.NetworkToFilePersister;
import persistence.PersistenceContainer;

public class DoPersistNetwork {

	//@ignore
	@Test
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
		
		ApplicationModelSettings appSettings = new ApplicationModelSettings();
		
		appSettings.AllGraphSettings.add(settings);
		appSettings.ActiveSettings = settings;
		appSettings.NetworkFacade = facade;
		PersistenceContainer container = NetworkToFilePersister.ToPersistenceContainer(appSettings);
		
		assertTrue(container.NetworkSpecialSettings.size() == 1);
		assertTrue(container.peerList.size() == settings._xLength * settings._yLength);			
	}
	

	
}
