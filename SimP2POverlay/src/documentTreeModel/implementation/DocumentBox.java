package documentTreeModel.implementation;

import java.util.ArrayList;
import java.util.Collection;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import commonHelper.GlobalLogger;
import commonHelper.GlobalTools;
import commonHelper.math.RandomUtilities;
import documentTreeModel.interfaces.IDocumentBox;
import documentTreeModel.interfaces.IDocumentBoxConnection;
import documentTreeModel.interfaces.IDocumentBoxEvaluation;
import documentTreeModel.interfaces.IDocumentBoxForLocalConnection;
import documentTreeModel.interfaces.IDocumentBoxMessage;
import documentTreeModel.interfaces.IDocumentBoxMessage.DocumentBoxMessageType;
import documentTreeModel.interfaces.IOutgoingMessageDocumentBox;
import documentTreeModel.interfaces.IPeerBox;
import documentTreeModel.interfaces.IDocumentBox.IAuthenticationState.State;

/**
 * For the document-box is a message pipe implemented. 
 * All messages are handled by an internal thread
 * @author Dimitri
 *
 */

public class DocumentBox implements IDocumentBox, IDocumentBoxForLocalConnection,IDocumentBoxEvaluation, Runnable
{
	//manage connections to other document-boxes .. 
	
	UUID _documentBoxId;
	IPeerBox _currentPeerBox;	
	private BlockingQueue<ReceivedMessage> _lstIncommingMessages = new LinkedBlockingQueue<ReceivedMessage>(); 
	
	private ArrayList<IOutgoingMessageDocumentBox> _lstOutMessages = new ArrayList<IOutgoingMessageDocumentBox>();
	
	private ArrayList<LocalConnectionDocumentBox> _lstConnections = new ArrayList<LocalConnectionDocumentBox>();
	
	private AuthenticationState _authenticataionState = new AuthenticationState(IAuthenticationState.State.UnAuthenticated, GlobalTools.GetLifetimeCounter(), GlobalTools.GetLifetimeCounter()); 
	
	Object _synchronizerController = new Object();

	ExecutorService _executor;
	
	public DocumentBox(UUID id)
	{
		_documentBoxId = id;
	}
	
	public DocumentBox()
	{
		_documentBoxId = UUID.randomUUID();		
	}
	
	
	private DocumentBox(AuthenticationState authState, ArrayList<LocalConnectionDocumentBox> connections, 
					    ArrayList<OutgoingMessageDocumentBox> msgOut, ArrayList<ReceivedMessage> rcvMsg, 
					    UUID documentBoxId)
	{
		_documentBoxId = documentBoxId;

		_lstConnections.addAll(connections);
		
		for(LocalConnectionDocumentBox conn : connections)
		{
			conn._parent = this;
			
			conn._stateAuthHandler = AuthenticationHandlerStateMachine.Idle;
			conn._stateAuthRunner = AuthenticationRunnerStateMachine.Idle;				
		}
		
		_authenticataionState = authState;
	}
	


	public Collection<IDocumentBoxConnection> GetConnections()
	{
		ArrayList<IDocumentBoxConnection> result = new  ArrayList<IDocumentBoxConnection>();
		
		for(LocalConnectionDocumentBox conn : _lstConnections)
		{
			result.add(conn.Connection);
		}
		
		return result;
	}

	
	public void AddConnections(ArrayList<IDocumentBoxConnection> lstConnections)
	{		
		for(IDocumentBoxConnection conn : lstConnections)
		{
			boolean bSkipThis = false;
			//check if already connected
			for(LocalConnectionDocumentBox cn : _lstConnections)
			{
				if(conn.GetDocumentBoxId().equals(cn.Connection.GetDocumentBoxId()))
				{
					bSkipThis = true;
				}
			}			
			if(bSkipThis) continue;
			
			LocalConnectionDocumentBox locConnection = new LocalConnectionDocumentBox();			
			locConnection.Connection = conn;	
			locConnection._parent = this;
			_lstConnections.add(locConnection);
		}
	}
			
