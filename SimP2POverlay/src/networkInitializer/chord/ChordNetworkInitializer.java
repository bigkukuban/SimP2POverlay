package networkInitializer.chord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import networkInitializer.interfaces.INetworkInitializer;
import peersModel.implementation.NetworkFacade;
import peersModel.implementation.Peer;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;

/**
 * For documentation see Chord: A Scalable Peer-to-peer Lookup Service for Internet Applications.
 * Ion Stoica, Robert Morris, David Karger, David Liben-Nowell,M. Frans Kaashoek,Frank Dabek
 * @author Dimitri
 *
 */
public class ChordNetworkInitializer implements INetworkInitializer
{

	NetworkSettingsChord _settingsToUse = null;
	
	public ChordNetworkInitializer(NetworkSettingsChord settingsToUse) throws Exception
	{				
		if(Math.pow(2, settingsToUse._m) < settingsToUse._N)
		{
			throw new Exception("Wrong number of peers and identifier length ... ");
		}
		_settingsToUse = settingsToUse;		
	}
	
	@Override
	public INetworkFacade GetInitializedNetwork() {
		
		ArrayList<IPeer> lstPeers = null; 

		//generate initial peers with according identifiers
		
		if(_settingsToUse._UseRandomNodePlacingInRing)
		{
			lstPeers = GeneratePeersWithRandomDistribution(_settingsToUse._N, _settingsToUse._m);
		} else 
		{
			lstPeers = GeneratePeersWithEqualDistribution(_settingsToUse._N, _settingsToUse._m);
		}
		
		List<IPeer> connectedPeers = ConnectPeers(lstPeers, _settingsToUse._m );
		
		NetworkFacade result = new NetworkFacade(lstPeers, new int[]{0,0});
		
		return result;
	}
	
	public static List<IPeer> ConnectPeers(ArrayList<IPeer> listOfPeers, int identifierBitLength )
	{
		List<IPeer> sortedPeers = SortPeersIncreasingByIdentifier(listOfPeers); 
		
		/* The ith entry in the table at node n contains the identity of the first node, s, that
			succeeds n by at least 2i-1 on the identifier circle, i.e., s = successor(n + 2i-1 ),
			where 1 <= i <= m (and all arithmetic is modulo 2m ).
		*/

		
		return listOfPeers;
	}
	
	public static List<IPeer> SortPeersIncreasingByIdentifier(ArrayList<IPeer> listOfPeers) 
	{
		//sort peers by identifiert and start with smallest value
		List<IPeer> sortedList = listOfPeers.stream().
								sorted((o1,o2)->   (int )( ((ChordAddress)o1.GetNetworkAdress())._identifier-((ChordAddress)o2.GetNetworkAdress())._identifier)   ).
							    collect(Collectors.toList());				
		return sortedList;			
		
	}
	
	private static ArrayList<IPeer> GeneratePeersWithEqualDistribution(int number, int identifierBitLength)
	{
		ArrayList<IPeer> result = new ArrayList<IPeer>();
				
		long maxIdentifierValue =(long) Math.pow(2, identifierBitLength);
		
		long identifierStep = maxIdentifierValue / number;
		
		if(identifierStep <= 0) return result;
		
		for(long i=0; i<identifierStep; i = identifierStep + i)
		{
			Peer p = new Peer();							
			p.SetNetworkAdress( new ChordAddress(0,0,i));
		}
		
		return result;
	}
	
	//generate
	private static ArrayList<IPeer> GeneratePeersWithRandomDistribution(int number, int identifierBitLength)
	{
		long minIdentifierValue =0;
		long maxIdentifierValue =(long) Math.pow(2, identifierBitLength);
		ArrayList<IPeer> result = new ArrayList<IPeer>();
		ArrayList<Long> alreadyUsedIdentifier = new ArrayList<Long>();
		for(int i=0; i<number; i++)
		{
			Peer p = new Peer();
			
			long identifier = GetNextRandomPeerIdentifier( alreadyUsedIdentifier, minIdentifierValue, maxIdentifierValue);
			alreadyUsedIdentifier.add(identifier);
			p.SetNetworkAdress( new ChordAddress(0,0,identifier));
		}
		
		return result;
	}
	
	public static long GetNextRandomPeerIdentifier(ArrayList<Long> usedIdentifiert, long min, long max)
	{
		long value = (long)((max-min) * Math.random()) + min;
		
		while(usedIdentifiert.contains(value))
		{
			 value = (long)((max-min) * Math.random()) + min;
		}
		
		return value;
	}

	@Override
	public long GetNumberOfItemsItendedToCreate() {
		return _settingsToUse._N;
	}

	@Override
	public String GetReadableDescription() 
	{
		return "Generates a network of peers as defined by chord protocol (stoica). Parameters N:" +_settingsToUse .toString()+" m:"+_settingsToUse._m;
	}

}
