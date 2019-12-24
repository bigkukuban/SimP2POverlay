package persistence;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import launcher.ApplicationModelSettings;
import launcher.ApplicationModelSettings.SupportedTopologyTypes;
import networkInitializer.NetworkSettingsBase;
import networkInitializer.baPreferentialAttachment.NetworkSettingsBaPreferentialAttachment;
import networkInitializer.gridStructured.NetworkSettingsGrid;
import networkInitializer.smallWorldKleinberg.NetworkSettingsSmallWorldKleinberg;
import networkInitializer.smallWorldKleinberg.SmallWorldAddress;
import peersModel.implementation.Peer;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;
import persistence.interfaces.IPersister;

public class NetworkToFilePersister implements IPersister
{

	@Override
	public boolean DoPersistNetwork(ApplicationModelSettings input, String targetPath) {

		return false;
	}

	@Override
	public ApplicationModelSettings DoRestoreNetwork(String filePath) {

		return null;
	}
	
	
	public static PersistenceContainer ToPersistenceContainer(ApplicationModelSettings input)
	{
		PersistenceContainer result = new PersistenceContainer();
	
		result.DimensionsNetwork = input.NetworkFacade.GetDimenstions();
		result.NetworkSpecialSettings = ToPersistenceFormat(input.AllGraphSettings);
		result.CurrentActiveSettings = ToPersistenceFormatType(input.ActiveSettings);
		result.ListPeerConnections = ToPersistenceFormat(input.NetworkFacade);
		result.peerList = ToPersistenceFormatPeers(input.NetworkFacade.GetPeers());
		
		return result;
	}
	
	public static ArrayList<PeerEntry> ToPersistenceFormatPeers( ArrayList<IPeer> peers)
	{
		ArrayList<PeerEntry>  result = new ArrayList<PeerEntry>();
		
		return result;
	}
	
	public static Map<Long, ArrayList<Long>> ToPersistenceFormat(INetworkFacade facade)
	{
		HashMap<Long, ArrayList<Long>> result = new HashMap<Long, ArrayList<Long>>();
			
		for(IPeer pr : facade.GetPeers())
		{
			long peerId = pr.GetPeerID();			
			List<Long> allNeigboursIds =pr.GetAllNeighbours().stream().map(o -> o.GetPeerID()).collect(Collectors.toList());			
			ArrayList<Long> arrLst = new ArrayList<Long>(allNeigboursIds);
			result.put(peerId, arrLst);
		}
		
		return result;
	}
	
	public static int ToPersistenceFormatType(NetworkSettingsBase address)
	{
		if(address instanceof NetworkSettingsBaPreferentialAttachment)
		{
			return 3;			
		}
		
		if(address instanceof NetworkSettingsSmallWorldKleinberg)
		{
			return 2;			
		}
		
		if(address instanceof NetworkSettingsGrid)
		{
			return 1;					
		}					
		return 0;
	}
	
	public static ArrayList<NetworkSettingsPersistenceBase>  ToPersistenceFormat(ArrayList<NetworkSettingsBase> input)
	{
		 
		ArrayList<NetworkSettingsPersistenceBase> result ;		
		List<NetworkSettingsPersistenceBase> allAddresses = input.stream().map(o -> ToPersistenceFormat(o)).collect(Collectors.toList());		 
		result = new ArrayList<NetworkSettingsPersistenceBase>(allAddresses);
		
		return result;
	}
	
	public static NetworkSettingsPersistenceBase ToPersistenceFormat(NetworkSettingsBase input)
	{
		if(input instanceof NetworkSettingsBaPreferentialAttachment)
		{
			return new NetworkSettingsPersistencePreferentialAttachment(){  };			
		}
		
		if(input instanceof NetworkSettingsSmallWorldKleinberg)
		{
			return new NetworkSettingsPersistenceSmallWorldKleinberg();			
		}
		
		if(input instanceof NetworkSettingsGrid)
		{
			return new NetworkSettingsPersistenceGrid();					
		}	
		
		return null;
	}
	
/*
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
		
		for (Entry<Long, ArrayList<Long>> entry : container.ListPeerConnections.entrySet()) {
		    Long idPeer = entry.getKey();
		    ArrayList<Long> contacts = entry.getValue();
		    IPeer peerSource = target.GetPeerById(idPeer);
		    
		    for(Long taregetId : contacts)
		    {
		    	IPeer peerTarget = target.GetPeerById(taregetId);
		    	peerSource.AddNeighbour(peerTarget);
		    }
		}
		
		for (Entry<Long, ArrayList<Long>> entry : container.ListPeerConnections.entrySet()) {
		    Long idPeer = entry.getKey();
		    ArrayList<Long> contacts = entry.getValue();
		    IPeer peerSource = target.GetPeerById(idPeer);
		    
		    for(Long taregetId : contacts)
		    {
		    	IPeer peerTarget = target.GetPeerById(taregetId);
		    	peerSource.AddNeighbour(peerTarget);
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
			
			
			if(!result.ListPeerConnections.containsKey(entry.PeerId))
			{
				result.ListPeerConnections.put(entry.PeerId, new ArrayList<Long>());
			}
								
			for(IPeer contact : peer.GetAllNeighbours())
			{
				result.ListPeerConnections.get(entry.PeerId).add(contact.GetPeerID());
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
	*/
	
}
