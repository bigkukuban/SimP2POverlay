package networkInitializer.chord;

import commonHelper.math.EuclideanPoint;
import commonHelper.math.interfaces.IEuclideanPoint;
import peersModel.interfaces.IPeerAdress;

public class ChordAddress  implements IPeerAdress 
{

	public long _identifier;
	public long GetIdentifier()
	{
		return _identifier;
	}
	
	public void SetIdentifier(long ident)
	{
		_identifier = ident;
	}
	
	public ChordAddress(int xPos, int yPos,long identifier )
	{
		_xPos = xPos;
		_yPos = yPos;
		_identifier = identifier;
	}
	
	@Override
	public int GetDistance(IPeerAdress toMe) {
		
		ChordAddress other = (ChordAddress)toMe;		
		return (int) Math.abs(_identifier - other.GetIdentifier());
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

	@Override
	public IEuclideanPoint GetPoint() {
		return new EuclideanPoint(new double[]{_xPos,_yPos});
	}

}
