package documentTreeModel.interfaces;

/**
 * This kind of messages is send as broad cast over all peerboxes
 * @author Dimitri
 *
 */
public interface IPeerBoxLoadStateBroadCastMessage extends IPeerBoxBroadCastRequest {

	double[] SourcePosition();
	// aktueller CAPpb
	long GetSourceNewLoadForceAmount();
	
	// alter CAPpb
	long GetSourcePreviousLoadForceAmount();
}
