package networkInitializer._SmallWorldAddress;

import static org.junit.Assert.*;

import org.junit.Test;

import networkInitializer.smallWorldKleinberg.SmallWorldAddress;

public class GetDistance {

	@Test
	public final void testGetDistance() {
		
		SmallWorldAddress address1 = new SmallWorldAddress(1,1);
		
		
		SmallWorldAddress address2 = new SmallWorldAddress(1,2);
		SmallWorldAddress address3 = new SmallWorldAddress(2,2);
		
		
		assertTrue(address2.GetDistance(address1) == 1);
		
		assertTrue(address3.GetDistance(address1) == 2);
		
				
	}

}
