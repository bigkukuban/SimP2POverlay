package documentTreeModel.implementation;

import java.util.UUID;

import documentTreeModel.interfaces.IPeerBoxResponse;

public class PeerBoxResponse implements IPeerBoxResponse
{
	UUID _requestId;
	ResponseType _respType;
	Object _privateData;
	public PeerBoxResponse(UUID requestId, ResponseType respType, Object privateData)
	{
		_requestId = requestId;
		_respType  =respType;
		_privateData = privateData;
		
	}
	
	
	@Override
	public Object GetPrivateData() {
		return _privateData;
	}

	@Override
	public UUID GetRequestUUID() {
		return _requestId;
	}

	@Override
	public ResponseType GetResponseType() 
	{
		return _respType;
	}

}
