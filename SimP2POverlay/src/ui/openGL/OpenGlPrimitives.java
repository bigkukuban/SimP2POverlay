package ui.openGL;

import java.util.ArrayList;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import commonHelper.math.LinearAlgebraHelper;

public class OpenGlPrimitives {

	/**
	 * Draws a connector between two points
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param drawable
	 */
	public static ArrayList<Point3D> calculateConnector(float x1, float y1, float x2, float y2, float  heigh)
	{
		float stepLength = 0.01f;
		float zMaxHeigh = heigh;
		
		ArrayList<Point3D> resultingPointsOfLine = new ArrayList<Point3D>();
		
		boolean bSwitched = false;
		
		//transform to use the y-axis as source for further calculations
		if(Math.abs(x1 - x2) < stepLength)
		{
			bSwitched = true;
			//
			float tmp1 = y1, tmp2 = y2;
			
			y2 = x2;
			y1 = x1;
			
			x1 = tmp1;
			x2 = tmp2;		
		}
		
		//swap if not in right order
		if(x1>x2)
		{
			float tmp;
			
			tmp = x1;			
			x1=x2;			
			x2=tmp;
			
			tmp = y1;			
			y1=y2;			
			y2=tmp;
		} 
		
		
		float Xn = (float) (((x2-x1)/2.0)+x1); 
		double[] linearParams = LinearAlgebraHelper.CalculateLinearEquation_ABParams(new double[]{x1,y1}, new double[]{x2,y2});		
		double[] quadraticParams = LinearAlgebraHelper.CalculateQuadraticEquation_ABCParams(new double[]{x1,0},new double[]{Xn,zMaxHeigh} ,new double[]{x2,0});		
		for(float x=x1;x<x2;x=x+stepLength)
		{
				float y = (float) (x*linearParams[0]+linearParams[1]);
				float z = (float) ( Math.pow(x, 2)*quadraticParams[0]+x*quadraticParams[1] + quadraticParams[2] );
				
				if(!bSwitched)
				{
					resultingPointsOfLine.add( new Point3D(x, y, z));
				
				} else 
				{
					resultingPointsOfLine.add( new Point3D(y, x, z));
				}						
		}		
		return resultingPointsOfLine;
		
	}
	

	
	public static void drawAxes(GLAutoDrawable drawable) 
	{
		GL2 gl = drawable.getGL().getGL2();
	    /*  Length of axes */
	    double lenXY = 1.0;
	    double lenZ = 1.0;
	    
	    gl.glLoadIdentity();	    
	    gl.glBegin(GL.GL_LINES);
	    gl.glColor3d(1.0,150.0,5.0);
	    gl.glVertex3d(0,0,0);
	    gl.glVertex3d(lenXY,0,0);
	    gl.glVertex3d(0,0,0);
	    gl.glVertex3d(0,lenXY,0);
	    gl.glVertex3d(0,0,0);
	    gl.glVertex3d(0,0,lenZ);
	    gl.glEnd();
	    /*  Label axes */
	    gl.glRasterPos3d(lenXY,0,0);
	    
	    gl.glRasterPos3d(0,lenXY,0);
	    
	    gl.glRasterPos3d(0,0,lenZ);
	    
	  
	}
	
}
