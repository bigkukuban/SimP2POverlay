package documentTreeModel.interfaces;

import java.util.Collection;

public interface IDocumentBoxForLocalConnection  extends IDocumentBox
{
	Collection<IOutgoingMessageDocumentBox> GetOutgoingList();
	IAuthenticationStateForLocalConnection GetAuthenticationStateObject();
	long GetPeerBoxAddress();
}
