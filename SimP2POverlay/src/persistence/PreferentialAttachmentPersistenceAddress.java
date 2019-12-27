package persistence;

public class PreferentialAttachmentPersistenceAddress extends PersistenceAddressBase  implements java.io.Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1516712427917495429L;
	public PreferentialAttachmentPersistenceAddress()
	{
		
	}
	
	public PreferentialAttachmentPersistenceAddress(int x, int y, int birthposition)
	{
		YPos = y;
		XPos = x;
		BirthPosition = birthposition;
	}
	
	public int XPos = 0;		
	public int YPos = 0;
	public int BirthPosition = 0;	
	
}