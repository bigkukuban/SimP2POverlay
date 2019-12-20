package documentTreeModel.interfaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public interface IDocumentBox 
{

	interface IAuthenticationState
	{
		public enum  State { Authenticated, UnAuthenticated };
				
		public State GetCurrentState();
		
		public long GetTimeStampAuthentication();
		
		void SetAuthenticationRequestedTimeStamp(long value);
		long GetAuthenticationRequestedTimeStamp();		
	}
	
	
	interface IAuthenticationStateForLocalConnection extends IAuthenticationState
	{						
		void SetCurrentState(State state );			
	}
	
	// unique id of the document box
	UUID GetDocumentBoxUUID();
	
	void SetAsAuthenticated();
	void PlaceMessagefromOtherDocumentBox(IDocumentBoxMessage message, UUID sourceDocumentBox,long sourcePeerBox);	
	void BeginMigration();	
	void MigrationDone(IPeerBox newPeerBox);
	void Authenticate();
	void AddConnections(ArrayList<IDocumentBoxConnection> connections);
	IAuthenticationState GetAuthenticationState();
	
	Collection<IDocumentBoxConnection> GetConnections();
		
	//Creates a deep copy of the given object
	IDocumentBox CloneMe();
	
	long GetPeerBoxAddress();
		
}
