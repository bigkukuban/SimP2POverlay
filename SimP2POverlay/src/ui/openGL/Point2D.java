package ui.openGL;

import ui.openGL.interfaces.IPoint2D;

public class Point2D implements IPoint2D 
{
	private float _posX;
	private float _posY;	
	
	public float GetPosX(){
		return _posX;
	};
	public float GetPosY()
	{
		return _posY;
	}
		

	public void SetPosX(float value){
		_posX = value;
	};
	public void SetPosY(float value)
	{
		_posY = value;
	}

}
