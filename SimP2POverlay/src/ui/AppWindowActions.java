package ui;



import commonHelper.math.EuclideanPoint;
import launcher.ApplicationModelSettings;
import launcher.ApplicationModelSettings.SupportedTopologyTypes;
import networkInitializer.InitializerFactory;
import networkInitializer.NetworkSettingsBase;
import networkInitializer.baPreferentialAttachment.NetworkSettingsBaPreferentialAttachment;
import networkInitializer.gridStructured.NetworkSettingsGrid;
import networkInitializer.interfaces.INetworkInitializer;
import networkInitializer.smallWorldKleinberg.NetworkSettingsSmallWorldKleinberg;
import peersModel.implementation.NetworkFacade;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;
import persistence.NetworkToFilePersister;
import ui.openGL.ViewModelNetworkEvent.EventAssignNewNetwork;
import ui.openGL.ViewModelNetworkEvent.EventChangeCameraPosition;
import ui.openGL.ViewModelNetworkEvent.EventChangeZoomFactor;

public class AppWindowActions 
{
		
	public static ApplicationModelSettings CreateInitialModelSettings()
	{
		ApplicationModelSettings result = new ApplicationModelSettings();
		
		result.AllGraphSettings.add(new NetworkSettingsGrid(10,10));
		result.AllGraphSettings.add(new NetworkSettingsSmallWorldKleinberg(10,10,1,1,1.0));	
		result.AllGraphSettings.add(new NetworkSettingsBaPreferentialAttachment(2,2,100));
		
		result.ActiveSettings = result.AllGraphSettings.stream().
									  filter(s -> s instanceof NetworkSettingsBaPreferentialAttachment).
									  findFirst().get();

		result.NetworkFacade = CreateNetwork(result.ActiveSettings);
		
		return result;
	}
	
	public static INetworkFacade CreateNetwork(NetworkSettingsBase settings)
	{
		INetworkFacade result = null;				
		try {
			INetworkInitializer peerBoxInitializer = InitializerFactory.GetInitializerBySettingsType(settings, true);
			result = peerBoxInitializer.GetInitializedNetwork();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		return result;
	}
	
	
	OpenGLView _openGlView;
	public ApplicationModelSettings ApplicationSettings = null;
	public AppWindowActions(OpenGLView openGlView)
	{
		_openGlView = openGlView; 
		ApplicationSettings = CreateInitialModelSettings();
	}
	
	public void UserChangedZoomValue(int zoomValue)
	{		
		_openGlView._networkViewModel.PlaceNewEventDelegate(new EventChangeZoomFactor(zoomValue));	  
		_openGlView.UpdateCanvas();
	}
	
	public void UsedResetedTheView()
	{
		_openGlView._networkViewModel.Reset();
		_openGlView.UpdateCanvas();
		
		/**
		 * //ApplicationWindow.this._networkViewModel.Reset();
			
			EventQueue.invokeLater(new Runnable(){
				public void run() 
				{
					try {
						ApplicationWindow.this.UpdateUISettings();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}});
			
			//UpdateCanvas();	
		 */
	}
	
	public void UserInitializedNetworkInApplicationView()
	{
		/*
		NetworkSettingsSmallWorldKleinberg settings = new NetworkSettingsSmallWorldKleinberg();
		
		settings._pPParameter = _settingsKleinberg._pPParameter;
		settings._qParameter = _settingsKleinberg._qParameter;
		settings._rParameter = _settingsKleinberg._rParameter;
		settings._xLength = _settingsKleinberg._xLength;		
		settings._yLength = _settingsKleinberg._yLength;
		*/
	
		NetworkSettingsBaPreferentialAttachment settings = new NetworkSettingsBaPreferentialAttachment();
		settings.m = 1;
		settings.m0 = 2;
		settings.N = 50;
						
		//NetworkSettingsGrid settings = new NetworkSettingsGrid(_settingsKleinberg._xLength,_settingsKleinberg._yLength);
				
		INetworkInitializer peerBoxInitializer  = null;
		
		try{		
			peerBoxInitializer =  InitializerFactory.GetInitializerBySettingsType(settings, true);					
									
			INetworkFacade facade = peerBoxInitializer.GetInitializedNetwork();																					
			_openGlView._networkViewModel.PlaceNewEventDelegate(new EventAssignNewNetwork(facade));	   	        	        		        
								
		}catch(Exception exp)
		{
			exp.printStackTrace(System.err);
		}
		
		_openGlView.UpdateCanvas();
		 				 
	}
	
	public void UserChangedCameraPosInApplilcationView(double dxPos, double dyPos, double dzPos)
	{
		_openGlView._networkViewModel.PlaceNewEventDelegate(new EventChangeCameraPosition(new EuclideanPoint(new double[]{dxPos,dyPos,dzPos})));	  
		_openGlView.UpdateCanvas();
		
	}
	
	public void UserChangedSettingsForSmallWorld(int xItems, int yItems, int qParam, int pParam, double rParam)
	{				
		NetworkSettingsSmallWorldKleinberg settings = (NetworkSettingsSmallWorldKleinberg)ApplicationSettings.GetSettingsByType(SupportedTopologyTypes.SmallWorld);
		settings._xLength = xItems;
		settings._yLength = yItems;
		settings._qParameter = qParam;
		settings._pPParameter = pParam;
		settings._rParameter = rParam;
		
		if(ApplicationSettings.ActiveSettings == settings)
		{
			ApplicationSettings.NetworkFacade = CreateNetwork(settings);
			_openGlView._networkViewModel.PlaceNewEventDelegate(new EventAssignNewNetwork(ApplicationSettings.NetworkFacade));	
		}
		
	}
	
	public void UserChangedSettingsForGrid(int numXItems, int numYItems)
	{
		NetworkSettingsGrid settings = (NetworkSettingsGrid)ApplicationSettings.GetSettingsByType(SupportedTopologyTypes.Grid);
		
		settings.XLength = numXItems;
		settings.YLength = numYItems;
		
		if(ApplicationSettings.ActiveSettings == settings)
		{
			ApplicationSettings.NetworkFacade = CreateNetwork(settings);
			_openGlView._networkViewModel.PlaceNewEventDelegate(new EventAssignNewNetwork(ApplicationSettings.NetworkFacade));	
		}
		
	}
	
	public boolean UserStoresTheSettings(String targetPath)
	{
		//NetworkToFilePersister persister = new  NetworkToFilePersister();
		
		//persister.InitializeTargetFile(filePath);
		
		//persister.DoPersistNetwork(_networkViewModel.GetNetwork(), _settingsKleinberg);

		return false;
	}
	
	public boolean UserOpensSettingsFromFile(String sourcePath)
	{
		/*
		 * NetworkToFilePersister persister = new  NetworkToFilePersister();
						
		persister.InitializeTargetFile(filePath);
		
		INetworkFacade network = new NetworkFacade();		
		if(!persister.DoRestoreNetwork(network))
		{
			return;
		}			
		_settingsKleinberg = (NetworkSettingsSmallWorldKleinberg)persister.GetLastRestoredNetworkSettings(); 
				
		_networkViewModel.SetNetwork(network);
		
		UpdateUISettings();		
		
		frame.setTitle(filePath);
		
		UpdateCanvas();
		 */
		
		return false;
	}
}
