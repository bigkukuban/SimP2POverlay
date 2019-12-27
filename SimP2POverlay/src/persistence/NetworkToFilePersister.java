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
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.math3.util.Pair;
import launcher.ApplicationModelSettings;
import networkInitializer.NetworkSettingsBase;
import networkInitializer.baPreferentialAttachment.BaPreferentialAttachmentAddress;
import networkInitializer.baPreferentialAttachment.NetworkSettingsBaPreferentialAttachment;
import networkInitializer.gridStructured.GridAddress;
import networkInitializer.gridStructured.NetworkSettingsGrid;
import networkInitializer.smallWorldKleinberg.NetworkSettingsSmallWorldKleinberg;
import networkInitializer.smallWorldKleinberg.SmallWorldAddress;
import peersModel.implementation.NetworkFacade;
import peersModel.implementation.Peer;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;
import peersModel.interfaces.IPeerAdress;


public class NetworkToFilePersister
{
	
	public static boolean DoPersistNetwork(ApplicationModelSettings input, String targetPath) 
	{
		PersistenceContainer container = ToPersistenceContainer(input);		
		return WriteObject(container,targetPath);
	}
	
	public static ApplicationModelSettings DoRestoreNetwork(String filePath) {

		PersistenceContainer readObject = ReadObject(filePath);
		
		if(readObject == null) return null;
					
		return ToLocalModel(readObject);
	}
	
	
	public static ApplicationModelSettings ToLocalModel(PersistenceContainer persistenceData)
	{
		ApplicationModelSettings appLocalSettings = new ApplicationModelSettings();
		
		appLocalSettings.NetworkFacade = ToLocalModel(persistenceData.ListPeerConnections, persistenceData.peerList,persistenceData.DimensionsNetwork);		
		Pair<ArrayList<NetworkSettingsBase>,NetworkSettingsBase> settings = ToLocalModel(persistenceData.CurrentActiveSettings, persistenceData.NetworkSpecialSettings);		
		appLocalSettings.AllGraphSettings = settings.getFirst();
		appLocalSettings.ActiveSettings = settings.getSecond();
		
		return appLocalSettings;
				
	}
	
	private static  Pair<ArrayList<NetworkSettingsBase>,NetworkSettingsBase> ToLocalModel(int currentActiveSettings,ArrayList<NetworkSettingsPersistenceBase> networkSpecialSettings) 
	{
		ArrayList<NetworkSettingsBase> allSettings = new ArrayList<NetworkSettingsBase>();
		NetworkSettingsBase activeSetting = null;
		
		for(NetworkSettingsPersistenceBase input : networkSpecialSettings)
		{
			if(input instanceof NetworkSettingsPersistencePreferentialAttachment)
			{
				NetworkSettingsBaPreferentialAttachment  settings =  new NetworkSettingsBaPreferentialAttachment();
				
				settings.m = ((NetworkSettingsPersistencePreferentialAttachment)input).m;
				settings.m0 = ((NetworkSettingsPersistencePreferentialAttachment)input).m0;
				settings.N = ((NetworkSettingsPersistencePreferentialAttachment)input).N;
				allSettings.add(settings);
				
				if(currentActiveSettings == 3)
				{
					activeSetting = settings;
				}
			}
			
			if(input instanceof NetworkSettingsPersistenceSmallWorldKleinberg)
			{						
				NetworkSettingsSmallWorldKleinberg  settings =  new NetworkSettingsSmallWorldKleinberg();			
				settings._pPParameter = ((NetworkSettingsPersistenceSmallWorldKleinberg)input)._pPParameter;
				settings._qParameter = ((NetworkSettingsPersistenceSmallWorldKleinberg)input)._qParameter;
				settings._rParameter = ((NetworkSettingsPersistenceSmallWorldKleinberg)input)._rParameter;
				settings._xLength = ((NetworkSettingsPersistenceSmallWorldKleinberg)input)._xLength;
				settings._yLength = ((NetworkSettingsPersistenceSmallWorldKleinberg)input)._yLength;
				allSettings.add(settings);
				
				if(currentActiveSettings == 2)
				{
					activeSetting = settings;	
				}
			}
			
			if(input instanceof NetworkSettingsPersistenceGrid)
			{
				NetworkSettingsGrid settings =  new NetworkSettingsGrid(((NetworkSettingsPersistenceGrid)input).XLength,((NetworkSettingsPersistenceGrid)input).YLength);								
				allSettings.add(settings);
				
				if(currentActiveSettings == 1)
				{
					activeSetting = settings;
				}
			}	
		}
		
		return new Pair<ArrayList<NetworkSettingsBase>,NetworkSettingsBase>(allSettings,activeSetting);
			
	}

