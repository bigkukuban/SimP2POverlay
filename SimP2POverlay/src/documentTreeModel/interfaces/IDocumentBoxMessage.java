package documentTreeModel.interfaces;

import java.util.UUID;

//message between two document-boxes

public interface IDocumentBoxMessage 
{
	
	enum DocumentBoxMessageType {   PingRequest, PingResponse,
									AuthenticateConnectionRequest, 
									AuthenticateConnectionResponse, 
									MigratedResponse,
									BeginAuthentication,
									AuthenticationDone
								   };
	
	DocumentBoxMessageType GetMessageType();
	
	//the UUID is defined by the request, the corresponding response has to return the uuid from correcponding request.
	UUID GetMessageGuid();
}
