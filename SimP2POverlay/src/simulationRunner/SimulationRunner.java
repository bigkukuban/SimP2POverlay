package simulationRunner;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import com.jogamp.opengl.awt.GLCanvas;
import commonHelper.GenericRandomUtilities;
import commonHelper.GlobalLogger;
import commonHelper.GlobalTools;
import commonHelper.math.MathematicalFunctions;
import commonHelper.math.RandomUtilities;
import documentBoxInitializer.interfaces.IDocumentBoxNetworkInitializer;
import documentTreeModel.implementation.GlobalSimulationParameters;
import documentTreeModel.interfaces.IDocumentBox;
import documentTreeModel.interfaces.IPeerBox;
import networkInitializer.interfaces.INetworkInitializer;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;
import simulationRunner.interfaces.ISimulationRunner;
import simulationRunner.topologyEvaluator.SimulationEvaluator;
import simulationRunner.topologyEvaluator.interfaces.IMigrationStateEvaluationResult;
import simulationRunner.topologyEvaluator.interfaces.ISimulationEvaluator;
import simulationRunner.topologyEvaluator.interfaces.ITopologyEvaluationResult;
import ui.openGL.interfaces.IViewmodelNetwork;

/**
 * @author Dimitri
 *
 */
public class SimulationRunner implements ISimulationRunner 
{		
		INetworkFacade _networkFacade;
		INetworkInitializer _peerBoxNetworkInitializer;
		IDocumentBoxNetworkInitializer _docBoxNetworkInitializer;
		IViewmodelNetwork _viewModelNetwork;
		ExecutorService _executor;
		GLCanvas _canvas;
		AtomicBoolean _isRunning = new AtomicBoolean();
		
		SimulationLogWriter _simulationWriter = new SimulationLogWriter();
	
		public SimulationRunner(GLCanvas canvas)
		{
			_canvas = canvas;
		}
		
		public boolean Initialize(INetworkInitializer initializer, IDocumentBoxNetworkInitializer docBoxNetworkInitializer,  IViewmodelNetwork viewModelnetwork)
		{
			if(_isRunning.get() == true) return false;
			_viewModelNetwork = viewModelnetwork;
			_peerBoxNetworkInitializer = 	initializer;	
			_docBoxNetworkInitializer = docBoxNetworkInitializer;
			return true;
		}
		
		
		public synchronized boolean  BeginSimulation()
		{
			if(_isRunning.get() == true) return false;
			
			_executor = Executors.newFixedThreadPool(1); 
			_executor.execute(new Runnable() {
				@Override
				public void run() {		
					
					try{
					
						RunSimulation();
						
					}catch(Throwable exp)
					{
						System.out.println("Simulation was stopped by Exception ... " +exp.getMessage());						
					}
					
					System.out.println("Simulation was stopped regulary... ");	
				}
			});
			
			return true;
		}

		
		  public static void gc() {
			     Object obj = new Object();
			     WeakReference<Object> ref = new WeakReference<Object>(obj);
			     obj = null;
			     while(ref.get() != null) {
			       System.gc();
			     }
			   }
		
		private void UpdateCanvas()
		{
			  if(_canvas != null)
			  {
				  _canvas.repaint();  
			  }			  			 
		}
		  
		
		private void DoStimulatePeers(long stimulatedPeerBoxes)
		{
			//evaluate the behaviour with the given number of stimulated DocumentBoxes
			
			ArrayList<Long> selectedStimulatedPeerBoxes = new ArrayList<Long>();
			ArrayList<IPeer> peers= _networkFacade.GetPeers();
			while(selectedStimulatedPeerBoxes.size() < stimulatedPeerBoxes)
			{
				//select stimulatedPeerBoxes random PeerBoxes.
				IPeer p = RandomUtilities.SelectOneByRandomFromList(peers);
				
				if(!selectedStimulatedPeerBoxes.contains(p.GetPeerID()))
				{
					selectedStimulatedPeerBoxes.add(p.GetPeerID());
				}
			}																										
										
			for(Long l: selectedStimulatedPeerBoxes)
			{
				// change the capacity randomly
				int newCapacity = 
						RandomUtilities.SelectRandomInteger(GlobalSimulationParameters.MaximalInitialNumberOfAllowedDocumentBoxesOnPeerBox);								
				// set the max capacity of this selected peerbox
				_networkFacade.ChangeCapacityOfPeerBox(newCapacity,l);	
			}
			
			GlobalLogger.LogSimulationRunner("SimulationRunner : Execute the next simulation step : Stimulation " +selectedStimulatedPeerBoxes.size());
		}
		
