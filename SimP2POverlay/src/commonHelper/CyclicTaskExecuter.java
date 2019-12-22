package commonHelper;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import commonHelper.interfaces.ICyclicTaskExecuter;
import commonHelper.math.RandomUtilities;



public class CyclicTaskExecuter implements ICyclicTaskExecuter, Runnable
{
	//alle Jobs werden zyklisch ausgeführt, ohne prioritäten und wartezeiten 
	ArrayList<Runnable> _jobList = new ArrayList<Runnable>();  
	
	ExecutorService _executor = null;
			
	AtomicBoolean _isRunning = new  AtomicBoolean();
	AtomicLong _currentCycle = new  AtomicLong();
	 
	public long GetCurrentCycle()
	{
		return _currentCycle.get();
	}
	@Override
	public void RegisterNewExecutable(Runnable job) 
	{
		synchronized(_jobList)
		{
			_jobList.add(job);	
		}
		
	}

	@Override
	public void UnRegisterNewExecutable(Runnable job) 
	{
		synchronized(_jobList)
		{
			_jobList.remove(job);				
		}
		
	}

	@Override
	public void BeginExecution() 
	{
		 _executor = Executors.newFixedThreadPool((int) GlobalSimulationParameters.NumerOfUsedThreads);
		
		 _currentCycle.set(0);
		_isRunning.set(true);
		_executor.submit(this);
	}

	public void ContinueExecution()
	{
		_isRunning.set(true);
		 _executor = Executors.newFixedThreadPool((int) GlobalSimulationParameters.NumerOfUsedThreads);
		_executor.submit(this);
	}
	
	public void PauseExecution()
	{
		_isRunning.set(false);
		try {
			
			if(_executor == null) return;
			
			_executor.shutdown();
			_executor.awaitTermination(100000, TimeUnit.SECONDS);
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}
	
	@Override
	public void StopExecution() 
	{
		_isRunning.set(false);
		try {
			
			if(_executor != null)
			{
				_executor.shutdown();
				_executor.awaitTermination(100000, TimeUnit.SECONDS);	
			}
									
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		_jobList.clear();
	}
	
	@Override
	public void run() {	
		
		Runnable itm = null;
		
		synchronized(_jobList)
		{
			itm = RandomUtilities.SelectOneByRandomFromList(_jobList);
		}
		
		try{
		
			if(itm != null)
			{			
				itm.run();	
			}
		}catch(Exception exp)
		{
			System.out.println("CyclicTaskExecuter stopped a job:"+ exp.getMessage());
			exp.printStackTrace();
		}
		
		_currentCycle.getAndIncrement();
		if(_isRunning.get())
		{					
			_executor.submit(this);	
		}
		
	}
	

}
