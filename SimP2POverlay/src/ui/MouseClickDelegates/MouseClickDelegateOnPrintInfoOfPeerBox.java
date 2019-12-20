package ui.MouseClickDelegates;

import commonHelper.math.interfaces.IVector;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;
import ui.interfaces.IMouseClickDelegate;

public class MouseClickDelegateOnPrintInfoOfPeerBox  implements IMouseClickDelegate
{
	@Override
	public boolean DoExecuteActionWithSelectedPeer(IPeer selectedPeer, INetworkFacade facade) {

		long currentCapacity = selectedPeer.GetPeerBox().GetDocumentBoxCapacity();
		
		IVector loadForce = selectedPeer.GetPeerBox().GetLoadForce();
		
		System.out.println(" Peer-Id: "+selectedPeer.GetPeerID()+ " capacity: "+currentCapacity+" load force x: "+loadForce.GetComponents()[0]+" y: "+loadForce.GetComponents()[1]);
		
		return true;
	}

}
