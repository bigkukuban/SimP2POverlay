package documentTreeModel.interfaces;

//use this interface to send broacast messages from PeerBox, broad cast request to not create 
//PeerBoxResponse messages 
public interface IPeerBoxBroadCastRequest extends IPeerBoxRequest 
{
	// Creates a deep copy of the object
	IPeerBoxBroadCastRequest CloneMe();
}
