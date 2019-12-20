package networkInitializer;

import networkInitializer.baPreferentialAttachment.BaPreferentialAttachmentInitializer;
import networkInitializer.baPreferentialAttachment.NetworkSettingsBaPreferentialAttachment;
import networkInitializer.gridStructured.GridStructuredInitializer;
import networkInitializer.gridStructured.NetworkSettingsGrid;
import networkInitializer.interfaces.INetworkInitializer;
import networkInitializer.smallWorldKleinberg.NetworkSettingsSmallWorldKleinberg;
import networkInitializer.smallWorldKleinberg.SmallWorldKleinbergInitializer;

public class InitializerFactory {
		
	/**
	 * Returns a new Network-Initializer
	 * @param SizeX
	 * @param SizeY
	 * @param parameterQ
	 * @param parameterP
	 * @param parameterR
	 * @param withLogOutput
	 * @return
	 */
	public static INetworkInitializer GetSmallWorldKleinbergInitializer(int SizeX, int SizeY, int parameterQ, int parameterP, double parameterR,boolean withLogOutput )
	{
		return (INetworkInitializer) new SmallWorldKleinbergInitializer(SizeX,SizeY, parameterQ,parameterP,parameterR);
	}
	
	
	public static INetworkInitializer GetSmallWorldKleinbergInitializer(NetworkSettingsBase settings,boolean withLogOutput )
	{
		return (INetworkInitializer) new SmallWorldKleinbergInitializer((NetworkSettingsSmallWorldKleinberg)settings);
	}
	
	
	public static INetworkInitializer GetBAPreferentialAttachmentInitializer(NetworkSettingsBase settings,boolean withLogOutput ) throws Exception
	{
		return (INetworkInitializer) new BaPreferentialAttachmentInitializer((NetworkSettingsBaPreferentialAttachment)settings);
	}
	
	
	public static INetworkInitializer GetInitializerBySettingsType(NetworkSettingsBase settings,boolean withLogOutput ) throws Exception
	{
		if(settings instanceof NetworkSettingsBaPreferentialAttachment)
		{
			return (INetworkInitializer) new BaPreferentialAttachmentInitializer((NetworkSettingsBaPreferentialAttachment)settings);	
		}
		
		if(settings instanceof NetworkSettingsSmallWorldKleinberg)
		{
			return (INetworkInitializer) new SmallWorldKleinbergInitializer((NetworkSettingsSmallWorldKleinberg)settings);
		}
		
		if(settings instanceof NetworkSettingsGrid){
			return   (INetworkInitializer) new GridStructuredInitializer((NetworkSettingsGrid)settings); 
		}
		
		return null;
	}
}
