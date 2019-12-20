package documentTreeModel.implementation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import commonHelper.GlobalLogger;
import commonHelper.GlobalTools;
import documentTreeModel.implementation.DocumentBox.AuthenticationHandlerStateMachine;
import documentTreeModel.implementation.DocumentBox.AuthenticationRunnerStateMachine;
import documentTreeModel.implementation.DocumentBox.ReceivedMessage;
import documentTreeModel.interfaces.IDocumentBoxConnection;
import documentTreeModel.interfaces.IDocumentBoxForLocalConnection;
import documentTreeModel.interfaces.IDocumentBoxMessage;
import documentTreeModel.interfaces.IDocumentBox.IAuthenticationState.State;
import documentTreeModel.interfaces.IDocumentBoxMessage.DocumentBoxMessageType;
import documentTreeModel.interfaces.IDocumentBoxMessageResponse.ResponseTypeDocumentBox;

public class LocalConnectionDocumentBox 
{
	public IDocumentBoxConnection Connection;
									
	Map<DocumentBoxMessageType, IDocumentBoxMessage> _msgSendByType = new HashMap<DocumentBoxMessageType, IDocumentBoxMessage>();
	
	AuthenticationRunnerStateMachine _stateAuthRunner = AuthenticationRunnerStateMachine.Idle;
	long _millsAuthRunnerSwitchedToAuthReqPending =0;
	long _beginOfAuthenticationProcessMs =0;
	
	AuthenticationHandlerStateMachine _stateAuthHandler = AuthenticationHandlerStateMachine.Idle;
	long _millsAuthHandlerSwitchedToAuthReqPending =0;
	
	IDocumentBoxForLocalConnection _parent; 
	
	public synchronized LocalConnectionDocumentBox CloneMe()
	{
		return new LocalConnectionDocumentBox(Connection.CloneMe(),
														_stateAuthRunner,
														_stateAuthHandler,
														_msgSendByType,
														_millsAuthRunnerSwitchedToAuthReqPending,
														_beginOfAuthenticationProcessMs,
														_millsAuthHandlerSwitchedToAuthReqPending, 
														null);
	}
	
	public LocalConnectionDocumentBox()
	{
		
	}
	
	public LocalConnectionDocumentBox(IDocumentBoxConnection conn,AuthenticationRunnerStateMachine authRunner,  
													   AuthenticationHandlerStateMachine authHandler, 
													   Map<DocumentBoxMessageType, IDocumentBoxMessage> msgDict,
													   long millsAuthRunnerSwitchedToAuthReqPending,
													   long beginOfAuthenticationProcessMs,
													   long millsAuthHandlerSwitchedToAuthReqPending,
													   IDocumentBoxForLocalConnection parent)
	{
		Connection = conn;
		_stateAuthRunner = authRunner;
		_stateAuthHandler = authHandler;
		_msgSendByType.putAll(msgDict);
		_millsAuthRunnerSwitchedToAuthReqPending = millsAuthRunnerSwitchedToAuthReqPending;
		_beginOfAuthenticationProcessMs = beginOfAuthenticationProcessMs;
		_millsAuthHandlerSwitchedToAuthReqPending = millsAuthHandlerSwitchedToAuthReqPending;
		_parent = parent;
	}
	
	//there are two state machines, they are handled without own class
			
