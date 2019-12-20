package simulationRunner.interfaces;

import documentBoxInitializer.interfaces.IDocumentBoxNetworkInitializer;
import networkInitializer.interfaces.INetworkInitializer;
import ui.openGL.interfaces.IViewmodelNetwork;

public interface ISimulationRunner 
{	
		boolean  BeginSimulation();
		boolean Initialize(INetworkInitializer initializer, IDocumentBoxNetworkInitializer docBoxNetworkInitializer, IViewmodelNetwork viewModelnetwork);
}