	private static INetworkFacade ToLocalModel(Map<Long, ArrayList<Long>> listPeerConnections,ArrayList<PeerEntry> peerList, int[] dimensionsNetwork) 
	{
		NetworkFacade  networkFacade = new NetworkFacade();		
		List<IPeer> resultingPeers = peerList.stream().map(o -> ToLocalModel(o)).collect(Collectors.toList());		
		//connect peers 
		for(IPeer pr : resultingPeers)
		{
			ArrayList<Long> peerConns = listPeerConnections.get(pr.GetPeerID());
			for(Long singleConn : peerConns)
			{
				Optional<IPeer> otherPeer =  resultingPeers.stream().filter(o -> o.GetPeerID() == singleConn).findAny();				
				if(!otherPeer.isPresent()) continue;				
				pr.AddNeighbour(otherPeer.get());
			}
		}
		//assign..
		networkFacade.SetPeers(new ArrayList<IPeer>(resultingPeers), dimensionsNetwork);

		return networkFacade;
	}

	
	private static IPeer ToLocalModel(PeerEntry peer)
	{
		Peer result = new Peer();		
		result.SetPeerID(peer.PeerId);		
		result.SetNetworkAdress(ToModelEntry(peer.Address));	
		
		return result;
	}
	
	private static PersistenceContainer ReadObject(String sourcePath)
	{
		
		PersistenceContainer container = null;
		  try
	      {
	         FileInputStream fileIn = new FileInputStream(sourcePath);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         container = (PersistenceContainer) in.readObject();
	         in.close();
	         fileIn.close();	        
	      }catch(IOException i)
	      {
	         i.printStackTrace();	         
	      }catch(ClassNotFoundException c)
	      {
	         System.out.println("Class not found");
	         c.printStackTrace();	         
	      }			
		  return container;
	}

