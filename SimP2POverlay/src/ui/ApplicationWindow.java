package ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import commonHelper.GlobalTools;
import commonHelper.math.EuclideanPoint;
import commonHelper.math.RandomUtilities;
import documentBoxInitializer.BinaryTree.DocumentBoxBinaryTreeSettings;
import documentBoxInitializer.baPreferentialAttachment.DocumentBoxBaPreferentialAttachmentSettings;
import documentBoxInitializer.interfaces.IDocumentBoxNetworkInitializer;
import documentTreeModel.implementation.GlobalSimulationParameters;
import documentTreeModel.interfaces.IDocumentBox;
import networkInitializer.InitializerFactory;
import networkInitializer.baPreferentialAttachment.NetworkSettingsBaPreferentialAttachment;
import networkInitializer.gridStructured.NetworkSettingsGrid;
import networkInitializer.interfaces.INetworkInitializer;
import networkInitializer.smallWorldKleinberg.NetworkSettingsSmallWorldKleinberg;
import peersModel.implementation.NetworkFacade;
import peersModel.interfaces.INetworkFacade;
import peersModel.interfaces.IPeer;
import persistence.NetworkSettingsPersistenceSmallWorldKleinberg;
import persistence.NetworkToFilePersister;
import simulationRunner.SimulationRunner;
import simulationRunner.interfaces.ISimulationRunner;
import ui.MouseClickDelegates.MouseClickDelegateOnDecrementCapacityOfPeerBox;
import ui.MouseClickDelegates.MouseClickDelegateOnIncrementCapacityOfPeerBox;
import ui.MouseClickDelegates.MouseClickDelegateOnPrintInfoOfPeerBox;
import ui.interfaces.IMouseClickDelegate;
import ui.openGL.OpenGLViewHandler;
import ui.openGL.ViewModelNetwork;
import ui.openGL.ViewModelNetworkEvent.EventAssignNewNetwork;
import ui.openGL.ViewModelNetworkEvent.EventChangeCameraPosition;
import ui.openGL.ViewModelNetworkEvent.EventChangeZoomFactor;
import ui.openGL.ViewModelNetworkEvent.EventExecuteSelectedPeerAction;
import ui.openGL.ViewModelNetworkEvent.EventSetLookAtMouseClick;
import ui.openGL.interfaces.IViewmodelNetwork;

import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.JTextPane;
import java.awt.Label;
import javax.swing.JSlider;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JMenu;

public class ApplicationWindow {

	private JFrame frame;

 	GLProfile _glprofile = null;
    GLCapabilities _glcapabilities = null;
    GLCanvas _glcanvas = null;    
        
    IViewmodelNetwork _networkViewModel = new ViewModelNetwork(); 
    
    FPSAnimator _animator = null; 
    
    NetworkSettingsSmallWorldKleinberg _settingsKleinberg = new NetworkSettingsSmallWorldKleinberg(); 
    
       
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ApplicationWindow window = new ApplicationWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	
	private boolean initializeOpenGl(){
		
		boolean bSuccessfull = true;
		
		try{
			 _glprofile = GLProfile.getDefault();
		     _glcapabilities = new GLCapabilities( _glprofile );
		     _glcanvas = new GLCanvas( _glcapabilities );
		     
		}catch(Exception exp)
		{
			bSuccessfull = false;
		}	        
		return bSuccessfull;
	}
	
	private void initializeModels()
	{		 
		
		if(initializeOpenGl())
		{
			_glcanvas.addGLEventListener( new OpenGLViewHandler(_networkViewModel));	
		}		 		 		 
		_simulationRunner = new SimulationRunner(_glcanvas);
	}
	
