package networkInitializer.gridStructured;

import networkInitializer.NetworkSettingsBase;

public class NetworkSettingsGrid extends NetworkSettingsBase
{
 	public Integer XLength = 10;	// Number of peers in x-Direction
    public Integer YLength = 10;	//Number of peers in y Direction
    
    public NetworkSettingsGrid(int xLength, int yLength)
    {
    	XLength = xLength;
    	YLength = yLength;
    }


}
