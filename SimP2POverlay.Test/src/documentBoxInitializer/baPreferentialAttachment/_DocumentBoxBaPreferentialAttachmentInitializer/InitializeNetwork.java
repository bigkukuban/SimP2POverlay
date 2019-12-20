package documentBoxInitializer.baPreferentialAttachment._DocumentBoxBaPreferentialAttachmentInitializer;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import commonHelper.math.interfaces.IVector;
import documentBoxInitializer.baPreferentialAttachment.DocumentBoxBaPreferentialAttachmentInitializer;
import documentBoxInitializer.baPreferentialAttachment.DocumentBoxBaPreferentialAttachmentSettings;
import documentTreeModel.interfaces.IDocumentBox;
import documentTreeModel.interfaces.IPeerBox;
import documentTreeModel.interfaces.IPeerBoxMessage;
import peersModel.interfaces.IPeer;
import peersModel.interfaces.IPeerAdress;
import peersModel.interfaces.IPeerCommunication;
import peersModel.interfaces.IPeerCommunicationMessage;
import randomWalk.interfaces.IRandomWalker;

public class InitializeNetwork 
{

	class peerFake implements IPeer{

		@Override
		public long GetPeerID() {
			return 0;
		}

		@Override
		public ArrayList<IPeer> GetLongRangeNeighbours() {
			return null;
		}

		@Override
		public ArrayList<IPeer> GetNearNeighbours() {
			return null;
		}

		@Override
		public ArrayList<IPeer> GetAllNeighbours() {
			return null;
		}

		@Override
		public boolean RemoveNeighbour(IPeer peer) {
			return false;
		}

		@Override
		public boolean AddNeighbour(IPeer peer, boolean isLongrangeContact) {
			return false;
		}

		@Override
		public IPeerAdress GetNetworkAdress() {
			return null;
		}

		@Override
		public void SetNetworkAdress(IPeerAdress newAdress) {}

		@Override
		public void ResetWalkerChanges() {}

		@Override
		public void AcceptRandomWalker(IRandomWalker walker) {}

		@Override
		public IPeerBox GetPeerBox() {
			return null;
		}

		@Override
		public void SetCommunicationInterface(IPeerCommunication peerCommunication) {			
		}

		@Override
		public IPeerCommunication GetCommunicationInterface() {
			return null;
		}

		@Override
		public void PostMessageFromOtherPeer(long SourcePeerId, IPeerCommunicationMessage peer) {			
		}

		@Override
		public void OnFinalizedInitialization() {
			
		}

		@Override
		public boolean SendMessageService(long iTargetPeerId, IPeerBoxMessage internalData) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean SendBroadCastMessageService(IPeerBoxMessage internalData, long initialTTL) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public long GetCurrentLoadState() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public IVector GetLoadForce() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	@Test
	public final void testInitializedNetwork() {
		DocumentBoxBaPreferentialAttachmentSettings settings = new DocumentBoxBaPreferentialAttachmentSettings();
		
		settings.m = 2;
		settings.m0 = 2;
		settings.N = 10;
		DocumentBoxBaPreferentialAttachmentInitializer initializer  = null;
		try {
			initializer = new DocumentBoxBaPreferentialAttachmentInitializer(settings);
		} catch (Exception e) {
			assertTrue(false);
		}				
		ArrayList<IDocumentBox> result = initializer.GetInitializedNetwork(new peerFake());
		
		assertTrue(result.size() == 10);		
		assertTrue(result.get(5).GetConnections().size() >= 2);
		assertTrue(result.get(6).GetConnections().size() >= 2);
		assertTrue(result.get(9).GetConnections().size() == 2);
	}
	@Test
	public final void testInitializedNetwork2Items() {
		DocumentBoxBaPreferentialAttachmentSettings settings = new DocumentBoxBaPreferentialAttachmentSettings();
		
		settings.m = 2;
		settings.m0 = 2;
		settings.N = 2;
		DocumentBoxBaPreferentialAttachmentInitializer initializer  = null;
		try {
			initializer = new DocumentBoxBaPreferentialAttachmentInitializer(settings);
		} catch (Exception e) {
			assertTrue(false);
		}				
		ArrayList<IDocumentBox> result = initializer.GetInitializedNetwork(new peerFake());
		
		assertTrue(result.size() == 2);		
		assertTrue(result.get(0).GetConnections().size() == 1);
		assertTrue(result.get(1).GetConnections().size() == 1);		
	}
}
