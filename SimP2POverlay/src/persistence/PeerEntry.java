package persistence;

import java.util.ArrayList;

public class PeerEntry implements java.io.Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7514899113012688661L;
	public Long PeerId;
	public AddressBase Address;
	public ArrayList<Long> ShortRangeConnections  = new ArrayList<Long>();
	public ArrayList<Long> LongRangeConnections  = new ArrayList<Long>();
	
}