	public void HandleConnection(ReceivedMessage msg)
	{
	
		if(msg != null)
		{
			if(msg.SourceDocumentBox == Connection.GetDocumentBoxId())
			{
			
				if(msg.Message.GetMessageType() == DocumentBoxMessageType.MigratedResponse)
				{
					
					GlobalLogger.LogStatesDocumentBox("DocumentBox " + _parent.GetDocumentBoxUUID().toString() + " MigratedResponse received from " +msg.SourceDocumentBox);
					
					// migrated message should only be delivered if messages were already sent
					DocumentBoxMessageDocumentBoxMigratedResponse rsp = (DocumentBoxMessageDocumentBoxMigratedResponse)msg.Message;
					
					if(rsp.GetSourcePeerBox() == Connection.GetLastKnownPeerBoxAdress())
					{
						
						// the message is only accepted if the sender PeerBox equals the last known, we follow the path
						IDocumentBoxMessage dcBoxMsg = (IDocumentBoxMessage)rsp.GetFailedMessage();
						
						IDocumentBoxMessage msgToSend = _msgSendByType.get(	dcBoxMsg.GetMessageType());								
						
						GlobalLogger.LogStatesDocumentBox("DocumentBox " + _parent.GetDocumentBoxUUID().toString() + " MigratedResponse received from " +msg.SourceDocumentBox +" resend message type " + dcBoxMsg.GetMessageType());
						
						Connection.SetLastKnownPeerBoxAdress(rsp.MigratedToPeerBoxTarget());
						
						
						OutgoingMessageDocumentBox msgOut = new OutgoingMessageDocumentBox();					
						msgOut.TargetDocumentBox = Connection.GetDocumentBoxId();
						msgOut.TargetPeerBox = Connection.GetLastKnownPeerBoxAdress();
						msgOut.Message = msgToSend;
																
						this._parent.GetOutgoingList().add(msgOut);	
						
						
						if(_stateAuthRunner != AuthenticationRunnerStateMachine.Idle)
						{
							_millsAuthRunnerSwitchedToAuthReqPending = GlobalTools.GetLifetimeCounter();							
						}
						if(_stateAuthHandler != AuthenticationHandlerStateMachine.Idle)
						{
							_millsAuthHandlerSwitchedToAuthReqPending = GlobalTools.GetLifetimeCounter();
						}
					}							 			
				}
				
				
				if( msg.Message.GetMessageType()  == IDocumentBoxMessage.DocumentBoxMessageType.PingRequest)
				{			
					
					//question: is it allowed to change the PeerBox-Adress by receiving a ping, is the pinger always authenticated?
					//NO: PeerBox-address is only changed by migrated message
					GlobalLogger.LogStatesDocumentBox("DocumentBox " + _parent.GetDocumentBoxUUID().toString() + " PingRequest received from " +msg.SourceDocumentBox);								
															
					OutgoingMessageDocumentBox msgOut = new OutgoingMessageDocumentBox();
														
					msgOut.TargetDocumentBox = Connection.GetDocumentBoxId();
					msgOut.TargetPeerBox = Connection.GetLastKnownPeerBoxAdress();
					msgOut.Message =  new DocumentBoxMessagePingResponse(ResponseTypeDocumentBox.OK, msg.Message.GetMessageGuid()); 
					
					//store this to handle the MigratedResponse message 
					_msgSendByType.put(msgOut.Message.GetMessageType(), msgOut.Message);
					
					this._parent.GetOutgoingList().add(msgOut);				
				}
			}
		}
						
		//handle the AuthenticationRunner
		AuthenticationRunner(msg);
		
		
		//handle AuthenticationHandler
		AuthenticationHandler(msg);

	}
	
	
	private void AuthenticationRunner(ReceivedMessage msg)
	{
		AuthenticationRunnerStateMachine nextState = _stateAuthRunner;
		
		if( msg != null)
		{
			if(msg.SourceDocumentBox == Connection.GetDocumentBoxId())
			{
				
				if( msg.Message.GetMessageType()  == IDocumentBoxMessage.DocumentBoxMessageType.BeginAuthentication)
				{
					//GlobalLogger.LogStatesDocumentBox("DocumentBox,AuthenticationRunner: Received message: " + _parent.GetDocumentBoxUUID().toString() + " AuthenticateConnectionRequest sent to " +msg.SourceDocumentBox+ " to PeerBox :" + Connection.GetLastKnownPeerBoxAdress());
					if(_stateAuthRunner == AuthenticationRunnerStateMachine.Idle)
					{
						nextState = AuthenticationRunnerStateMachine.AuthenticationRequestPending;
						
						OutgoingMessageDocumentBox msgOut = new OutgoingMessageDocumentBox();
						
						msgOut.TargetDocumentBox = Connection.GetDocumentBoxId();
						msgOut.TargetPeerBox = Connection.GetLastKnownPeerBoxAdress();
						msgOut.Message =  new DocumentBoxMessageAuthenticateConnectionRequest();
						this._parent.GetOutgoingList().add(msgOut);	
						
						_msgSendByType.put(msgOut.Message.GetMessageType(), msgOut.Message);
						
						GlobalLogger.LogStatesDocumentBox("DocumentBox,AuthenticationRunner:" + _parent.GetDocumentBoxUUID().toString() + " AuthenticationRequest sent to " +msg.SourceDocumentBox+ " on PeerBox :" + Connection.GetLastKnownPeerBoxAdress());
						
						_millsAuthRunnerSwitchedToAuthReqPending = GlobalTools.GetLifetimeCounter();					
						_beginOfAuthenticationProcessMs = _millsAuthRunnerSwitchedToAuthReqPending;
					} 
				}				
								
				if( msg.Message.GetMessageType()  == IDocumentBoxMessage.DocumentBoxMessageType.AuthenticateConnectionResponse)
				{
					IDocumentBoxMessage msgToSend = _msgSendByType.get(	IDocumentBoxMessage.DocumentBoxMessageType.AuthenticateConnectionRequest);
					GlobalLogger.LogStatesDocumentBox("DocumentBox,AuthenticationRunner:" + _parent.GetDocumentBoxUUID().toString() + " AuthenticateConnectionResponse received from " +msg.SourceDocumentBox);
					if(msgToSend.GetMessageGuid().equals(msg.Message.GetMessageGuid())) // do this only for responses
					{																		
						GlobalLogger.LogStatesDocumentBox("DocumentBox,AuthenticationRunner:" + _parent.GetDocumentBoxUUID().toString() + " AuthenticateConnectionResponse hat correct UUID");
						if(_stateAuthRunner == AuthenticationRunnerStateMachine.AuthenticationRequestPending)
						{
							nextState = AuthenticationRunnerStateMachine.Idle;
							
							DocumentBoxMessageAuthenticateConnectionResponse msgAuth = (DocumentBoxMessageAuthenticateConnectionResponse)msg.Message;
							
							if(msgAuth.GetResponseType() == ResponseTypeDocumentBox.OK)
							{
								
								this._parent.GetAuthenticationStateObject().SetCurrentState(State.Authenticated);
								
								// message will be sent to parent PeerBox, not forwarded outside, just internal usage
								OutgoingMessageDocumentBox msgOut = new OutgoingMessageDocumentBox();																	
								msgOut.Message = new DocumentBoxMessageAuthenticationDone(GlobalTools.GetLifetimeCounter() - _beginOfAuthenticationProcessMs);			
								msgOut.TargetPeerBox = this._parent.GetPeerBoxAddress();
								this._parent.GetOutgoingList().add(msgOut);	
								
								
								GlobalLogger.LogStatesDocumentBox("DocumentBox,AuthenticationRunner:" + _parent.GetDocumentBoxUUID().toString()+" connection " +Connection.GetDocumentBoxId() +" is AUTHENTICATED by "+msg.SourceDocumentBox.toString()+" TimeStamp: "+ GlobalTools.GetLifetimeCounter());
								
								
								
								
							} else
							{
								// one connection says your are not authenticated .. 
								this._parent.GetAuthenticationStateObject().SetCurrentState(State.UnAuthenticated);
								GlobalLogger.LogStatesDocumentBox("DocumentBox,AuthenticationRunner:" + _parent.GetDocumentBoxUUID().toString()+" connection " +Connection.GetDocumentBoxId() +" is NOT authenticated by "+msg.SourceDocumentBox.toString());
							}
						}
					}
				}
			}
		}	
		
		if(_stateAuthRunner != AuthenticationRunnerStateMachine.Idle)
		{
			if(GlobalTools.GetLifetimeCounter() - _millsAuthRunnerSwitchedToAuthReqPending > GlobalSimulationParameters.TimeoutForDocumentBoxMessageBeginAuthState)
			{
				nextState = AuthenticationRunnerStateMachine.Idle;
				
				GlobalLogger.LogStatesDocumentBox("DocumentBox,AuthenticationRunner:" + _parent.GetDocumentBoxUUID().toString() + " Switch to  " +nextState + "  after Timeout ");		
											
			}
		}
		
					
		if(_stateAuthRunner != nextState)
		{
			GlobalLogger.LogStatesDocumentBox("DocumentBox,AuthenticationRunner:" + _parent.GetDocumentBoxUUID().toString() + " Switch to  " +nextState);				
			_stateAuthRunner = nextState;
		}
		
	}
	
