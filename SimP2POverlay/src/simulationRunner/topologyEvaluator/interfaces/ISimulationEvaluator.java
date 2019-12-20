package simulationRunner.topologyEvaluator.interfaces;
import peersModel.interfaces.INetworkFacade;

public interface ISimulationEvaluator 
{
	void ExtractInitialTopology(INetworkFacade facade);	
	ITopologyEvaluationResult CompareSetTopology(INetworkFacade facade);		
	IMigrationStateEvaluationResult EvaluateNetworkState(INetworkFacade facade);
}
