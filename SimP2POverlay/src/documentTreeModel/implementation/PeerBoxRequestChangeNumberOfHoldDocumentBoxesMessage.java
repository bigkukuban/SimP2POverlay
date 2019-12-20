package documentTreeModel.implementation;

import java.util.UUID;

import documentTreeModel.interfaces.IPeerBoxRequestChangeNumberOfHoldDocumentBoxesMessage;

public class PeerBoxRequestChangeNumberOfHoldDocumentBoxesMessage implements IPeerBoxRequestChangeNumberOfHoldDocumentBoxesMessage
{
	long _newNumberOfDocBoxes;
	UUID _msgId;
	
	public PeerBoxRequestChangeNumberOfHoldDocumentBoxesMessage(long newNumberOfDocBoxes)
	{
		_newNumberOfDocBoxes = newNumberOfDocBoxes;
		_msgId = UUID.randomUUID();
	}
	public long GetNewNumberOfDocumentBoxesContainedHere()
	{
		return _newNumberOfDocBoxes;
	}

	@Override
	public UUID GetRequestUUID() {

		return _msgId;
	}

	@Override
	public Object GetPrivateData() {

		return null;
	}
	
}
