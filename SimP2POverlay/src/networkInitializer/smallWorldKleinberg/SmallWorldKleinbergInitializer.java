package networkInitializer.smallWorldKleinberg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import commonHelper.GlobalLogger;
import commonHelper.math.RandomUtilities;
import networkInitializer.interfaces.INetworkInitializer;
import peersModel.implementation.NetworkFacade;
import peersModel.implementation.Peer;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;

public class SmallWorldKleinbergInitializer implements INetworkInitializer
{

	int _sizeX;
	int _sizeY;
	double _rParameter = 2;		//proportionalty parameter (see Kleinberg)
	int _qParameter = 2; 		//number of long-range contacts of a node (see Kleinberg)
	int _pParameter = 1; 		//distance to direct neighbors (see Kleinberg)
		
	
	public SmallWorldKleinbergInitializer(NetworkSettingsSmallWorldKleinberg settings )
	{
		_sizeX = settings._xLength;
		_sizeY = settings._yLength;
		_qParameter = settings._qParameter;
		_pParameter = settings._pPParameter;
		_rParameter = settings._rParameter;	
	}	
	
	public SmallWorldKleinbergInitializer(int SizeX, int SizeY, int parameterQ, int parameterP, double parameterR )
	{
		_sizeX = SizeX;
		_sizeY = SizeY;
		_qParameter = parameterQ;
		_pParameter = parameterP;
		_rParameter = parameterR;		
	}
	
	public SmallWorldKleinbergInitializer(int SizeX, int SizeY)
	{
		_sizeX = SizeX;
		_sizeY = SizeY;		
	}
	
	@Override
	public INetworkFacade GetInitializedNetwork() 
	{
		//we begin with 1, 0 is invalid
		long lPeerIds = 0;
		ArrayList<IPeer> lstResult = new ArrayList<IPeer>(); 
		
		for(int x =0; x<_sizeX;x++)
		{
			for(int y =0; y<_sizeY;y++)
			{				
				Peer node = new Peer();
				//assign specific address
				node.SetNetworkAdress(new SmallWorldAddress(x,y));
				//create one address
				lPeerIds++;
				node.SetPeerID(lPeerIds);
				lstResult.add(node);
			}
		}				
		
		for(IPeer peer: lstResult)
		{
			
			GlobalLogger.LogNetworkInitializer(String.format("Behandle Peer: x:%d  y:%d ",  ((SmallWorldAddress)peer.GetNetworkAdress())._xPos, ((SmallWorldAddress)peer.GetNetworkAdress())._yPos));
									
			CalculatePeerConnections(peer, lstResult);
									
			GlobalLogger.LogNetworkInitializer(String.format("Anzahl der Kontakte : %s \n\n", peer.GetAllNeighbours().size()));
			
			peer.OnFinalizedInitialization();		
			ArrayList<IPeer> contacts  = peer.GetAllNeighbours();
			for(IPeer contact : contacts)
			{
				int iDistance = contact.GetNetworkAdress().GetDistance(peer.GetNetworkAdress());					
				GlobalLogger.LogNetworkInitializer(String.format(" Distanz  : %d \n\n", iDistance));										
			}											
		}
				
		NetworkFacade result = new NetworkFacade(lstResult, new int[]{_sizeX,_sizeY});
		
		return result;
	}
	
	private void CalculatePeerConnections(IPeer peer,ArrayList<IPeer> peers)
	{
		double normalizingConstant = 0.0;
		//map<distance, probability>
		
		Map<Integer, HistogramEntry> distanceProbabilityMap = new HashMap<Integer, HistogramEntry>();
		Map<Integer, Double> distanceDistributionFunction = new HashMap<Integer, Double>();
		
		
		//first calculate distance to each other peer
		for(IPeer pr: peers)
		{
			
			if(peer == pr) continue;
			
			TemporalDataPeer pData = new TemporalDataPeer();
			
			pData.Distance = peer.GetNetworkAdress().GetDistance(pr.GetNetworkAdress());
			pData.SimpleProportionalDistance = Math.pow(pData.Distance, -1*_rParameter);
			
			
			if(!distanceProbabilityMap.containsKey(pData.Distance))
			{
				distanceProbabilityMap.put(pData.Distance, new HistogramEntry());	
			}
								
			distanceProbabilityMap.get(pData.Distance).IssuedItems.add(pr);
			
			normalizingConstant = normalizingConstant + pData.SimpleProportionalDistance;
			if(pData.Distance <=_pParameter)
			{
				//set as local contact
				peer.AddNeighbour(pr);
			}
			
			((SmallWorldAddress)pr.GetNetworkAdress()).SetPrivateTempData(pData);
		}
		
		//Calculate the real probability for each peer
		NormalizeProbability( peers, normalizingConstant,peer);
			
		//collect the distances and calculate the common distance - probability per distance (calculate the inverse Distribution function)		
		for(IPeer pr: peers)
		{
			if(peer == pr) continue;
			TemporalDataPeer data = ((TemporalDataPeer)((SmallWorldAddress)pr.GetNetworkAdress()).GetPrivateTempData());
						
			
			HistogramEntry distanceEntry= distanceProbabilityMap.get(data.Distance);
			
			distanceEntry.Probability = distanceEntry.Probability + data.Probability;																				
		}
		Double distributionValue = 0.0;
		
		for(Integer distance :  distanceProbabilityMap.keySet())
		{
			distributionValue = distributionValue + distanceProbabilityMap.get(distance).Probability; 
			
			distanceDistributionFunction.put(distance, distributionValue);
		}
		
	
		//now generate the long-range connections
		for(int q=0; q<_qParameter;q++)
		{
			Double randomNumber = Math.random();
			
			
			int iMinDistanceSelected = (int)distanceDistributionFunction.keySet().toArray()[0];
			for(Integer distance: distanceDistributionFunction.keySet())
			{
				if( Math.abs( distanceDistributionFunction.get(distance) - randomNumber) < Math.abs( distanceDistributionFunction.get(iMinDistanceSelected) - randomNumber))
				{
					iMinDistanceSelected = distance;
				}
			}
											
			
			//now we have the desired distance, now select from the peers-list one peer and 
			//set him as long-range-neighbor for peer
			IPeer longRangeContact = RandomUtilities.SelectOneByRandomFromList(distanceProbabilityMap.get(iMinDistanceSelected).IssuedItems);
			
			if(!peer.AddNeighbour(longRangeContact))
			{
				// This place i'm unsure what the right way should be ..., 
				// should the short range connections be accepted as valid long range connections??
				//	q--;
			}
			
						
		}		
	}
	
	private void NormalizeProbability(ArrayList<IPeer> peers, double normalizingConstant, IPeer ignoredPeer)
	{
		//normalize the probability
		for(IPeer pr: peers)
		{
			if(ignoredPeer == pr) continue;
			TemporalDataPeer data = ((TemporalDataPeer)((SmallWorldAddress)pr.GetNetworkAdress()).GetPrivateTempData());
			data.Probability = data.SimpleProportionalDistance / normalizingConstant;				
		}
	}

	@Override
	public long GetNumberOfItemsItendedToCreate() {
		return this._sizeX*this._sizeY;
	}
		
	@Override
	public String GetReadableDescription()
	{				
		return "PeerBoxes: SmallWorldKleinbergInitializer: X="+_sizeX+" Y= "+_sizeY+ " qParamter:"+_qParameter+" pParameter  "+_pParameter+" rParamter:"+_rParameter;
	}

}
