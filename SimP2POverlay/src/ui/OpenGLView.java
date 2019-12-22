package ui;

import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

import ui.interfaces.IMouseClickDelegate;
import ui.openGL.OpenGLViewHandler;
import ui.openGL.ViewModelNetwork;
import ui.openGL.ViewModelNetworkEvent.EventExecuteSelectedPeerAction;
import ui.openGL.ViewModelNetworkEvent.EventSetLookAtMouseClick;
import ui.openGL.interfaces.IViewmodelNetwork;

public class OpenGLView {

	GLProfile _glprofile = null;
    GLCapabilities _glcapabilities = null;
    public GLCanvas _glcanvas = null;    
    public IViewmodelNetwork _networkViewModel = new ViewModelNetwork();    
    IMouseClickDelegate _currentMouseClickDelegate = null;
    
    public OpenGLView()
    {
    	initializeModels();
    }
    
    private boolean initializeOpenGl(){
		
		boolean bSuccessfull = true;
		
		try{
			 _glprofile = GLProfile.getDefault();
		     _glcapabilities = new GLCapabilities( _glprofile );
		     _glcanvas = new GLCanvas( _glcapabilities );
		     _glcanvas.addMouseListener(_mouseListenerViewPort);		
		     
		}catch(Exception exp)
		{
			bSuccessfull = false;
		}	        
		return bSuccessfull;
	}
    
	private void initializeModels()
	{		 		
		if(initializeOpenGl())
		{
			_glcanvas.addGLEventListener( new OpenGLViewHandler(_networkViewModel));	
		}		 		 		 
	}
	
	public void UpdateCanvas()
	{
		if(_glcanvas != null)
	    {
	       _glcanvas.repaint();	        	
	    }	        
	}
	
	MouseListener _mouseListenerViewPort = new MouseListener()
	{

		@Override
		public void mouseClicked(MouseEvent e) 
		{
			
			if(_currentMouseClickDelegate == null || e.isControlDown())
			{
				OpenGLView.this._networkViewModel.PlaceNewEventDelegate(new EventSetLookAtMouseClick(e.getX(),e.getY()));	
			} else 
			{
				OpenGLView.this._networkViewModel.PlaceNewEventDelegate(new EventExecuteSelectedPeerAction(_currentMouseClickDelegate,e.getX(),e.getY()));	
			}
																															
			EventQueue.invokeLater(new Runnable(){
				public void run() 
				{
					try {
						//OpenGLView.this.UpdateUISettings();													
					} catch (Exception e) {
						e.printStackTrace();
					}
				}});
			UpdateCanvas();								
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

	};
	
}
