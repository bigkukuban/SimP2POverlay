package randomWalk.implementation;

import randomWalk.interfaces.IRandomWalker;
import randomWalk.interfaces.IRandomWalkerHost;

public class RandomWalkerSimpleColorSetter implements IRandomWalker 
{
	int _iColor;

	public RandomWalkerSimpleColorSetter(int iColor )
	{
		_iColor = iColor;
	}
	
	public boolean IsLongHaulWalker()
	{
		return true;
	}
	
	@Override
	public void Visit(IRandomWalkerHost host) 		
	{		
		host.SetHostColor(_iColor);								
	}

	volatile boolean _isKilled = false;
	public boolean IsKilled()
	{
		return _isKilled;
	}
	
	@Override
	public void KillWalker() {		
		_isKilled = true;		
	}

}
