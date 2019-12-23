package networkInitializer.baPreferentialAttachment;

import networkInitializer.NetworkSettingsBase;

public class NetworkSettingsBaPreferentialAttachment extends NetworkSettingsBase
{
	public NetworkSettingsBaPreferentialAttachment(){}
	public NetworkSettingsBaPreferentialAttachment(int m0Prm, int mPrm, int NPrm)
	{
		m0 = m0Prm;
		m = mPrm;
		N = NPrm;
	}
	
 	public Integer m0 = 2;			// Number of initial peers (not connected to each other)
 	public Integer m  = 2;			// Number of connections each peer holds
    public Integer N = 10;			// The whole number of peers to be generated 
    
}
