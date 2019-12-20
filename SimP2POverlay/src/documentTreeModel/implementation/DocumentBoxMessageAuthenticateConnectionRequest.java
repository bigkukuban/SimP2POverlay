package documentTreeModel.implementation;

import java.util.UUID;

import documentTreeModel.interfaces.IDocumentBoxMessageRequest;

public class DocumentBoxMessageAuthenticateConnectionRequest  implements IDocumentBoxMessageRequest
{

	UUID _myUUID = UUID.randomUUID();
	
	@Override
	public UUID GetMessageGuid() {
		// TODO Auto-generated method stub
		return _myUUID;
	}
	
	@Override
	public DocumentBoxMessageType GetMessageType() {
		
		return DocumentBoxMessageType.AuthenticateConnectionRequest;
	}





}
