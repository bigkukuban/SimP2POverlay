package documentTreeModel.interfaces;

import java.util.UUID;

public interface IPeerBoxRequestSendDocumentBoxMessage extends IPeerBoxRequest
{
	UUID GetSourceDocumentBox();
	UUID GetTargetDocumentBox();

}
