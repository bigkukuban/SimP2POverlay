package ui.openGL;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;

import ui.openGL.interfaces.IPoint3D;
import ui.openGL.interfaces.IViewmodelNetwork;

public class OpenGLViewHandler implements GLEventListener {
		 
	private IViewmodelNetwork model; 
	
	public OpenGLViewHandler (IViewmodelNetwork networkView)
	{
		model = networkView;
	}
	
	
	@Override
	public void display(GLAutoDrawable glautodrawable) 
	{
		
		GL2 gl = glautodrawable.getGL().getGL2();
		
		GLU glu = GLU.createGLU(gl); 
		
		model.SetContext(glautodrawable, gl, glu);
		
		model.EvaluateViewEventsBeforeDrawing();
		
		double ratio = GetRatio(gl);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		IPoint3D  pos = model.GetCameraPosition();
		IPoint3D  lookAt = model.GetLookAtPosition();
			//angle, aspec-ratio (width/heigh), must near object to render, most far object to be rendered
		glu.gluPerspective(model.GetZoomAngle(), ratio, 0.01, 100);
			//gluLookAt(MyPos[GLdouble eyex, GLdouble eyey, GLdouble, eyez]LookAt[GLdouble centerx, GLdouble centery, GLdouble, centerz] Up-Dir[GLdouble upx, GLdouble upy, GLdouble upz]);
		glu.gluLookAt(pos.GetPosX(),pos.GetPosY(), pos.GetPosZ(), lookAt.GetPosX(),lookAt.GetPosY(),lookAt.GetPosZ(), 0.0f, 1.0f, 0.0f);					
		
		gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
		gl.glMatrixMode(GL2.GL_MODELVIEW);
				
		
		gl.glLoadIdentity();             // Set up modelview transform.
						
		model.RepaintNetwork();
		
		// handle now events after the view was drawn
		model.EvaluateViewEventsAfterDrawing();
	}

	
	@Override
	/**
	 * Notifies the listener to perform the release of all 
	 * OpenGL resources per GLContext, such as memory buffers and GLSL programs.
	 */
	public void dispose(GLAutoDrawable glautodrawable) {
		// TODO Auto-generated method stub
		
	}

	private double GetRatio(GL2 gl)
	{
		int[] viewPort = new int[4];
		
		gl.glGetIntegerv( GL2.GL_VIEWPORT, viewPort,0 );
		
		return (double)viewPort[2] / viewPort[3]; 
		
	}
	
	@Override
	/**
	 * Called by the drawable immediately after the OpenGL context is initialized.
	 */
	public void init(GLAutoDrawable glautodrawable) {
		
		GL2 gl = glautodrawable.getGL().getGL2();
		gl.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
		gl.glEnable(GL.GL_DEPTH_TEST);
		
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		
		
		GLU glu = GLU.createGLU(gl); 
		
		double ratio = GetRatio(gl);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(40, ratio, 1, 10);
						
		//gluLookAt(MyPos[GLdouble eyex, GLdouble eyey, GLdouble, eyez]LookAt[GLdouble centerx, GLdouble centery, GLdouble, centerz] Up-Dir[GLdouble upx, GLdouble upy, GLdouble upz]);
		glu.gluLookAt(0.5, 0.5, 5.0, 0.5,0.5, 0, 0.0f, 1.0f, 0.0f);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		
		
	}

	@Override
	/**
	 * Called by the drawable during the first repaint after the component has been resized.
	 */
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		
	}

}
