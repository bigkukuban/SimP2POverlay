package ui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import com.jogamp.opengl.awt.GLCanvas;

import launcher.ApplicationModelSettings;
import launcher.ApplicationModelSettings.SupportedTopologyTypes;
import networkInitializer.baPreferentialAttachment.NetworkSettingsBaPreferentialAttachment;
import networkInitializer.gridStructured.NetworkSettingsGrid;
import networkInitializer.smallWorldKleinberg.NetworkSettingsSmallWorldKleinberg;
import ui.interfaces.IMouseClickDelegate;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JTextPane;
import java.awt.Label;
import javax.swing.JSlider;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class ApplicationWindow {

	private JFrame frame;
 	       
    AppWindowActions _actionsHandler;
	
	JPanel _openGLpanel;	   
	GLCanvas _glCanvas;
	JSlider _sliderZoomFactor;
	JSlider _cameraSliderYPos;
	JSlider _cameraSliderXPos;
	JSlider _cameraSliderZPos;
	JTextPane textPaneLongRangeContacts;
     
	private static class Item {

        private SupportedTopologyTypes type = SupportedTopologyTypes.Unknown;
        private String description;

        public Item(SupportedTopologyTypes tp, String description) {
            this.type = tp;
            this.description = description;
        }

        public SupportedTopologyTypes getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return description;
        }
        
        public static Item[] GetSupportedTopologies()
        {
        	 return new Item[] {
        			 			new Item(SupportedTopologyTypes.Grid, "Grid"),
        			 			new Item(SupportedTopologyTypes.PreferentialAttachment, "Preferential Attachment"),
        			 			new Item(SupportedTopologyTypes.SmallWorld, "Small World")
        			 			};
        }
        
        public static Item GetItemByEnum(SupportedTopologyTypes type, Item[] source)
        {
        	return Arrays.stream(source).filter(o -> o.type == type).findFirst().get();        	
        }
    }

	
	/**
	 * Create the application.
	 */
	public ApplicationWindow(GLCanvas glCanvas, AppWindowActions actionsHandler) 
	{			
		_actionsHandler = actionsHandler;
		initialize(glCanvas);			
		this.frame.setVisible(true);		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(GLCanvas glCanvas) {
		_glCanvas = glCanvas;
		frame = new JFrame();
		frame.setBounds(100, 100, 974, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.addComponentListener(_jFrameComponentListener);
		
			
		_openGLpanel = new JPanel();
		_openGLpanel.setBackground(Color.BLACK);
		Dimension dim = CalculateNewSizeForOpenGlCanvas(frame);
		_openGLpanel.setBounds(10, 116, 938,  534);
		_openGLpanel.setLayout(new BorderLayout(0, 0));
		frame.getContentPane().add(_openGLpanel);

		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(null);
		cameraPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Camera", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		cameraPanel.setBounds(10, 32, 731, 52);
		frame.getContentPane().add(cameraPanel);
		
		_cameraSliderZPos = new JSlider();
		_cameraSliderZPos.setToolTipText("Camera Height");
		_cameraSliderZPos.setBounds(513, 18, 112, 22);
		cameraPanel.add(_cameraSliderZPos);
		
		_sliderZoomFactor = new JSlider();
		_sliderZoomFactor.setMaximum(179);
		_sliderZoomFactor.setToolTipText("Camera Height");
		_sliderZoomFactor.setBounds(359, 18, 112, 22);
		cameraPanel.add(_sliderZoomFactor);
		
		Label label_2 = new Label("Heigh");
		label_2.setBounds(474, 18, 43, 22);
		cameraPanel.add(label_2);
		
		Label label_3 = new Label("Zoom");
		label_3.setBounds(320, 18, 43, 22);
		cameraPanel.add(label_3);
		
		_cameraSliderXPos = new JSlider();
		_cameraSliderXPos.setToolTipText("X-Position");
		_cameraSliderXPos.setBounds(208, 18, 102, 22);
		cameraPanel.add(_cameraSliderXPos);
		
		_cameraSliderYPos = new JSlider();
		_cameraSliderYPos.setToolTipText("Y - Position");
		_cameraSliderYPos.setBounds(49, 18, 102, 22);		
		cameraPanel.add(_cameraSliderYPos);
		
		Label label_4 = new Label("X-Pos");
		label_4.setBounds(169, 18, 43, 22);
		cameraPanel.add(label_4);
		
		Label label_5 = new Label("Y-Pos");
		label_5.setBounds(10, 18, 43, 22);
		cameraPanel.add(label_5);
		
		JButton btnReset = new JButton("Reset");
		btnReset.setActionCommand("letReset");
		btnReset.addActionListener(_actionListenerButtons);
		btnReset.setBounds(643, 18, 74, 23);
		cameraPanel.add(btnReset);
														
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
		
		JMenu mnNewMenuSettings = new JMenu("Settings");
		mnNewMenuSettings.setActionCommand("Settings");
		menuBar.add(mnNewMenuSettings);
				
		JMenuItem mntmMenuItemGridSettings = new JMenuItem("Grid");
		mntmMenuItemGridSettings.setActionCommand("letConfigureGridSettings");
		mnNewMenuSettings.add(mntmMenuItemGridSettings);
		
		JMenuItem mntmSmallworldSettings = new JMenuItem("Small-World");
		mntmSmallworldSettings.setActionCommand("letConfigureSmallWorldSettings");
		mnNewMenuSettings.add(mntmSmallworldSettings);
		
		JMenuItem mntmPreferentialattachmentSettings = new JMenuItem("Preferential-Attachment");
		mntmPreferentialattachmentSettings.setActionCommand("letConfigurePreferentialAttachmentSettings");
		mnNewMenuSettings.add(mntmPreferentialattachmentSettings);
		
		JPanel topologyPanel = new JPanel();
		topologyPanel.setBorder(new TitledBorder(null, "Overlay topology", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		topologyPanel.setBounds(743, 32, 205, 52);
		frame.getContentPane().add(topologyPanel);
		topologyPanel.setLayout(null);
		
		JComboBox<Item> comboBox = new JComboBox<Item>();
		Item[]  supportedItems = Item.GetSupportedTopologies();
		comboBox.setModel(new DefaultComboBoxModel<Item>(supportedItems));
		comboBox.setBounds(10, 21, 185, 20);
		comboBox.setPreferredSize(new Dimension(120, 20));
		comboBox.setActionCommand("letChangeTopology");				
		comboBox.setSelectedItem(Item.GetItemByEnum(ApplicationModelSettings.ConvertTypeToEnum(_actionsHandler.ApplicationSettings.ActiveSettings),supportedItems));		
		
		topologyPanel.add(comboBox);
						
		mntmMenuItemGridSettings.addActionListener(_actionListenerButtons);
		mntmSmallworldSettings.addActionListener(_actionListenerButtons);
		mntmPreferentialattachmentSettings.addActionListener(_actionListenerButtons);
		comboBox.addActionListener(_actionListenerButtons);
								
		InitializeCustom(glCanvas);									
	}
	
	private static Dimension CalculateNewSizeForOpenGlCanvas(Component cmp)
	{
		return  new Dimension(cmp.getSize().width-30, cmp.getSize().height-180);
	}
	
	ComponentListener _jFrameComponentListener = new ComponentListener()
			{

				@Override
				public void componentHidden(ComponentEvent arg0) {

					
				}

				@Override
				public void componentMoved(ComponentEvent arg0) {

					
				}

				@Override
				public void componentResized(ComponentEvent arg0) 
				{					
					Dimension dim = CalculateNewSizeForOpenGlCanvas(arg0.getComponent());
					
					_openGLpanel.setSize(dim);
					_glCanvas.setSize(dim);
					
				}

				@Override
				public void componentShown(ComponentEvent arg0) {
					
				}
		
			};
	
	private void InitializeCustom(GLCanvas glCanvas)
	{											
		_sliderZoomFactor.addChangeListener(_changeListenersliderZoom);
		_cameraSliderXPos.addChangeListener(_changeListenersliderCameraPos);
		_cameraSliderYPos.addChangeListener(_changeListenersliderCameraPos);
		_cameraSliderZPos.addChangeListener(_changeListenersliderCameraPos);
							
		try{
			_openGLpanel.add(glCanvas, BorderLayout.CENTER);							
		}catch(Exception exp)
		{
			
		}		
		UpdateUISettings();		
	}

			
	/**
	 * Update settings back into UI-Items
	 */
	private void UpdateUISettings()
	{
		//	_cameraSliderXPos.setValue(CalculationHelper.RecalcPositionInUserValue(this._networkViewModel.GetCameraPosition().GetPosX()));
		//	_cameraSliderYPos.setValue(CalculationHelper.RecalcPositionInUserValue(this._networkViewModel.GetCameraPosition().GetPosY()));
		//	_cameraSliderZPos.setValue(CalculationHelper.RecalcHeighInUserValue(this._networkViewModel.GetCameraPosition().GetPosZ()));
		//	_sliderZoomFactor.setValue( (int)this._networkViewModel.GetZoomAngle() );					
	}

		
	ChangeListener _changeListenersliderZoom = new ChangeListener()
	{
		@Override
		public void stateChanged(ChangeEvent arg0) 
		{
			JSlider source = (JSlider)arg0.getSource();			
	        int zoomValue = (int)source.getValue();			        
	        _actionsHandler.UserChangedZoomValue(zoomValue);	        
		}
		
	};
	
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
		
		_actionsHandler.UserChangedCameraPosInApplilcationView(dxPos,dyPos  , dzPos);		
	}
				
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
						
		String  filePath = DoSelectFile(false);			
		if(_actionsHandler.UserStoresTheSettings(filePath) == false)
		{
			JOptionPane.showMessageDialog(null, "Any generated network found.\nPlease generate the network first!", "Saving failed", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		frame.setTitle(filePath);
		
		JOptionPane.showMessageDialog(null, "Saving of the file done!", "Save as operation",JOptionPane.INFORMATION_MESSAGE);						
	}
	
	private void DoOpenFile()
	{
	
		String  filePath = DoSelectFile(true);				
		
		if(_actionsHandler.UserOpensSettingsFromFile(filePath) == false)
		{
			JOptionPane.showMessageDialog(null, "Open failed of the settings... !", "Open failed", JOptionPane.ERROR_MESSAGE);
			return;
		}
		frame.setTitle(filePath);
	}

	private void DoConfigurePreferentialAttachmentSettings()
	{
		NetworkSettingsBaPreferentialAttachment settings = (NetworkSettingsBaPreferentialAttachment)_actionsHandler.
																		ApplicationSettings.
																		GetSettingsByType(SupportedTopologyTypes.PreferentialAttachment);
		
		SettingsPreferentialAttachmentEditor paSettingsFrame = new SettingsPreferentialAttachmentEditor(frame, settings.m0, settings.m,settings.N);
		paSettingsFrame.pack();
		paSettingsFrame.setLocationRelativeTo(frame);
		paSettingsFrame.setVisible(true);
		int[] params = paSettingsFrame.GetResult();
		
		_actionsHandler.UserChangedSettingsForPreferentialAttachment(params[0], params[1], params[2]);
	}
	
	private void DoConfigureSmallWorldSettings()
	{
		//send a broad cast				
		//DoSetupNetwork();
		NetworkSettingsSmallWorldKleinberg settings = (NetworkSettingsSmallWorldKleinberg)_actionsHandler.ApplicationSettings.GetSettingsByType(SupportedTopologyTypes.SmallWorld);
		
		SettingsSmallWorldEditor gridSettingsFrame = new SettingsSmallWorldEditor(frame, settings._xLength, 
																				 settings._yLength, settings._qParameter, 
																				 settings._pPParameter, 
																				 settings._rParameter);
		gridSettingsFrame.pack();
		gridSettingsFrame.setLocationRelativeTo(frame);
		gridSettingsFrame.setVisible(true);
		
		SettingsSmallWorldEditor.Result res = gridSettingsFrame.GetResult();
		
		_actionsHandler.UserChangedSettingsForSmallWorld(res.xItems,res.yItems,res.qParam,res.pParam, res.rParam);
	}
	
	private void DoConfigureGridSettings()
	{
		//send a broad cast				
		//DoSetupNetwork();
		NetworkSettingsGrid settings = (NetworkSettingsGrid)_actionsHandler.ApplicationSettings.GetSettingsByType(SupportedTopologyTypes.Grid);
		
		SettingsGridEditor gridSettingsFrame = new SettingsGridEditor(frame, settings.XLength, settings.YLength);
		gridSettingsFrame.pack();
		gridSettingsFrame.setLocationRelativeTo(frame);
		gridSettingsFrame.setVisible(true);
		
		int[] xySizes = gridSettingsFrame.GetResult();
		
		_actionsHandler.UserChangedSettingsForGrid(xySizes[0], xySizes[1]);
	}
	
	private void DoSwitchToNewTopology(SupportedTopologyTypes topologyType)
	{
		_actionsHandler.UserChangedToOtherTopology(topologyType);
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
															
			if(arg0.getActionCommand() == "letReset")
			{
				_actionsHandler.UsedResetedTheView();	
			}
			
			if(arg0.getActionCommand() == "letConfigureGridSettings")
			{
				DoConfigureGridSettings();
			}
			if(arg0.getActionCommand() == "letConfigureSmallWorldSettings")
			{
				DoConfigureSmallWorldSettings();
			}
			
			if(arg0.getActionCommand() == "letConfigurePreferentialAttachmentSettings")
			{
				DoConfigurePreferentialAttachmentSettings();
			}
			
			if(arg0.getActionCommand() == "letChangeTopology")
			{
				JComboBox<Item> comboBox = (JComboBox<Item>) arg0.getSource();
                Item item = (Item) comboBox.getSelectedItem();
                DoSwitchToNewTopology(item.type);
			}
									
		}		
	};
}
