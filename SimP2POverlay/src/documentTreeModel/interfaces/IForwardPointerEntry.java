package documentTreeModel.interfaces;

import java.util.UUID;

public interface IForwardPointerEntry 
{
	UUID GetIdOfDocumentBox() ;
	long GetForwardedToPeerBoxWithThisId();
}
