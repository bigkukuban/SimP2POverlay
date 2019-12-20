package documentTreeModel.implementation;

import java.util.UUID;

import documentTreeModel.interfaces.IDocumentBoxMessageRequest;

/**
 * Private internal message to begin the authentication (DocumentBox --> DocumentBox)
 * @author Dimitri
 *
 */
public class DocumentBoxMessageBeginAuthenticationRequest implements IDocumentBoxMessageRequest
{
	
	UUID _myUUID = UUID.randomUUID();
	
	@Override
	public UUID GetMessageGuid() {
		// TODO Auto-generated method stub
		return _myUUID;
	}
	
	public DocumentBoxMessageBeginAuthenticationRequest()
	{
	
	}
	
	@Override
	public DocumentBoxMessageType GetMessageType() {

		return DocumentBoxMessageType.BeginAuthentication;
	}	
}