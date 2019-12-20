package ui.openGL;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

import commonHelper.math.LinearAlgebraHelper;
import ui.openGL.interfaces.IPoint3D;

public class LookAtPointCalculator 
{
		
	double _matModelView[] = new double[16];
	double  _matProjection [] = new double [16]; 
	int _viewport [] = new int[4];
	
	public LookAtPointCalculator()
	{
		
	}
	
	
	private void CaptureMatrix()
	{												
		_gl2.glGetIntegerv( GL2.GL_VIEWPORT, _viewport,0 );
		_gl2.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, _matModelView,0);
		_gl2.glGetDoublev(GL2.GL_PROJECTION_MATRIX, _matProjection,0);		
	}

	
	int _iMouseClickX;
	int _iMouseClickY;
	GLAutoDrawable _drawable;
	GL2 _gl2;
	GLU _glu;
	boolean _mouseClicked = false;
	
	public void AssignNewMouseCoordinates( int iMouseClickX, int iMouseClickY)
	{
		_iMouseClickX = iMouseClickX;
		_iMouseClickY = iMouseClickY;		
	}
	
	public void SetContext(GLAutoDrawable drawable, GL2 gl2,GLU glu)
	{
		_drawable = drawable;
		_gl2 = gl2;
		_glu = glu;				
	}
	
	/**
	 * Returns the mouse click position on the xy-plane
	 * @return
	 */
	public IPoint3D CalculateLookAtPointInXYPlane()	
	{					
			
		CaptureMatrix();
		
		double wcoordP1[] = new double[4];
		double wcoordP2[] = new double[4];
							
		double winX = (double)_iMouseClickX; 
		double winY = _viewport[3] - (double)_iMouseClickY; 		
				
		
		boolean bRes  = _glu.gluUnProject(	winX, winY, 5.0, 
 							_matModelView, 0, 
 							_matProjection,0,
 							_viewport, 	  0, 
 							wcoordP1, 0);	
		
		if(!bRes) return null;
	
		bRes  = _glu.gluUnProject(	winX, winY, 1.0, 
						 				_matModelView, 0, 
						 				_matProjection,0,
						 				_viewport, 	  0, 
						 				wcoordP2, 0);
		
		if(!bRes) return null;
		
		double f_xzP1[] = new double[2];
		double f_xzP2[] = new double[2];
		
		
		double f_yzP1[] = new double[2];
		double f_yzP2[] = new double[2];
		
		f_xzP1[0] = wcoordP1[0];
		f_xzP1[1] = wcoordP1[2];
		
		f_xzP2[0] = wcoordP2[0];
		f_xzP2[1] = wcoordP2[2];
		
		
		f_yzP1[0] = wcoordP1[1];
		f_yzP1[1] = wcoordP1[2];
		
		f_yzP2[0] = wcoordP2[1];
		f_yzP2[1] = wcoordP2[2];
		
		
		double[] resultParamsEqXZ = LinearAlgebraHelper.CalculateLinearEquation_ABParams(f_xzP1, f_xzP2);
				
		double[] resultParamsEqYZ = LinearAlgebraHelper.CalculateLinearEquation_ABParams(f_yzP1, f_yzP2);
		
		//Calculates the null-positions on the xy-plane
		double nullX = resultParamsEqXZ[1]/resultParamsEqXZ[0]*(-1);
		double nullY = resultParamsEqYZ[1]/resultParamsEqYZ[0]*(-1);												 	  		
		
		Point3D result = new Point3D();
	  
		result.SetPosX((float)nullX);
		result.SetPosY((float)nullY);
		result.SetPosZ(0.0f);	  
	  
		return result;
	}
	
}