	private void StartExecutorService()
	{		
		
		GlobalLogger.LogDocumentBoxMigration("DocumentBox " + _documentBoxId.toString() +" started execution! Current PeerBox : " + this._currentPeerBox.GetPeerBoxAddress());
		
		_authenticataionState.SetCurrentState(IAuthenticationState.State.UnAuthenticated);
		GlobalTools.GetTaskExecutor().RegisterNewExecutable(this);
		
		
		
	}
	
		
	private void StopExecutorService() 
	{			
		GlobalTools.GetTaskExecutor().UnRegisterNewExecutable(this);		
		_authenticataionState.PrepareForMigration();
		
		GlobalLogger.LogDocumentBoxMigration("DocumentBox " + _documentBoxId.toString() +" stopped execution! Current PeerBox : " + this._currentPeerBox.GetPeerBoxAddress());
		
	}
			
	public long GetPeerBoxAddress()
	{				
		return _currentPeerBox.GetPeerBoxAddress();
	}
	
	private void HandleRemoteConnections(ReceivedMessage msg)
	{
		
		if(msg != null)
		{
			if(msg.Message.GetMessageType() == DocumentBoxMessageType.BeginAuthentication)
			{
				this._authenticataionState.SetAuthenticationRequestedTimeStamp(GlobalTools.GetLifetimeCounter());
			}			
		}
		
		
		for(LocalConnectionDocumentBox conn : _lstConnections)
		{
			//place the message to each connection
			conn.HandleConnection(msg);				
					
		}
	}
	
	public void run() 
	{					 	
		ReceivedMessage msg = null;
		
		GlobalLogger.LogNumberOfInputMessagesState("DocumentBox:" + this.GetPeerBoxAddress() + " number of messages: "+ _lstIncommingMessages.size());
					
		do{
				
			msg = _lstIncommingMessages.poll();
			_lstOutMessages.clear();
					
			HandleRemoteConnections(msg);
					
					
			for(IOutgoingMessageDocumentBox msgEntry : _lstOutMessages)
			{
				
				GlobalLogger.LogStatesDocumentBox("DocumentBox " + _documentBoxId.toString() + " Sending message :"+msgEntry.getClass().getName());
				_currentPeerBox.SendMessageToDocumentBox(this._documentBoxId,
														msgEntry.GetTargetDocumentBox(), 
														msgEntry.GetTargetPeerBox(), 
														msgEntry.GetDocumentBoxMessage());
			}
		}while(msg != null);
	}
	
	@Override
	public UUID GetDocumentBoxUUID() {

		return _documentBoxId;
	}

	@Override
	public void PlaceMessagefromOtherDocumentBox(IDocumentBoxMessage message, UUID sourceDocumentBox, long sourcePeerBox) 
	{
		ReceivedMessage msgReceived = new ReceivedMessage();
		msgReceived.Message = message;
		msgReceived.SourceDocumentBox = sourceDocumentBox;
		msgReceived.SourcePeerBox = sourcePeerBox;			
		
		_lstIncommingMessages.add(msgReceived);
	}

	@Override
	public void BeginMigration() 
	{
		//stop all internal processes, wait for the process to end .. 		
		StopExecutorService();		
	}

	@Override
	public void MigrationDone(IPeerBox newPeerBox) 
	{
		//begin all internal processes
		_currentPeerBox = newPeerBox;		
		
		GlobalTools.GlobalMigrationCounter.incrementAndGet();
		
		StartExecutorService();
						
		
		
		if(GlobalSimulationParameters.DisturbeAuthentificationAfterMigration)
		{
			if(RandomUtilities.p05ProbablityTrial())
			{
				// Send ping to all connected DocumentBoxes
				Authenticate();				
			}			
		} else {
			Authenticate();	
		}
	}
	
	
	/**
	 * For Evaluation issues only
	 */
	public void SetParentPeerBox(IPeerBox pb)
	{
		_currentPeerBox = pb;
	}
	
	private LocalConnectionDocumentBox GetConnectionForRemoteDocumentBox(UUID idOfDocumentBox)
	{
		LocalConnectionDocumentBox result = null;
		
		for(LocalConnectionDocumentBox conn : _lstConnections)
		{
			if(conn.Connection.GetDocumentBoxId() == idOfDocumentBox)
			{
				result = conn;
			}
		}
		
		return result;
	}
	
	public enum AuthenticationRunnerStateMachine
	{
		AuthenticationRequestPending,
		Idle,
		Undefined
	}
	
	public enum AuthenticationHandlerStateMachine
	{
		PingRequestPending,
		Idle,
		Undefined
	}
		
	
			
