package commonHelper;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import commonHelper.interfaces.ICyclicTaskExecuter;
import commonHelper.interfaces.IPeerIdIntoEuclideanAdressConverter;

public class GlobalTools {		
	
	public static AtomicInteger GlobalMigrationCounter = new AtomicInteger();
	
	public static AtomicBoolean GlobalSetting_SimulationActive = new AtomicBoolean(true);	
	
	public static long GetLifetimeCounter()
	{
		return TaskExecutor.GetCurrentCycle();
		
		//return System.currentTimeMillis();
	}
	private static IPeerIdIntoEuclideanAdressConverter currentServiceHandler;
	public static void RegisterGlobalService(IPeerIdIntoEuclideanAdressConverter converter)
	{
		currentServiceHandler = converter;
	}
	public static IPeerIdIntoEuclideanAdressConverter GetAddressConverterService(){
		return currentServiceHandler;
	}
	
	private static ICyclicTaskExecuter TaskExecutor = new CyclicTaskExecuter();
	
	public static ICyclicTaskExecuter GetTaskExecutor()
	{
		return TaskExecutor;
	}
}
