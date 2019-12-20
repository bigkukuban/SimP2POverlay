package ui.openGL.ViewModelNetworkEvent;

import ui.openGL.interfaces.IViewModelNetworkDelegate;
import ui.openGL.interfaces.IViewmodelNetwork;

public class EventChangeZoomFactor implements IViewModelNetworkDelegate 
{

	float _fZoomAngle;
	
	public EventChangeZoomFactor(float fZoomAngle)
	{
		_fZoomAngle = fZoomAngle;
	}
	
	@Override
	public void HandleBeforeDrawing(IViewmodelNetwork network) {

		network.SetZoomAngle(_fZoomAngle);
	}

	@Override
	public void HandleAfterDrawing(IViewmodelNetwork network) 
	{		
	}

}
