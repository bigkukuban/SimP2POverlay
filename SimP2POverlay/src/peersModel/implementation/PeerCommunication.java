package peersModel.implementation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import commonHelper.GlobalLogger;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;
import peersModel.interfaces.IPeerCommunication;
import peersModel.interfaces.IPeerCommunicationMessage;

public class PeerCommunication implements IPeerCommunication {
	
	INetworkFacade _facade;
	ExecutorService _executor;
	
	private static BlockingQueue<MessageSender> _lstIncommingMessages = new LinkedBlockingQueue<MessageSender>(); 
	
	boolean _isStarted = false;
	
	public PeerCommunication(INetworkFacade facade)
	{
		_facade = facade;		
	}

	private void EnsureLoopRuns()
	{
		if(!_isStarted)
		{			
			_isStarted = true;
			_executor  = Executors.newSingleThreadExecutor();
			
			_executor.execute(new Runnable() 
			{
				@Override
				public void run() 
				{
					while (true) 
					{					
						try {			
																					
							Loop();
							
						} catch (InterruptedException e) {

							GlobalLogger.LogControllerState("PeerCommunication stopped ...");
							break;
						}
					}
				}
			});			
		}		
	}
	
	@Override
	public boolean SendMessageToPeer(long iTargetPeerId, IPeerCommunicationMessage msg, long iSourcePeerId) 
	{
				 		
		EnsureLoopRuns();
		
		IPeer targetPeer = _facade.GetPeerById(iTargetPeerId);
		IPeer sourcePeer = _facade.GetPeerById(iSourcePeerId);				
		
		if(targetPeer == null) return false;
		if(sourcePeer == null) return false;
						
		_lstIncommingMessages.add(new MessageSender(targetPeer,sourcePeer,msg));
							
		return true;
	}
	
	private void Loop() throws InterruptedException
	{
		MessageSender msg = _lstIncommingMessages.take();
						
		if(_lstIncommingMessages.size() > 1)
		{
			GlobalLogger.LogControllerState("PeerCommunication messages in queue:"+_lstIncommingMessages.size());	
		}
		
		
		msg._target.PostMessageFromOtherPeer(msg._source.GetPeerID(), msg._message);				
	}

	
	class MessageSender 
	{

		public IPeer _target;
		public IPeer _source;
		public IPeerCommunicationMessage _message;
						
		public MessageSender(IPeer target,IPeer source, IPeerCommunicationMessage message)
		{
			_target = target;
			_source = source;
			_message = message;
								
		}
				
	}


	@Override
	public void CleanUp() 
	{		
		if(_executor == null) return;
		
		_executor.shutdownNow();
		try {
			_executor.awaitTermination(100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	
}
