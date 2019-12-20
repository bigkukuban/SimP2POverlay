package peersModel.interfaces;

import java.util.UUID;

public interface IPeerCommunicationBroadCastMessage extends IPeerCommunicationMessage{

	public UUID GetBroadCastMessageId();
	public double GetRemainingDTL();
	// copies the object, with decremented TTL
	public IPeerCommunicationBroadCastMessage CloneMe(double targetJumpDistance);
	
}
