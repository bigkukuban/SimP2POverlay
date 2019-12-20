package documentBoxInitializer.interfaces;

import java.util.ArrayList;

import documentTreeModel.interfaces.IDocumentBox;
import peersModel.interfaces.IPeer;

public interface IDocumentBoxNetworkInitializer {
	 ArrayList<IDocumentBox> GetInitializedNetwork(IPeer toPlaceOnPeer) ;
	long GetNumberOfItemsItendedToCreate();
	
	
	String GetReadableDescription();
}
