package ui.openGL;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

import commonHelper.math.EuclideanPoint;
import commonHelper.math.interfaces.IEuclideanPoint;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;
import peersModel.interfaces.IPeerAdress;
import randomWalk.interfaces.IRandomWalkerHost;

import ui.openGL.interfaces.IPoint3D;
import ui.openGL.interfaces.IViewModelNetworkDelegate;
import ui.openGL.interfaces.IViewmodelNetwork;

public class ViewModelNetwork implements IViewmodelNetwork {

	private INetworkFacade _myNetwork;
	private GLAutoDrawable _drawable;
	private GL2 _gl2;	
		
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
	
	
			
	private void DrawConnections(IPeer peer)
	{
				
		float[] sourceCoodrs = GetPeerCoordinates(peer);								
		ArrayList<ArrayList<Point3D>> allLines = new ArrayList<ArrayList<Point3D>>();		
		for(IPeer neighbour: peer.GetAllNeighbours() )
		{
			float[] targetCoords =  GetPeerCoordinates(neighbour);	
									
			//dinstance = 1 --> 0.01 heigth
			float lineHeight = 0.1f;						
			
			ArrayList<Point3D> linePoints = OpenGlPrimitives.calculateConnector(sourceCoodrs[0],sourceCoodrs[1],targetCoords[0],targetCoords[1],lineHeight);	
			allLines.add(linePoints);			
		}	
		
		DrawManyLines(allLines,_drawable.getGL().getGL2());
	}

	private void DrawManyLines(ArrayList<ArrayList<Point3D>> lines, GL2 gl)
	{		
		gl.glLoadIdentity();	    
		
		
		for(ArrayList<Point3D> line : lines)
		{
			gl.glBegin(GL.GL_LINES);
			gl.glColor3f(0,127, 0);
				for(Point3D pnt : line)
				{
					gl.glVertex3d(pnt.GetPosX(),pnt.GetPosY(),pnt.GetPosZ());
				}
			gl.glEnd();
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

