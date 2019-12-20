package networkCreator;

import static org.junit.Assert.*;

import org.junit.Test;

import networkInitializer.smallWorldKleinberg.TemporalDataPeer;

public class TestCase {

	@Test
	public void test() {
		
		TemporalDataPeer test =  new TemporalDataPeer();
		
		test.Distance = 1;
				
		assertTrue(test.Distance == 1);		
	}

}
