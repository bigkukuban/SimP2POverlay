package ui.openGL;
import org.apache.commons.math3.util.Pair;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import commonHelper.math.LinearAlgebraHelper;
import ui.openGL.interfaces.IPoint3D;

public class LookAtPointCalculator 
{		
	public static Pair<double[],double[]> GetP1P2Coordinates(GL2 gl2, GLU glu, int mouseClickX, int mouseClickY)
	{
		double matModelView[] = new double[16];
		double  matProjection [] = new double [16]; 
		int viewport [] = new int[4];
				
		gl2.glGetIntegerv( GL2.GL_VIEWPORT, viewport,0 );
		gl2.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, matModelView,0);
		gl2.glGetDoublev(GL2.GL_PROJECTION_MATRIX, matProjection,0);
		
		double wcoordP1[] = new double[4];
		double wcoordP2[] = new double[4];
							
		double winX = (double)mouseClickX; 
		double winY = viewport[3] - (double)mouseClickY; 		
				
		
		boolean bRes  = glu.gluUnProject(	winX, winY, 5.0, 
				 							matModelView, 0, 
				 							matProjection,0,
				 							viewport, 	  0, 
				 							wcoordP1, 0);	
		
		if(!bRes) return null;
	
		bRes  = glu.gluUnProject(	winX, winY, 1.0, 
						 			matModelView, 0, 
						 			matProjection,0,
						 			viewport, 	  0, 
						 			wcoordP2, 0);
		
		if(!bRes) return null;
		
		return new Pair<double[],double[]> (wcoordP1,wcoordP2);								
	}
	
	/**
	 * Returns the mouse click position on the xy-plane
	 * @return
	 */
	public static IPoint3D CalculateLookAtPointInXYPlane(int mouseX, int mouseY, GL2 gl2,GLU glu)	
	{					
		Pair<double[],double[]> planeCoords = GetP1P2Coordinates(gl2,glu,mouseX,mouseY);	
		return CalculateLookAtPointInXYPlane(planeCoords);		
	}
	
	/**
	 * 
	 * @param planeCoords
	 * @return
	 */
	public static IPoint3D CalculateLookAtPointInXYPlane(Pair<double[],double[]>  planeCoords)
	{
		//should be testable now
		double f_xzP1[] = new double[2];
		double f_xzP2[] = new double[2];
		
		
		double f_yzP1[] = new double[2];
		double f_yzP2[] = new double[2];
		
		f_xzP1[0] = planeCoords.getFirst()[0];
		f_xzP1[1] = planeCoords.getFirst()[2];
		
		f_xzP2[0] = planeCoords.getSecond()[0];
		f_xzP2[1] = planeCoords.getSecond()[2];
		
		
		f_yzP1[0] = planeCoords.getFirst()[1];
		f_yzP1[1] = planeCoords.getFirst()[2];
		
		f_yzP2[0] = planeCoords.getSecond()[1];
		f_yzP2[1] = planeCoords.getSecond()[2];
		
		
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
