package ui.openGL;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

class GlDisplayLists
{
	private GLAutoDrawable _drawable;
	
	public void Initialize(GLAutoDrawable drawable)
	{
		_drawable = drawable;
	}
	
	public void RecallAxes()
	{
		GL2 gl = _drawable.getGL().getGL2();
		gl.glCallList(GLListAxes);
	}
	
	public void RecallPeers()
	{
		GL2 gl = _drawable.getGL().getGL2();
		gl.glCallList(GLListPeers);
	}
	
	public void RecallConnections()
	{
		GL2 gl = _drawable.getGL().getGL2();
		gl.glCallList(GLListConnections);
	}

	public void ResetConnectionLists()
	{
		
		if(!IsConnectionsAvaliable()) return;
		
		GL2 gl = _drawable.getGL().getGL2();
		
		
		gl.glDeleteLists(GLListConnections, 1);
		GLListConnections = -1; 
	}
	
	public void ResetAllLists()
	{
		GL2 gl = _drawable.getGL().getGL2();
		gl.glDeleteLists(GLListConnections, 1);
		gl.glDeleteLists(GLListPeers, 1);
		gl.glDeleteLists(GLListAxes, 1);
		GLListConnections = -1;
		GLListPeers = -1;
		GLListAxes = -1;
	}
	
	public boolean IsConnectionsAvaliable()
	{
		return GLListConnections != -1; 
	}
	
	public boolean IsListPeersAvaliable()
	{
		return GLListPeers != -1; 
	}
	
	public boolean IsListAxesAvaliable()
	{
		return GLListAxes != -1; 
	}
	
	public int GLListConnections = -1;
	public int GLListPeers = -1;
	public int GLListAxes = -1;				
}