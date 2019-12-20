package documentTreeModel.implementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import commonHelper.ForwardPointerMap;
import commonHelper.GenericRandomUtilities;
import commonHelper.GlobalLogger;
import commonHelper.GlobalTools;
import commonHelper.LRDMap;
import commonHelper.LRUMap;
import commonHelper.RandomSelecter;
import commonHelper.interfaces.IRandomSelecterInputObject;
import commonHelper.math.RandomUtilities;
import commonHelper.math.interfaces.IVector;
import documentTreeModel.interfaces.IDocumentBox;
import documentTreeModel.interfaces.IDocumentBox.IAuthenticationState.State;
import documentTreeModel.interfaces.IDocumentBoxEvaluation;
import documentTreeModel.interfaces.IDocumentBoxMessage;
import documentTreeModel.interfaces.IDocumentBoxMigrationDirectionResult;
import documentTreeModel.interfaces.IDocumentBoxMessage.DocumentBoxMessageType;
import documentTreeModel.interfaces.IForwardPointerEntry;
import documentTreeModel.interfaces.IPeerBox;
import documentTreeModel.interfaces.IPeerBoxBroadCastRequest;
import documentTreeModel.interfaces.IPeerBoxEvaluation;
import documentTreeModel.interfaces.IPeerBoxLoadStateBroadCastMessage;
import documentTreeModel.interfaces.IPeerBoxMessage;
import documentTreeModel.interfaces.IPeerBoxRequestChangeNumberOfHoldDocumentBoxesMessage;
import documentTreeModel.interfaces.IPeerBoxRequestForceAuthenticationFromDocumentBoxesMessage;
import documentTreeModel.interfaces.IPeerBoxRequestMigrateDocumentBoxMessage;
import documentTreeModel.interfaces.IPeerBoxRequestSendDocumentBoxMessage;
import documentTreeModel.interfaces.IPeerBoxResponse;
import documentTreeModel.interfaces.IPeerBoxResponse.ResponseType;
import peersModel.interfaces.IPeer;

/**
 * @author Dimitri
 *
 */
public class PeerBox implements IPeerBox, IPeerBoxEvaluation, Runnable {

	final long _authenticationRepeatRatio = GlobalSimulationParameters.AuthenticationRepeatRatio;// ratio authenticationBeginDuration = authenticationRepeatRatio*meanAuthenticatioDuration

	final long _garbageCollectionRatio = GlobalSimulationParameters.GarbageCollectionRatio; // ratio garbageCollectionBeginDuration = garbageCollectionRatio*meanAuthenticatioDuration

	IPeer _parentPeer;
	
	LoadBalancedMigrationSelecter _migrationSelecter ;

	ArrayList<DocumentBoxMessageState> _documentBoxMessageStates = new ArrayList<DocumentBoxMessageState>();

	ArrayList<IDocumentBox> _lstContainedDocumentBox = new ArrayList<IDocumentBox>();

	ArrayList<MigrationState> _lstCurrentlyMigratingDocumentBoxes = new ArrayList<MigrationState>();

	ForwardPointerMap<UUID, ForwardPointerEntry> _forwardPointerCacheMap = null;

	private BlockingQueue<ReceivedMessage> _lstIncommingMessages = new LinkedBlockingQueue<ReceivedMessage>();

	// this holds the mean value for authentication duration, over the last N
	// successful authentications, used for
	// garbage collection..
	MeanValueCalculation _authenticationMeanDurationMsValue = new MeanValueCalculation(GlobalSimulationParameters.MeanInitialAuthenticationDuration);

	private long _capacityForDocumentBoxes = 0;
	
	
	LoadBalancingForceFlHandler _loadBalancingForceFl; 
	
	public long GetMeanAuthenticationDuration()
	{
		return _authenticationMeanDurationMsValue.GetCurrentValue();
	}

	@Override
	public void CleanUpMe() {
			
		_lstContainedDocumentBox.clear();
		_lstCurrentlyMigratingDocumentBoxes.clear();
		_documentBoxMessageStates.clear();
		_lstIncommingMessages.clear();
		
		if(_forwardPointerCacheMap != null) _forwardPointerCacheMap.clear();
	}
	
	public PeerBox() {
		
	}

	public PeerBox(IPeer parentPeer) {
		_parentPeer = parentPeer;
		
		_loadBalancingForceFl = new LoadBalancingForceFlHandler(parentPeer.GetNetworkAdress().GetPoint(),parentPeer.GetPeerID());
		_migrationSelecter = new LoadBalancedMigrationSelecter(parentPeer);
		
	
	}

