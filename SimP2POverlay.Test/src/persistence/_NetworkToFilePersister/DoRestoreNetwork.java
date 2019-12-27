package persistence._NetworkToFilePersister;
import org.junit.Test;
import launcher.ApplicationModelSettings;
import networkInitializer.smallWorldKleinberg.NetworkSettingsSmallWorldKleinberg;
import networkInitializer.smallWorldKleinberg.SmallWorldKleinbergInitializer;
import peersModel.interfaces.INetworkFacade;
import persistence.NetworkToFilePersister;

public class DoRestoreNetwork {

	
	@Test
	public final void testRestoreNetwork() 
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
							
		
		
		NetworkToFilePersister.DoPersistNetwork(appSettings, "testtile");
		
		//now read the settings .. 
		
		
	}
	
}
