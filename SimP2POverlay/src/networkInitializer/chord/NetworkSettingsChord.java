package networkInitializer.chord;

import networkInitializer.NetworkSettingsBase;

public class NetworkSettingsChord extends NetworkSettingsBase 
{
	public int _m; // identifier length in bits
	public int _N; // number of nodes to generate
	public boolean _UseRandomNodePlacingInRing;
	
	public NetworkSettingsChord(int m, int N, boolean useRandomNodePlacingInRing)
	{
		_m = m;
		_N = N;
		_UseRandomNodePlacingInRing = useRandomNodePlacingInRing;
		
	}

}
