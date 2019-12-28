package ui._OpenGL;

import static org.junit.Assert.*;

import org.apache.commons.math3.util.Pair;
import org.junit.Test;

import ui.openGL.LookAtPointCalculator;
import ui.openGL.interfaces.IPoint3D;

public class CalculateLookAtPointInXYPlane {

	@Test
	public void ShouldCalculateLookAtPointInXYPlane() {
		
		Pair<double[], double[]> tt = new Pair<double[], double[]>(new double[]{88,88,88,0}, new double[]{10,90,-8,2});
		
		IPoint3D pos = LookAtPointCalculator.CalculateLookAtPointInXYPlane(tt);
		
		assertTrue(pos.GetPosX() > 16);
	}
	
	
}
