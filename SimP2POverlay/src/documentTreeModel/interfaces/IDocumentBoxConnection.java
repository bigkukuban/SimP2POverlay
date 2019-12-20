package documentTreeModel.interfaces;

import java.util.UUID;

public interface IDocumentBoxConnection
{
	UUID GetDocumentBoxId();
	long GetLastKnownPeerBoxAdress();
	
	void SetDocumentBoxId(UUID value);
	void SetLastKnownPeerBoxAdress(long value);
	
	IDocumentBoxConnection CloneMe();
	
}
