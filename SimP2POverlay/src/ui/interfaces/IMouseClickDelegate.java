package ui.interfaces;

import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;

public interface IMouseClickDelegate {
		boolean DoExecuteActionWithSelectedPeer(IPeer selectedPeer, INetworkFacade facade);
		
}
