package documentTreeModel.implementation;

import java.util.UUID;

import documentTreeModel.interfaces.IDocumentBoxMessageResponse;

public class DocumentBoxMessagePingResponse implements IDocumentBoxMessageResponse
{
	
	UUID _requestUUID;	
	@Override
	public UUID GetMessageGuid() {
			return _requestUUID;
	}

	 
	ResponseTypeDocumentBox _responseType;
	public DocumentBoxMessagePingResponse(ResponseTypeDocumentBox responseType, UUID requestUUID)
	{
		_responseType = responseType;
		_requestUUID = requestUUID;
	}
	
	@Override
	public DocumentBoxMessageType GetMessageType() {

		return DocumentBoxMessageType.PingResponse;
	}

	@Override
	public ResponseTypeDocumentBox GetResponseType() {

		return _responseType;
	}


}
