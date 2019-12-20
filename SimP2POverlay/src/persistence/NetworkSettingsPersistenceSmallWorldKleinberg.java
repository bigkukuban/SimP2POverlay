package persistence;

public class NetworkSettingsPersistenceSmallWorldKleinberg extends NetworkSettingsPersistenceBase implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8218094661130997737L;
	public Integer _xLength = 10;		// Number of peers in x-Direction
    public Integer _yLength = 10;		//Number of peers in y Direction
    public Integer _qParameter = 1;	//number of Long-Range-Contacts
    public Integer _pPParameter = 1;	//distance to direct neighbours
    public Double _rParameter = 1.0;	//proportionality parameter
}
