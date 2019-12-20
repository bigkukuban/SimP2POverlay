package commonHelper.interfaces;

import commonHelper.math.interfaces.IEuclideanPoint;


/**
 * This interfaces is just a fucing workaround, the peerid should be replaced by the Network-Adress, everywhere in the project. 
 * 
 * @author Dimitri
 *
 */
public interface IPeerIdIntoEuclideanAdressConverter {

		IEuclideanPoint GetPeerPositionWithinEuclideanSpace(long peerId);
	
}