	/* (non-Javadoc)
	 * @see documentTreeModel.interfaces.IPeerBox#GetNumberOfAllContainedDocumentBoxes()
	 */
	public long GetNumberOfAllContainedDocumentBoxes() {
		return _lstContainedDocumentBox.size();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	
	/*
	 * Returns the whole llist of all assigned DocumentBoxes known by the PeerBox, 
	 * but also DocumentBoxes not evaluated in the input message queue
	 * (non-Javadoc)
	 * @see documentTreeModel.interfaces.IPeerBoxEvaluation#GetListOfAssignedDocumentBoxes()
	 */
	public Collection<IDocumentBox> GetListOfAssignedDocumentBoxes() {
		// this function will always be called in case of deactivated message handling

		Collection<IDocumentBox>  result = GetListOfDocumentBoxesInInputQueue();
		
		result.addAll(GetListOfDocumentBoxesInMigration());
		
		result.addAll(Collections.unmodifiableList(_lstContainedDocumentBox));
		
		return result;
	}
	
	public Collection<IDocumentBox> GetListOfDocumentBoxesInMigration()
	{
		ArrayList<IDocumentBox> resultingList  = new  ArrayList<IDocumentBox>();
		for(MigrationState state :_lstCurrentlyMigratingDocumentBoxes)
		{						
			IDocumentBoxEvaluation clone = (IDocumentBoxEvaluation)state.MigratedDocumentBox.CloneMe().CloneMe();
			
			clone.SetParentPeerBox(this);
			
			resultingList.add((IDocumentBox)clone);
		}
		
		return resultingList;
	}
	
	private Collection<IDocumentBox> GetListOfDocumentBoxesInInputQueue()
	{
		// this function will always be called in case of deactivated message handling
		ArrayList<IDocumentBox> containedInIncominng  = new  ArrayList<IDocumentBox>();
		for(ReceivedMessage msg :_lstIncommingMessages)
		{
			if(msg instanceof ReceivedMessageFromPeer)
			{
				ReceivedMessageFromPeer msgIn = (ReceivedMessageFromPeer)  msg;
				
				if (msgIn.Message instanceof PeerBoxRequestMigrateDocumentBoxMessage)
				{
					PeerBoxRequestMigrateDocumentBoxMessage migratingMsg = (PeerBoxRequestMigrateDocumentBoxMessage)msgIn.Message;
					
					IDocumentBox db = (IDocumentBox)migratingMsg.GetPrivateData();
					
					IDocumentBoxEvaluation clone = (IDocumentBoxEvaluation)db.CloneMe();
					
					clone.SetParentPeerBox(this);
					
					containedInIncominng.add((IDocumentBox)clone);
				}
			}
		}
		
		return containedInIncominng;
	}
	
	
	
	public long GetForwardingPointerLength()
	{
		return _forwardPointerCacheMap.size();
	}
	
	public IForwardPointerEntry GetForwardingPointerForDocumentBox(UUID idDocumentBox, boolean bUpdateCache) {
		
		//GlobalLogger.WriteOnce("PeerBox:" + this.GetPeerBoxAddress()+" Looking up for in Cache: " + idDocumentBox);
		
		return _forwardPointerCacheMap.get(idDocumentBox, bUpdateCache);
	}


	public long GetDocumentBoxCapacity()
	{
		return _capacityForDocumentBoxes;
	}
	
	
	@Override
	public void OnFinalizedInitialization() {
		
		InitializeForwardPointerCache();			
		_migrationSelecter.SetLocalAddress(new double[]{ this._parentPeer.GetNetworkAdress().GetPositionX(),this._parentPeer.GetNetworkAdress().GetPositionY()});		
	}

	
	private void InitializeForwardPointerCache() {
		
		//let the cache size correlate with node centrality
		int cacheSize = GlobalSimulationParameters.ForwardPointerCacheLength*_parentPeer.GetAllNeighbours().size();
		
		if (GlobalSimulationParameters.bUseLRDCache) {
			_forwardPointerCacheMap = new LRDMap<UUID, ForwardPointerEntry>(cacheSize);

		} else if (GlobalSimulationParameters.bUseLRUCache) {
			_forwardPointerCacheMap = new LRUMap<UUID, ForwardPointerEntry>(cacheSize);
		}
	}

	private MigrationState BeginMigrationForDocumentBoxByForces() 
	{
		
		IDocumentBoxMigrationDirectionResult result =  _migrationSelecter.DetermineDocumentBoxForMigration((List<IDocumentBox>) _lstContainedDocumentBox.clone(),
																											_loadBalancingForceFl.GetForceVector());			
		if(result == null) return null;										
		result.GetDocumentBox().BeginMigration();

		MigrationState state = new MigrationState();
		state.MigratedDocumentBox = result.GetDocumentBox();
		state.RequestUUID = UUID.randomUUID();
		state.TimeStampSendRequest = GlobalTools.GetLifetimeCounter();
		state.TargetPeer = result.TargetPeerId(); 
				
		
		_lstContainedDocumentBox.remove(result.GetDocumentBox());
		
		return state;
	}
	
	private MigrationState BeginMigrationForDocumentBox() {
		// select a random active document box
		int size = _lstContainedDocumentBox.size();

		if (size <= _capacityForDocumentBoxes)
			return null; // keep one in the list

		
		
		// determine the DocumentBox to be migrated ...
		ArrayList<IRandomSelecterInputObject> lstRandom = new ArrayList<IRandomSelecterInputObject>();
		long maximalValue = 0;

		for (IDocumentBox bx : _lstContainedDocumentBox) {
			long issuedValue = bx.GetAuthenticationState().GetTimeStampAuthentication();
			if (issuedValue > maximalValue) {
				maximalValue = issuedValue;
			}
		}

		for (final IDocumentBox bx : _lstContainedDocumentBox) {
			final long issuedValue = maximalValue - bx.GetAuthenticationState().GetTimeStampAuthentication();

			IRandomSelecterInputObject obj = new IRandomSelecterInputObject() {
				final IDocumentBox objReferenced = bx;

				@Override
				public Object GetReferencedObject() {
					return objReferenced;
				}

				@Override
				public long GetIssuedValue() {
					return issuedValue;
				}
			};
			lstRandom.add(obj);
		}

		if (lstRandom.isEmpty())
		{			
			return null;
		}
		RandomSelecter selected = new RandomSelecter();

		IDocumentBox result = (IDocumentBox) selected.CalculateRandomFromObjects(lstRandom);

		if (result == null) {
			// all elements had the probabiity 0, select one by random
			GenericRandomUtilities<IRandomSelecterInputObject> rand = new GenericRandomUtilities<IRandomSelecterInputObject>();
			result = (IDocumentBox) rand.SelectOneByRandom(lstRandom).GetReferencedObject();
		}

		result.BeginMigration();

		IPeer selectedTargetPeer = RandomUtilities.SelectOneByRandomFromList(_parentPeer.GetAllNeighbours());
		
		MigrationState state = new MigrationState();
		state.MigratedDocumentBox = result;
		state.RequestUUID = UUID.randomUUID();
		state.TimeStampSendRequest = GlobalTools.GetLifetimeCounter();
		state.TargetPeer = selectedTargetPeer.GetPeerID();
		
		_lstContainedDocumentBox.remove(result);

		return state;
	}

	private IDocumentBox RemoveDocumentBoxByGarbageCollection() {
		// determine the DocumentBox to be migrated ...
		ArrayList<IRandomSelecterInputObject> lstRandom = new ArrayList<IRandomSelecterInputObject>();

		long currentMeanValueAuthentication = _authenticationMeanDurationMsValue.GetCurrentValue();

		long currentMills = GlobalTools.GetLifetimeCounter();

		for (final IDocumentBox bx : _lstContainedDocumentBox) {

			if (bx.GetAuthenticationState().GetCurrentState() == State.Authenticated)
				continue;

			long lastAuthenticationTime = bx.GetAuthenticationState().GetTimeStampAuthentication();

			// decide if the garbage collection duration was reached

			long durationOverDueTimeSet = (lastAuthenticationTime
					+ _garbageCollectionRatio * currentMeanValueAuthentication);
			
			final long currentOverDue = currentMills -durationOverDueTimeSet; 

			if (currentOverDue > 0) {
				IRandomSelecterInputObject obj = new IRandomSelecterInputObject() {
					IDocumentBox objReferenced = bx;

					@Override
					public Object GetReferencedObject() {
						return objReferenced;
					}

					@Override
					public long GetIssuedValue() {
						return currentOverDue;
					}
				};
				lstRandom.add(obj);
			}
		}

		if (lstRandom.isEmpty())
			return null;

		RandomSelecter selected = new RandomSelecter();
		IDocumentBox result = (IDocumentBox) selected.CalculateRandomFromObjects(lstRandom);

		// return the removed DocumentBox, we ignore at this point, that the
		// DocumentBox may be currently migrating...

		_lstContainedDocumentBox.remove(result);

		return result;
	}

	/**
	 * Helper function
	 * 
	 * @param id
	 * @return
	 */
	private IDocumentBox GetDocumentBoxByUUID(UUID id) {
		IDocumentBox result = null;
		for (IDocumentBox docBox : _lstContainedDocumentBox) {
			if (docBox.GetDocumentBoxUUID() == id) {
				result = docBox;
			}
		}
		return result;
	}


	// Check first if the entry already available then remove it
	private void AddForwardingPointer(ForwardPointerEntry entry) {

		if (_forwardPointerCacheMap.containsKey(entry.IdOfDocumentBox)) {
			_forwardPointerCacheMap.remove(entry.IdOfDocumentBox);
		}
		
		//GlobalLogger.WriteOnce("PeerBox:" + this.GetPeerBoxAddress()+" entering entry into cache: " + entry.GetIdOfDocumentBox() );

		_forwardPointerCacheMap.put(entry.IdOfDocumentBox, entry);
	}

	
	
	

	@Override
	public long GetPeerBoxAddress() {
		
		assert(_parentPeer != null);
		
		return _parentPeer.GetPeerID();
	}

	@Override
	public void PlaceMessageFromOtherPeerBox(IPeerBoxMessage message, long sourcePeerBoxAdress) {

		ReceivedMessageFromPeer incoming = new ReceivedMessageFromPeer();

		incoming.Message = message;
		incoming.SourcePeerBoxAdress = sourcePeerBoxAdress;

		try {
			_lstIncommingMessages.put(incoming);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			GlobalLogger.LogPeerBoxState("PeerBox:" + this.GetPeerBoxAddress() + " PlaceMessageFromOtherPeerBox failed ");
		}
	}

	@Override
	public void SendMessageToDocumentBox(UUID sourceDocumentBox, UUID targetDocumentBox, long targetPeerBoxAddress,
			IDocumentBoxMessage message) {
		ReceivedMessageFromDocumentBox incoming = new ReceivedMessageFromDocumentBox();
		incoming.Message = message;
		incoming.targetRemoteDocumentBox = targetDocumentBox;
		incoming.targetPeerBoxAddress = targetPeerBoxAddress;
		incoming.sourceLocalDocumentBox = sourceDocumentBox;

		GlobalLogger.LogPeerBoxState("PeerBox (SendMessageToDocumentBox):" + this.GetPeerBoxAddress() + " Received message from " + sourceDocumentBox + "  to "+ targetDocumentBox + " on PeerBox " + targetPeerBoxAddress);
		
		_lstIncommingMessages.add(incoming);
	}

	public class MeanValueCalculation {
		long _numberOfEvents;
		long _sumOfEvents;

		public MeanValueCalculation(long initialValue) {
			_numberOfEvents = 1;
			_sumOfEvents = initialValue;
		}

		public void AddNewEventValue(long value) {
			_sumOfEvents = _sumOfEvents + value;
			_numberOfEvents++;

			if (_numberOfEvents > 1000) {
				// we have collected 1000 events, so we cut the history and
				// begin again
				_sumOfEvents = GetCurrentValue();
				_numberOfEvents = 1;
			}
		}

		public long GetCurrentValue() {
			return _sumOfEvents / _numberOfEvents;
		}
	}

	private void ReInitAuthetnicationForLocalDocumentBoxes() {
		long currentMs = GlobalTools.GetLifetimeCounter();

		for (IDocumentBox bx : _lstContainedDocumentBox) {
			if (bx.GetAuthenticationState().GetCurrentState() == State.Authenticated)
				continue;

			long lastAuthenticationTime = bx.GetAuthenticationState().GetTimeStampAuthentication();

			long overdueTime = lastAuthenticationTime
					+ this._authenticationMeanDurationMsValue.GetCurrentValue() * _authenticationRepeatRatio;

			if (overdueTime > currentMs) {
				//last request 
				long overdueTimeBeginAuthentication = bx.GetAuthenticationState().GetAuthenticationRequestedTimeStamp()
						+ this._authenticationMeanDurationMsValue.GetCurrentValue();
				if (currentMs > overdueTimeBeginAuthentication) {
					// we repeate the request again
					bx.Authenticate();
				}
			}
		}
	}

	/**
	 * We received a new value for the duration, recalculate the authentication
	 * duration
	 * 
	 * @param authDuration
	 */
	private void HandleNewReceivedAuthenticationDuration(long authDuration) {
		_authenticationMeanDurationMsValue.AddNewEventValue(authDuration);
	}
	
	long _previousSentFreeLoadCapacityValue = 0;
	long _lastCapacityChangeMessageTime = RandomUtilities.SelectRandomInteger(5000);
	private OutgoingBroadcastMessageToPeer HandleCapacityStateChange()
	{

		if( GlobalTools.GetLifetimeCounter() < _lastCapacityChangeMessageTime + GlobalSimulationParameters.BroadCastGuardTime) return null;
						
		long freeNewCapacity = _capacityForDocumentBoxes - GetNumberOfAllContainedDocumentBoxes();
		
		if(freeNewCapacity == _previousSentFreeLoadCapacityValue) return null;
		
		_lastCapacityChangeMessageTime = GlobalTools.GetLifetimeCounter();
		
				
		// send the new capacity as broad cast
		OutgoingBroadcastMessageToPeer msgOutPeer = new OutgoingBroadcastMessageToPeer();
		
		if(_previousSentFreeLoadCapacityValue > freeNewCapacity)
		{
			msgOutPeer.InitialDTL = (long) LoadBalancingForceFlHandler.DistanceFromLoadState(_previousSentFreeLoadCapacityValue);	
		} else 
		{
			msgOutPeer.InitialDTL = (long) LoadBalancingForceFlHandler.DistanceFromLoadState(freeNewCapacity);
		}
		
		GlobalLogger.LogCapacityChange("PeerBox:" + this.GetPeerBoxAddress()+ " has send a broadcast with new Capacity: " + freeNewCapacity + ",old Capacity "+_previousSentFreeLoadCapacityValue +  " BroadCast Distance : " + msgOutPeer.InitialDTL);				
		
		msgOutPeer.outgoingMessage = new PeerBoxLoadStateBroadCastMessage(UUID.randomUUID(),
																			freeNewCapacity,
																			_previousSentFreeLoadCapacityValue,
																			_parentPeer.GetNetworkAdress().GetPoint().GetComponents());					
		_previousSentFreeLoadCapacityValue = freeNewCapacity;
		
		return msgOutPeer;
	}

	public void run() {
		ReceivedMessage msg = null;;
		
		GlobalLogger.LogNumberOfInputMessagesState("PeerBox:" + this.GetPeerBoxAddress() + " number of messages: "+ _lstIncommingMessages.size());
		
		do{
	
		msg = _lstIncommingMessages.poll();
		GlobalLogger.LogNumberOfInputMessagesState("PeerBox:" + this.GetPeerBoxAddress() + " number of messages: "+ _lstIncommingMessages.size());
	
		
		// first we delete all old DocumentBoxes
		IDocumentBox deletedDocumentBox = RemoveDocumentBoxByGarbageCollection();

		if (deletedDocumentBox != null) {
			deletedDocumentBox.BeginMigration(); // migration into nirvana ..
													// hehe..

			// DocumentBox was removed
			GlobalLogger.LogPeerBoxDeletingOfDocumentBox("PeerBox: DocumentBox " + deletedDocumentBox.GetDocumentBoxUUID()
					+ " has not authenticated itself and was removed from PeerBox " + this.GetPeerBoxAddress()
					+"  After migrations "+ GlobalTools.GlobalMigrationCounter.incrementAndGet() 
					+ " Current Authenication mean value: " + this._authenticationMeanDurationMsValue.GetCurrentValue()
					+ " TimeStamp: " + +GlobalTools.GetLifetimeCounter());
		}

		ReInitAuthetnicationForLocalDocumentBoxes();

		ArrayList<OutgoigMessage> msgListOutGoingMessages = new ArrayList<OutgoigMessage>();

		// check if message from documentbox

		if(msg != null)
		{
			GlobalLogger.LogPeerBoxState("PeerBox(1):" + this.GetPeerBoxAddress() + " has received a message  "
					+ msg.getClass().getName());
		}
		
		//handle the load state messages, send new broadcast if there where changes for CapPb
		
		OutgoingBroadcastMessageToPeer msgOutLoadState = HandleCapacityStateChange();
		if(msgOutLoadState != null)
		{
			msgListOutGoingMessages.add(msgOutLoadState);	
		}
		
				
		if (msg instanceof ReceivedMessageFromDocumentBox) {
			// DocumentBox verschickt eine Nachricht, die Bestätigung wird
			// erwartet, OK, NOK, Migrated
			ReceivedMessageFromDocumentBox msgIn = (ReceivedMessageFromDocumentBox) msg;

			if (msgIn.Message.GetMessageType() == DocumentBoxMessageType.AuthenticationDone) {

				// message to parent peer, that authentication was performed,
				// store the received duration

				HandleNewReceivedAuthenticationDuration(
						((DocumentBoxMessageAuthenticationDone) msgIn.Message).GetSystemTimeOfAuthenticationDuration());

			} else {

				DocumentBoxMessageState state = new DocumentBoxMessageState();
				state.ReceivedMessage = msgIn;
				state.RequestUUID = UUID.randomUUID();
				state.TimeStampSendRequest = GlobalTools.GetLifetimeCounter();

				_documentBoxMessageStates.add(state);

				OutgoingMessageToPeer msgOutPeer = new OutgoingMessageToPeer();
				msgOutPeer.outgoingMessage = new PeerBoxRequestSendDocumentBoxMessage(state.RequestUUID,
						state.ReceivedMessage.Message, state.ReceivedMessage.sourceLocalDocumentBox,
						state.ReceivedMessage.targetRemoteDocumentBox);
				msgOutPeer.targetPeerBox = msgIn.targetPeerBoxAddress;

				msgListOutGoingMessages.add(msgOutPeer);

				GlobalLogger.LogPeerBoxState("PeerBox:" + this.GetPeerBoxAddress()
						+ " Has send a Documentbox message to target PeerBox-Id " + msgOutPeer.targetPeerBox);

			}

		}

		ArrayList<DocumentBoxMessageState> deperecatedMessages = new ArrayList<DocumentBoxMessageState>();

		// State machine for sending of document box messages
		// Check pending document-box messages and create according responses to
		// local document box if needed
		for (DocumentBoxMessageState state : _documentBoxMessageStates) {
			if ((GlobalTools.GetLifetimeCounter()
					- state.TimeStampSendRequest) > GlobalSimulationParameters.TimeoutForDocumentBoxMessageResponse) {
				// just remove the message, the documentBox has to resent it's message in case of error
				deperecatedMessages.add(state);
			}
		}

		for (DocumentBoxMessageState state : deperecatedMessages) {
			_documentBoxMessageStates.remove(state);
		}

		// check if message from peerbox
		if (msg instanceof ReceivedMessageFromPeer) {
			ReceivedMessageFromPeer msgPeer = (ReceivedMessageFromPeer) msg;

			// OK, hier die message an DocBox weiterleiten und eine Bestätigung
			// zurückschicken, oder Abweisung
			// OK, NOK, Migrated generieren
			GlobalLogger.LogPeerBoxState("PeerBox(2):" + this.GetPeerBoxAddress() + " has received a message  "
					+ msgPeer.Message.getClass().getName());
			if (msgPeer.Message instanceof IPeerBoxRequestSendDocumentBoxMessage) {
				IPeerBoxRequestSendDocumentBoxMessage msgIn = (IPeerBoxRequestSendDocumentBoxMessage) msgPeer.Message;

				GlobalLogger.LogPeerBoxState(
						"PeerBox(3):" + this.GetPeerBoxAddress() + " Source DocBox  " + msgIn.GetSourceDocumentBox());
				if (GetDocumentBoxByUUID(msgIn.GetTargetDocumentBox()) == null) {


					GlobalLogger.LogPeerBoxState("PeerBox(4):" + this.GetPeerBoxAddress()
							+ " Message to unknown local DocumentBox received, to" + msgIn.GetSourceDocumentBox()
							+ " to  " + msgIn.GetTargetDocumentBox() + " from PeerBox: " + msgPeer.SourcePeerBoxAdress);
					
					// not in local cache
					ForwardPointerEntry fpEntry = null;
					
					try{
						fpEntry = (ForwardPointerEntry) GetForwardingPointerForDocumentBox(msgIn.GetTargetDocumentBox(), true);
						
					}catch(Exception exp)
					{
						exp.printStackTrace();
					}

					if (fpEntry != null) {
						
						GlobalLogger.LogPeerBoxState("PeerBox(5):" + this.GetPeerBoxAddress()
						+ " Message to unknown local DocumentBox received, to" + msgIn.GetSourceDocumentBox()
						+ " to  " + msgIn.GetTargetDocumentBox() + " from PeerBox: " + msgPeer.SourcePeerBoxAdress + " Sending Response: Migrated to PeerBox: " + fpEntry.ForwardedToPeerBoxWithThisId);
						
						// migrated to new peerBox
						OutgoingMessageToPeer msgOutPeer = new OutgoingMessageToPeer();
						msgOutPeer.outgoingMessage = new PeerBoxResponseDocumentBoxMigrated(msgIn.GetRequestUUID(),
								IPeerBoxResponse.ResponseType.MigratedDocumentBox, msgPeer.Message.GetPrivateData(),
								fpEntry.ForwardedToPeerBoxWithThisId);
						msgOutPeer.targetPeerBox = ((ReceivedMessageFromPeer) msg).SourcePeerBoxAdress;
						msgListOutGoingMessages.add(msgOutPeer);
						
						

					} else {
						
						GlobalLogger.LogPeerBoxState("PeerBox(5):" + this.GetPeerBoxAddress()
						+ " Message to unknown local DocumentBox received, to" + msgIn.GetSourceDocumentBox()
						+ " to  " + msgIn.GetTargetDocumentBox() + " from PeerBox: " + msgPeer.SourcePeerBoxAdress + " Sending Response: NOK, realy lost " );
						
						// unknown DocumentBox
						OutgoingMessageToPeer msgOutPeer = new OutgoingMessageToPeer();
						msgOutPeer.outgoingMessage = new PeerBoxResponse(msgIn.GetRequestUUID(),
								IPeerBoxResponse.ResponseType.NOK, null);
						msgOutPeer.targetPeerBox = ((ReceivedMessageFromPeer) msg).SourcePeerBoxAdress;
						msgListOutGoingMessages.add(msgOutPeer);
					
					}
				} else {

					// forward the message to document box
					OutgoingMessageToDocumentBox msgOut = new OutgoingMessageToDocumentBox();
					msgOut.Message = (IDocumentBoxMessage) msgIn.GetPrivateData();
					msgOut.sourcePeerBoxAddress = ((ReceivedMessageFromPeer) msg).SourcePeerBoxAdress;
					msgOut.sourceRemoteDocumentBox = msgIn.GetSourceDocumentBox();
					msgOut.targetLocalDocumentBox = msgIn.GetTargetDocumentBox();
					msgListOutGoingMessages.add(msgOut);

					// and the according response to the calling peer

					OutgoingMessageToPeer msgOutPeer = new OutgoingMessageToPeer();
					msgOutPeer.outgoingMessage = new PeerBoxResponse(msgIn.GetRequestUUID(),
							IPeerBoxResponse.ResponseType.OK, null);
					msgOutPeer.targetPeerBox = ((ReceivedMessageFromPeer) msg).SourcePeerBoxAdress;

					msgListOutGoingMessages.add(msgOutPeer);

					GlobalLogger.LogPeerBoxState("PeerBox:" + this.GetPeerBoxAddress()
							+ " Message to local DocumentBox received to" + msgOut.targetLocalDocumentBox + " to  "
							+ msgOut.sourceRemoteDocumentBox + " from PeerBox " + msgOut.sourcePeerBoxAddress);

				}
			}

			if (msgPeer.Message instanceof IPeerBoxRequestForceAuthenticationFromDocumentBoxesMessage) {
				for (IDocumentBox docBox : _lstContainedDocumentBox) {
					docBox.Authenticate();
				}
			}

			if (msgPeer.Message instanceof IPeerBoxRequestChangeNumberOfHoldDocumentBoxesMessage) {
				
				
				GlobalLogger.LogCapacityChange("PeerBox:" + this.GetPeerBoxAddress()+ " IPeerBoxRequestChangeNumberOfHoldDocumentBoxesMessage received ");
				
				IPeerBoxRequestChangeNumberOfHoldDocumentBoxesMessage msgIn = (IPeerBoxRequestChangeNumberOfHoldDocumentBoxesMessage) msgPeer.Message;							
				
				_capacityForDocumentBoxes = msgIn.GetNewNumberOfDocumentBoxesContainedHere();							
				
			}

			if(msgPeer.Message instanceof IPeerBoxLoadStateBroadCastMessage)
			{
				//broadcast: changed load state of one of the adjacent PeerBoxes
				IPeerBoxLoadStateBroadCastMessage msgLoadState = (IPeerBoxLoadStateBroadCastMessage)msgPeer.Message;
												
				_loadBalancingForceFl.UpdateValue(msgLoadState.SourcePosition(), 												  
												  msgLoadState.GetSourceNewLoadForceAmount(), 
												  msgLoadState.GetSourcePreviousLoadForceAmount());
				
			}
			
			if (msgPeer.Message instanceof IPeerBoxRequestMigrateDocumentBoxMessage) {
				// has received the document box
				IPeerBoxRequestMigrateDocumentBoxMessage msgIn = (IPeerBoxRequestMigrateDocumentBoxMessage) msgPeer.Message;

				OutgoingMessageToPeer msgOutPeer = new OutgoingMessageToPeer();
				msgOutPeer.outgoingMessage = new PeerBoxResponse(msgIn.GetRequestUUID(),
						IPeerBoxResponse.ResponseType.OK, null);
				msgOutPeer.targetPeerBox = ((ReceivedMessageFromPeer) msg).SourcePeerBoxAdress;

				msgListOutGoingMessages.add(msgOutPeer);

				// document box now in the current peer box, activate it ??
				IDocumentBox documentBox = (IDocumentBox) msgIn.GetPrivateData();
				documentBox.MigrationDone(this);
				_lstContainedDocumentBox.add(documentBox);
								
				GlobalLogger.LogFollowDocumentBoxMigration("PeerBox:" + this.GetPeerBoxAddress()
						+ " new DocumentBox received, UUID is  " + msgIn.GetUUIDDocumentBox() + " from:" +msgPeer.SourcePeerBoxAdress);

			}

			if (msgPeer.Message instanceof PeerBoxResponseDocumentBoxMigrated) {
				// The received message from remote peer says the
				// target-document box was migrated to other peer,
				// this is not handeled by the peer box, the documentbox has to
				// create new request ...
				// response: document box moved to new peerbox
				PeerBoxResponseDocumentBoxMigrated msgIn = (PeerBoxResponseDocumentBoxMigrated) msgPeer.Message;

				DocumentBoxMessageState resultingMessageState = null;

				for (DocumentBoxMessageState oldState : _documentBoxMessageStates) {
					if (oldState.RequestUUID == msgIn.GetRequestUUID()) {
						resultingMessageState = oldState;
					}
				}
				
				if (resultingMessageState != null) {
					// it was the migration response, just remove the document
					// box
					_documentBoxMessageStates.remove(resultingMessageState);
					OutgoingMessageToDocumentBox msgOut = new OutgoingMessageToDocumentBox();
					msgOut.Message = new DocumentBoxMessageDocumentBoxMigratedResponse(msgIn.MigrationTarget(),
																					   msgIn.GetPrivateData(), msgPeer.SourcePeerBoxAdress);
					msgOut.sourcePeerBoxAddress = msgPeer.SourcePeerBoxAdress;
						
						
					msgOut.sourceRemoteDocumentBox = resultingMessageState.ReceivedMessage.targetRemoteDocumentBox;
					msgOut.targetLocalDocumentBox = resultingMessageState.ReceivedMessage.sourceLocalDocumentBox;
					msgListOutGoingMessages.add(msgOut);
				} else 
				{
					//TimeoutForDocumentBoxMessageResponse was reached and the request was removed 
				}

			} else if (msgPeer.Message instanceof IPeerBoxResponse) {
				// here all responses are dealed: MigrationResult or
				// DocumentBoxMessage-Result

				// there is a response for one request, determine the according
				// request
				IPeerBoxResponse response = (IPeerBoxResponse) msgPeer.Message;

				if (response.GetResponseType() == ResponseType.OK) {
					// we have received a successfull response , remove the
					// local document box
					MigrationState resultingState = null;
					for (MigrationState oldState : _lstCurrentlyMigratingDocumentBoxes) {
						if (oldState.RequestUUID == response.GetRequestUUID()) {
							resultingState = oldState;
						}
					}
					if (resultingState != null) {

						// create forwarding pointer
						ForwardPointerEntry entry = new ForwardPointerEntry();
						entry.IdOfDocumentBox = resultingState.MigratedDocumentBox.GetDocumentBoxUUID();
						entry.ForwardedToPeerBoxWithThisId = msgPeer.SourcePeerBoxAdress;

						this.AddForwardingPointer(entry);

						// .. and remove the document box from local storage
						_lstCurrentlyMigratingDocumentBoxes.remove(resultingState);

					}

					DocumentBoxMessageState resultingMessageState = null;
					for (DocumentBoxMessageState oldState : _documentBoxMessageStates) {
						if (oldState.RequestUUID == response.GetRequestUUID()) {
							resultingMessageState = oldState;
						}
					}
					if (resultingState != null) {
						// it was the migration response, just remove the
						// document box
						_documentBoxMessageStates.remove(resultingMessageState);
					}
				}
			}
		}

		// implement state machine for document box migration

		// 1) Select a documentBox for migration
		// 2) deactivate it
		// 3) Send according message (select a random PeerBox für Migration, )
		// 4) If positive aknowledge: delete the documentbox else send to other
		// selected PeerBox
		// timeout for Response
		MigrationState state = null;
		do{
			//state = BeginMigrationForDocumentBox();
			state = BeginMigrationForDocumentBoxByForces();

			if (state != null) {
				// create output message for a new selected document box
				// and store the message in StateList
				_lstCurrentlyMigratingDocumentBoxes.add(state);
				PeerBoxRequestMigrateDocumentBoxMessage outMessage = new PeerBoxRequestMigrateDocumentBoxMessage(
						state.RequestUUID, state.MigratedDocumentBox.CloneMe(),
						state.MigratedDocumentBox.GetDocumentBoxUUID());
				OutgoingMessageToPeer msgOutPeer = new OutgoingMessageToPeer();
				msgOutPeer.outgoingMessage = outMessage;
				
				msgOutPeer.targetPeerBox = state.TargetPeer;
				msgListOutGoingMessages.add(msgOutPeer);
			}
		}while(state != null);
		
		// check if there are documentboxes in migration state which have to be
		// resent
		for (MigrationState oldState : _lstCurrentlyMigratingDocumentBoxes) {
			if ((GlobalTools.GetLifetimeCounter()
					- oldState.TimeStampSendRequest) > GlobalSimulationParameters.TimeoutForDocumentBoxResentDuringMigration) {
				GlobalLogger.LogPeerBoxState("PeerBox:" + this.GetPeerBoxAddress() + " will resend the documentbox "
						+ oldState.MigratedDocumentBox.GetDocumentBoxUUID());

				// prepare new resend - message
				oldState.RequestUUID = UUID.randomUUID();
				oldState.TimeStampSendRequest = GlobalTools.GetLifetimeCounter();

				// the problem: it is possible that a documentbox gets doubled
				// during migration
				PeerBoxRequestMigrateDocumentBoxMessage outMessage = new PeerBoxRequestMigrateDocumentBoxMessage(
						oldState.RequestUUID, oldState.MigratedDocumentBox.CloneMe(),
						oldState.MigratedDocumentBox.GetDocumentBoxUUID());
				OutgoingMessageToPeer msgOutPeer = new OutgoingMessageToPeer();
				msgOutPeer.outgoingMessage = outMessage;
				
				//IPeer selected = RandomUtilities.SelectOneByRandomFromList(_parentPeer.GetAllNeighbours());
				
				msgOutPeer.targetPeerBox = oldState.TargetPeer;
				msgListOutGoingMessages.add(msgOutPeer);
			}
		}

		// implement receiving of a document box
		for (OutgoigMessage msgOut : msgListOutGoingMessages) 
		{
			if (msgOut.GetAddress() == MessageAddressType.FromOrToDocumentBox) {
				// to documentbox
				OutgoingMessageToDocumentBox outMsgDocBox = (OutgoingMessageToDocumentBox) msgOut;
				boolean bMessageDelivered = false;
				for (IDocumentBox docBox : _lstContainedDocumentBox) {
					if (docBox.GetDocumentBoxUUID() == outMsgDocBox.targetLocalDocumentBox) {
						docBox.PlaceMessagefromOtherDocumentBox(outMsgDocBox.Message,
								outMsgDocBox.sourceRemoteDocumentBox, outMsgDocBox.sourcePeerBoxAddress);
						bMessageDelivered = true;
					}
				}

				if (!bMessageDelivered) {
					// Message-Target was not found ...
				}
			}

			
			if (msgOut.GetAddress() == MessageAddressType.FromOrToPeer) {
				// to Peer
				
				if(msgOut instanceof OutgoingBroadcastMessageToPeer)
				{					
					OutgoingBroadcastMessageToPeer outMsgPeer = (OutgoingBroadcastMessageToPeer) msgOut;
					//may be not so nice, the creation of inter - peer message in
					// this layer, but we let it simply so ..
					if (!_parentPeer.SendBroadCastMessageService(outMsgPeer.outgoingMessage, outMsgPeer.InitialDTL)) 
					{
						GlobalLogger.LogPeerBoxState("PeerBox:" + this.GetPeerBoxAddress() + " not able to send broadcast message");
					}
					
				} else 
				{
					OutgoingMessageToPeer outMsgPeer = (OutgoingMessageToPeer) msgOut;
					//may be not so nice, the creation of inter - peer message in
					// this layer, but we let it simply so ..
					if (!_parentPeer.SendMessageService(outMsgPeer.targetPeerBox, outMsgPeer.outgoingMessage)) 
					{
						GlobalLogger.LogPeerBoxState("PeerBox:" + this.GetPeerBoxAddress() + " not able to send message to "
								+ outMsgPeer.targetPeerBox);
					}
					
				}
											
			}
		}
		}while(msg != null);

	}

	
	
	public void StartExecution() 
	{
		GlobalLogger.LogBroadCasts("BroadCast-Debug Starte PeerBox");
		
		GlobalTools.GetTaskExecutor().RegisterNewExecutable(this);	
	}

	public void StopExecution() 
	{
		GlobalLogger.LogBroadCasts("BroadCast-Debug Beende PeerBox");
		
		GlobalTools.GetTaskExecutor().UnRegisterNewExecutable(this);
	}

	public enum MessageAddressType {
		FromOrToDocumentBox, FromOrToPeer, Undefined
	};

	class ReceivedMessage {
		// either from a document - box or other peer-box
		public MessageAddressType GetAddress() {
			return MessageAddressType.Undefined;
		}

	}

	class ReceivedMessageFromDocumentBox extends ReceivedMessage {
		public UUID sourceLocalDocumentBox;
		public UUID targetRemoteDocumentBox;
		public long targetPeerBoxAddress;
		public IDocumentBoxMessage Message;

		public MessageAddressType GetAddress() {
			return MessageAddressType.FromOrToDocumentBox;
		}
	}

	class ReceivedMessageFromPeer extends ReceivedMessage {
		public IPeerBoxMessage Message;
		public long SourcePeerBoxAdress;

		public MessageAddressType GetAddress() {
			return MessageAddressType.FromOrToPeer;
		}
	}

	class ForwardPointerEntry implements IForwardPointerEntry {
		public UUID IdOfDocumentBox;
		public long ForwardedToPeerBoxWithThisId;

		@Override
		public UUID GetIdOfDocumentBox() {
			return IdOfDocumentBox;
		}

		@Override
		public long GetForwardedToPeerBoxWithThisId() {
			return ForwardedToPeerBoxWithThisId;
		}
	}

	class OutgoigMessage {
		// either from a document - box or other peer-box
		public MessageAddressType GetAddress() {
			return MessageAddressType.Undefined;
		}
	}

	class OutgoingMessageToDocumentBox extends OutgoigMessage {
		public UUID sourceRemoteDocumentBox;
		public UUID targetLocalDocumentBox;
		public long sourcePeerBoxAddress;
		public IDocumentBoxMessage Message;

		public MessageAddressType GetAddress() {
			return MessageAddressType.FromOrToDocumentBox;
		}
	}

	class OutgoingMessageToPeer extends OutgoigMessage {
		public IPeerBoxMessage outgoingMessage;
		public long targetPeerBox;

		public MessageAddressType GetAddress() {
			return MessageAddressType.FromOrToPeer;
		}
	}

	class OutgoingBroadcastMessageToPeer extends OutgoigMessage {
		public IPeerBoxBroadCastRequest outgoingMessage;
		public long InitialDTL; //Distance to live

		public MessageAddressType GetAddress() {
			return MessageAddressType.FromOrToPeer;
		}
	}

	
	class DocumentBoxMessageState {
		public long TimeStampSendRequest;
		public UUID RequestUUID;
		public ReceivedMessageFromDocumentBox ReceivedMessage;
	}

	class MigrationState {
		public long TargetPeer;
		public IDocumentBox MigratedDocumentBox;
		public long TimeStampSendRequest;
		public UUID RequestUUID;
	}

	@Override
	public IVector GetLoadForce() 
	{
		return _loadBalancingForceFl.GetForceVector(); 				
	}


}
