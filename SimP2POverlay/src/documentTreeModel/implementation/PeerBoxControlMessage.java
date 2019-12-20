package documentTreeModel.implementation;

import peersModel.interfaces.IPeerCommunicationMessage;

public class PeerBoxControlMessage implements IPeerCommunicationMessage
{

	boolean _msg;
	
	public PeerBoxControlMessage(boolean bStartExecution)
	{
		_msg = bStartExecution;
	}
	
	@Override
	public Object GetPrivateData() {

		return _msg;
	}

}
