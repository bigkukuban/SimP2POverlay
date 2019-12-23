package networkInitializer.baPreferentialAttachment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import commonHelper.math.RandomUtilities;
import networkInitializer.interfaces.INetworkInitializer;
import peersModel.implementation.NetworkFacade;
import peersModel.implementation.Peer;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;

/**
 * @author Dimitri
 *
 */
public class BaPreferentialAttachmentInitializer implements INetworkInitializer
{
	NetworkSettingsBaPreferentialAttachment _settings = null;
	public BaPreferentialAttachmentInitializer(NetworkSettingsBaPreferentialAttachment parameter) throws Exception
	{
		if(parameter.m > parameter.m0 ) throw new Exception("wrong parameters (m,m0)");
		if(parameter.N < parameter.m0 ) throw new Exception("wrong parameters (N,m0)");
				
		_settings = parameter;				
	}
	
	@Override
	public long GetNumberOfItemsItendedToCreate() {
		return this._settings.N;
	}
	
	@Override
	public INetworkFacade GetInitializedNetwork() 
	{
	
		long lPeerIds = 0;		
		ArrayList<IPeer> lstResult = new ArrayList<IPeer>(); 
		
		//always initialize m peers at the beginning, they are not connected
		
		int sizeX  = (int) Math.sqrt((double)_settings.N);
		int sizeY  = sizeX;
		
		int remainer = _settings.N - (sizeX * sizeY);
		
		
		//then the rest from paramter N
		for(int x =0; x < sizeX + remainer;x++)
		{
			for(int y =0; y < sizeY;y++)
			{
				if(lstResult.size() >= _settings.N) break;
				
				Peer node = new Peer();
				//assign specific address
				node.SetNetworkAdress(new BaPreferentialAttachmentAddress(x,y));
				//create one address
				lPeerIds++;
				node.SetPeerID(lPeerIds);
				lstResult.add(node);
			}
		}
		
		//now Initialize connections, consider the m0-Parameter, this two peers have always the same initial probability to be connected.
		ArrayList<IPeer> lstInitializedNodes = new ArrayList<IPeer>();
		lstInitializedNodes.addAll(lstResult.subList(0, _settings.m0));
		
		//set the degree of all initialized nodes to 2, thus they will have the same probability to beeing connected to..
		for(int i= 0; i<_settings.m0; i++)
		{
			IPeer p = lstInitializedNodes.get(i);
			IPeer next = null;						
			if(lstInitializedNodes.size()>i+1)
			{
				next = lstInitializedNodes.get(i+1);
			} else 
			{
				next = lstInitializedNodes.get(0);
			}			
			p.AddNeighbour(next);
			next.AddNeighbour(p);
		}
		
		
		//we use always the long range connections ... 
		//now initialize the remaining peers ...
		for(IPeer peer: lstResult)
		{
			if(lstInitializedNodes.contains(peer)) continue;
						
			CalculatePeerConnections(peer,lstInitializedNodes);
			
			lstInitializedNodes.add(peer);
		}
		
		for(IPeer peer: lstResult)
		{
			peer.OnFinalizedInitialization();	
		}
				
		//the connections are always undirected.
		NetworkFacade result = new NetworkFacade(lstResult, new int[]{sizeX + remainer,sizeY});
		return result;
	}
	
	
	 
	/**
	 * @param peer
	 * @param alreadyInitializedPeers
	 * Adds according m connections to alreadyInitializedPeers to the peers
	 * 
	 * 
	 */
	private void CalculatePeerConnections(IPeer peer,ArrayList<IPeer> alreadyInitializedPeers)
	{
		// adds connections
		
		// calculate first the probability density function (PDF)
		// select m - peers to connect the given peer to.., calculate the cumulative distribution function (CDF)
		
		//calculate histogramm <number of connections, HistogrammEntry>
		Map<Integer, HistogramEntry> pdfFunction = new HashMap<Integer, HistogramEntry>(); 		
		Map<Integer, Double[]> cdfFunction = new HashMap<Integer, Double[]>();		
		
		int iAllConnections = 0;
		for(IPeer itm : alreadyInitializedPeers)
		{
			int connections = itm.GetAllNeighbours().size();
			
			if(!pdfFunction.containsKey(connections))
			{
				pdfFunction.put(connections, new HistogramEntry());
			}			
			pdfFunction.get(connections).IssuedItems.add(itm);
			iAllConnections = iAllConnections + connections;
		}
		
		//normalize it all		
		for(Integer connections :  pdfFunction.keySet())
		{
			HistogramEntry entry = pdfFunction.get(connections);
			
			//probability for the histogramm group
			
			entry.Probability = (double)(entry.IssuedItems.size() * connections) / (double)iAllConnections;
		}
			
		double previousValue = 0;
		//calculate the CDF from PDF
		for(Integer connections :  pdfFunction.keySet())
		{
			double currentValue  = pdfFunction.get(connections).Probability;
			
			Double range[] = new Double[2];
			
			range[0] = previousValue; //lower bound
			range[1] = currentValue + previousValue; // higher bound
			
			cdfFunction.put(connections, range);			
			previousValue  = previousValue   + currentValue;
		}
		
		
		//now calculate the connections for the peer, peer needs m - connections						
		for(int m_i = 0; m_i < this._settings.m; m_i++)
		{
			double randValue = Math.random();
						
			Integer selectedConnectionGroup = -1;
			
			//select the connections - group from CDF
			for(Integer connections :  cdfFunction.keySet())
			{
				if(selectedConnectionGroup < 0)
				{
					selectedConnectionGroup = connections;
				}
				
				if( cdfFunction.get(connections)[0]  <= randValue && randValue <= cdfFunction.get(connections)[1] )
				{
					selectedConnectionGroup = connections;
				}
			}
			
			IPeer selected = RandomUtilities.SelectOneByRandomFromList(pdfFunction.get(selectedConnectionGroup).IssuedItems);						
			if(!peer.AddNeighbour(selected))
			{
				m_i--; // try again				
				continue;
			}
			selected.AddNeighbour(peer);
		}
											
	}
	
	@Override
	public String GetReadableDescription() {

		return "PeerBoxes: BaPreferentialAttachmentInitializer: N="+_settings.N+" m0= "+_settings.m0;
	}
	
}



