package simulationRunner.topologyEvaluator.interfaces;

import java.util.Collection;

public interface ITopologyEvaluationResult 
{
	
	//nodes where one or more connections are unavailable
	Collection<INode> GetUnAuthenticatedNodes();
	
	//nodes that are fully lost in the set topology
	Collection<INode> GetAbsentNodes();
	
	long GetAverageChannelLength();
}
