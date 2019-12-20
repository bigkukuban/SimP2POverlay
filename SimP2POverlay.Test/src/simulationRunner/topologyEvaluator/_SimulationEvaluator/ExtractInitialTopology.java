package simulationRunner.topologyEvaluator._SimulationEvaluator;

import org.junit.Test;

import peersModel.implementation.NetworkFacade;
import peersModel.implementation.Peer;
import simulationRunner.topologyEvaluator.SimulationEvaluator;

public class ExtractInitialTopology {

	@Test
	public final void testShouldExtractTopology() 
	{
		
		SimulationEvaluator dut = new SimulationEvaluator();
		
		NetworkFacade facade = new NetworkFacade();
		
		Peer peer1 = new Peer();
		Peer peer2 = new Peer();
				
		
		dut.ExtractInitialTopology(facade);
	
	}
}
