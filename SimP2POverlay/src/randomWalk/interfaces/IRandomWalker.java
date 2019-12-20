package randomWalk.interfaces;



public interface IRandomWalker {
	
	enum RandomWalkerType{
		PeerAcquiererChildWalker,
		PeerAcquiererStandByWalker,
		PeerAcquiererRootStandByWalker,
		Unknown
	}

	void Visit(IRandomWalkerHost host);
	void KillWalker();
	boolean IsKilled();
	
	//returns true if the walker needs far routes as well as near routes  
	boolean IsLongHaulWalker();
		
}
