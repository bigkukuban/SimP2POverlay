package peersModel.implementation._Peer;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import networkInitializer.smallWorldKleinberg.SmallWorldAddress;
import peersModel.implementation.Peer;
import randomWalk.interfaces.IRandomWalkerHost;

public class SelectByRandomConsiderDistance {
	
	@Test
	public final void testShouldSelectANeighbour() {
					
		Peer dut = new Peer();
		dut.SetNetworkAdress(new SmallWorldAddress(0,0));
		
		
		Peer p1 = new Peer();
		p1.SetNetworkAdress(new SmallWorldAddress(1,0));
		
		Peer p2 = new Peer();
		p2.SetNetworkAdress(new SmallWorldAddress(0,1));
		
		
		Peer p3 = new Peer();
		p3.SetNetworkAdress(new SmallWorldAddress(0,3));
		
		dut.AddNeighbour(p1, false);
		dut.AddNeighbour(p2, false);
		dut.AddNeighbour(p3, true);
		
		
		try {
			
			int iPositiveResults = 0;
			for(int i =0; i<500; i++)
			{
				IRandomWalkerHost result = dut.SelectByRandomConsiderDistance(-2.0);
				if(result != p3)
				{
					iPositiveResults++;
				}
			}
										
			assertTrue(iPositiveResults > 430);
									
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
