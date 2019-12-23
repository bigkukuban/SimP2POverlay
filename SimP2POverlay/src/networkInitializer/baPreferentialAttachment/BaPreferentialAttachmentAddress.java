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
	
	int _birthPosition = 0;	
	public int GetBirthPosition()
	{
		return _birthPosition;
	}
	
	
	public BaPreferentialAttachmentAddress(int xPos, int yPos, int birthPosition)
	{
		_xPos = xPos;
		_yPos = yPos;
		_birthPosition = birthPosition;
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
		return Math.abs(((BaPreferentialAttachmentAddress)toMe)._birthPosition - _birthPosition) ;		
	}
	
	@Override
	public IEuclideanPoint GetPoint() {

		return new EuclideanPoint(new double[]{_xPos,_yPos});
	}

}
