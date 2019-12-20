package ui.MouseClickDelegates;

import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;
import ui.interfaces.IMouseClickDelegate;

public class MouseClickDelegateOnIncrementCapacityOfPeerBox implements IMouseClickDelegate
{
	@Override
	public boolean DoExecuteActionWithSelectedPeer(IPeer selectedPeer, INetworkFacade facade) {

		long currentCapacity = selectedPeer.GetPeerBox().GetDocumentBoxCapacity();
		
		if(currentCapacity <=1) currentCapacity = 2;
				
		facade.ChangeCapacityOfPeerBox(currentCapacity + currentCapacity  / 2, selectedPeer.GetPeerID());
						
		return true;
	}

}
