package documentTreeModel.implementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import commonHelper.GlobalLogger;
import commonHelper.GlobalTools;
import commonHelper.RandomSelecterDouble;
import commonHelper.interfaces.IRandomSelecterDoubleInputObject;
import commonHelper.math.LinearAlgebraHelper;
import commonHelper.math.LinearAlgebraHelper.LineDistanceResult;
import commonHelper.math.Vector_XD;
import commonHelper.math.interfaces.IEuclideanPoint;
import commonHelper.math.interfaces.IVector;
import documentTreeModel.interfaces.IDocumentBox;
import documentTreeModel.interfaces.IDocumentBoxConnection;
import documentTreeModel.interfaces.IDocumentBoxMigrationDirectionResult;
import peersModel.interfaces.IPeer;
import peersModel.interfaces.IPeerAdress;

/**
 * Should select a single documentbox and direction for migration
 * Input: current LoadBalancingForce, 
 * -- calculates local Fd and Fc for each DocumentBox and decides about migration and direction
 *  
 * @author Dimitri
 *
 */
public class LoadBalancedMigrationSelecter 
{		
	private double _factorCommForceDamp = GlobalSimulationParameters.FactorFc;
		
	private IPeer _parentPeer;
	
	public LoadBalancedMigrationSelecter(IPeer parentPeer)
	{
		_parentPeer = parentPeer;
	}
	double  [] _localAddressWithinSpace = null;
	public void SetLocalAddress(double  [] addressWithinSpace)
	{
		_localAddressWithinSpace = addressWithinSpace;
	}
	
	
	/**
	 * Returns the length of damping force
	 * @param capacity
	 * @param fillLevel
	 * @param numberOfDocumentBoxes
	 * @return
	 */
	private double GetDampingForceLength( double numberOfLocalDocumentBoxes, IDocumentBox db)
	{
 		
		double capacity = _parentPeer.GetPeerBox().GetDocumentBoxCapacity();
		
		double fillLevelPeer = (double)numberOfLocalDocumentBoxes/capacity;
			
		//rises with capacity... 
		double result =  capacity  - fillLevelPeer*capacity;
		if(capacity <= 1.0) return 0;		
			
		// get damping force as part of communication force		
		 for(IDocumentBoxConnection cn : db.GetConnections())
		 {
			 long lastKnownPeer = cn.GetLastKnownPeerBoxAdress();
			 
			 if(lastKnownPeer != _parentPeer.GetPeerID()) continue;
			 
			 result = result + _factorCommForceDamp;
		 }
		
		return result;
	}
	
	
	/**
	 * Returns the vector of communication force, the vector considers only neigbours on foreign peers, 
	 * DocumentBoxes on the same peer are considered as part of damping force
	 * @param db
	 * @return
	 */
	private IVector GetCommunicationForce(IDocumentBox db )
	{
		
		 IVector vecLocalPeerVec = new Vector_XD(_localAddressWithinSpace);
		
		 IVector resultingForceDirection = new Vector_XD(new double[] {0,0});
 
		 Collection<IDocumentBoxConnection> connections = db.GetConnections();
		 for(IDocumentBoxConnection cn : connections)
		 {
			 long lastKnownPeer = cn.GetLastKnownPeerBoxAdress();
			 
			 IEuclideanPoint peerPos = 
					 GlobalTools.GetAddressConverterService().GetPeerPositionWithinEuclideanSpace(lastKnownPeer);
			 			 
			 IVector vecOtherPeerBox = (new Vector_XD(peerPos)).SubstractVector(vecLocalPeerVec);	
			 
			 vecOtherPeerBox = vecOtherPeerBox.MultiplicateWithScalar(_factorCommForceDamp);
			 
			 if(peerPos == null)
			 {
				 GlobalLogger.LogBroadCasts("There is a seriuos problem : GetCommunicationForce");
			 }
			 
			 // may be null of initialization
			 resultingForceDirection = resultingForceDirection.AddVector(vecOtherPeerBox.GetUnitVector());
			 // this force should not depend on the distance, we should not have distance dependent forces except the load balancing force.
			// resultingForceDirection = resultingForceDirection.AddVector(vecOtherPeerBox);
		 }

		return resultingForceDirection;
	}
	
	private IVector CalculateResultingVector(IVector currentLoadBalancingForce,IVector communicationForceVec, double dampingForceLength)
	{
	
		IVector sum = currentLoadBalancingForce.AddVector(communicationForceVec);
			
		IVector  result = new Vector_XD(new double[(int) currentLoadBalancingForce.GetDimensions()]);
		
		if(sum.GetLength() > dampingForceLength)
		{
			//remove the  damping force from sum-vector			
			IVector ss = sum.GetUnitVector().MultiplicateWithScalar(dampingForceLength);			
			result = sum.SubstractVector(ss);
		}
												
		return result;
	}
	
	 
	