	private static boolean WriteObject(Object obj,String targetPath)
	{
		  try
	      {
	         FileOutputStream fileOut = new FileOutputStream(targetPath);
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
		List<PeerEntry> allNeigboursIds =peers.stream().map(o -> ToPersistenceFormatPeers(o)).collect(Collectors.toList());			
		return  new ArrayList<PeerEntry>(allNeigboursIds);				
	}
	
	
	private static PeerEntry ToPersistenceFormatPeers( IPeer peer)
	{
		PeerEntry result = new  PeerEntry();
		
		result.PeerId = peer.GetPeerID();
		result.Address = ToPersistenceEntry(peer.GetNetworkAdress());
		
		return result;				
	}
	
	private static PersistenceAddressBase ToPersistenceEntry(IPeerAdress address)
	{
		if(address instanceof GridAddress )
		{
			GridPersistenceAddress result = new GridPersistenceAddress();
			result.XPos = ((GridAddress)address).GetPositionX();
			result.YPos = ((GridAddress)address).GetPositionY();
			result.Addresstype = 1;
			return result;
		} 
				
		if(address instanceof BaPreferentialAttachmentAddress )
		{
			PreferentialAttachmentPersistenceAddress result = new PreferentialAttachmentPersistenceAddress();
			
			result.Addresstype = 3;
			result.XPos = ((BaPreferentialAttachmentAddress)address).GetPositionX();
			result.YPos =  ((BaPreferentialAttachmentAddress)address).GetPositionY();
			result.BirthPosition =  ((BaPreferentialAttachmentAddress)address).GetBirthPosition();
			return result;
		}
		
		if(address instanceof SmallWorldAddress )
		{
			SmallWorldPersistenceAddress result = new SmallWorldPersistenceAddress();	
			
			result.Addresstype = 2;
			result.XPos = ((SmallWorldAddress)address).GetPositionX();
			result.YPos =  ((SmallWorldAddress)address).GetPositionY();
			return result;
		}
		
		return null;
		
	}
	
	private static IPeerAdress ToModelEntry(PersistenceAddressBase address)
	{
		if(address instanceof GridPersistenceAddress )
		{
			GridAddress result = new GridAddress( ((GridPersistenceAddress)address).XPos,  ((GridPersistenceAddress)address).YPos);
						
			return result;
		} 
				
		if(address instanceof PreferentialAttachmentPersistenceAddress )
		{
			BaPreferentialAttachmentAddress result = new BaPreferentialAttachmentAddress(((PreferentialAttachmentPersistenceAddress)address).XPos,
																						((PreferentialAttachmentPersistenceAddress)address).YPos,
																						((PreferentialAttachmentPersistenceAddress)address).BirthPosition);						
			return result;
		}
		
		if(address instanceof SmallWorldPersistenceAddress )
		{
			SmallWorldAddress result = new SmallWorldAddress(((SmallWorldPersistenceAddress)address).XPos,
															  ((SmallWorldPersistenceAddress)address).YPos);				
			return result;
		}
		
		return null;
		
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
		List<NetworkSettingsPersistenceBase> allAddresses = input.stream().map(o -> ToPersistenceFormat(o)).collect(Collectors.toList());		 
		ArrayList<NetworkSettingsPersistenceBase> result = new ArrayList<NetworkSettingsPersistenceBase>(allAddresses);		
		return result;
	}
	
	public static NetworkSettingsPersistenceBase ToPersistenceFormat(NetworkSettingsBase input)
	{
		if(input instanceof NetworkSettingsBaPreferentialAttachment)
		{
			NetworkSettingsPersistencePreferentialAttachment  settings =  new NetworkSettingsPersistencePreferentialAttachment();
			
			settings.m = ((NetworkSettingsBaPreferentialAttachment)input).m;
			settings.m0 = ((NetworkSettingsBaPreferentialAttachment)input).m0;
			settings.N = ((NetworkSettingsBaPreferentialAttachment)input).N;
			
			return settings;
		}
		
		if(input instanceof NetworkSettingsSmallWorldKleinberg)
		{						
			NetworkSettingsPersistenceSmallWorldKleinberg  settings =  new NetworkSettingsPersistenceSmallWorldKleinberg();			
			settings._pPParameter = ((NetworkSettingsSmallWorldKleinberg)input)._pPParameter;
			settings._qParameter = ((NetworkSettingsSmallWorldKleinberg)input)._qParameter;
			settings._rParameter = ((NetworkSettingsSmallWorldKleinberg)input)._rParameter;
			settings._xLength = ((NetworkSettingsSmallWorldKleinberg)input)._xLength;
			settings._yLength = ((NetworkSettingsSmallWorldKleinberg)input)._yLength;
			
			return settings;
		}
		
		if(input instanceof NetworkSettingsGrid)
		{
			NetworkSettingsPersistenceGrid settings =  new NetworkSettingsPersistenceGrid();				
			settings.XLength = ((NetworkSettingsGrid)input).XLength;
			settings.YLength = ((NetworkSettingsGrid)input).YLength;
			return settings;
		}	
		
		return null;
	}
		
}
