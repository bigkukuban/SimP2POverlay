package simulationRunner.topologyEvaluator;

import java.util.ArrayList;
import java.util.Collection;

import simulationRunner.topologyEvaluator.interfaces.IMigrationStateEvaluationResult;
import simulationRunner.topologyEvaluator.interfaces.IPeerBoxForwardPointerLengthState;

public class MigrationStateEvaluationResult implements IMigrationStateEvaluationResult
{

	public long CountDocumentBoxes;
	public long CountInMigration;			
	public long CountAuthenticatedDocumentBoxes;
	public long CountUnAuthenticatedDocumentBoxes;
	public long CurrentMeanAuthenticationDuration;
	public long SystemDocumentBoxcapacity;
	public ArrayList<IPeerBoxForwardPointerLengthState> ForwardPointerStates = new  ArrayList<IPeerBoxForwardPointerLengthState>();
	
	public Collection<IPeerBoxForwardPointerLengthState> GetForwardPointerLength(){
		return ForwardPointerStates;
	}

	@Override
	public long GetCountDocumentBoxes() {
		return CountDocumentBoxes;
	}

	@Override
	public long GetCountInMigration() {
		return CountInMigration;
	}

	@Override
	public long GetCountAuthenticatedDocumentBoxes() {
		return CountAuthenticatedDocumentBoxes;
	}

	@Override
	public long GetCountUnAuthenticatedDocumentBoxes() 
	{		
		return CountUnAuthenticatedDocumentBoxes;
	}

	public String GetReadableForwardPointerStates()
	{
		String result = "";
		for(IPeerBoxForwardPointerLengthState s : ForwardPointerStates)
		{
			result = result +" PeerBox: "+s.GetPeerBoxId() + " Length: "+s.GetLengthForwardPointerCache()+" --";
		}
		
		return result;
	}
	
	@Override
	public String GetReadableEvaluationState() 
	{			
		return "At the moment the are "+GetCountDocumentBoxes()+
				" DocumentBoxes. Authenticated: " + GetCountAuthenticatedDocumentBoxes() + 
				" UnAuthenticated: "+ GetCountUnAuthenticatedDocumentBoxes() +
				" InMigration: "  + GetCountInMigration()+
				" Mean authentication duration:  "+ CurrentMeanAuthenticationDuration;
	}

	@Override
	public long SystemCapacityForDocumentBoxes() {
		return SystemDocumentBoxcapacity;
	}

}