	class ReceivedMessage
	{
		public IDocumentBoxMessage Message;
		public UUID SourceDocumentBox;
		public long SourcePeerBox;
	}

	class AuthenticationState implements IAuthenticationStateForLocalConnection
	{

		public State _state;
		public long _timeStamp;
		public long _lastAuthenticationRequested;
		
		public AuthenticationState(State st, long timeStamp, long beginAuth)
		{
			_state = st;
			_timeStamp = timeStamp;
			_lastAuthenticationRequested = beginAuth;
		}

		public void SetAuthenticationRequestedTimeStamp(long value)
		{
			_lastAuthenticationRequested = value;
		}
		
		public long GetAuthenticationRequestedTimeStamp()
		{
			return _lastAuthenticationRequested;
		}
		
		public void SetCurrentState(State st) {
			
			if(_state == State.Authenticated && st == State.UnAuthenticated)
			{
				GlobalLogger.LogStatesDocumentBox("DocumentBox " + _documentBoxId.toString() +" set to Unauthenticated!");
			}
			
			if(_state == State.UnAuthenticated && st == State.Authenticated)
			{
				GlobalLogger.LogStatesDocumentBox("DocumentBox " + _documentBoxId.toString() +" set to Authenticated");
			}
											
			_state = st;
		}

		public void PrepareForMigration()
		{
			if( _state == State.Authenticated)
			{
				_timeStamp  = GlobalTools.GetLifetimeCounter();				
			}
		}
		
		
		public void SetTimeStampAuthentication(long value) {
			_timeStamp = value;
		}
		
		@Override
		public State GetCurrentState() {									
			return _state;
		}
		
		
		@Override
		public long GetTimeStampAuthentication() {
			return _timeStamp;
		}
		
		public synchronized IAuthenticationState CloneMe()
		{
			IAuthenticationState result = new  AuthenticationState(_state,_timeStamp, _lastAuthenticationRequested);
			
			return result;
		}
		
	}
	
	@Override
	public void Authenticate() {
		// enqueue a message to begin the authentication
		for(LocalConnectionDocumentBox conn : _lstConnections)
		{
			DoAuthenticate(conn.Connection.GetDocumentBoxId());
		}
	}
	
			
	/*
	 * enque the message into the input loop
	 */
	private void DoAuthenticate(UUID documentBoxId)
	{
		LocalConnectionDocumentBox conn = GetConnectionForRemoteDocumentBox(documentBoxId);
		
		ReceivedMessage msgOut = new ReceivedMessage();
		msgOut.Message = new DocumentBoxMessageBeginAuthenticationRequest();
		msgOut.SourceDocumentBox = conn.Connection.GetDocumentBoxId(); //this sender needs to be the 
		msgOut.SourcePeerBox = conn.Connection.GetLastKnownPeerBoxAdress();		
			
		_lstIncommingMessages.add(msgOut);
	}
	
	
	public IAuthenticationStateForLocalConnection GetAuthenticationStateObject()
	{
		return _authenticataionState;
	}
	
	public IAuthenticationState GetAuthenticationState()
	{
		return _authenticataionState.CloneMe();
	}

	@Override
	public IDocumentBox CloneMe() 
	{	
		//should only be called in stopped state of the documentbox
		
		ArrayList<LocalConnectionDocumentBox>  lstConns = new ArrayList<LocalConnectionDocumentBox>();
		
		for(LocalConnectionDocumentBox cn : _lstConnections)
		{
			lstConns.add(cn.CloneMe());
		}
			
		ArrayList<OutgoingMessageDocumentBox> outMsgList = new  ArrayList<OutgoingMessageDocumentBox>();		
		
		ArrayList<ReceivedMessage> rcvMsgList = new  ArrayList<ReceivedMessage>();
		
		DocumentBox docBx = new DocumentBox((AuthenticationState)_authenticataionState.CloneMe(), lstConns, 
											outMsgList,rcvMsgList, this._documentBoxId);		
		
		return docBx;
	}

	@Override
	public void SetAsAuthenticated() 
	{				
		_authenticataionState = new AuthenticationState(State.Authenticated, GlobalTools.GetLifetimeCounter(), GlobalTools.GetLifetimeCounter());
	}

	@Override
	public Collection<IOutgoingMessageDocumentBox> GetOutgoingList() {
			return this._lstOutMessages;
	}
	
	
}
