package ui.openGL;

import java.util.ArrayDeque;
import java.util.Deque;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

import commonHelper.GlobalLogger;
import commonHelper.math.EuclideanPoint;
import commonHelper.math.LinearAlgebraHelper;
import commonHelper.math.interfaces.IEuclideanPoint;
import commonHelper.math.interfaces.IVector;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;
import peersModel.interfaces.IPeerAdress;
import randomWalk.interfaces.IRandomWalkerHost;
import ui.interfaces.IMouseClickDelegate;
import ui.openGL.interfaces.IPoint3D;
import ui.openGL.interfaces.IViewModelNetworkDelegate;
import ui.openGL.interfaces.IViewmodelNetwork;

public class ViewModelNetwork implements IViewmodelNetwork {

	private INetworkFacade _myNetwork;
	private GLAutoDrawable _drawable;
	private GL2 _gl2;	
	
	boolean _renderLongRangeContacts = true;
	boolean _renderNearRangeContacts = true;
	
	float _stepX;
	float _stepY;	
	double _radius = 0.0;			
	float _viewAngle = 60.0f;	
		
	private GLUT _glut = new GLUT();  // for drawing the teapot
	
	LookAtPointCalculator _lookAtCalculator;
	
	IPoint3D _lookAtPoint = new Point3D();	
	IPoint3D _cameraPosition = new Point3D();
	
	IEuclideanPoint _selectionPoint = new EuclideanPoint(new double[]{0,0,0});
	
	GlDisplayLists _displayLists = new GlDisplayLists(); 
		
	
	
	public ViewModelNetwork()
	{		
		Reset();		
		_lookAtCalculator = new LookAtPointCalculator();				
	}
	
	public void SetViewPortMouseClickPosition(int xPos, int yPos, boolean withLookAtPointUpdate)
	{	
		_lookAtCalculator.AssignNewMouseCoordinates(xPos, yPos);
		
		if(withLookAtPointUpdate )
		{
			_lookAtPoint = _lookAtCalculator.CalculateLookAtPointInXYPlane();	
		}						
	}
	
	
	
	/* (non-Javadoc)
	 * @see ui.openGL.IViewmodelNetwork#SetContext(com.jogamp.opengl.GLAutoDrawable)
	 */
	@Override
	public void SetContext(GLAutoDrawable drawable, GL2 gl2,GLU glu)
	{
		_drawable = drawable;
		_displayLists.Initialize(drawable);
		_lookAtCalculator.SetContext(drawable,gl2,glu);
		_gl2 = gl2;		
	
		
	}
	 
	Deque<IViewModelNetworkDelegate> _occuredEvents = new ArrayDeque<IViewModelNetworkDelegate>();
	
	//called by framework
	public void EvaluateViewEventsBeforeDrawing()
	{
		for(IViewModelNetworkDelegate evt : _occuredEvents)
		{
			evt.HandleBeforeDrawing(this);
		}
	}
	
	@Override
	public void EvaluateViewEventsAfterDrawing()
	{
		for(IViewModelNetworkDelegate evt : _occuredEvents)
		{
			evt.HandleAfterDrawing(this);
		}		
		_occuredEvents.clear();
	}
	
	public void PlaceNewEventDelegate(IViewModelNetworkDelegate evt)
	{
		_occuredEvents.add(evt);	
	}
	
	
	
	/* (non-Javadoc)
	 * @see ui.openGL.IViewmodelNetwork#SetNetwork(peersModel.interfaces.INetworkFacade)
	 */
	@Override
	public void SetNetwork(INetworkFacade nw)
	{				
		_myNetwork = nw;		
		
		int[] dim = _myNetwork.GetDimenstions();
		
		_stepX = 1.0f/(float)dim[0];
		_stepY = 1.0f/(float)dim[1];
		
		recalculatePeerRadius();		
		_displayLists.ResetAllLists();	
		
	}
		
	/* (non-Javadoc)
	 * @see ui.openGL.IViewmodelNetwork#GetNetwork()
	 */
	@Override
	public INetworkFacade GetNetwork()
	{
		return _myNetwork;	
	}
	

	/* (non-Javadoc)
	 * @see ui.openGL.IViewmodelNetwork#RepantNetwork()
	 */
	@Override
	public void RepaintNetwork()
	{				
		if(_myNetwork == null) return;
				
		
		if(this._displayLists.IsListAxesAvaliable())
		{
			this._displayLists.RecallAxes();
		} else 
		{
			this._displayLists.GLListAxes = _gl2.glGenLists(1);
			_gl2.glNewList(this._displayLists.GLListAxes, GL2.GL_COMPILE_AND_EXECUTE);
						
			OpenGlPrimitives.drawAxes(_drawable);
			_gl2.glEndList();	
		}
	
			
		DrawPeers();
		
		if(this._displayLists.IsConnectionsAvaliable())
		{
			this._displayLists.RecallConnections();
		} else 
		{
			this._displayLists.GLListConnections = _gl2.glGenLists(1);
			
			_gl2.glNewList(this._displayLists.GLListConnections, GL2.GL_COMPILE_AND_EXECUTE);
						
			DrawAllConnections();
			_gl2.glEndList();	
		}		
	}
	
	
	
