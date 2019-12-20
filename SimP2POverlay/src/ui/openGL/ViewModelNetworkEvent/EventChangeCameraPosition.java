package ui.openGL.ViewModelNetworkEvent;

import commonHelper.math.interfaces.IEuclideanPoint;
import ui.openGL.interfaces.IViewModelNetworkDelegate;
import ui.openGL.interfaces.IViewmodelNetwork;

public class EventChangeCameraPosition implements IViewModelNetworkDelegate 
{

	IEuclideanPoint _cameraPoint;
	
	public EventChangeCameraPosition(IEuclideanPoint cameraPoint)
	{
		_cameraPoint = cameraPoint;
	}
	
	@Override
	public void HandleBeforeDrawing(IViewmodelNetwork network) {

		network.GetCameraPosition().SetPosX((float) _cameraPoint.GetComponents()[0]);
		network.GetCameraPosition().SetPosY((float) _cameraPoint.GetComponents()[1]);
		network.GetCameraPosition().SetPosZ((float) _cameraPoint.GetComponents()[2]);
	}

	@Override
	public void HandleAfterDrawing(IViewmodelNetwork network) 
	{		
	}

}
