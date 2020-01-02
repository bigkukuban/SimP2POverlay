package persistence;

public class NetworkSettingsPersistenceChord extends NetworkSettingsPersistenceBase implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8218096861130999737L;
	public Integer MParameter = 0;	// identifier length
    public Integer NParamter = 0;	// number of nodes
    public boolean UseRandomPlacing = false;	//nodes are places at random identifier in the network
}