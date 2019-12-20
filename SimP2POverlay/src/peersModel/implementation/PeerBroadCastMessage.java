package peersModel.implementation;

import java.util.UUID;

import documentTreeModel.interfaces.IPeerBoxBroadCastRequest;
import documentTreeModel.interfaces.IPeerBoxMessage;
import peersModel.interfaces.IPeerCommunicationBroadCastMessage;

public class PeerBroadCastMessage implements IPeerCommunicationBroadCastMessage
{
	Object _internalMessage;
	double iCurrentDTL;
	UUID _messageId;

	/**
	 * 
	 * @param internalMessage message of the higher level
	 * @param initialDTL - DTL-value, distance to live, not ttl (time to live)
	 * @param messageId - unique id for the message
	 */
	public PeerBroadCastMessage(IPeerBoxMessage internalMessage, double initialDTL, UUID messageId)
	{
		_internalMessage = internalMessage;
		iCurrentDTL = initialDTL;
		_messageId = messageId;
	}
	
	@Override
	public Object GetPrivateData() {

		return _internalMessage;
	}

	@Override
	public UUID GetBroadCastMessageId() {
		return _messageId;
	}

	@Override
	public double GetRemainingDTL() {

		return iCurrentDTL;
	}

	@Override
	public IPeerCommunicationBroadCastMessage CloneMe(double targetJumpDistance) 
	{
		
		if(iCurrentDTL - targetJumpDistance < 0)
		{
			return null;
		}
		
		return new PeerBroadCastMessage(((IPeerBoxBroadCastRequest)_internalMessage).CloneMe(),
										iCurrentDTL - targetJumpDistance , UUID.fromString(_messageId.toString()));
	}

}
