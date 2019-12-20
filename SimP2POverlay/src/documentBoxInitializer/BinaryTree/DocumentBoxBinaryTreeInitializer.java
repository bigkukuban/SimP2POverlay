package documentBoxInitializer.BinaryTree;
import java.util.ArrayList;
import java.util.UUID;

import documentBoxInitializer.interfaces.IDocumentBoxNetworkInitializer;
import documentTreeModel.implementation.DocumentBox;
import documentTreeModel.implementation.DocumentBoxConnection;
import documentTreeModel.interfaces.IDocumentBox;
import documentTreeModel.interfaces.IDocumentBoxConnection;
import peersModel.interfaces.IPeer;

public class DocumentBoxBinaryTreeInitializer implements IDocumentBoxNetworkInitializer {
	
	DocumentBoxBinaryTreeSettings _settings;
	
	public DocumentBoxBinaryTreeInitializer(DocumentBoxBinaryTreeSettings settings)
	{
		_settings = settings;
	}
	
	@Override
	public long GetNumberOfItemsItendedToCreate() {
		return this._settings.NumberOfDocumentBoxes;
	}

	
	private UUID GetUUDIForDocumentBox(int iCurrentNumber)
	{		
		//thus we are able to handle FFFFh - DocumentBoxes -->65535 Elemente
		return UUID.fromString(String.format("00000000-0000-0000-0000-%012x",iCurrentNumber+1));				
	}
		
	public ArrayList<IDocumentBox> GetInitializedNetwork(IPeer toPlaceOnPeer) 
	{
		return InitializeDocumentBoxesOnPeers(toPlaceOnPeer);
	}
	
	private  ArrayList<IDocumentBox> InitializeDocumentBoxesOnPeers(IPeer toPlaceOnPeer)
	{		
		//we just need to create more DocumentBoxes than PeerBoxes
		ArrayList<IDocumentBox> allCreated = new ArrayList<IDocumentBox>(); 
		ArrayList<IDocumentBox> arrayPreviousLevel = new ArrayList<IDocumentBox>();								
		int iCreatedDocumentBoxes = 0;		
		while(iCreatedDocumentBoxes < _settings.NumberOfDocumentBoxes)
		{						
			// add to each item from previous level 2 new Children (do not forget vice versa connections )			
			ArrayList<IDocumentBox> arrayCurrentLevel  = new  ArrayList<IDocumentBox>();
			
			if(arrayPreviousLevel.size() == 0)
			{
				DocumentBox root = new DocumentBox(GetUUDIForDocumentBox(iCreatedDocumentBoxes));
				arrayCurrentLevel.add(root);
				iCreatedDocumentBoxes++;
			}
			
			for(IDocumentBox db : arrayPreviousLevel)
			{											
				DocumentBox docBox1 = new DocumentBox(GetUUDIForDocumentBox(iCreatedDocumentBoxes));
				arrayCurrentLevel.add(docBox1);
				
				//between parent and dcbx1
				DocumentBoxConnection connDocBoxParent1 = new DocumentBoxConnection(docBox1.GetDocumentBoxUUID(),toPlaceOnPeer.GetPeerID());				
				//between dcbx1 and parent 				
				DocumentBoxConnection connDocBox1Parent = new DocumentBoxConnection(db.GetDocumentBoxUUID(),toPlaceOnPeer.GetPeerID());

				ArrayList<IDocumentBoxConnection> listDocBox1 = new  ArrayList<IDocumentBoxConnection>();
				listDocBox1.add(connDocBox1Parent);
				
				
				ArrayList<IDocumentBoxConnection> listDocBox1Parent = new  ArrayList<IDocumentBoxConnection>();
				listDocBox1Parent.add(connDocBoxParent1);
														
				docBox1.AddConnections(listDocBox1);
				db.AddConnections(listDocBox1Parent);						
				iCreatedDocumentBoxes++;								
				
				if(iCreatedDocumentBoxes >= _settings.NumberOfDocumentBoxes) break; // breaks only the inner loop
				
				//add second				
				DocumentBox docBox2 = new DocumentBox(GetUUDIForDocumentBox(iCreatedDocumentBoxes));
				arrayCurrentLevel.add(docBox2);
				
				//between dcbx2 and parent
				DocumentBoxConnection connDocBox2Parent = new DocumentBoxConnection(db.GetDocumentBoxUUID(),toPlaceOnPeer.GetPeerID());
				//between parent and dcbx2
				DocumentBoxConnection connParentDocBox2 = new DocumentBoxConnection(docBox2.GetDocumentBoxUUID(),toPlaceOnPeer.GetPeerID());
												
				ArrayList<IDocumentBoxConnection> listDocBox2 = new  ArrayList<IDocumentBoxConnection>();
				ArrayList<IDocumentBoxConnection> listDocBox2Parent = new ArrayList<IDocumentBoxConnection>(); 
																
				listDocBox2Parent.add(connParentDocBox2);								
				listDocBox2.add(connDocBox2Parent);		
				
				db.AddConnections(listDocBox2Parent);				
				docBox2.AddConnections(listDocBox2);
												
				iCreatedDocumentBoxes++;
				
				if(iCreatedDocumentBoxes >= _settings.NumberOfDocumentBoxes) break; // breaks only the inner loop
				
			}			
			arrayPreviousLevel = arrayCurrentLevel;		
			allCreated.addAll(arrayCurrentLevel);			
		}
					
		return allCreated;
	}

	@Override
	public String GetReadableDescription() {
	
			return "DocumentBoxBinaryTreeInitializer: N="+_settings.NumberOfDocumentBoxes;
	}
}
