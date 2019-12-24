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
	
	public ArrayList<NetworkSettingsPersistenceBase> NetworkSpecialSettings;
	public int CurrentActiveSettings;
	public int DimensionsNetwork[];
	public ArrayList<PeerEntry> peerList = new ArrayList<PeerEntry>();	
	public Map<Long, ArrayList<Long>>  ListPeerConnections = new HashMap<Long, ArrayList<Long>>();

}
