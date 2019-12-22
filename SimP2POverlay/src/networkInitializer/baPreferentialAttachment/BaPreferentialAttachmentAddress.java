package networkInitializer.baPreferentialAttachment;

import commonHelper.math.EuclideanPoint;
import commonHelper.math.interfaces.IEuclideanPoint;
import peersModel.interfaces.IPeerAdress;

public class BaPreferentialAttachmentAddress implements IPeerAdress {

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
	
	public BaPreferentialAttachmentAddress(int xPos, int yPos)
	{
		_xPos = xPos;
		_yPos = yPos;
	}
	
	/* 
	 * TODO: Distance for preferential attachment depends on the adding time of a peer to the network. We need a kind of addin time for each address. 
	 * This will be the address and the distance in the difference between addresses.
	 * 
	 * The adding time is the address 
	 * 
	 */
	
	@Override
	public int GetDistance(IPeerAdress toMe) {
		return Math.abs(((BaPreferentialAttachmentAddress)toMe).GetPositionX() - GetPositionX()) + Math.abs(((BaPreferentialAttachmentAddress)toMe).GetPositionY() - GetPositionY()) ;		
	}
	
	@Override
	public IEuclideanPoint GetPoint() {

		return new EuclideanPoint(new double[]{_xPos,_yPos});
	}

}