	private void recalculatePeerRadius()
	{
		_radius = _stepX;		
		if(_radius > _stepY)
		{
			_radius = _stepY;
		}		
		_radius = _radius / 10;
				
	}
		
	public IRandomWalkerHost GetLastSelectedPeer()
	{
		IPoint3D currentSelected = _lookAtCalculator.CalculateLookAtPointInXYPlane();
		IEuclideanPoint selectionPoint = new EuclideanPoint(new double[]{currentSelected.GetPosX(),currentSelected.GetPosY(),currentSelected.GetPosZ()});
		
		return (IRandomWalkerHost)GetSelectedPeer(selectionPoint);
	}
	
	private IPeer GetSelectedPeer(IEuclideanPoint selectedPoint)
	{		
		IPeer result = null;
		for(IPeer peer: _myNetwork.GetPeers())
		{
			float[] coords =  GetPeerCoordinates(peer);				
			EuclideanPoint peerPos = new EuclideanPoint(coords);			
			double dist = peerPos.GetDistanceToOther(selectedPoint);			
			if(dist <= _radius)
			{
				result = peer;
			}
		}	
		return result;
	}
	
	private void DrawPeers()
	{
		if(_myNetwork == null) return;
		
		GL2 gl = _drawable.getGL().getGL2();
								
		gl.glMatrixMode(GL2.GL_MODELVIEW);		
		gl.glLoadIdentity();
										
		for(IPeer peer: _myNetwork.GetPeers())
		{								
			float[] coords =  GetPeerCoordinates(peer);
						
			IRandomWalkerHost peerHost = (IRandomWalkerHost)peer;
						
			int color = peerHost.GetHostColor();
			
			gl.glLoadIdentity();	
			gl.glTranslatef(coords[0], coords[1], 0.0f);
			
			float colorR = ((float)((color&0xFF0000)>>16))/255.0f; 
			float colorG = ((float)((color&0x00FF00)>>8))/255.0f;
			float colorB = ((float)((color&0x00FF)))/255.0f;
			
			gl.glColor3f(colorR,colorG,colorB);	
									
			_glut.glutWireSphere(_radius, 4, 4);
			
			
			DrawPeerLoadState(peer);
			DrawPeerLoadBalancingForce(peer);
			DrawConnections(peer);
		}		
	}
	
	
	private void DrawAllConnections()
	{
		if(_myNetwork == null) return;
						
		for(IPeer peer: _myNetwork.GetPeers())
		{																		
			DrawConnections(peer);
		}	
	}
	
	private float[] GetPeerCoordinates(IPeer peer )
	{
		float[] result = new float[3];
		
		IPeerAdress address  = peer.GetNetworkAdress(); 
		
		result[0] = address.GetPositionX()*_stepX;
		result[1] = address.GetPositionY()*_stepY;
		result[2] = 0;
		
		return result;
	}
	
	private void DrawPeerLoadBalancingForce(IPeer peer)
	{
		GL2 gl = _drawable.getGL().getGL2();
		float[] coords = GetPeerCoordinates(peer);
		IVector loadForce = peer.GetLoadForce();
		double [] loadForceDirection =  loadForce.GetUnit();
		double length = loadForce.GetLength(); // normalize the length to 10.000 --> one step
						
		length = (1-1/(0.1*length+1))*_stepX*0.5;
	
		gl.glPushMatrix();
		
		gl.glLoadIdentity();		
				
		gl.glTranslatef(coords[0], coords[1], 0.0f);
				
		/*
		gl.glBegin(GL.GL_LINES);			
			gl.glVertex3d(0,0,_radius);			
			gl.glVertex3d(5*_radius*loadForceDirection[0],5*_radius*loadForceDirection[1],0);
		
		gl.glEnd();
		*/		
		gl.glRotated(90,0, 1, 0.0f);
		
		double angle = LinearAlgebraHelper.CalculateAngleForVector2D(5*_radius*loadForceDirection[0],5*_radius*loadForceDirection[1]);
		
		gl.glRotated(-angle,1, 0, 0);
		gl.glColor3d(1.3,0,1.5);	
		_glut.glutWireCone(0.3*_radius, length, 5, 5);
								
		//GlobalLogger.LogBroadCasts(" Load direction X"+loadForceDirection[0]+ " Y: "+loadForceDirection[1]+" Length: "+_radius + " angle :"+angle);		 
		gl.glPopMatrix();
		
	}
	
