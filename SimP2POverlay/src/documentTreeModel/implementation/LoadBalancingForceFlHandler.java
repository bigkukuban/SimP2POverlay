package documentTreeModel.implementation;
import commonHelper.GlobalLogger;
import commonHelper.math.EuclideanPoint;
import commonHelper.math.Vector_XD;
import commonHelper.math.interfaces.IEuclideanPoint;
import commonHelper.math.interfaces.IVector;
import documentTreeModel.interfaces.ILoadForceProvider;

/**
 * Holds and calculates the direction of the acting force Fl
 * @author Dimitri
 *
 */
public class LoadBalancingForceFlHandler implements ILoadForceProvider
{

	
	IVector _actingForce = null;
	
	IEuclideanPoint _parentPeerAdress;
	
	long _peerId;
	
	public LoadBalancingForceFlHandler(IEuclideanPoint parentPeer, long peerId)
	{
		_parentPeerAdress = parentPeer;	
		_peerId = peerId;
		
		long dims = _parentPeerAdress.GetDimensions();				
		
		_actingForce = new Vector_XD(new double [(int) dims]);
	}
	
	/**
	 * Calculates the distance (DTL) ( broad-cast reach ) from given PeerBox load state (CAPpb)
	 * @param loadState
	 * @return
	 */
	public static double DistanceFromLoadState(long loadState)
	{
		return Math.pow(loadState/GlobalSimulationParameters.AccuracyLimitValue, 1.0/GlobalSimulationParameters.DistanceReductionPower) - 1;	
	}
	
	/**
	 * Calculates the acting local Fl-magnitude from the remote Capacity CapPb und the distance (DTL) of the source 
	 * @param loadForceAmount
	 * @param distance
	 * @return
	 */
	public static double MagnitudeFromDistance(long loadForceAmount, double distance)
	{
		//this never reaches the value 0, thus the accuracy value is the new limit for the function
		double result =  loadForceAmount/Math.pow((1+distance), GlobalSimulationParameters.DistanceReductionPower);
		
		if(result < GlobalSimulationParameters.AccuracyLimitValue) result =0;
		
		return result;
	}
	
	public void UpdateValue(double [] sourcePos, long newValue, long previousValue)
	{		
		
		IEuclideanPoint pointSourcePos = new EuclideanPoint(sourcePos);
		
		// calculate the vector to be substracted from current acting force 
		IVector distanceVector = new Vector_XD(pointSourcePos.GetComponents());		
		distanceVector = distanceVector.SubstractVector(new Vector_XD(_parentPeerAdress.GetComponents()));				
				
		double previousMagnitude = MagnitudeFromDistance(previousValue, distanceVector.GetLength());
				
		// calculate previous force vecotr
		IVector directionUnitVector =  new Vector_XD (distanceVector.GetUnit());
		
		IVector prevForceVector =  directionUnitVector.MultiplicateWithScalar(previousMagnitude);
		
							
		IVector newForce = _actingForce.SubstractVector(prevForceVector); 
		
		if(newForce == null)
		{
			GlobalLogger.LogForces("Force Fl updated to (X): Wrong OP1");
		}
		// calculate the new vector to add
		
		double newMagnitude = MagnitudeFromDistance(newValue, distanceVector.GetLength());
		IVector newForceVector =  directionUnitVector.MultiplicateWithScalar(newMagnitude);						
		newForce = newForce.AddVector(newForceVector);
		
		if(newForce == null)
		{
			GlobalLogger.LogForces("Force Fl updated to (X): Wrong OP2");	
		} else
		{
			_actingForce = newForce;
		}
		
		
		GlobalLogger.LogForces("Force Fl updated to (X):"+_actingForce.GetComponents()[0]+" (Y)"+_actingForce.GetComponents()[1]+" on peer: "+_peerId);		
	}				

	@Override
	public IVector GetForceVector() {

		return _actingForce;
	}

}
