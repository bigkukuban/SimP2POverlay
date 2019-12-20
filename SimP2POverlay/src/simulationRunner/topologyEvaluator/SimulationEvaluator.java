package simulationRunner.topologyEvaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import commonHelper.GlobalLogger;
import documentTreeModel.interfaces.IDocumentBox;
import documentTreeModel.interfaces.IPeerBoxEvaluation;
import documentTreeModel.interfaces.IDocumentBox.IAuthenticationState.State;
import documentTreeModel.interfaces.IDocumentBoxConnection;
import documentTreeModel.interfaces.IForwardPointerEntry;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;
import simulationRunner.topologyEvaluator.interfaces.IMigrationStateEvaluationResult;
import simulationRunner.topologyEvaluator.interfaces.INode;
import simulationRunner.topologyEvaluator.interfaces.ISimulationEvaluator;
import simulationRunner.topologyEvaluator.interfaces.ITopologyEvaluationResult;

public class SimulationEvaluator implements ISimulationEvaluator {


	ArrayList<INode> _initialTopology = new ArrayList<INode>(); 
	
	private Collection<INode> GetNodeFromCollection(ArrayList<INode> lstNodes, UUID id)
	{		
		ArrayList<INode> foundNodes = new ArrayList<INode>();
		
		for(INode nd : lstNodes)
		{
			if( nd.GetDocumentBoxUUID() == id)
			{
				foundNodes.add(nd);
			}
		}
		
		return foundNodes;
	}
	
	private INode GetNodeFromCollection(ArrayList<INode> lstNodes, UUID id, long peerId)
	{
		INode result = null;
		
		for(INode nd : lstNodes)
		{
			if(((Node)nd).GetPeerId() == peerId && nd.GetDocumentBoxUUID() == id)
			{
				result = nd;
			}
		}
		
		return result;
	}
	
	
	private IDocumentBox GetNodeFromCollection(Collection<IDocumentBox> lstNodes, UUID id)
	{
		IDocumentBox result = null;
		
		for(IDocumentBox nd : lstNodes)
		{
			if(nd.GetDocumentBoxUUID() == id)
			{
				result = nd;
			}
		}
		
		return result;
	}
	
