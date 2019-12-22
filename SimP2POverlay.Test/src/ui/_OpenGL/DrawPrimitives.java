package ui._OpenGL;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import ui.openGL.OpenGlPrimitives;
import ui.openGL.Point3D;

public class DrawPrimitives {

	@Test
	public void ShouldDrawALine() {
		
		ArrayList<Point3D> points = OpenGlPrimitives.drawConnector(0, 0, 1, 1, 2);
		
		assertTrue(points.size() > 100);
		
		assertTrue(points.size() < 1000);
		
	}

}
