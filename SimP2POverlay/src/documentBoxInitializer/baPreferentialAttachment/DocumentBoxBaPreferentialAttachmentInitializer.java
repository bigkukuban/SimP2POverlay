package documentBoxInitializer.baPreferentialAttachment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import commonHelper.math.RandomUtilities;
import documentBoxInitializer.interfaces.IDocumentBoxNetworkInitializer;
import documentTreeModel.implementation.DocumentBox;
import documentTreeModel.implementation.DocumentBoxConnection;
import documentTreeModel.interfaces.IDocumentBox;
import documentTreeModel.interfaces.IDocumentBoxConnection;
import peersModel.interfaces.IPeer;

public class DocumentBoxBaPreferentialAttachmentInitializer implements IDocumentBoxNetworkInitializer  
{
	
	DocumentBoxBaPreferentialAttachmentSettings _settings;
	
	public DocumentBoxBaPreferentialAttachmentInitializer(DocumentBoxBaPreferentialAttachmentSettings settings) throws Exception
	{
		
		if(settings.m > settings.m0 ) throw new Exception("wrong parameters (m,m0)");
		if(settings.N < settings.m0 ) throw new Exception("wrong parameters (N, m0)");
		
		_settings = settings;
	}
	
	@Override
	public long GetNumberOfItemsItendedToCreate() {
		return this._settings.N;
	}


	@Override
	public ArrayList<IDocumentBox> GetInitializedNetwork(IPeer toPlaceOnPeer) 
	{
		ResetDocumentBoxIds();
		ArrayList<IDocumentBox> createdItems = new ArrayList<IDocumentBox>();			
		
		//create N requested needed DocumentBoxes
		for(int i=0; i<_settings.N;i++)
		{
			DocumentBox itm = new DocumentBox(GetNextUUDIForDocumentBox());
			createdItems.add(itm);
		}
		
		ArrayList<IDocumentBox> lstInitializedNodes = new ArrayList<IDocumentBox>();
		lstInitializedNodes.addAll(createdItems.subList(0, _settings.m0));
		
		//create initial connections between first m0-DocumentBoxes
		for(int i=0; i<_settings.m0;i++)
		{
			IDocumentBox bx = lstInitializedNodes.get(i);
			
			IDocumentBox next = null;						
			if(lstInitializedNodes.size()>i+1)
			{
				next = lstInitializedNodes.get(i+1);
			} else 
			{
				next = lstInitializedNodes.get(0);
			}							
			ConnectDocumentBoxes(bx, next,toPlaceOnPeer);						
		}
		
		//we use always the long range connections ... 
		//now initialize the remaining peers ...
		for(IDocumentBox db: createdItems)
		{
			if(lstInitializedNodes.contains(db)) continue;
						
			CalculatePeerConnections(db,lstInitializedNodes,toPlaceOnPeer);
			
			lstInitializedNodes.add(db);
		}
						
		return createdItems;
	}
	
	
	private void CalculatePeerConnections(IDocumentBox db, ArrayList<IDocumentBox> lstInitializedNodes, IPeer toPlaceOnPeer) 
	{
		Map<Integer, HistogramEntry> pdfFunction = new HashMap<Integer, HistogramEntry>(); 		
		Map<Integer, Double[]> cdfFunction = new HashMap<Integer, Double[]>();		
		
		int iAllConnections = 0;
		for(IDocumentBox itm : lstInitializedNodes)
		{
			int connections = itm.GetConnections().size();
			
			if(!pdfFunction.containsKey(connections))
			{
				pdfFunction.put(connections, new HistogramEntry());
			}			
			pdfFunction.get(connections).IssuedItems.add(itm);
			iAllConnections = iAllConnections + connections;
		}
		
		//normalize it all		
		for(Integer connections :  pdfFunction.keySet())
		{
			HistogramEntry entry = pdfFunction.get(connections);			
			//probability for the histogramm group			
			entry.Probability = (double)(entry.IssuedItems.size() * connections) / (double)iAllConnections;
		}
		
		double previousValue = 0;
		//calculate the CDF from PDF
		for(Integer connections :  pdfFunction.keySet())
		{
			double currentValue  = pdfFunction.get(connections).Probability;
			
			Double range[] = new Double[2];
			
			range[0] = previousValue; //lower bound
			range[1] = currentValue + previousValue; // higher bound
			
			cdfFunction.put(connections, range);			
			previousValue  = previousValue   + currentValue;
		}
		
		//now calculate the connections for the peer, peer needs m - connections						
		for(int m_i = 0; m_i < this._settings.m; m_i++)
		{
			double randValue = Math.random();
						
			Integer selectedConnectionGroup = -1;
			
			//select the connections - group from CDF
			for(Integer connections :  cdfFunction.keySet())
			{
				if(selectedConnectionGroup < 0)
				{
					selectedConnectionGroup = connections;
				}
				
				if( cdfFunction.get(connections)[0]  <= randValue && randValue <= cdfFunction.get(connections)[1] )
				{
					selectedConnectionGroup = connections;
				}
			}
			
			IDocumentBox selected = RandomUtilities.SelectOneByRandomFromList(pdfFunction.get(selectedConnectionGroup).IssuedItems);
			
			if(!ConnectDocumentBoxes(selected,db,toPlaceOnPeer)) 
			{
				m_i--; // try again				
				continue;
			}			
		}
		
	}

	private boolean ConnectDocumentBoxes(IDocumentBox db1, IDocumentBox db2,IPeer toPlaceOnPeer)
	{
		
		//check if the connection already exists
		boolean connectionValid = true;
		for(IDocumentBoxConnection connection : db1.GetConnections())
		{
			if(connection.GetDocumentBoxId().equals(db2.GetDocumentBoxUUID()))
			{
				connectionValid = false;
			}
		}
		
		for(IDocumentBoxConnection connection : db2.GetConnections())
		{
			if(connection.GetDocumentBoxId().equals(db1.GetDocumentBoxUUID()))
			{
				connectionValid = false;
			}
		}
		
		if(!connectionValid )return connectionValid;
		
		//between db1 and db2
		DocumentBoxConnection connDocToDb1 = new DocumentBoxConnection(db1.GetDocumentBoxUUID(),toPlaceOnPeer.GetPeerID());				
		//between db2 and db1 				
		DocumentBoxConnection connDocToDb2 = new DocumentBoxConnection(db2.GetDocumentBoxUUID(),toPlaceOnPeer.GetPeerID());

		ArrayList<IDocumentBoxConnection> listDb1 = new  ArrayList<IDocumentBoxConnection>();
		listDb1.add(connDocToDb2);						
		
		ArrayList<IDocumentBoxConnection> listDb2 = new  ArrayList<IDocumentBoxConnection>();
		listDb2.add(connDocToDb1);
												
		db1.AddConnections(listDb1);
		db2.AddConnections(listDb2);						

		return connectionValid;
	}
	
	
	int _iCurrentNumber = 0;
	private void ResetDocumentBoxIds()
	{
		_iCurrentNumber =0;
	}
	private UUID GetNextUUDIForDocumentBox()
	{		
		_iCurrentNumber++;
		//thus we are able to handle FFFFh - DocumentBoxes -->65535 Elemente
		return UUID.fromString(String.format("00000000-0000-0000-0000-%012x",_iCurrentNumber));				
	}

	@Override
	public String GetReadableDescription() {

		return "DocumentBoxBaPreferentialAttachmentInitializer: N="+_settings.N+" m0= "+_settings.m0;
	}

}
