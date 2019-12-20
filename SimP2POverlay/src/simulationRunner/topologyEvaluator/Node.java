package simulationRunner.topologyEvaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import simulationRunner.topologyEvaluator.interfaces.INode;

public class Node implements INode 
{
	private  UUID _nodeId;
	public Node(UUID nodeId)
	{
		_nodeId = nodeId;
	}
	
	private long _peerId;
	public long GetPeerId(){
		return _peerId;
	}
	
	public void SetPeerId(long peerId){
		_peerId = peerId;
	}
	
	@Override
	public UUID GetDocumentBoxUUID() {	
		return _nodeId;
	}

	@Override
	public void SetDocumentBoxUUID(UUID id) {
		_nodeId = id;		
	}

	ArrayList<INode> _outgoingConnections = new ArrayList<INode>(); 
	
	public Collection<UUID> GetIdsOfConnectedNodes()
	{
		ArrayList<UUID> result = new  ArrayList<UUID>();
		for(INode nd : _outgoingConnections)
		{
			result.add(nd.GetDocumentBoxUUID());
		}		
		return result;		
	}
	
	@Override
	public Collection<INode> GetConnections() {
			
		return 	Collections.unmodifiableList(_outgoingConnections);
	}
	
	
	public boolean AddConnectionToNode(INode connectedNode)
	{
		if(!_outgoingConnections.contains(connectedNode))
		{
			_outgoingConnections.add(connectedNode);
			return true;
		}		
		return false;
	}

}
