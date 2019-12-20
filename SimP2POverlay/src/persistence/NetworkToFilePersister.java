package persistence;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map.Entry;

import networkInitializer.NetworkSettingsBase;
import networkInitializer.smallWorldKleinberg.NetworkSettingsSmallWorldKleinberg;
import networkInitializer.smallWorldKleinberg.SmallWorldAddress;
import peersModel.implementation.Peer;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;
import persistence.interfaces.IPersister;

public class NetworkToFilePersister implements IPersister
{

	String _fullPathToFile = "";
	private NetworkSettingsBase _lastCreatedNetworkSettings;
	
	public void InitializeTargetFile(String fullPathToFile)
	{
		_fullPathToFile = fullPathToFile;	
	}
	
	@Override
	public NetworkSettingsBase GetLastRestoredNetworkSettings()
	{
		return _lastCreatedNetworkSettings;
	}
	
	@Override
	public boolean DoPersistNetwork(INetworkFacade facade, NetworkSettingsBase settings) 
	{		
		PersistenceContainer container =  SerializeToContainer(facade,settings);		
		return WriteObject(container);
	}

	@Override
	public boolean DoRestoreNetwork(INetworkFacade facade) 
	{
		_lastCreatedNetworkSettings = null;
		PersistenceContainer container = null;
		  try
	      {
	         FileInputStream fileIn = new FileInputStream(_fullPathToFile);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         container = (PersistenceContainer) in.readObject();
	         in.close();
	         fileIn.close();
	      }catch(IOException i)
	      {
	         i.printStackTrace();
	         return false;
	      }catch(ClassNotFoundException c)
	      {
	         System.out.println("Class not found");
	         c.printStackTrace();
	         return false;
	      }
			
		return DeserializeFromContainer(container, facade);	
	}

	
	private boolean WriteObject(Object obj)
	{
		  try
	      {
	         FileOutputStream fileOut = new FileOutputStream(_fullPathToFile);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(obj);
	         out.close();
	         fileOut.close();
	         
	         return true;
	         
	      }catch(IOException i)
	      {
	          i.printStackTrace();	          
	          return false;
	      }		  		 
	}
	
	
	private boolean DeserializeFromContainer(PersistenceContainer container, INetworkFacade target)
	{		
		ArrayList<IPeer> createdPeers = new  ArrayList<IPeer>();
		for(PeerEntry peer : container.peerList)
		{
			Peer created = new Peer();
			created.SetPeerID(peer.PeerId);
									
			SmallWorldAddress addressPeer = new  SmallWorldAddress(((SmallWorldPersistenceAddress)peer.Address).XPos,
																	((SmallWorldPersistenceAddress)peer.Address).YPos);
			
			created.SetNetworkAdress(addressPeer);
			createdPeers.add(created);			
		}		
		target.SetPeers(createdPeers, container.DimensionsNetwork);
		
		for (Entry<Long, ArrayList<Long>> entry : container.ListPeerConnectionsLongRange.entrySet()) {
		    Long idPeer = entry.getKey();
		    ArrayList<Long> contacts = entry.getValue();
		    IPeer peerSource = target.GetPeerById(idPeer);
		    
		    for(Long taregetId : contacts)
		    {
		    	IPeer peerTarget = target.GetPeerById(taregetId);
		    	peerSource.AddNeighbour(peerTarget, true);
		    }
		}
		
		for (Entry<Long, ArrayList<Long>> entry : container.ListPeerConnectionsShortRange.entrySet()) {
		    Long idPeer = entry.getKey();
		    ArrayList<Long> contacts = entry.getValue();
		    IPeer peerSource = target.GetPeerById(idPeer);
		    
		    for(Long taregetId : contacts)
		    {
		    	IPeer peerTarget = target.GetPeerById(taregetId);
		    	peerSource.AddNeighbour(peerTarget, false);
		    }
		}
		
		if(container.NetworkSpecialSettings != null)
		{
			if(container.NetworkSpecialSettings instanceof NetworkSettingsPersistenceSmallWorldKleinberg)
			{
				_lastCreatedNetworkSettings = new NetworkSettingsSmallWorldKleinberg();								
				NetworkSettingsPersistenceSmallWorldKleinberg kleinbergSettings = (NetworkSettingsPersistenceSmallWorldKleinberg) container.NetworkSpecialSettings;
											
				((NetworkSettingsSmallWorldKleinberg)_lastCreatedNetworkSettings)._pPParameter = kleinbergSettings._pPParameter;
				((NetworkSettingsSmallWorldKleinberg)_lastCreatedNetworkSettings)._rParameter = kleinbergSettings._rParameter;			
				((NetworkSettingsSmallWorldKleinberg)_lastCreatedNetworkSettings)._qParameter = kleinbergSettings._qParameter;
				((NetworkSettingsSmallWorldKleinberg)_lastCreatedNetworkSettings)._xLength = kleinbergSettings._xLength;
				((NetworkSettingsSmallWorldKleinberg)_lastCreatedNetworkSettings)._yLength = kleinbergSettings._yLength;				 				
			}
		}
		
		return true;
	}
	
	private PersistenceContainer SerializeToContainer(INetworkFacade facade,NetworkSettingsBase settings)
	{
		PersistenceContainer result = new PersistenceContainer();
		
		result.DimensionsNetwork = facade.GetDimenstions();
		
		for(IPeer peer : facade.GetPeers())
		{
			PeerEntry entry = new PeerEntry();
			
			SmallWorldAddress inputAddr = (SmallWorldAddress)peer.GetNetworkAdress();
			
			entry.PeerId =peer.GetPeerID();
			entry.Address = new SmallWorldPersistenceAddress(inputAddr.GetPositionX(), inputAddr.GetPositionY());
			
			
			result.peerList.add(entry);
			
			
			if(!result.ListPeerConnectionsLongRange.containsKey(entry.PeerId))
			{
				result.ListPeerConnectionsLongRange.put(entry.PeerId, new ArrayList<Long>());
			}
			
			if(!result.ListPeerConnectionsShortRange.containsKey(entry.PeerId))
			{
				result.ListPeerConnectionsShortRange.put(entry.PeerId, new ArrayList<Long>());
			}
		
			for(IPeer contact : peer.GetLongRangeNeighbours())
			{
				result.ListPeerConnectionsLongRange.get(entry.PeerId).add(contact.GetPeerID());
			}
			
			for(IPeer contact : peer.GetNearNeighbours())
			{
				result.ListPeerConnectionsShortRange.get(entry.PeerId).add(contact.GetPeerID());
			}
						
		}
		
		if(settings instanceof NetworkSettingsSmallWorldKleinberg)
		{
			NetworkSettingsSmallWorldKleinberg kleinbergSettings = (NetworkSettingsSmallWorldKleinberg) settings;
			
			NetworkSettingsPersistenceSmallWorldKleinberg persistenceSettings = new  NetworkSettingsPersistenceSmallWorldKleinberg();
			
			persistenceSettings._pPParameter = kleinbergSettings._pPParameter;
			persistenceSettings._rParameter = kleinbergSettings._rParameter;			
			persistenceSettings._qParameter = kleinbergSettings._qParameter;
			persistenceSettings._xLength = kleinbergSettings._xLength;
			persistenceSettings._yLength = kleinbergSettings._yLength;			
			result.NetworkSpecialSettings = persistenceSettings;
		}
		
		return result;
	}
	
	
}