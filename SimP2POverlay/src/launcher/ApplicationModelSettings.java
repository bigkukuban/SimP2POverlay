package launcher;

import java.util.ArrayList;

import networkInitializer.NetworkSettingsBase;
import networkInitializer.baPreferentialAttachment.NetworkSettingsBaPreferentialAttachment;
import networkInitializer.gridStructured.NetworkSettingsGrid;
import networkInitializer.smallWorldKleinberg.NetworkSettingsSmallWorldKleinberg;
import peersModel.interfaces.INetworkFacade;

public class ApplicationModelSettings 
{
	public enum SupportedTopologyTypes
	{
		PreferentialAttachment, SmallWorld, Grid, Unknown
	}
	
	public NetworkSettingsBase ActiveSettings = null;
	public ArrayList<NetworkSettingsBase>  AllGraphSettings = new ArrayList<NetworkSettingsBase>();
	public INetworkFacade NetworkFacade = null;
	
	public static Class<?> ConvertEnumToType(SupportedTopologyTypes supType)
	{
		Class<?> typeToSelect = null;
		
		if(supType ==  SupportedTopologyTypes.PreferentialAttachment)
		{
			typeToSelect = NetworkSettingsBaPreferentialAttachment.class;
		}
		
		if(supType ==  SupportedTopologyTypes.SmallWorld)
		{
			typeToSelect = NetworkSettingsSmallWorldKleinberg.class;
		}
		
		if(supType ==  SupportedTopologyTypes.Grid)
		{
			typeToSelect = NetworkSettingsGrid.class;
		}
		
		return typeToSelect;
	}
	
	public static SupportedTopologyTypes ConvertTypeToEnum(NetworkSettingsBase supType)
	{				
		if(supType instanceof NetworkSettingsBaPreferentialAttachment)
		{
			return SupportedTopologyTypes.PreferentialAttachment;			
		}
		
		if(supType instanceof NetworkSettingsSmallWorldKleinberg)
		{
			return SupportedTopologyTypes.SmallWorld;			
		}
		
		if(supType instanceof NetworkSettingsGrid)
		{
			return SupportedTopologyTypes.Grid;					
		}
		
		return SupportedTopologyTypes.Unknown;
	}		
	
	public NetworkSettingsBase GetSettingsByType(SupportedTopologyTypes supType)
	{	
		return AllGraphSettings.stream().filter(o -> o.getClass().equals(ConvertEnumToType(supType) ) ).findFirst().get();
	}
		
}
