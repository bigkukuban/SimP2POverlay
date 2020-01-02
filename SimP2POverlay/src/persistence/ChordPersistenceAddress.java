package persistence;

public class ChordPersistenceAddress extends PersistenceAddressBase  implements java.io.Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1516712427917495429L;
	public ChordPersistenceAddress()
	{
		
	}
	
	public ChordPersistenceAddress(int x, int y, long identifier)
	{
		YPos = y;
		XPos = x;
		Identifier = identifier;
	}
	
	public int XPos = 0;		
	public int YPos = 0;
	public long Identifier = 0;
	
}