package ui;

import java.awt.EventQueue;

import commonHelper.math.EuclideanPoint;
import networkInitializer.InitializerFactory;
import networkInitializer.baPreferentialAttachment.NetworkSettingsBaPreferentialAttachment;
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
	OpenGLView _openGlView;
	public AppWindowActions(OpenGLView openGlView)
	{
		_openGlView = openGlView; 
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
	
	public void UserChangedSettingsForGrid(int numXItems, int numYItems)
	{
		
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
