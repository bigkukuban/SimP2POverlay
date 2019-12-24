package persistence;

public class NetworkSettingsPersistencePreferentialAttachment extends NetworkSettingsPersistenceBase implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8218094661130999737L;
	public Integer m0 = 2;		// Number of initial peers (not connected to each other)
 	public Integer m  = 2;		// Number of connections each peer holds
    public Integer N = 10;		// The whole number of peers to be generated
}