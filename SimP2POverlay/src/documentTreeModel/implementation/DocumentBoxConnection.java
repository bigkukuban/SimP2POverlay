package documentTreeModel.implementation;

import java.util.UUID;

import documentTreeModel.interfaces.IDocumentBoxConnection;

public class DocumentBoxConnection  implements IDocumentBoxConnection
{
	private UUID DocumentBoxId;
	private long LastKnownPeerBoxAdress;		
	
	
	public DocumentBoxConnection()
	{		
	}
		
	public DocumentBoxConnection(UUID docBox, long lastKnownPeerBox)
	{
		DocumentBoxId =  docBox;
		LastKnownPeerBoxAdress = lastKnownPeerBox;	
	}
	
	public UUID GetDocumentBoxId()
	{
		return DocumentBoxId;
	}
	public long GetLastKnownPeerBoxAdress()
	{
		return LastKnownPeerBoxAdress;
	}
	
	public void SetDocumentBoxId(UUID value)
	{
		DocumentBoxId = value;
	}
	
	public void SetLastKnownPeerBoxAdress(long value)
	{
		LastKnownPeerBoxAdress = value;
	}

	@Override
	public synchronized IDocumentBoxConnection CloneMe() 
	{
		return new DocumentBoxConnection(DocumentBoxId, LastKnownPeerBoxAdress);
	}
}
