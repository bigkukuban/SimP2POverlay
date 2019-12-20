package documentTreeModel.interfaces;

import java.util.UUID;

public interface IOutgoingMessageDocumentBox {

	
	 IDocumentBoxMessage GetDocumentBoxMessage();
	 UUID GetTargetDocumentBox();
	 long GetTargetPeerBox();
}
