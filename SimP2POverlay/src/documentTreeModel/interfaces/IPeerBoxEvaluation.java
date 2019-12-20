package documentTreeModel.interfaces;

import java.util.Collection;
import java.util.UUID;

public interface IPeerBoxEvaluation extends IPeerBox
{
	Collection<IDocumentBox> GetListOfAssignedDocumentBoxes();
	
	// for common use	
	long GetNumberOfAllContainedDocumentBoxes();
	
	// use here always false as parameter value for bUpdateCache
	IForwardPointerEntry GetForwardingPointerForDocumentBox(UUID idDocumentBox, boolean bUpdateCache);
	
	
	Collection<IDocumentBox> GetListOfDocumentBoxesInMigration();
	
	long GetForwardingPointerLength(); 
		
	
	long GetMeanAuthenticationDuration();
}
