package persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PersistenceContainer implements java.io.Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6107861111873471162L;
	
	public NetworkSettingsPersistenceBase NetworkSpecialSettings;
	
	public int DimensionsNetwork[];
	public ArrayList<PeerEntry> peerList = new ArrayList<PeerEntry>();
	public Map<Long, ArrayList<Long>>  ListPeerConnectionsShortRange = new HashMap<Long, ArrayList<Long>>(); 
	public Map<Long, ArrayList<Long>>  ListPeerConnectionsLongRange = new HashMap<Long, ArrayList<Long>>();

}
