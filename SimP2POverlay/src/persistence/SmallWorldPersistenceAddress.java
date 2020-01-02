package persistence;

public class SmallWorldPersistenceAddress extends PersistenceAddressBase  implements java.io.Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1516712427917495429L;
	public SmallWorldPersistenceAddress()
	{
		
	}
	
	public SmallWorldPersistenceAddress(int x, int y)
	{
		YPos = y;
		XPos = x;
	}
	
	public int XPos = 0;		
	public int YPos = 0;
	
}


