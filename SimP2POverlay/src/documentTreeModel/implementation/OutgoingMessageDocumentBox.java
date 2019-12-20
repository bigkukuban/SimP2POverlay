package documentTreeModel.implementation;

import java.util.UUID;

import documentTreeModel.interfaces.IDocumentBoxMessage;
import documentTreeModel.interfaces.IOutgoingMessageDocumentBox;

class OutgoingMessageDocumentBox implements IOutgoingMessageDocumentBox
{
	public IDocumentBoxMessage Message;
	public UUID TargetDocumentBox;
	public long TargetPeerBox;
	@Override
	public IDocumentBoxMessage GetDocumentBoxMessage() {
		return Message;
	}
	@Override
	public UUID GetTargetDocumentBox() {

		return TargetDocumentBox;
	}
	@Override
	public long GetTargetPeerBox() {

		return TargetPeerBox;
	}
}