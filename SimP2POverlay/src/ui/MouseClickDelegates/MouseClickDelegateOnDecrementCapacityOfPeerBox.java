package ui.MouseClickDelegates;

import documentTreeModel.implementation.GlobalSimulationParameters;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;
import ui.interfaces.IMouseClickDelegate;

public class MouseClickDelegateOnDecrementCapacityOfPeerBox implements IMouseClickDelegate
{
	@Override
	public boolean DoExecuteActionWithSelectedPeer(IPeer selectedPeer, INetworkFacade facade) {

		long currentCapacity = selectedPeer.GetPeerBox().GetDocumentBoxCapacity();
		
		
		currentCapacity = currentCapacity -currentCapacity/2;
		
		if(currentCapacity <=GlobalSimulationParameters.MaximalInitialNumberOfAllowedDocumentBoxesOnPeerBox ) return false; 
				
		facade.ChangeCapacityOfPeerBox(currentCapacity, selectedPeer.GetPeerID());
						
		return true;
	}

}
