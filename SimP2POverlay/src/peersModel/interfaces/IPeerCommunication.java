package peersModel.interfaces;

public interface IPeerCommunication {
	
	/**
	 * Interface used by the peer to send message to other peers within network
	 * @param iTargetPeerId - id of the target peer
	 * @param msg - message to send
	 * @return
	 */
	boolean SendMessageToPeer(long iTargetPeerId, IPeerCommunicationMessage msg, long iSourcePeerId);	
	
	
	void CleanUp();
	
}
