package simulationRunner.topologyEvaluator.interfaces;

import java.util.Collection;
import java.util.UUID;

public interface INode 
{	
	UUID GetDocumentBoxUUID();
	void SetDocumentBoxUUID(UUID id);	
	Collection<INode> GetConnections();
}
