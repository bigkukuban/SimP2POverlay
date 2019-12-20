package networkInitializer.interfaces;

import peersModel.interfaces.INetworkFacade;

public interface INetworkInitializer {
	
	INetworkFacade GetInitializedNetwork();
	long GetNumberOfItemsItendedToCreate();
	String GetReadableDescription();
}
