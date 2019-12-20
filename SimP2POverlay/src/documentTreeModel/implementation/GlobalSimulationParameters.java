package documentTreeModel.implementation;

public class GlobalSimulationParameters {	
	
	
	public static void Recalculate(long newNumberOfPeerBoxes, int numberOfDocumentBoxes)
	{
		NumberOfItemsInSimulation = newNumberOfPeerBoxes+numberOfDocumentBoxes;
		TimeoutForDocumentBoxResentDuringMigration = (long) (1000*NumberOfItemsInSimulation);
		TimeoutForDocumentBoxMessageResponse = (long) (400*NumberOfItemsInSimulation);
		TimeoutForDocumentBoxMessageBeginAuthState = (long) (800*NumberOfItemsInSimulation);
		
		ForwardPointerCacheLength =  (int)(numberOfDocumentBoxes);
		MeanInitialAuthenticationDuration = (long) (NumberOfItemsInSimulation);
		

		GarbageCollectionRatio = (long) (400*NumberOfItemsInSimulation);
		AuthenticationRepeatRatio = (long) (200*NumberOfItemsInSimulation);			
		DisturbeAuthentificationAfterMigration = false;
		
		MaximalInitialNumberOfAllowedDocumentBoxesOnPeerBox = numberOfDocumentBoxes;
		
		RecalculateDistanceReduction( Math.sqrt(newNumberOfPeerBoxes) * DistanceReductionFactor);
	}
	
	
	private static void RecalculateDistanceReduction(double peerBoxOneSideLength)
	{
		double CapPbMax = MaximalInitialNumberOfAllowedDocumentBoxesOnPeerBox;
		double MagnitueOnLastPeerBox = 1;
		double desiredDistance = peerBoxOneSideLength;
		
		DistanceReductionPower = Math.log(CapPbMax/MagnitueOnLastPeerBox)/Math.log(1+desiredDistance);
	}
	
	//the initial authentication of a peerbox is beeing disturbed by probability of 0.5
	public static boolean DisturbeAuthentificationAfterMigration = false;
	
	//number of threads used to handle the network participants
	public static long NumerOfUsedThreads =1;
	
	//number of DocumentBoxes + PeerBoxes within Simulation
	public static long NumberOfItemsInSimulation = 140;
	
	// this is very critical issue, this will lead to resent of a document box and their doubling
	// after this timeout the given documentBox will be resent again to other PeerBox
	public static long TimeoutForDocumentBoxResentDuringMigration = (long) (3000*NumberOfItemsInSimulation); 
	
	//after this time the peerbox removes the sent message in case of no response
	public static long TimeoutForDocumentBoxMessageResponse = (long) (200*NumberOfItemsInSimulation);
		
	//after  this timeout the documentBox switches into state idle again (from authentication)
	public static long TimeoutForDocumentBoxMessageBeginAuthState = (long) (100*NumberOfItemsInSimulation);
	
	
	public static boolean bUseLRUCache = true;
	
	public static boolean bUseLRDCache = false;
	
	// ratio authenticationBeginDuration = authenticationRepeatRatio*meanAuthenticatioDuration,
	// after this period the DocumentBox will be asked to repeat the authentication
	public static long AuthenticationRepeatRatio = (long) (2);
	
	// ratio garbageCollectionBeginDuration = garbageCollectionRatio*meanAuthenticatioDuration,
	// after this period the DocumentBox is allowed to be deleted 
	public static long GarbageCollectionRatio = (long) (1000*AuthenticationRepeatRatio+1000000);
	
	public static long MeanInitialAuthenticationDuration = (long) (1000*NumberOfItemsInSimulation);
	
	// this should be the minimal cache size ..
	public static int ForwardPointerCacheLength = (int)NumberOfItemsInSimulation;
	
	public static int MaximalInitialNumberOfAllowedDocumentBoxesOnPeerBox = 0;
	
	
	public static int MaximalNumberOfAllExecutedMigrationsToStopTheSimulationStep = 10000000;
	
	
	//+++++++++LoadBalancing Parameter++++++++	
	//factor for attraction between two connected documentBoxes
	public static double FactorFc = 1;	
	//factor for migration probability --> migration probability as function of distance (exp(factor))
	public static double FactorProbabilityReduction = -1.6;	
	//limit value for distance calculation --> accuracy
	public static double AccuracyLimitValue = 1;	
	// influences the load force distance , the higher the value the smaller is the distance
	public static double DistanceReductionPower = 1.0;	
	//influences the calculaten of DistanceReductionPower, the max. broarcast is multiplicated by this value
	public static double DistanceReductionFactor = 0.3;
	
	//guard time between two load force broadcast originatig from the same PeerBox (avoiding overtaking of broadcast messages and system overload)
	// the value depends on maximal system size
	public static long BroadCastGuardTime = 1000;
				
}
