package documentTreeModel.interfaces;
import java.util.UUID;

import commonHelper.math.interfaces.IVector;


public interface IPeerBox 
{	
	// for common use	
	/**
	 * returns the number of all contained DocumentBoxes, migrated and non-migrated
	 * @return
	 */
	long GetNumberOfAllContainedDocumentBoxes();
	
	long GetDocumentBoxCapacity();
	
	// each peerbox has an own address, the peer - address may be used 	
	long GetPeerBoxAddress();
	
	//to be used by IPeer	
	void PlaceMessageFromOtherPeerBox(IPeerBoxMessage message, long sourcePeerBoxAdress);
		
	IVector GetLoadForce();
	
	void StartExecution();	
	void StopExecution();

	void CleanUpMe();
	
	// to be used by DocumentBox to place outgoing message 	
	void SendMessageToDocumentBox(UUID sourceDocumentBox,
								  UUID targetDocumentBox, 
								  long targetPeerBoxAddress, 
								  IDocumentBoxMessage message);

	void OnFinalizedInitialization();
		
}
