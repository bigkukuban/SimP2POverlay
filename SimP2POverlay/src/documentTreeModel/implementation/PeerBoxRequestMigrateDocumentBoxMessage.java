package documentTreeModel.implementation;

import java.util.UUID;

import documentTreeModel.interfaces.IPeerBoxRequestMigrateDocumentBoxMessage;

public class PeerBoxRequestMigrateDocumentBoxMessage implements IPeerBoxRequestMigrateDocumentBoxMessage
{
	UUID _request;
	Object _documentBox;
	UUID _documentBoxUUID;
	public PeerBoxRequestMigrateDocumentBoxMessage(UUID uuidRequest,Object documentBox,UUID incomingDocumentBox  )
	{
		_request = uuidRequest;
		_documentBox = documentBox;
		_documentBoxUUID = incomingDocumentBox;		
	}
	

	@Override
	public UUID GetRequestUUID() {
		return _request;
	}

	@Override
	public Object GetPrivateData() {
		return _documentBox;
	}

	@Override
	public UUID GetUUIDDocumentBox() {
		return _documentBoxUUID;
	}

}
