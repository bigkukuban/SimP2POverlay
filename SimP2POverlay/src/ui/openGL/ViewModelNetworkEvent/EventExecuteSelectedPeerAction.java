package ui.openGL.ViewModelNetworkEvent;

import peersModel.interfaces.IPeer;
import ui.interfaces.IMouseClickDelegate;
import ui.openGL.interfaces.IViewModelNetworkDelegate;
import ui.openGL.interfaces.IViewmodelNetwork;

public class EventExecuteSelectedPeerAction implements IViewModelNetworkDelegate 
{

	IMouseClickDelegate _delegate = null;
	int _xPosMouseClick;
	int _yPosMouseClick;
	public EventExecuteSelectedPeerAction(IMouseClickDelegate delegate,int xPosMouseClick,int yPosMouseClick)
	{
		_delegate = delegate;
		_xPosMouseClick= xPosMouseClick;
		_yPosMouseClick= yPosMouseClick;
		
	}
					
	@Override
	public void HandleBeforeDrawing(IViewmodelNetwork network) 
	{
		network.SetViewPortMouseClickPosition(_xPosMouseClick, _yPosMouseClick, false);
	}

	@Override
	public void HandleAfterDrawing(IViewmodelNetwork network) 
	{
		IPeer peer = (IPeer) network.GetLastSelectedPeer();
		
		if(peer == null)
		{
			System.out.println("no peer selected ..");
			return;
		}
		
		_delegate.DoExecuteActionWithSelectedPeer(peer,	network.GetNetwork());
		
	}

}
