package documentTreeModel.implementation;

import java.util.UUID;

import documentTreeModel.interfaces.IDocumentBoxMessage;

public class DocumentBoxMessagePingRequest implements IDocumentBoxMessage
{
	
	UUID _myUUID = UUID.randomUUID();
	
	@Override
	public UUID GetMessageGuid() {
		// TODO Auto-generated method stub
		return _myUUID;
	}
	
	public DocumentBoxMessagePingRequest()
	{
	
	}
	
	@Override
	public DocumentBoxMessageType GetMessageType() {

		return DocumentBoxMessageType.PingRequest;
	}	
}