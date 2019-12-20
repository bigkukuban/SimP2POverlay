package peersModel.interfaces;

import commonHelper.math.interfaces.IEuclideanPoint;

public interface IPeerAdress {
	
	int GetDistance(IPeerAdress toMe);	
	public int GetPositionX();		
	public int GetPositionY();

	public IEuclideanPoint GetPoint();
	
}
