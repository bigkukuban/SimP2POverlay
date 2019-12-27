package persistence;

public class GridPersistenceAddress extends PersistenceAddressBase  implements java.io.Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1516712427917495429L;
	public GridPersistenceAddress()
	{
		
	}
	
	public GridPersistenceAddress(int x, int y)
	{
		YPos = y;
		XPos = x;
	}
	
	public int XPos = 0;		
	public int YPos = 0;
	
}