	UUID lastAuthenticationRequestUUID; //shitty workaround for received requests
	
	private void AuthenticationHandler(ReceivedMessage msg)
	{
		AuthenticationHandlerStateMachine nextState = _stateAuthHandler;
		
		if( msg != null)
		{
			if(_stateAuthHandler == AuthenticationHandlerStateMachine.Idle)
			{
				if(msg.SourceDocumentBox == Connection.GetDocumentBoxId())
				{
				
					if( msg.Message.GetMessageType()  == IDocumentBoxMessage.DocumentBoxMessageType.AuthenticateConnectionRequest)
					{
						GlobalLogger.LogStatesDocumentBox("DocumentBox,AuthenticationHandler:" + _parent.GetDocumentBoxUUID().toString() + " AuthenticateConnectionRequest received from " +msg.SourceDocumentBox);
						
						nextState = AuthenticationHandlerStateMachine.PingRequestPending;
						
						lastAuthenticationRequestUUID = msg.Message.GetMessageGuid();
						
						OutgoingMessageDocumentBox msgOut = new OutgoingMessageDocumentBox();
						
						msgOut.TargetDocumentBox = Connection.GetDocumentBoxId();
						msgOut.TargetPeerBox = Connection.GetLastKnownPeerBoxAdress();
						msgOut.Message =  new DocumentBoxMessagePingRequest();
						_msgSendByType.put(msgOut.Message.GetMessageType(), msgOut.Message);
						this._parent.GetOutgoingList().add(msgOut);	
						
						_millsAuthHandlerSwitchedToAuthReqPending = GlobalTools.GetLifetimeCounter();
					}
				}
			}
			
			
			if(_stateAuthHandler == AuthenticationHandlerStateMachine.PingRequestPending)
			{
				if(msg.SourceDocumentBox == Connection.GetDocumentBoxId())
				{																	
					if( msg.Message.GetMessageType()  == IDocumentBoxMessage.DocumentBoxMessageType.PingResponse)
					{
						
						IDocumentBoxMessage sentMessageByType = _msgSendByType.get(	IDocumentBoxMessage.DocumentBoxMessageType.PingRequest);
						
						if(sentMessageByType.GetMessageGuid().equals(msg.Message.GetMessageGuid())) // do this only for responses
						{	
																						
							nextState = AuthenticationHandlerStateMachine.Idle;
							
							OutgoingMessageDocumentBox msgOut = new OutgoingMessageDocumentBox();
							
							msgOut.TargetDocumentBox = Connection.GetDocumentBoxId();
							msgOut.TargetPeerBox = msg.SourcePeerBox;
							
							if(msg.SourcePeerBox == Connection.GetLastKnownPeerBoxAdress())
							{
								msgOut.Message =  new DocumentBoxMessageAuthenticateConnectionResponse(ResponseTypeDocumentBox.OK,lastAuthenticationRequestUUID);	
							} else
							{
								//something went wrong, the peer box address was not updated, the response is sent to requester ..
								msgOut.Message =  new DocumentBoxMessageAuthenticateConnectionResponse(ResponseTypeDocumentBox.NOK,lastAuthenticationRequestUUID);
							}
												
							GlobalLogger.LogStatesDocumentBox("DocumentBox,AuthenticationHandler:" + _parent.GetDocumentBoxUUID().toString() + " PingResponse received from " +msg.SourceDocumentBox +" Response was " + ((DocumentBoxMessageAuthenticateConnectionResponse)msgOut.Message).GetResponseType());
							
							_msgSendByType.put(msgOut.Message.GetMessageType(), msgOut.Message);
							this._parent.GetOutgoingList().add(msgOut);
						}
					}
				}
			}
											
		}
		

		if(_stateAuthHandler != AuthenticationHandlerStateMachine.Idle)
		{
			if(GlobalTools.GetLifetimeCounter() - _millsAuthHandlerSwitchedToAuthReqPending > GlobalSimulationParameters.TimeoutForDocumentBoxMessageBeginAuthState)
			{					
				nextState = AuthenticationHandlerStateMachine.Idle;					
				GlobalLogger.LogStatesDocumentBox("DocumentBox,AuthenticationHandler:" + _parent.GetDocumentBoxUUID().toString() + " Switch to  " +nextState + "  after Timeout ");										
			}
		}
		
		if(_stateAuthHandler != nextState)
		{
			GlobalLogger.LogStatesDocumentBox("DocumentBox,AuthenticationHandler:" + _parent.GetDocumentBoxUUID().toString() + " Switch to  " +nextState);				
			_stateAuthHandler = nextState;
		}
	}
	
}