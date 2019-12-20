package ui.openGL.interfaces;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

import peersModel.interfaces.INetworkFacade;
import randomWalk.interfaces.IRandomWalkerHost;

public interface IViewmodelNetwork {

	/**
	 * First function to be called
	 * @param drawable
	 * @param gl2
	 * @param glu
	 */
	void SetContext(GLAutoDrawable drawable, GL2 gl2,GLU glu);
	
	/**
	 * This function should be called before cleaning the screen
	 * and update the projection matrix
	 */
	void EvaluateViewEventsBeforeDrawing();
	
	/**
	 *The may exist events that should be handeled after drawing of the view 
	 *
	 */
	void EvaluateViewEventsAfterDrawing();

	
	void PlaceNewEventDelegate(IViewModelNetworkDelegate evt);
	
	void SetNetwork(INetworkFacade nw);
	INetworkFacade GetNetwork();

	/**
	 * Draws the network peers
	 */
	void RepaintNetwork();	
	
	/**
	 * Resets the settings
	 */
	void Reset();
	
	
	void SetLongRangeContactsShownGui(boolean value);
	void SetNearContactsShownGui(boolean value);
	boolean GetLongRangeContactsShownGui();
	boolean GetNearContactsShownGui();
		
	
	void SetViewPortMouseClickPosition(int xPos, int yPos, boolean withPosUpadate);
		
	/**
	 * Sets the view angle in degree (0..360°)
	 * @param zoom
	 */
	void SetZoomAngle(float zoom);		
	
	/**
	 * Gets the view angle in degree (0..360°)
	 * @param zoom
	 */
	float GetZoomAngle();
	
	IPoint3D GetCameraPosition();	
	IPoint3D GetLookAtPosition();
	

	/**
	 * Returns the last by mouse click selected peer
	 * @return
	 */
	IRandomWalkerHost GetLastSelectedPeer();
}