package documentTreeModel.implementation;

import java.util.UUID;

import documentTreeModel.interfaces.IPeerBoxBroadCastRequest;
import documentTreeModel.interfaces.IPeerBoxLoadStateBroadCastMessage;

public class PeerBoxLoadStateBroadCastMessage implements IPeerBoxLoadStateBroadCastMessage
{
	UUID _requestID; 	
	double [] _sourcePosition;
	long _newLoadForceAmount;
	long _previousLoadForceAmount;	
	
	public PeerBoxLoadStateBroadCastMessage(UUID messageUUID, long newLoadForceAmount,long previousLoadForceAmount, double [] sourcePosition)
	{
		
		_requestID = messageUUID;
		_newLoadForceAmount = newLoadForceAmount;
		_previousLoadForceAmount = previousLoadForceAmount;
		_sourcePosition = sourcePosition;
		
	}

	
	@Override
	public UUID GetRequestUUID() {

		return _requestID;
	}

	@Override
	public Object GetPrivateData() {

		return null;
	}
	

	@Override
	public long GetSourceNewLoadForceAmount() {

		return _newLoadForceAmount;
	}
	
	@Override
	public long GetSourcePreviousLoadForceAmount() {

		return _previousLoadForceAmount;
	}

	@Override
	public IPeerBoxBroadCastRequest CloneMe()
	{
		return new PeerBoxLoadStateBroadCastMessage(UUID.fromString(_requestID.toString()),_newLoadForceAmount,_previousLoadForceAmount,_sourcePosition);
	}


	@Override
	public double[] SourcePosition() {
		return _sourcePosition;
	}



}
