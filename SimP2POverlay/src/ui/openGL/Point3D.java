package ui.openGL;

import ui.openGL.interfaces.IPoint3D;

public class Point3D implements IPoint3D 
{
	public Point3D()
	{
		 _posX = 0.5f;
		 _posY = 0.5f;
		 _posZ = 0;
	}
	
	
	public Point3D(float posX,float posY,float posZ)
	{
		 _posX = posX;
		 _posY = posY;
		 _posZ = posZ;
	}
	
	private float _posX;
	private float _posY;
	private float _posZ;
	
	public float GetPosX(){
		return _posX;
	};
	public float GetPosY()
	{
		return _posY;
	}
	public float GetPosZ()
	{
		return _posZ;
	}
	
	

	public void SetPosX(float value){
		_posX = value;
	};
	public void SetPosY(float value)
	{
		_posY = value;
	}
	public void SetPosZ(float value)
	{
		_posZ = value;
	}
}