	private long GetNearestNeighbour(ArrayList<IPeer> neighbours,IVector direction)
	{		
		//determine the peer with minimal positive distance, negative distance is in the wrong direction, do not consider them.		
		
		RandomSelecterDouble randomTargetSelecter = new RandomSelecterDouble();		
		ArrayList<IRandomSelecterDoubleInputObject> randomInputerList = new ArrayList<IRandomSelecterDoubleInputObject>(); 		
		for(IPeer otherPeer : neighbours)
		{
			IPeerAdress other = otherPeer.GetNetworkAdress();								
			LineDistanceResult distance = LinearAlgebraHelper.CalculateDistanceBetwennLineAndPoint(_parentPeer.GetNetworkAdress().GetPoint(), 
																						new Vector_XD(direction.GetUnit()), other.GetPoint());
			
			if(!distance.IsInTheSameDirectionAsDirectionPointer) continue;						
			IRandomSelecterDoubleInputObject entry = new IRandomSelecterDoubleInputObject()
			{
				double  _value;
				Object _referencedObject;
				@Override
				public Object GetReferencedObject() {
					return _referencedObject;
				}

				@Override
				public double GetIssuedValue() {
						return _value;
				}					
				public  IRandomSelecterDoubleInputObject setParams(Object issued, double value)
				{
					_referencedObject = issued;
					_value = value;
					return this;
				}
			}.setParams(otherPeer, distance.DistanceFromLine);
			
			randomInputerList.add(entry);					
		}		
		randomTargetSelecter.AssignObjectlist(randomInputerList);
		
		return ((IPeer)randomTargetSelecter.GetRandomObjectFromCurrentDensity()).GetPeerID();
	}
	
	/**
	 * All parameters can be changes within a signle cycle
	 * @param allDocumentBoxes
	 * @param currentForceProvider
	 * @param peerBoxMaximualCapacity
	 * @return
	 */
	public IDocumentBoxMigrationDirectionResult DetermineDocumentBoxForMigration(List<IDocumentBox> allDocumentBoxes,
														 						 IVector currentLoadBalancingForce)
	{		
		MigrationResult result = null;
		ArrayList<IPeer> neighbours = _parentPeer.GetAllNeighbours();
		
		double lastFoundForceLength = 0;
								
		for(IDocumentBox db : allDocumentBoxes)
		{
			IVector communicationForceVec = this.GetCommunicationForce(db);
			double dampingForceLength = this.GetDampingForceLength(allDocumentBoxes.size(), db);								
			IVector resultingForce =CalculateResultingVector(currentLoadBalancingForce,communicationForceVec, dampingForceLength);								
			if(resultingForce.GetLength() >lastFoundForceLength )
			{
				long targetPeer = GetNearestNeighbour(neighbours,resultingForce);
				
				if(targetPeer != Long.MIN_VALUE)
				{
					result = new MigrationResult();			
					result.DocumentBox = db;
					result.TargetPeerId =	targetPeer;						
					lastFoundForceLength = resultingForce.GetLength();	
					
					
					GlobalLogger.LogFollowDocumentBoxMigration("PeerId: "+ this._parentPeer.GetPeerID()+" Begin with migration of DocumentBox:" 
																  + db.GetDocumentBoxUUID()+ " to target peer: "+targetPeer+ 
																  "  Force x:"	   + resultingForce.GetComponents()[0] +
																  "  Force y:"	   + resultingForce.GetComponents()[1]+ 
																  " DampingForce: "+ dampingForceLength+
																  " CommForce x: " + communicationForceVec.GetComponents()[0]+
																  " CommForce y: " + communicationForceVec.GetComponents()[1]+
																  " PeerBoxCapacity:  "+_parentPeer.GetPeerBox().GetDocumentBoxCapacity()+
																  " from PeerBox:" + _parentPeer.GetPeerID());						
				}										 
			}																		
		}
						
		return result;
	}
	
	
	class MigrationResult implements IDocumentBoxMigrationDirectionResult
	{

		public IDocumentBox DocumentBox;
		public IVector Vector;
		public long TargetPeerId;
		
		@Override
		public IDocumentBox GetDocumentBox() 
		{
			return DocumentBox;
		}

		@Override
		public long TargetPeerId() {
			return TargetPeerId;
		}
		
	}
}
