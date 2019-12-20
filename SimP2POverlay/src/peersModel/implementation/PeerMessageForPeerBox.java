package peersModel.implementation;

import documentTreeModel.interfaces.IPeerBoxMessage;
import peersModel.interfaces.IPeerCommunicationMessage;

public class PeerMessageForPeerBox implements IPeerCommunicationMessage
{

	IPeerBoxMessage _msg;
	
	public PeerMessageForPeerBox(IPeerBoxMessage msg)
	{
		_msg = msg;
	}
	
	@Override
	public Object GetPrivateData() {

		return _msg;
	}

}