	private void DrawPeerLoadState(IPeer peer)
	{
		GL2 gl = _drawable.getGL().getGL2();
		
		float[] coords = GetPeerCoordinates(peer);
		
		double loadState =   Math.log(peer.GetCurrentLoadState())/10;
					
		gl.glPushMatrix();
		gl.glLoadIdentity();		
		gl.glTranslatef(coords[0], coords[1], 0.0f);				
	    gl.glBegin(GL.GL_LINES);
	    gl.glColor3d(100.0,0.0,150.0);	    
	    gl.glVertex3d(0,0,0);	    
	    gl.glVertex3d(0,0,loadState);	    
	    gl.glEnd();
	    
	    if(peer.GetCurrentLoadState() > 0)
		{ 
		    gl.glLoadIdentity();
		    gl.glTranslated(coords[0]+_radius*1.2f, coords[1]+_radius/2.0f, (float) _radius*3.0f);
		    gl.glScaled(_radius*0.01,_radius*0.01, _radius*0.01);
		    gl.glColor3d(100.0,100.0,0.0);
		    _glut.glutStrokeString(GLUT.STROKE_ROMAN, String.format("%d", peer.GetCurrentLoadState()));			    	    
		
		}
	    
	    //peer id 
	    gl.glLoadIdentity();
	    gl.glTranslated(coords[0]-_radius*3f, coords[1]+_radius/2.0f, (float) _radius*3.0f);
	    gl.glScaled(_radius*0.01,_radius*0.01, _radius*0.01);
	    gl.glColor3d(40.0,0.0,0.0);
	    _glut.glutStrokeString(GLUT.STROKE_ROMAN, String.format("%d", peer.GetPeerID()));
	    
	    gl.glPopMatrix();
	    //and peer id
	    
	}
	
	private void DrawConnections(IPeer peer)
	{
		GL2 gl = _drawable.getGL().getGL2();
		
		float[] sourceCoodrs = GetPeerCoordinates(peer);
		
		gl.glLoadIdentity();
		if(_renderLongRangeContacts )
		{
			gl.glColor3f(127,0, 0);
			for(IPeer neighbour: peer.GetLongRangeNeighbours() )
			{
				float[] targetCoords =  GetPeerCoordinates(neighbour);													
				OpenGlPrimitives.drawConnector(sourceCoodrs[0],sourceCoodrs[1],targetCoords[0],targetCoords[1],0.1f, _drawable);
			}	
		}
		
		if(_renderNearRangeContacts )
		{		
			gl.glColor3f(0,127, 0);
			for(IPeer neighbour: peer.GetNearNeighbours() )
			{
				float[] targetCoords =  GetPeerCoordinates(neighbour);
				
				OpenGlPrimitives.drawConnector(sourceCoodrs[0],sourceCoodrs[1],targetCoords[0],targetCoords[1],0.01f, _drawable);
												
			}	
		}			
	}


	public void Reset()
	{
		
		_cameraPosition.SetPosX(0.5f);
		_cameraPosition.SetPosY(0.5f);
		_cameraPosition.SetPosZ(0.5f);
		
		_lookAtPoint.SetPosX(0.5f);
		_lookAtPoint.SetPosY(0.5f);
		_lookAtPoint.SetPosZ(0.0f);
		
		_viewAngle = 60f;
				
		_renderLongRangeContacts = true;
		_renderNearRangeContacts = true;
	}
	

	public void SetLongRangeContactsShownGui(boolean value)
	{
		_renderLongRangeContacts = value;
		_displayLists.ResetConnectionLists();
	
	}
	public void SetNearContactsShownGui(boolean value)
	{
		_renderNearRangeContacts = value;
		_displayLists.ResetConnectionLists();
		
	}
	public boolean GetLongRangeContactsShownGui()
	{		
		return _renderLongRangeContacts;
	}
	public boolean GetNearContactsShownGui()
	{
		return _renderNearRangeContacts;
	}
				
	public void SetZoomAngle(float zoom)
	{
		if(zoom >=180) zoom = 179;
		_viewAngle = ((float)zoom);	
	}
	
	
	public float GetZoomAngle(){ return _viewAngle; 	}
	
	public IPoint3D GetCameraPosition()
	{			
		return _cameraPosition;
	}
	
	public IPoint3D GetLookAtPosition()
	{				
		return _lookAtPoint;					
	}



}

