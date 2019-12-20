package peersModel.implementation._Peer;

import static org.junit.Assert.*;

import org.junit.Test;

import networkInitializer.smallWorldKleinberg.SmallWorldAddress;
import peersModel.implementation.Peer;
import randomWalk.interfaces.IRandomWalker;
import randomWalk.interfaces.IRandomWalkerHost;

public class AcceptRandomWalker {
	
	
	@Test
	public final void testShouldChangeColor() {
		
		Peer dut = new Peer();
				
		dut.AcceptRandomWalker(new IRandomWalker(){

			@Override
			public void Visit(IRandomWalkerHost host) {
					host.SetHostColor(30);				
			}

			@Override
			public void KillWalker() {}

			@Override
			public boolean IsKilled() {return false;}

			@Override
			public boolean IsLongHaulWalker() {
				// TODO Auto-generated method stub
				return false;
			}			
		});
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue(dut.GetHostColorSetByWalker() == 30);		
		
	}
	
	 
	@Test
	public final void testShouldJumpToNext() 
	{
		
		Peer dut = new Peer();
		Peer next = new Peer();
		
		dut.SetNetworkAdress(new SmallWorldAddress(0,0));
		
		next.AddNeighbour(dut, true);
		next.SetNetworkAdress(new SmallWorldAddress(1,1));
		
		dut.AddNeighbour(next, true);
		
		dut.AcceptRandomWalker(new IRandomWalker(){
			
			@Override
			public void Visit(IRandomWalkerHost host) {				
					host.SetHostColor(30);
				
			}

			@Override
			public void KillWalker() {				
			}

			@Override
			public boolean IsKilled() {
				return false;
			}

			@Override
			public boolean IsLongHaulWalker() {
				// TODO Auto-generated method stub
				return false;
			}			
		});
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue("Dut wrong color: "+dut.GetHostColorSetByWalker(), dut.GetHostColorSetByWalker() == 30);		
		assertTrue("Next wrong color: "+next.GetHostColorSetByWalker(),next.GetHostColorSetByWalker() == 30);		
		
	}
}
