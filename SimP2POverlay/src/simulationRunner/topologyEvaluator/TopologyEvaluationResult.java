package simulationRunner.topologyEvaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import simulationRunner.topologyEvaluator.interfaces.INode;
import simulationRunner.topologyEvaluator.interfaces.ITopologyEvaluationResult;

public class TopologyEvaluationResult implements ITopologyEvaluationResult
{

	public ArrayList<INode> _unAuthenticatedNodes = new ArrayList<INode>();
	public ArrayList<INode> _unAbsentNodes = new ArrayList<INode>();
	public ArrayList<Long> _channelLengthes = new ArrayList<Long>();
	
	@Override
	public Collection<INode> GetUnAuthenticatedNodes() 
	{
		return Collections.unmodifiableList(_unAuthenticatedNodes);			
	}

	@Override
	public Collection<INode> GetAbsentNodes() 
	{
		return Collections.unmodifiableList(_unAbsentNodes);
	}
	
	public long GetAverageChannelLength()
	{
		long sum = 0;
		
		for(long i : _channelLengthes)
		{
			sum = sum +i;
		}
		
		return sum/_channelLengthes.size();
	}

}
