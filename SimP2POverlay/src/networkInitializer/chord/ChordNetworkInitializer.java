package networkInitializer.chord;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Pair;

import commonHelper.math.Vector_XD;
import commonHelper.math.interfaces.IVector;
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
		int[] dimenstions = ArrangePeersIn2DSpace(connectedPeers,_settingsToUse._m);		
		
		NetworkFacade result = new NetworkFacade(lstPeers, dimenstions);
		
		return result;
	}
	
	public static int[] ArrangePeersIn2DSpace(List<IPeer> listOfPeers, int identifierBitLength )
	{
		//peers are still sorted by their identifiert, thus we plate the first peer at the position 0 degree  in the circle.
		double minStep = 360.0 / Math.pow(2, identifierBitLength);
							
		// get two peers with min identifiert distance
		IPeer first = listOfPeers.get(0);
		IPeer second = listOfPeers.get(listOfPeers.size()-1);
		
		IPeer prevPeer = second;		
		for(IPeer pr: listOfPeers)
		{		
			if(GetIdentifiertDistance(pr, prevPeer) <  GetIdentifiertDistance(first, second)  )
			{
				first = prevPeer;
				second = pr;
			}				
			prevPeer = pr;															 							
		}
		
		if(first == second){
			//there is only one peer in the list..
		}
		
		//calculate vector between first and second
		ChordAddress address1 = (ChordAddress)first.GetNetworkAdress();
		ChordAddress address2 = (ChordAddress)second.GetNetworkAdress();
		
		double phi = address1._identifier * minStep;
		double alpha = address2._identifier * minStep;
		
		IVector vecFirst = Vector_XD.TwoDimFromAngleAndLength(phi, 1);
		IVector vecSec = Vector_XD.TwoDimFromAngleAndLength(alpha, 1);
		
		IVector diff = vecSec.SubstractVector(vecFirst);
		
		double FactorN = 2/ diff.GetLength();
		
		//now set 
		for(IPeer pr: listOfPeers)
		{		
			ChordAddress address = (ChordAddress)first.GetNetworkAdress();
			double gamma = address._identifier * minStep;
			IVector vector = Vector_XD.TwoDimFromAngleAndLength(gamma, 2*FactorN);
			
			address._xPos = (int) ((int)vector.GetComponents()[0] + 2*FactorN);
			address._yPos = (int) ((int)vector.GetComponents()[1] + 2*FactorN);
			
		}
		return new int[]{(int) (2*FactorN),(int) (2*FactorN)};
	}
	
	public static long GetIdentifiertDistance(IPeer pr1, IPeer pr2)
	{
		ChordAddress address1 = (ChordAddress)pr1.GetNetworkAdress();
		ChordAddress address2 = (ChordAddress)pr2.GetNetworkAdress();
		
		return Math.abs(address1._identifier - address2._identifier);
	}
	
	public static int[] GetNeededDimenstions(List<IPeer> listOfPeers )
	{
		return new int[]{0,0};
	}
	
	public static List<IPeer> ConnectPeers(ArrayList<IPeer> listOfPeers, int identifierBitLength )
	{
		List<IPeer> sortedPeers = SortPeersIncreasingByIdentifier(listOfPeers); 
		
		/* The ith entry in the table at node n contains the identity of the first node, s, that
			succeeds n by at least 2i-1 on the identifier circle, i.e., s = successor(n + 2i-1 ),
			where 1 <= i <= m (and all arithmetic is modulo 2m ).
		*/
	
		for(IPeer pr : sortedPeers)
		{
			//calculate fingers and connect them to the other peers... s = successor(n + 2i-1 ),
			ChordAddress address = (ChordAddress)pr.GetNetworkAdress();
			List<finger> fingers = CalculateFingers(address._identifier,  identifierBitLength);
			fingers =  CalculateEffectivePeerForFinger(sortedPeers,pr, address._identifier, fingers);
			ConnectPeerToFingers( pr, fingers,sortedPeers);
		}		
		return listOfPeers;
	}
	
	public static void ConnectPeerToFingers(IPeer pr,List<finger> fingers,  List<IPeer> sortedPeers)
	{
		for(finger fg: fingers)
		{
			//get peer
			for(IPeer neighbour : sortedPeers)
			{
				ChordAddress address = (ChordAddress)neighbour.GetNetworkAdress();
				
				if(address._identifier ==fg.Identifier)
				{
					//now build a connection
					pr.AddNeighbour(neighbour);
				}
			}
		}
	}

	public static List<finger> CalculateFingers(long ownPeerIdentifier, long identifierBitLength)
	{
		ArrayList<finger> fingers = new ArrayList<finger>();
		long maxAddressSpace = (long)Math.pow(2, identifierBitLength);
		for(int i=0; i<=identifierBitLength; i++)
		{
			//calculate next finger
			finger fg = new finger();
			fg.i = i;
			fg.Identifier = (ownPeerIdentifier + (long)Math.pow(2, i)) % maxAddressSpace;
			fingers.add(fg);
		}	
		return fingers;
	}
	
	public static List<finger> CalculateEffectivePeerForFinger(List<IPeer> sortedPeers,IPeer  ownPeer, long ownPeerIdentifier, List<finger> fingers)
	{		
		
		for(finger fg: fingers)
		{
			//find peer after or equal fg.identifiert
			for(IPeer pr : sortedPeers)
			{
				ChordAddress address = (ChordAddress)pr.GetNetworkAdress();
				
				if(address._identifier >=fg.Identifier)
				{
					fg.EffectiveIdentifier = address._identifier;
					break;
				}
			}			
		}
		
		return fingers;
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
			result.add(p);
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

