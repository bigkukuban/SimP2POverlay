package randomWalk.interfaces;

import java.util.UUID;

import randomWalk.interfaces.IRandomWalker.RandomWalkerType;

/**
 * The functions from this interface should always be implemented 
 * in synchronized way
 * @author Dimitri
 *
 */
public interface IRandomWalkerHost 
{
	//now for tests
	int GetHostColor();
	int GetHostColorSetByWalker();
	void SetHostColor(int iColor);			
	
	/**
	 * Called from received random walker to force peer
	 * to react on walker receiving
	 * @param type - type of the received random walker
	 * @param walkerId - unique walker id
	 * @return true if walker-command  was successfully executed
	 */	
	boolean BeginActionOnReceivedRandomWalkerType(RandomWalkerType type, UUID walkerId, long walkerSender);	
	
	
	/**
	 * Returns the date time stamp of the current date
	 * @return
	 */
	long GetCurrentDateTimeStamp();
	
	
	/**
	 * Returns the peer id
	 * @return
	 */
	long GetPeerID();
	
	/**
	 * Selects a neighbor, without consideration of distance
	 * @return
	 */
	IRandomWalkerHost SelectByRandomOneNeighbour();
	
	/**
	 * 
	 * @param proprtionalityConstant - this constant is set by walker
	 *        it represents the distance - probabilty to be choosen
	 * @return
	 * @throws Exception 
	 */
	IRandomWalkerHost SelectByRandomConsiderDistance(Double proprtionalityConstant) throws Exception;
		
}
