package commonHelper.interfaces;

public interface ICyclicTaskExecuter{

	void RegisterNewExecutable(Runnable job);
	void UnRegisterNewExecutable(Runnable job);
	
	void BeginExecution();
	void StopExecution();
	long GetCurrentCycle();
	
	void ContinueExecution();
	void PauseExecution();
}
