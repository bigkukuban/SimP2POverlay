package documentTreeModel.implementation;

import java.util.UUID;

import documentTreeModel.interfaces.IDocumentBoxMessageRequest;

/**
 * Private internal message to notify PeerBox about successful authentication of a specific connection.
 * This message will only be used by the PeerBox, not forwarded to any DocumentBox
 * @author Dimitri
 *
 */
public class DocumentBoxMessageAuthenticationDone implements IDocumentBoxMessageRequest
{
	
	UUID _myUUID = UUID.randomUUID();
	
	@Override
	public UUID GetMessageGuid() {
		// TODO Auto-generated method stub
		return _myUUID;
	}
	
	long _systemTimeOfAuthenticationDuration = 0;
	
	public DocumentBoxMessageAuthenticationDone(long systemTimeOfAuthenticationDuration)
	{
		_systemTimeOfAuthenticationDuration = systemTimeOfAuthenticationDuration;
	}
	
	
	
	public long GetSystemTimeOfAuthenticationDuration()
	{
		return _systemTimeOfAuthenticationDuration;
	}
	
	@Override
	public DocumentBoxMessageType GetMessageType() {

		return DocumentBoxMessageType.AuthenticationDone;
	}	
}