		public void RunSimulation()
		{			
			
			_isRunning.set(true);
			GlobalLogger.LogSimulationRunner("SimulationRunner: Simulation Started " );
						
			try{
				
				ISimulationEvaluator evaluator = new SimulationEvaluator();
												
				_simulationWriter.BeginNewSimulation(_peerBoxNetworkInitializer,_docBoxNetworkInitializer);
				
				GlobalTools.GetTaskExecutor().BeginExecution();	
				
				for(int stimulatedPeerBoxes =  50; 
						stimulatedPeerBoxes <= 1000; 
						stimulatedPeerBoxes =  stimulatedPeerBoxes + 15 )
				{																					
				
						//inialize and create a new network, repeate with different number of stimulated PeerBoxes
						GlobalLogger.LogSimulationRunner("Start new Simulation step, reinitialize all .." );
						ArrayList<IDocumentBox> createdDocBoxes = InitializePeersAndDocumentBoxes();
																																															
						GlobalTools.GetTaskExecutor().BeginExecution();
						
						DoStimulatePeers(stimulatedPeerBoxes);
						for(int z =0; z<1200; z++)
						{
							UpdateCanvas();
							Thread.sleep(100);	
						}
						
						GlobalLogger.LogSimulationRunner("SimulationRunner (init): Id of the initial Peer: " + _initialPeer.GetPeerID());
						PlaceDocumentBoxesOnPeer(createdDocBoxes);
						
						GlobalTools.GlobalMigrationCounter.set(0);
						boolean bContinueInitialization = true;
						while(bContinueInitialization)
						{
							
							UpdateCanvas();
							
							Thread.sleep(3000);							
							GlobalTools.GetTaskExecutor().PauseExecution();
							
							IMigrationStateEvaluationResult result = evaluator.EvaluateNetworkState(_networkFacade);													
																
							GlobalLogger.LogSimulationRunner("SimulationRunner (init): Initialization-Step: migration counter at : " + GlobalTools.GlobalMigrationCounter.get() + 
															" number of stimulated PeerBoxes: "+ stimulatedPeerBoxes+
															" DBs authenticated: " + result.GetCountAuthenticatedDocumentBoxes() +
															" DBs not authenticated: " + result.GetCountUnAuthenticatedDocumentBoxes() +
															" DB's in migration : "+ result.GetCountInMigration());
							
							GlobalTools.GetTaskExecutor().ContinueExecution();
																											
							if(result.GetCountInMigration() == 0  &&  result.GetCountAuthenticatedDocumentBoxes() ==  _docBoxNetworkInitializer.GetNumberOfItemsItendedToCreate())
							{
								
								// done successfully
								bContinueInitialization = false;
							}
							
							if( GlobalTools.GlobalMigrationCounter.get() >1000*_docBoxNetworkInitializer.GetNumberOfItemsItendedToCreate() )
							{
								//failed ... 
								bContinueInitialization = false;
							}
						}
																 								
						GlobalTools.GetTaskExecutor().StopExecution();				
						GlobalLogger.LogSimulationRunner("SimulationRunner : Simulation run done with: " + stimulatedPeerBoxes + " after : " 
																										+ GlobalTools.GlobalMigrationCounter + " migrations");						
					}
										
			}catch(Throwable exp)
			{
				exp.printStackTrace(System.out);				
			}
			
			_simulationWriter.EndUpSimulation();
			
			_isRunning.set(false);
			
			GlobalLogger.LogSimulationRunner("SimulationRunner: Simulation stopped " );
		}
					
		
		
		long _maxCountDocumentBoxes = 0;
		IPeer _initialPeer = null;
		
		private void PlaceDocumentBoxesOnPeer(ArrayList<IDocumentBox>  docBoxes)
		{
			// Select any peer and place DocBoxes on it 			
			_networkFacade.InitializeDocumentBoxesOnPeers(docBoxes,_initialPeer);		
		}
		
		private ArrayList<IDocumentBox> InitializePeersAndDocumentBoxes()
		{	
			long numberOfPeers = _peerBoxNetworkInitializer.GetNumberOfItemsItendedToCreate();
			_maxCountDocumentBoxes = _docBoxNetworkInitializer.GetNumberOfItemsItendedToCreate();			
			GlobalSimulationParameters.Recalculate(numberOfPeers, (int) _maxCountDocumentBoxes);
										
			_networkFacade = _peerBoxNetworkInitializer.GetInitializedNetwork();
									
			_viewModelNetwork.SetNetwork(_networkFacade);
															
			GlobalTools.RegisterGlobalService(_networkFacade);			
			_initialPeer = RandomUtilities.SelectOneByRandomFromList(_networkFacade.GetPeers());																				
			//reinit the documentbox network by the given strategy
			 return _docBoxNetworkInitializer.GetInitializedNetwork(_initialPeer);										
		}
}
