package networkInitializer.baPreferentialAttachment._baPreferentialAttachmentInitializer;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import networkInitializer.baPreferentialAttachment.NetworkSettingsBaPreferentialAttachment;
import networkInitializer.baPreferentialAttachment.BaPreferentialAttachmentInitializer;
import peersModel.interfaces.INetworkFacade;

public class GetInitializedNetwork {
	
	@Test
	public final void testInitializedNetwork() {
		NetworkSettingsBaPreferentialAttachment settings = new  NetworkSettingsBaPreferentialAttachment();
		settings.N = 100;
		settings.m0 = 4;
		settings.m = 3;
		BaPreferentialAttachmentInitializer dut = null;
		try {
			dut = new  BaPreferentialAttachmentInitializer(settings);
		} catch (Exception e) {
			// 
			e.printStackTrace();
		}
		
		INetworkFacade facade = dut.GetInitializedNetwork();
		
		assertTrue(facade.GetPeers().size() == 100);		
	}
	
	@Test
	public final void testInitializedNetworkOtherParams13() {
		NetworkSettingsBaPreferentialAttachment settings = new  NetworkSettingsBaPreferentialAttachment();
		settings.N = 13;
		settings.m0 = 2;
		settings.m = 1;
		BaPreferentialAttachmentInitializer dut = null;
		try {
			dut = new  BaPreferentialAttachmentInitializer(settings);
		} catch (Exception e) {
			// 
			e.printStackTrace();
		}
		
		INetworkFacade facade = dut.GetInitializedNetwork();
		
		assertTrue(facade.GetPeers().size() == 13);
		
	}
	
	@Test
	public final void testInitializedNetworkOtherParams20() {
		NetworkSettingsBaPreferentialAttachment settings = new  NetworkSettingsBaPreferentialAttachment();
		settings.N = 20;
		settings.m0 = 1;
		settings.m = 1;
		BaPreferentialAttachmentInitializer dut = null;
		try {
			dut = new  BaPreferentialAttachmentInitializer(settings);
		} catch (Exception e) {
			// 
			e.printStackTrace();
		}
		
		INetworkFacade facade = dut.GetInitializedNetwork();
		
		assertTrue(facade.GetPeers().size() == 20);
		
	}


}
