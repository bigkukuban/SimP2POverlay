package commonHelper._RandomUtilities;

import static org.junit.Assert.*;
import org.junit.Test;
import commonHelper.math.RandomUtilities;

public class RandomSelecterDoubleTest {
	
	
	@Test
	public final void ShouldSelectRandomItem() 
	{
		int z = RandomUtilities.SelectRandomInteger(1000);
		
		assertTrue(z<1000); 
	}

}
