package persistence.interfaces;

import peersModel.interfaces.INetworkFacade;


public interface IPersister {
	
	boolean DoPersistNetwork(INetworkFacade facade,networkInitializer.NetworkSettingsBase settings);
	boolean DoRestoreNetwork(INetworkFacade facade);
	networkInitializer.NetworkSettingsBase GetLastRestoredNetworkSettings();	
	
}
