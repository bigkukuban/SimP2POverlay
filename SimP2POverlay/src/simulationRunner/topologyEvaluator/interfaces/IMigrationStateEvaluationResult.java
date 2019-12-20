package simulationRunner.topologyEvaluator.interfaces;

public interface IMigrationStateEvaluationResult 
{
	
	long GetCountDocumentBoxes();
	long GetCountInMigration();			
	long GetCountAuthenticatedDocumentBoxes();
	long GetCountUnAuthenticatedDocumentBoxes();
	String GetReadableEvaluationState();
	String GetReadableForwardPointerStates();
	
	//returns the full system capacity for DocumentBoxes over all PeerBoxes
	long SystemCapacityForDocumentBoxes();

}
