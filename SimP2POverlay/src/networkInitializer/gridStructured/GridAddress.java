package networkInitializer.gridStructured;

import commonHelper.math.EuclideanPoint;
import commonHelper.math.interfaces.IEuclideanPoint;
import peersModel.interfaces.IPeerAdress;

public class GridAddress implements IPeerAdress  {
	
	int _xPos = 0;
	@Override
	public int GetPositionX()
	{
		return _xPos;
	}
		
	int _yPos = 0;
	@Override
	public int GetPositionY()
	{
		return _yPos;
	}
	
	public GridAddress(int xPos, int yPos)
	{
		_xPos = xPos;
		_yPos = yPos;
	}
	
	public int GetDistance(IPeerAdress toMe) {
		return (int) Math.sqrt( Math.pow(toMe.GetPositionX() - GetPositionX(), 2) + Math.pow(toMe.GetPositionY() - GetPositionY(), 2));			
	}
	
	public double GetCartesianDistance(IPeerAdress toMe) 
	{		
		return Math.sqrt( Math.pow(toMe.GetPositionX() - GetPositionX(), 2) + Math.pow(toMe.GetPositionY() - GetPositionY(), 2)) ;		
	}

	@Override
	public IEuclideanPoint GetPoint() {

		return new EuclideanPoint(new double[]{_xPos,_yPos});
	}
}
