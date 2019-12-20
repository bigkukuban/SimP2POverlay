package ui.openGL.ViewModelNetworkEvent;

import peersModel.interfaces.INetworkFacade;
import ui.openGL.interfaces.IViewModelNetworkDelegate;
import ui.openGL.interfaces.IViewmodelNetwork;

public class EventAssignNewNetwork implements IViewModelNetworkDelegate 
{

	INetworkFacade _newFacadeNetwork;
	public EventAssignNewNetwork(INetworkFacade nw)
	{
		_newFacadeNetwork = nw;
	}
	
	@Override
	public void HandleBeforeDrawing(IViewmodelNetwork network) {

		network.SetNetwork(_newFacadeNetwork);
	}

	@Override
	public void HandleAfterDrawing(IViewmodelNetwork network) 
	{		
	}

}
