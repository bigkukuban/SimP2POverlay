package commonHelper._CyclicTaskExecuter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import commonHelper.CyclicTaskExecuter;

public class RegisterNewExecutable {

	boolean  bWasRun = false;
	AtomicInteger _atomicInt = new  AtomicInteger();
	
	@Test
	public final void RegisterOneNewExecutable() {
		
		CyclicTaskExecuter executor = new  CyclicTaskExecuter();
		
		executor.BeginExecution();
				
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		bWasRun = false;
		executor.RegisterNewExecutable(new Runnable(){

			@Override
			public void run() {
				bWasRun = true;				
			}
		
		});
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		executor.StopExecution();
		
		assertTrue(bWasRun);
	}
	
	
	class Job implements Runnable
	{

		public boolean _executed = false;
		
		int iId = 0;
		public Job(int id)
		{
			iId = id;
		}
		@Override
		public void run() {
			_atomicInt.addAndGet(iId*iId);
			_executed = true;
		}
		
	}
	
	@Test
	public final void RegisterManyNewExecutables() 
	{
		
		CyclicTaskExecuter executor = new  CyclicTaskExecuter();
		
		executor.BeginExecution();
				
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Job> jList = new ArrayList<Job>(); 
		bWasRun = false;
		for(int z =0; z<10000; z++)
		{
			Job j = new Job(z);
			executor.RegisterNewExecutable(j);
	
			jList.add(j);
		}	
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		executor.StopExecution();
		
		int iCounter = 0;
		
		for(Job j : jList)
		{
			if(j._executed)
			{
				iCounter++;
			}
		}
		
		assertTrue(iCounter == 10000);
	}
	
}
