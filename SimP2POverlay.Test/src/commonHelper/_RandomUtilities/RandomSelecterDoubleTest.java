package commonHelper._RandomUtilities;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Objects;

import org.junit.Test;

import commonHelper.RandomSelecter;
import commonHelper.RandomSelecterDouble;
import commonHelper.interfaces.IRandomSelecterDoubleInputObject;
import commonHelper.interfaces.IRandomSelecterInputObject;
import commonHelper.math.RandomUtilities;

public class RandomSelecterDoubleTest {
	
	
	@Test
	public final void ShouldSelectRandomItem() 
	{
		int z = RandomUtilities.SelectRandomInteger(1000);
		
		assertTrue(z<1000); 
	}

}