	private <T> String GetItemsAsString(ArrayList<T> listInput, String separator)
	{
		String result = "";
		for (T o : listInput) {
			result = result + o.toString() + separator;
	    }
		
		return result;
	}
	
	
	/**
	 * @param facade - source facade ...
	 * @return the extracted topology build by DocumentBoxes
	 * 
	 * Collect all available DocumentBoxes and connect them to each other ..
	 * 
	 */
	private ArrayList<INode> ExtractTopology(INetworkFacade facade,ArrayList<Long>  channelLenghtList)
	{				
		//attention: the same documentBox-Id may be contained in several entries, due to doubling problem...				
		ArrayList<INode> resultingTopology = new ArrayList<INode>();
		
		Collection<IDocumentBox>  allItems = facade.GetAllDocumentBoxes();
							
		//Create all nodes (without connections yet)
		for(IDocumentBox bx : allItems)
		{					
			Node d = new Node(bx.GetDocumentBoxUUID());					
			d.SetPeerId(bx.GetPeerBoxAddress());										
			resultingTopology.add(d);			
		}
			
		//now add connections from set topology 
		for(IDocumentBox bx : allItems)
		{					
			long onPeerId = bx.GetPeerBoxAddress();
			
			INode nd = GetNodeFromCollection(resultingTopology, bx.GetDocumentBoxUUID(), onPeerId);
			
			//create connections to this item
			for(IDocumentBoxConnection cn : bx.GetConnections())
			{
				UUID otherDbUUID = cn.GetDocumentBoxId();
				long otherPeerId = cn.GetLastKnownPeerBoxAdress();
				
				boolean bChannelHasEnded = false;
				boolean bCycleDetected = false;
				
				IDocumentBox otherDocumentBox = null;
				
				ArrayList<Long> currentChannel= new ArrayList<Long>();				
				do
				{					
					// try to detect the connected DocumentBox on other Peers, locate it and memorize it
					IPeerBoxEvaluation ipbEval = (IPeerBoxEvaluation) facade.GetPeerById(otherPeerId).GetPeerBox();
					Collection<IDocumentBox> availableDocBoxes = ipbEval.GetListOfAssignedDocumentBoxes();
					// here may exist circular dependencies
					otherDocumentBox = GetNodeFromCollection(availableDocBoxes, otherDbUUID);
					if(otherDocumentBox == null)
					{
						//then look into incomming messages list, may be there is it						
						//try following the channel
						IForwardPointerEntry entry = ipbEval.GetForwardingPointerForDocumentBox(otherDbUUID, false);						
						if(entry == null){
							
							bChannelHasEnded = true;
							
						} else 
						{
							otherPeerId = entry.GetForwardedToPeerBoxWithThisId();
							
							
							if(currentChannel.contains(otherPeerId))
							{
								// the other peer was already visited and the DOcumentBox was not there, we have found a cycle 
								bCycleDetected = true;
								currentChannel.add(otherPeerId);
								
							} else
							{
								currentChannel.add(otherPeerId);	
							}																					
						}
					} else
					{
						bChannelHasEnded = true; 
					}
					
				}while(!bChannelHasEnded && !bCycleDetected);
					
				channelLenghtList.add((long) currentChannel.size());
				
				//add connection only if the according DocumentBox was really found on other PeerBox, follow the channel				
				if(otherDocumentBox != null)
				{
					try{
						
						//add connection to the found item 					
						INode otherNode =  GetNodeFromCollection(resultingTopology, otherDbUUID, otherPeerId);
						((Node)nd).AddConnectionToNode(otherNode);
						
					}catch(NullPointerException exp)
					{
						
						GlobalLogger.LogSimulationEvaluator("SimulationEvaluation: DocBox: "+ 	bx.GetDocumentBoxUUID() +" on PeerBox "+
																								bx.GetPeerBoxAddress()+
																								" otherOB " + otherDbUUID + " PeerId:"+ otherPeerId);
						throw exp;
					}
				} else 
				{
					if(bCycleDetected)
					{
						GlobalLogger.LogSimulationEvaluator("SimulationEvaluation: DocBox: "+ bx.GetDocumentBoxUUID() +" on PeerBox "+bx.GetPeerBoxAddress()+" There was a cycle of length : " + 
																			 currentChannel.size() + "  Items:" + 
																			 GetItemsAsString(currentChannel, " ,")+" for connection with  "+
																		     cn.GetDocumentBoxId());
					} else 
					{
						GlobalLogger.LogSimulationEvaluator("SimulationEvaluation: DocBox: "+ bx.GetDocumentBoxUUID() +" on PeerBox "+bx.GetPeerBoxAddress() +" Other DocumentBox not found : " + 
								 													currentChannel.size() + "  Items:" + 
								 													GetItemsAsString(currentChannel, " , ")+ " for connection with  "+
								 													cn.GetDocumentBoxId());					
					}					
				}
			}
			
		}
		
		
		return resultingTopology;
	}
	
