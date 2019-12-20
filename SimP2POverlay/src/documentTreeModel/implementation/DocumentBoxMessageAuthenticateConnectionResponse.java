package documentTreeModel.implementation;

import java.util.UUID;

import documentTreeModel.interfaces.IDocumentBoxMessageResponse;

public class DocumentBoxMessageAuthenticateConnectionResponse  implements IDocumentBoxMessageResponse
{

	UUID _requestUUID;	
	@Override
	public UUID GetMessageGuid() {
			return _requestUUID;
	}
	
	ResponseTypeDocumentBox _responseType;
	public DocumentBoxMessageAuthenticateConnectionResponse(ResponseTypeDocumentBox response,UUID requestUUID)
	{
		_responseType = response;	
		_requestUUID = requestUUID;
	}
	
	@Override
	public DocumentBoxMessageType GetMessageType() {
		
		return DocumentBoxMessageType.AuthenticateConnectionResponse;
	}

	@Override
	public ResponseTypeDocumentBox GetResponseType() {

		return _responseType;
	}

}
