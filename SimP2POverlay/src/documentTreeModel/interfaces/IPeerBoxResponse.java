package documentTreeModel.interfaces;

import java.util.UUID;

public interface IPeerBoxResponse extends IPeerBoxMessage 
{
	public enum ResponseType { OK, NOK,MigratedDocumentBox};
	
	UUID GetRequestUUID();
	ResponseType GetResponseType(); 
}