	/**
	 * Check if all items from _initialTopology are still avaiable and connected 
	 * @param extractedTopology
	 * @return
	 */
	private TopologyEvaluationResult CompareTopologies(ArrayList<INode> extractedTopology)
	{
		TopologyEvaluationResult result = new TopologyEvaluationResult();
		
		for(INode nd : _initialTopology)
		{
			// try to detect the nd in the extractedTopology, if detected check the connections
			
			 Collection<INode> foundNodesWithTheGivenID = GetNodeFromCollection(extractedTopology, nd.GetDocumentBoxUUID());
			 
			 if(foundNodesWithTheGivenID.size() == 0)
			 {
				 result._unAbsentNodes.add(nd);				 
			 } else 
			 {
				 Collection<UUID> ndOutgoingConnections =  ((Node)nd).GetIdsOfConnectedNodes();
				 
				 //check if nd is validated: there is at least one node in extractedTopology that contains the same connections as nd (incoming and outgoing)
				 boolean isNdAuthenticated = false;
				 for(INode ndSet : foundNodesWithTheGivenID)
				 {			
					 isNdAuthenticated = false;
					 
					 //first check outgoing connections from node
					 Collection<UUID> ndSetOutgoingConnections =  ((Node)ndSet).GetIdsOfConnectedNodes();
					 
					 if(ndOutgoingConnections.size() == ndOutgoingConnections.size())
					 {
						 if(!ndOutgoingConnections.containsAll(ndSetOutgoingConnections))
						 {
							 continue;
						 }
					 }							 					 
					 
					 // then check incomming connections, ask all connected for the nd-ids
					 isNdAuthenticated = true;
					 for(INode connNdSet : ndSet.GetConnections())
					 {
						 if(!((Node)connNdSet).GetIdsOfConnectedNodes().contains(nd.GetDocumentBoxUUID()))
						 {
							 //incomming connection not available
							 isNdAuthenticated = false;
						 }
					 }
					 
					 if(isNdAuthenticated) break; // successfully found one node in extracted topology with required connections
				 }	 
				 				 
				 if(!isNdAuthenticated)
				 {
					 result._unAuthenticatedNodes.add(nd);
				 }
			 }			 			 			 
		}
		
		return result;
	}
	
	public void ExtractInitialTopology(INetworkFacade facade)
	{
		ArrayList<Long>  channels = new ArrayList<Long>(); 
		
		_initialTopology = ExtractTopology(facade,channels);
	}
	
	
	public ITopologyEvaluationResult CompareSetTopology(INetworkFacade facade)
	{
		TopologyEvaluationResult  result = null;
		
		ArrayList<INode> currentTopology = null;
		ArrayList<Long>  channels = new ArrayList<Long>(); 
		try{
			
			currentTopology  = ExtractTopology(facade,channels);
			
		}catch(NullPointerException exp)
		{
			GlobalLogger.LogSimulationEvaluator("SimulationEvaluation: Error in ExtractTopology NullPointerException ... "+ exp.getMessage());					
		}	
		
		try{
			
			result =  CompareTopologies(currentTopology);
		}catch(NullPointerException exp)
		{
			GlobalLogger.LogSimulationEvaluator("SimulationEvaluation: Error in CompareTopologies, NullPointerException ... "+ exp.getMessage());					
		}	
		result._channelLengthes = channels;
		
		return result;
	}
	
	
	
	public IMigrationStateEvaluationResult EvaluateNetworkState(INetworkFacade facade)
	{
		MigrationStateEvaluationResult result = new  MigrationStateEvaluationResult();
		
			
		for(IPeer peer : facade.GetPeers())
		{
			result.CountDocumentBoxes  = result.CountDocumentBoxes +peer.GetPeerBox().GetNumberOfAllContainedDocumentBoxes();
			
			IPeerBoxEvaluation ipbEval = (IPeerBoxEvaluation) peer.GetPeerBox();
			result.SystemDocumentBoxcapacity = result.SystemDocumentBoxcapacity + ipbEval.GetDocumentBoxCapacity();
			
			PeerBoxForwardPointerLengthState fpState = new PeerBoxForwardPointerLengthState();
			
			fpState.PeerBoxId = peer.GetPeerID();
			fpState.ForwardPointerCacheId = ipbEval.GetForwardingPointerLength();
			
			result.ForwardPointerStates.add(fpState);
			
			result.CurrentMeanAuthenticationDuration = result.CurrentMeanAuthenticationDuration + ipbEval.GetMeanAuthenticationDuration();
			
			for(IDocumentBox bx : ipbEval.GetListOfAssignedDocumentBoxes())
			{
				if(bx.GetAuthenticationState().GetCurrentState() == State.Authenticated)
				{				
					result.CountAuthenticatedDocumentBoxes++;
				} else 
				{
					result.CountUnAuthenticatedDocumentBoxes++;
				}				
			}
			result.CountInMigration = result.CountInMigration + ipbEval.GetListOfDocumentBoxesInMigration().size();
			
		}
		result.CurrentMeanAuthenticationDuration = result.CurrentMeanAuthenticationDuration / facade.GetPeers().size();				
								
		return result;
	}		
	
}
