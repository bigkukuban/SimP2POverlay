package documentTreeModel.implementation;

import java.util.UUID;

import documentTreeModel.interfaces.IPeerBoxRequestSendDocumentBoxMessage;

public class PeerBoxRequestSendDocumentBoxMessage implements IPeerBoxRequestSendDocumentBoxMessage
{
	UUID _request;
	Object _privateData;
	UUID _sourceDocumentBox;
	UUID _targetDocumentBox;
	public PeerBoxRequestSendDocumentBoxMessage(UUID requestId, Object privateData,UUID sourceDocumentBox, UUID targetDocumentBox )
	{
		_request = requestId;
		_privateData = privateData;
		_sourceDocumentBox = sourceDocumentBox;
		_targetDocumentBox = targetDocumentBox;
		
	}
	
	
	@Override
	public UUID GetRequestUUID() {
		return _request;
	}

	@Override
	public Object GetPrivateData() {
		return _privateData;
	}

	@Override
	public UUID GetSourceDocumentBox() {
		return _sourceDocumentBox;
	}

	@Override
	public UUID GetTargetDocumentBox() {
		return _targetDocumentBox;
	}

}
