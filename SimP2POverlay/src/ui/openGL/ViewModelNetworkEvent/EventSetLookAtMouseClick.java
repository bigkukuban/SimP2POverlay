package ui.openGL.ViewModelNetworkEvent;

import ui.openGL.interfaces.IViewModelNetworkDelegate;
import ui.openGL.interfaces.IViewmodelNetwork;

public class EventSetLookAtMouseClick implements IViewModelNetworkDelegate 
{

	int xPosMouseClick;
	int yPosMouseClick;
	
	public EventSetLookAtMouseClick(int xPos, int yPos)
	{
		xPosMouseClick = xPos;
		yPosMouseClick = yPos;
	}
	
	@Override
	public void HandleBeforeDrawing(IViewmodelNetwork network) {

		network.SetViewPortMouseClickPosition(xPosMouseClick, yPosMouseClick, true);
						
		network.GetCameraPosition().SetPosX(network.GetLookAtPosition().GetPosX());
		network.GetCameraPosition().SetPosY(network.GetLookAtPosition().GetPosY());
	}

	@Override
	public void HandleAfterDrawing(IViewmodelNetwork network) 
	{		
	}

}
