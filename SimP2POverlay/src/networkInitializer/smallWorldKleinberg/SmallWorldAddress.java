package networkInitializer.smallWorldKleinberg;

import commonHelper.math.EuclideanPoint;
import commonHelper.math.interfaces.IEuclideanPoint;
import peersModel.interfaces.IPeerAdress;

public class SmallWorldAddress implements IPeerAdress 
{
	public SmallWorldAddress(int xPos, int yPos)
	{
		_xPos = xPos;
		_yPos = yPos;
	}
	
	int _xPos = 0;
	public int GetPositionX()
	{
		return _xPos;
	}
		
	int _yPos = 0;
	public int GetPositionY()
	{
		return _yPos;
	}
	
	
	
	/**
	 * if the function retuns 0, so the own address is found
	 */
	public int GetDistance(IPeerAdress toMe)
	{
		return Math.abs(((SmallWorldAddress)toMe).GetPositionX() - GetPositionX()) + Math.abs(((SmallWorldAddress)toMe).GetPositionY() - GetPositionY()) ;		
	}
	
	
	private Object _tempInternalData;
	public Object GetPrivateTempData()
	{
		return _tempInternalData;
	}
	
	public void SetPrivateTempData(Object obj)
	{
		_tempInternalData = obj;
	}



	@Override
	public IEuclideanPoint GetPoint() {

		return new EuclideanPoint(new double[]{_xPos,_yPos});
	}
}
