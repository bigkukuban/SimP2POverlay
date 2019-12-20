package documentTreeModel.implementation;

import java.util.UUID;

import documentTreeModel.interfaces.IPeerBoxRequestForceAuthenticationFromDocumentBoxesMessage;

public class PeerBoxRequestForceAuthenticationFromDocumentBoxMessage implements IPeerBoxRequestForceAuthenticationFromDocumentBoxesMessage
{
	
	UUID _msgId = UUID.randomUUID();
	
	@Override
	public UUID GetRequestUUID() {

		return _msgId;
	}

	@Override
	public Object GetPrivateData() {

		return null;
	}
	
}