	/**
	 * Create the application.
	 */
	public ApplicationWindow() 
	{				 			
		initializeModels();
		initialize();				
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1070, 1080);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);				
		
		
		_openGLpanel = new JPanel(new BorderLayout());
		_openGLpanel.setBounds(10, 200, 1034, 831);
		frame.getContentPane().add(_openGLpanel);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Network Parameter", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBounds(183, 39, 538, 86);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		_textPaneSizeX = new JTextPane();
		
		_textPaneSizeX.setText("20");
		_textPaneSizeX.setBounds(78, 22, 93, 20);
		panel.add(_textPaneSizeX);
		
		_textPaneSizeY = new JTextPane();
		
		
		_textPaneSizeY.setText("20");
		_textPaneSizeY.setBounds(78, 53, 93, 20);
		panel.add(_textPaneSizeY);
		
		Label label = new Label("Size X");
		label.setBounds(10, 22, 62, 22);
		panel.add(label);
		
		Label label_1 = new Label("Size Y");
		label_1.setBounds(10, 53, 62, 22);
		panel.add(label_1);
		
		Label label_7 = new Label("Number long range contacts");
		label_7.setBounds(189, 20, 158, 22);
		panel.add(label_7);
		
		_textPaneLongRangeContacts = new JTextPane();
		_textPaneLongRangeContacts.setText("20");
		_textPaneLongRangeContacts.setBounds(353, 22, 90, 20);
		panel.add(_textPaneLongRangeContacts);
		
		_textPaneDirectNeighboursDistance = new JTextPane();
		_textPaneDirectNeighboursDistance.setText("20");
		_textPaneDirectNeighboursDistance.setBounds(353, 53, 90, 20);
		panel.add(_textPaneDirectNeighboursDistance);
		
		Label label_8 = new Label("Direct neighbours(Distance)");
		label_8.setBounds(189, 51, 158, 22);
		panel.add(label_8);
		
		Label label_9 = new Label("R");
		label_9.setAlignment(Label.RIGHT);
		label_9.setBounds(426, 22, 54, 22);
		panel.add(label_9);
		
		_textPaneProportionality = new JTextPane();
		_textPaneProportionality.setText("0.01");
		_textPaneProportionality.setBounds(486, 22, 47, 20);
		panel.add(_textPaneProportionality);
		
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Move View", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_1.setBounds(10, 136, 983, 52);
		frame.getContentPane().add(panel_1);
		
		_cameraSliderZPos = new JSlider();
		_cameraSliderZPos.setToolTipText("Camera Height");
		_cameraSliderZPos.setBounds(640, 18, 112, 22);
		panel_1.add(_cameraSliderZPos);
		
		_sliderZoomFactor = new JSlider();
		_sliderZoomFactor.setMaximum(179);
		_sliderZoomFactor.setToolTipText("Camera Height");
		_sliderZoomFactor.setBounds(405, 18, 112, 22);
		panel_1.add(_sliderZoomFactor);
		
		Label label_2 = new Label("Camera Heigh");
		label_2.setAlignment(Label.RIGHT);
		label_2.setBounds(519, 18, 115, 22);
		panel_1.add(label_2);
		
		Label label_3 = new Label("Zoom");
		label_3.setAlignment(Label.RIGHT);
		label_3.setBounds(363, 18, 41, 22);
		panel_1.add(label_3);
		
		_cameraSliderXPos = new JSlider();
		_cameraSliderXPos.setToolTipText("X-Position");
		_cameraSliderXPos.setBounds(240, 18, 102, 22);
		panel_1.add(_cameraSliderXPos);
		
		_cameraSliderYPos = new JSlider();
		_cameraSliderYPos.setToolTipText("Y - Position");
		_cameraSliderYPos.setBounds(70, 18, 102, 22);		
		panel_1.add(_cameraSliderYPos);
		
		Label label_4 = new Label("X-Pos");
		label_4.setAlignment(Label.RIGHT);
		label_4.setBounds(180, 18, 54, 22);
		panel_1.add(label_4);
		
		Label label_5 = new Label("Y-Pos");
		label_5.setAlignment(Label.RIGHT);
		label_5.setBounds(10, 18, 54, 22);
		panel_1.add(label_5);
		
		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(_actionListenerReset);
		btnReset.setBounds(770, 18, 104, 23);
		panel_1.add(btnReset);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(735, 39, 258, 86);
		frame.getContentPane().add(panel_2);
		panel_2.setLayout(null);
		panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Connection View", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		JSlider slider = new JSlider();
		slider.setToolTipText("Camera Height");
		slider.setBounds(259, 64, 112, 22);
		panel_2.add(slider);
		
		JTextPane textPane = new JTextPane();
		textPane.setText("0.01");
		textPane.setBounds(441, 31, 47, 20);
		panel_2.add(textPane);
		
		Label label_11 = new Label("Faktor");
		label_11.setAlignment(Label.RIGHT);
		label_11.setBounds(381, 31, 54, 22);
		panel_2.add(label_11);
		
		JButton button = new JButton("Reset");
		button.setBounds(381, 64, 104, 23);
		panel_2.add(button);
		
		JCheckBox chckbxShowNearRange = new JCheckBox("Show near range connections");
		chckbxShowNearRange.setBounds(20, 17, 189, 23);
		chckbxShowNearRange.setSelected(_networkViewModel.GetNearContactsShownGui());
		chckbxShowNearRange.addChangeListener(_changeShowNearRangeConnectionsCheckBox);
		panel_2.add(chckbxShowNearRange);
		
		JCheckBox checkShowLongRangeContacts = new JCheckBox("Show long range connections");
		checkShowLongRangeContacts.setBounds(20, 49, 201, 23);
		checkShowLongRangeContacts.setSelected(_networkViewModel.GetLongRangeContactsShownGui());
		checkShowLongRangeContacts.addChangeListener(_changeShowLongRangeConnectionsCheckBox);
		
		panel_2.add(checkShowLongRangeContacts);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(5, 0, 1034, 21);
		frame.getContentPane().add(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItemOpen = new JMenuItem("Open");
		mnNewMenu.add(mntmNewMenuItemOpen);
		mntmNewMenuItemOpen.setActionCommand("FileOpen");
		mntmNewMenuItemOpen.addActionListener(_actionListenerButtons);
		
		JMenuItem mntmNewMenuItemSaveAs = new JMenuItem("Save As");
		mnNewMenu.add(mntmNewMenuItemSaveAs);
		mntmNewMenuItemSaveAs.setActionCommand("FileSaveAs");
		
		JMenu mnNewMenuDocumentBoxes = new JMenu("DocumentBoxes Overlay");
		menuBar.add(mnNewMenuDocumentBoxes);
															
				
		JMenuItem mntmNewMenuItemLetStartSimulationAtMouseClick = new JMenuItem("Let start simulation");
		mntmNewMenuItemLetStartSimulationAtMouseClick.setActionCommand("letStartSimulation");
		mnNewMenuDocumentBoxes.add(mntmNewMenuItemLetStartSimulationAtMouseClick);
		mntmNewMenuItemLetStartSimulationAtMouseClick.addActionListener(_actionListenerButtons);
		
		
		JMenuItem mntmNewMenuItemSendBroadcastAtMouseClick = new JMenuItem("Increment Capacity");
		mntmNewMenuItemSendBroadcastAtMouseClick.setActionCommand("letIncrementCapacity");
		mnNewMenuDocumentBoxes.add(mntmNewMenuItemSendBroadcastAtMouseClick);
		mntmNewMenuItemSendBroadcastAtMouseClick.addActionListener(_actionListenerButtons);
		
		JMenuItem mntmNewMenuItemDecrementCapacityAtMouseClick = new JMenuItem("Decrement Capacity");
		mntmNewMenuItemDecrementCapacityAtMouseClick.setActionCommand("letDecrementCapacity");
		mnNewMenuDocumentBoxes.add(mntmNewMenuItemDecrementCapacityAtMouseClick);
		mntmNewMenuItemDecrementCapacityAtMouseClick.addActionListener(_actionListenerButtons);
		
		
		JMenuItem mntmNewMenuItemResetNetworkAtMouseClick = new JMenuItem("Reset capacity");
		mntmNewMenuItemResetNetworkAtMouseClick.setActionCommand("letResetCapacity");
		mnNewMenuDocumentBoxes.add(mntmNewMenuItemResetNetworkAtMouseClick);
		mntmNewMenuItemResetNetworkAtMouseClick.addActionListener(_actionListenerButtons);
		
		
		JMenuItem mntmNewMenuItemSetupNetworktAtMouseClick = new JMenuItem("Setup network");
		mntmNewMenuItemSetupNetworktAtMouseClick.setActionCommand("letSetupNetwork");
		mnNewMenuDocumentBoxes.add(mntmNewMenuItemSetupNetworktAtMouseClick);
		mntmNewMenuItemSetupNetworktAtMouseClick.addActionListener(_actionListenerButtons);
		
		
		
		JMenuItem mntmNewMenuItemPrintInfoAtMouseClick = new JMenuItem("Print Peer info");
		mntmNewMenuItemPrintInfoAtMouseClick.setActionCommand("letPrintPeerInfo");
		mnNewMenuDocumentBoxes.add(mntmNewMenuItemPrintInfoAtMouseClick);
		mntmNewMenuItemPrintInfoAtMouseClick.addActionListener(_actionListenerButtons);
		
		
		
		
		InitializeCustom();									
	}
	
	private void InitializeCustom()
	{				
		
		
		ThisDocumentListener listener = new ThisDocumentListener();
		
		_sliderZoomFactor.addChangeListener(_changeListenersliderZoom);
		_cameraSliderXPos.addChangeListener(_changeListenersliderCameraPos);
		_cameraSliderYPos.addChangeListener(_changeListenersliderCameraPos);
		_cameraSliderZPos.addChangeListener(_changeListenersliderCameraPos);
			
		_textPaneSizeX.getDocument().addDocumentListener(listener);		
		_textPaneSizeY.getDocument().addDocumentListener(listener);
		_textPaneLongRangeContacts.getDocument().addDocumentListener(listener);		
		
		_textPaneDirectNeighboursDistance.getDocument().addDocumentListener(listener);
		_textPaneProportionality.getDocument().addDocumentListener(listener);		
		
		try{
		_openGLpanel.add(_glcanvas);		
		_glcanvas.addMouseListener(_mouseListenerViewPort);				
		
		_animator = new FPSAnimator(_glcanvas, 5);
		}catch(Exception exp)
		{
			
		}
		
		UpdateUISettings();
		
	}
	
	class ThisDocumentListener implements DocumentListener 
	{		
		public ThisDocumentListener()
		{	
		}
		
		@Override
		public void changedUpdate(DocumentEvent arg0) {
			evaluate(arg0);  
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			evaluate(arg0);
			
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			evaluate(arg0);
			
		}
		
		   
	    public void evaluate(DocumentEvent e) 
	    {	    			    
	    	try{
		    	int itxtLength = e.getDocument().getLength();
	    		String strText = e.getDocument().getText(0, itxtLength);
	    		
	    		if(e.getDocument()==_textPaneSizeX.getDocument())
	    		{
	    			
	    			_settingsKleinberg._xLength = Integer.parseInt(strText);	
	    			return;
	    		}

	    		if(e.getDocument()==_textPaneSizeY.getDocument())
	    		{
	    			_settingsKleinberg._yLength = Integer.parseInt(strText);
	    			return;
	    		}
	    		if(e.getDocument() == _textPaneLongRangeContacts.getDocument())
	    		{
	    			_settingsKleinberg._qParameter = Integer.parseInt(strText);	
	    			return;
	    		}
	    		if(e.getDocument() == _textPaneDirectNeighboursDistance.getDocument())
	    		{
	    			_settingsKleinberg._pPParameter = Integer.parseInt(strText);
	    			return;
	    		}
	    		if(e.getDocument() == _textPaneProportionality.getDocument())
	    		{
	    			_settingsKleinberg._rParameter = Double.parseDouble(strText);	
	    			return;
	    		} 
	    		
	    			    			    			    			    
	    	}catch(Exception exp)
	    	{		    		
	    	}
	        			        
	     }		
	}
	
	JPanel _openGLpanel;	    
	JSlider _sliderZoomFactor;
	JSlider _cameraSliderYPos;
	JSlider _cameraSliderXPos;
	JSlider _cameraSliderZPos;
	JTextPane textPaneLongRangeContacts;
	
	JTextPane _textPaneSizeX;
	JTextPane _textPaneSizeY;
	JTextPane _textPaneLongRangeContacts;
	JTextPane _textPaneDirectNeighboursDistance;
	JTextPane _textPaneProportionality;
			
	/**
	 * Update settings back into UI-Items
	 */
	private void UpdateUISettings()
	{
		_cameraSliderXPos.setValue(CalculationHelper.RecalcPositionInUserValue(this._networkViewModel.GetCameraPosition().GetPosX()));
		_cameraSliderYPos.setValue(CalculationHelper.RecalcPositionInUserValue(this._networkViewModel.GetCameraPosition().GetPosY()));
		_cameraSliderZPos.setValue(CalculationHelper.RecalcHeighInUserValue(this._networkViewModel.GetCameraPosition().GetPosZ()));
		_sliderZoomFactor.setValue( (int)this._networkViewModel.GetZoomAngle() );		
				
		_textPaneSizeX.setText(_settingsKleinberg._xLength.toString());
		_textPaneSizeY.setText(_settingsKleinberg._yLength.toString());
		_textPaneLongRangeContacts.setText(_settingsKleinberg._qParameter.toString());
		_textPaneDirectNeighboursDistance.setText(_settingsKleinberg._pPParameter.toString());
		_textPaneProportionality.setText(_settingsKleinberg._rParameter.toString());
					
	}


	MouseListener _mouseListenerViewPort = new MouseListener()
			{

				@Override
				public void mouseClicked(MouseEvent e) 
				{
					
					if(_currentMouseClickDelegate == null || e.isControlDown())
					{
						ApplicationWindow.this._networkViewModel.PlaceNewEventDelegate(new EventSetLookAtMouseClick(e.getX(),e.getY()));	
					} else 
					{
						ApplicationWindow.this._networkViewModel.PlaceNewEventDelegate(new EventExecuteSelectedPeerAction(_currentMouseClickDelegate,e.getX(),e.getY()));	
					}
																																	
					EventQueue.invokeLater(new Runnable(){
						public void run() 
						{
							try {
								ApplicationWindow.this.UpdateUISettings();													
							} catch (Exception e) {
								e.printStackTrace();
							}
						}});
					UpdateCanvas();								
				}

				@Override
				public void mouseEntered(MouseEvent e) {}

				@Override
				public void mouseExited(MouseEvent e) {}

				@Override
				public void mousePressed(MouseEvent e) {}

				@Override
				public void mouseReleased(MouseEvent e) {}
		
			};
	
	ChangeListener _changeShowNearRangeConnectionsCheckBox = new ChangeListener()
	{

		@Override
		public void stateChanged(ChangeEvent arg0) 
		{
			JCheckBox source = (JCheckBox)arg0.getSource();			
	        
	        ApplicationWindow.this._networkViewModel.SetNearContactsShownGui(source.isSelected());
	        UpdateCanvas();

		}
	};
	
	ChangeListener _changeShowLongRangeConnectionsCheckBox = new ChangeListener()
	{

		@Override
		public void stateChanged(ChangeEvent arg0) 
		{
			JCheckBox source = (JCheckBox)arg0.getSource();			
	        
	        ApplicationWindow.this._networkViewModel.SetLongRangeContactsShownGui(source.isSelected());
	        
	        UpdateCanvas();

		}
	};
	
	ChangeListener _changeListenersliderZoom = new ChangeListener()
	{
		@Override
		public void stateChanged(ChangeEvent arg0) 
		{
			JSlider source = (JSlider)arg0.getSource();			
	        int zoomValue = (int)source.getValue();		        
	        ApplicationWindow.this._networkViewModel.PlaceNewEventDelegate(new EventChangeZoomFactor(zoomValue));	   	        	        
	        UpdateCanvas();	    
		}
		
	};
	
	private void UpdateCanvas()
	{
		if(_glcanvas != null)
	    {
	       _glcanvas.repaint();	        	
	    }	        
	}
	
	
	ChangeListener _changeListenersliderCameraPos= new ChangeListener()
	{
		@Override
		public void stateChanged(ChangeEvent arg0) 
		{
			ChangeCamera();
		}		
	};
		
	
	private void ChangeCamera()
	{
		int xPos = (int)_cameraSliderXPos.getValue();
		int yPos = (int)_cameraSliderYPos.getValue();
		int zPos = (int)_cameraSliderZPos.getValue();
				
		double dxPos = CalculationHelper.RecalcPositionInModelValue(xPos);
		double dyPos = CalculationHelper.RecalcPositionInModelValue(yPos);
		double dzPos = CalculationHelper.RecalcHeighInModelValue(zPos);
		
		ApplicationWindow.this._networkViewModel.PlaceNewEventDelegate(new EventChangeCameraPosition(new EuclideanPoint(new double[]{dxPos,dyPos,dzPos})));	        
	    UpdateCanvas();
	}
	
	
	ActionListener _actionListenerReset = new ActionListener() 
	{
		public void actionPerformed(ActionEvent arg0) 
		{								
			ApplicationWindow.this._networkViewModel.Reset();
			
			EventQueue.invokeLater(new Runnable(){
				public void run() 
				{
					try {
						ApplicationWindow.this.UpdateUISettings();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}});
			
			UpdateCanvas();											
		}
	};

	
	IMouseClickDelegate _currentMouseClickDelegate = null;
	
			
	private String DoSelectFile(boolean toRead)
	{
		 JFileChooser fc = new JFileChooser();
		 fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		 fc.setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File arg0) {
				if(arg0.isDirectory())
				{
					return true;
				}
				return arg0.getName().endsWith(".network");				
			}

			@Override
			public String getDescription() {				
				return "Network-Files";
			}});
		 
		 int result = -1;
		 if(toRead )
		 {
			 result = fc.showOpenDialog(frame);	 
		 } else 
		 {
			 result = fc.showSaveDialog(frame);
		 }
		 		 
		 if (result != JFileChooser.APPROVE_OPTION) 
		 {
			 return null;			
		 }
		 
		 return fc.getSelectedFile().getAbsolutePath();
		 		 		 
	}
		
	private void DoSaveAs()
	{
		if(_networkViewModel.GetNetwork() == null)
		{
			JOptionPane.showMessageDialog(null, "Any generated network found.\nPlease generate the network first!", "Saving failed", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String  filePath = DoSelectFile(false);
		
		NetworkToFilePersister persister = new  NetworkToFilePersister();
		
		persister.InitializeTargetFile(filePath);
		
		persister.DoPersistNetwork(_networkViewModel.GetNetwork(), _settingsKleinberg);
		
		frame.setTitle(filePath);
		
		JOptionPane.showMessageDialog(null, "Saving of the file done!", "Save as operation",JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void DoOpenFile()
	{
		String  filePath = DoSelectFile(true);				
		
		NetworkToFilePersister persister = new  NetworkToFilePersister();
						
		persister.InitializeTargetFile(filePath);
		
		INetworkFacade network = new NetworkFacade();		
		if(!persister.DoRestoreNetwork(network))
		{
			return;
		}			
		_settingsKleinberg = (NetworkSettingsSmallWorldKleinberg)persister.GetLastRestoredNetworkSettings(); 
				
		_networkViewModel.SetNetwork(network);
		
		UpdateUISettings();		
		
		frame.setTitle(filePath);
		
		UpdateCanvas();
	}
	
	ISimulationRunner _simulationRunner = null;
	
	private void DoBeginSimulation()
	{
		_currentMouseClickDelegate = null;
		
		//NetworkSettingsSmallWorldKleinberg settings = new NetworkSettingsSmallWorldKleinberg();
		
		//settings._pPParameter = 1;
		//settings._qParameter = 1;
		//settings._rParameter = 1.0;
		//settings._xLength = 34;		
		//settings._yLength = 34;

	/*
		NetworkSettingsBaPreferentialAttachment settings = new NetworkSettingsBaPreferentialAttachment();
		settings.m = 1;
		settings.m0 = 2;
		settings.N = 1156;
	*/
	    NetworkSettingsGrid settings = new NetworkSettingsGrid(34,34);
		/*
		DocumentBoxBaPreferentialAttachmentSettings docBoxNetworkSettings = new DocumentBoxBaPreferentialAttachmentSettings();
		docBoxNetworkSettings.m = 1;
		docBoxNetworkSettings.m0 = 2;
		docBoxNetworkSettings.N = 1000;
		 */
		DocumentBoxBinaryTreeSettings docBoxNetworkSettings = new DocumentBoxBinaryTreeSettings(1000);
						
		INetworkInitializer peerBoxInitializer  = null;
		IDocumentBoxNetworkInitializer docBoxInitializer = null;
		try{		
			peerBoxInitializer =  InitializerFactory.GetInitializerBySettingsType(settings, true);					
			docBoxInitializer = documentBoxInitializer.InitializerFactory.CreateBySettingsType(docBoxNetworkSettings);
		}catch(Exception exp)
		{
			exp.printStackTrace(System.err);
		}
			
		if(!_simulationRunner.Initialize(peerBoxInitializer,docBoxInitializer, _networkViewModel)) return;
		
		if(!_simulationRunner.BeginSimulation())
		{
			JOptionPane.showMessageDialog(null,"Simulation is already running.... ");
		}
	}
	
	
	private void DoSetupNetwork()
	{
		_currentMouseClickDelegate = null;
		
		GlobalTools.GetTaskExecutor().StopExecution();
		
		/*
		NetworkSettingsSmallWorldKleinberg settings = new NetworkSettingsSmallWorldKleinberg();
		
		settings._pPParameter = _settingsKleinberg._pPParameter;
		settings._qParameter = _settingsKleinberg._qParameter;
		settings._rParameter = _settingsKleinberg._rParameter;
		settings._xLength = _settingsKleinberg._xLength;		
		settings._yLength = _settingsKleinberg._yLength;
		
	
		NetworkSettingsBaPreferentialAttachment settings = new NetworkSettingsBaPreferentialAttachment();
		settings.m = 1;
		settings.m0 = 2;
		settings.N = _settingsKleinberg._xLength*_settingsKleinberg._yLength;
	
	*/		
		
		NetworkSettingsGrid settings = new NetworkSettingsGrid(_settingsKleinberg._xLength,_settingsKleinberg._yLength);
		
		/*
		DocumentBoxBaPreferentialAttachmentSettings docBoxNetworkSettings = new DocumentBoxBaPreferentialAttachmentSettings();
		docBoxNetworkSettings.m = 1;
		docBoxNetworkSettings.m0 = 2;
		docBoxNetworkSettings.N = 1000;
		 */
		DocumentBoxBinaryTreeSettings docBoxNetworkSettings = new DocumentBoxBinaryTreeSettings(1000);
						
		INetworkInitializer peerBoxInitializer  = null;
		IDocumentBoxNetworkInitializer docBoxInitializer = null;
		try{		
			peerBoxInitializer =  InitializerFactory.GetInitializerBySettingsType(settings, true);					
			docBoxInitializer = documentBoxInitializer.InitializerFactory.CreateBySettingsType(docBoxNetworkSettings);
			
			
			INetworkFacade facade = peerBoxInitializer.GetInitializedNetwork();
			IPeer randomPeer = facade.GetPeers().get(12);//RandomUtilities.SelectOneByRandomFromList();
			
			
			
			ArrayList<IDocumentBox> createdDocumentBoxNetwork = docBoxInitializer.GetInitializedNetwork(randomPeer);
			
			facade.ChangeCapacityOfPeerBox(50, randomPeer.GetPeerID());
			facade.InitializeDocumentBoxesOnPeers(createdDocumentBoxNetwork, randomPeer);
			
			ApplicationWindow.this._networkViewModel.PlaceNewEventDelegate(new EventAssignNewNetwork(facade));	   	        	        		        
		
			GlobalTools.RegisterGlobalService(facade);
			GlobalTools.GetTaskExecutor().ContinueExecution();
			
		}catch(Exception exp)
		{
			exp.printStackTrace(System.err);
		}
		
		UpdateCanvas();			
	}
	
	
	private void DoResetCapacity()
	{
		if(this._networkViewModel.GetNetwork() != null)		
			this._networkViewModel.GetNetwork().ChangeCapacityOfPeerBoxes(0);
	}
	
	
	private void DoIncrementCapacityOnSelectedPeerBox()
	{		
		//increment capacity
		_currentMouseClickDelegate = new MouseClickDelegateOnIncrementCapacityOfPeerBox();						
	}
	
	
	private void DoDecrementCapacityOnSelectedPeerBox()
	{		
		//increment capacity
		_currentMouseClickDelegate = new MouseClickDelegateOnDecrementCapacityOfPeerBox();				
		
	}
	
	private void DoPrintPeerInfo() {		
		//increment capacity
		_currentMouseClickDelegate = new MouseClickDelegateOnPrintInfoOfPeerBox();
		
	}
	
	ActionListener _actionListenerButtons  = new ActionListener() 
	{
		public void actionPerformed(ActionEvent arg0) 
		{											
			
			if(arg0.getActionCommand() == "FileOpen")
			{
				DoOpenFile();
			}			
			if(arg0.getActionCommand() == "FileSaveAs")
			{
				DoSaveAs();
			}			
															
			if(arg0.getActionCommand() == "letStartSimulation")
			{
				DoBeginSimulation();
			}
			
			if(arg0.getActionCommand() == "letIncrementCapacity")
			{
				//send a broad cast
				DoIncrementCapacityOnSelectedPeerBox();
			}
			
			if(arg0.getActionCommand() == "letDecrementCapacity")
			{
				//send a broad cast
				DoDecrementCapacityOnSelectedPeerBox();
			}						
			
			if(arg0.getActionCommand() == "letResetCapacity")
			{
				DoResetCapacity();
			}
			
			if(arg0.getActionCommand() == "letPrintPeerInfo")
			{
				DoPrintPeerInfo();
			}
			
			
			if(arg0.getActionCommand() == "letSetupNetwork")
			{
				//send a broad cast
				
				DoSetupNetwork();
			}
					
								
		}

		
	};
}
