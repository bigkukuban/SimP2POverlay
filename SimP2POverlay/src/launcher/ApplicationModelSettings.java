package launcher;

import java.util.ArrayList;

import networkInitializer.NetworkSettingsBase;
import peersModel.interfaces.INetworkFacade;

public class ApplicationModelSettings 
{

	public NetworkSettingsBase GetActiveNetworkSettings()
	{
		return null;
	}
	
	public void SetNetworkSettings(NetworkSettingsBase newSettings)
	{
		
	}
	
	public ArrayList<NetworkSettingsBase> GetAllNetworkSettings()
	{
		return null;
	}
	
	public INetworkFacade GetCurrentFacade()
	{
		return null;
	}
	
	public void SetCurrentFacade(INetworkFacade facade)
	{
		
	}
	
}
