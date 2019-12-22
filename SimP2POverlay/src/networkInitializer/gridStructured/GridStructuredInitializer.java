package networkInitializer.gridStructured;

import java.util.ArrayList;

import networkInitializer.interfaces.INetworkInitializer;
import peersModel.implementation.NetworkFacade;
import peersModel.implementation.Peer;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;

public class GridStructuredInitializer implements INetworkInitializer
{
	
	
	NetworkSettingsGrid _settings = null;
	public GridStructuredInitializer(NetworkSettingsGrid parameter) throws Exception
	{
		if(parameter.XLength < 1 ) throw new Exception("wrong parameters (XLength)");
		if(parameter.YLength < 1 ) throw new Exception("wrong parameters (YLength)");
				
		_settings = parameter;
				
	}


	@Override
	public long GetNumberOfItemsItendedToCreate() {
		return this._settings.XLength*this._settings.YLength;
	}

	
	@Override
	public INetworkFacade GetInitializedNetwork() 
	{
		
		long lPeerIds = 0;		
		ArrayList<IPeer> lstResult = new ArrayList<IPeer>(); 
					
		//then the rest from paramter N
		for(int x =0; x < _settings.XLength;x++)
		{
			for(int y =0; y <_settings.YLength;y++)
			{							
				Peer node = new Peer();
				//assign specific address
				node.SetNetworkAdress(new GridAddress(x,y));
				//create one address
				lPeerIds++;
				node.SetPeerID(lPeerIds);
				lstResult.add(node);
			}
		}
		
		//we use always the long range connections ... 
		//now initialize the remaining peers ...
		for(IPeer peer: lstResult)
		{								
			CalculatePeerConnections(peer,lstResult);					
		}
		
		for(IPeer peer: lstResult)
		{
			peer.OnFinalizedInitialization();	
		}
		
		//the connections are always undirected.
		NetworkFacade result = new NetworkFacade(lstResult, new int[]{_settings.XLength,_settings.YLength});
		return result;
	}

	private void CalculatePeerConnections(IPeer peer, ArrayList<IPeer> lstResult) 
	{	
		for(IPeer p: lstResult)
		{
			if(p == peer) continue; //same reference
			GridAddress otherAdress = (GridAddress)p.GetNetworkAdress();
			if(otherAdress.GetCartesianDistance((GridAddress)peer.GetNetworkAdress()) <= 1.42)
			{
				//connect them				
				peer.AddNeighbour(p);
				p.AddNeighbour(peer);
			}
		}
		
	}
	
	@Override
	public String GetReadableDescription()
	{
		return "PeerBoxes: GridStructuredInitializer: X="+_settings.XLength+" Y= "+_settings.YLength;
	}
	

}
