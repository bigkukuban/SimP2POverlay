package commonHelper;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalLogger {
	

	public static boolean DebugFollowDocumentBoxMigration = false;
	
	public static boolean DebugForces = false;
	
	public static boolean DebugBroadCasts = false;
	
	public static boolean DebugWalkerStates = false;
	
	public static boolean DebugSimulationEvaluator = false;
	
	public static boolean DebugPeerSelectionStates = false;
	public static boolean DebugPeerStateChanged = false;
			
	public static boolean DebugDocumentBoxState = false;
	public static boolean DebugPeerBoxState = false;
	
	public static boolean DebugPeerBoxDeletingDocumentBox = true;
	public static boolean DebugNumberOfMessages = false;
	
			
	public static boolean DebugSimulationRunner = true;
	
	public static boolean DebugCapacityChange = false;
	
	
	public static boolean DebugDocumentBoxMigration = false;
	public static boolean DebugNetworkInitializer = false;
	public static boolean DebugPeerBoxRunState = false;
	
				
	private static ExecutorService _executor;
					

	private static void EnsureStarted()
	{		
		if(_executor == null)		
			_executor = Executors.newFixedThreadPool(1); 
				
	}
	
	private static void placeMessage(final String message)
	{
		EnsureStarted();
		Callable<Boolean> job = new Callable<Boolean>(){

			@Override
			public Boolean call() throws Exception {

				System.out.println(System.currentTimeMillis()+" (LTC: "+ GlobalTools.GetLifetimeCounter()  + ")" +message);
				
				return true;
			}			
		};
		_executor.submit(job);
	}

	public static void WriteOnce(String message)
	{
		placeMessage(message);
	}
	
	public static void LogFollowDocumentBoxMigration(String message)
	{
		if(DebugFollowDocumentBoxMigration)
		{
			placeMessage(message);
		}
	}
	
	public static void LogNetworkInitializer(String message)
	{
		if(DebugNetworkInitializer)
		{		
			placeMessage(message);
		}
	}
	
	public static void LogStatesDocumentBox(String message)
	{
		if(DebugDocumentBoxState)
		{		
			placeMessage(message);
		}
	}
	
	public static void LogCapacityChange(String message)
	{
		if(DebugCapacityChange)
		{		
			placeMessage(message);
		}
	}
	
	public static void LogForces(String message)
	{
		if(DebugForces)
		{		
			placeMessage(message);
		}
	}
	
	
	public static void LogDocumentBoxMigration(String message)
	{
		if(DebugDocumentBoxMigration)
		{		
			placeMessage(message);
		}	
	}
	
	public static void LogBroadCasts(String message)
	{
		if(DebugBroadCasts)
		{		
			placeMessage(message);
		}
	}
	
	public static void LogsSelectionState(String message)
	{
		if(DebugPeerSelectionStates )
		{		
			placeMessage(message);
		}
	}
	
	public static void LogSimulationRunner(String message)
	{
		if(DebugSimulationRunner)
		{			
			placeMessage(message);
		}
	}
	
	
	
	
	public static void LogNumberOfInputMessagesState(String message)
	{
		if(DebugNumberOfMessages)
		{			
			placeMessage(message);
		}
	}
	
	public static void LogPeerBoxDeletingOfDocumentBox(String message)
	{
		if(DebugPeerBoxDeletingDocumentBox)
		{			
			placeMessage(message);
		}
	}
	
	public static void LogPeerBoxRunState(String message)
	{
		if(DebugPeerBoxRunState)
		{			
			placeMessage(message);
		}
	}
	
	public static void LogPeerBoxState(String message)
	{
		if(DebugPeerBoxState)
		{			
			placeMessage(message);
		}
	}
	
	public static void LogControllerState(String message)
	{
		if(DebugPeerStateChanged )
		{			
			placeMessage(message);
		}
	}
		
	
	public static void LogWalkerState(String message)
	{
		if(DebugWalkerStates )
		{			
			placeMessage(message);
		}
	}
	
	
	public static void LogSimulationEvaluator(String message)
	{
		if(DebugSimulationEvaluator )
		{			
			placeMessage(message);
		}
	}
	
	
		
	
}
