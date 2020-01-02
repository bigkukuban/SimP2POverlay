package ui;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import ui.inputVerifier.IntVerifier;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.Dimension;

public class SettingsChordEditor  extends JDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textFieldParameterM;
	private JTextField textFieldParameterN;
	private JCheckBox chckbxUseRandomPlacing;
	
	public SettingsChordEditor(Frame parentFrame, int m, int N, boolean useRandomPlacing)
	{
		super(parentFrame,"Chord settings", true);
		getContentPane().setPreferredSize(new Dimension(320, 150));
		getContentPane().setSize(new Dimension(320, 150));
		getContentPane().setMinimumSize(new Dimension(320, 150));
		setResizable(false);
		getContentPane().setLayout(null);
		
		JLabel lblIdentifierLengthm = new JLabel("Identifier length (m):");
		lblIdentifierLengthm.setBounds(10, 11, 140, 14);
		getContentPane().add(lblIdentifierLengthm);
		
		textFieldParameterM = new JTextField();
		textFieldParameterM.setText("0");
		textFieldParameterM.setColumns(10);
		textFieldParameterM.setBounds(198, 11, 111, 20);
		getContentPane().add(textFieldParameterM);
		
		JLabel lblNumberOfNodes = new JLabel("Number of nodes (N):");
		lblNumberOfNodes.setBounds(10, 36, 140, 14);
		getContentPane().add(lblNumberOfNodes);
		
		textFieldParameterN = new JTextField();
		textFieldParameterN.setText("0");
		textFieldParameterN.setColumns(10);
		textFieldParameterN.setBounds(198, 36, 111, 20);
		getContentPane().add(textFieldParameterN);
		
		JButton buttonOK = new JButton("OK");
		buttonOK.setBounds(220, 106, 89, 23);
		getContentPane().add(buttonOK);
		
		chckbxUseRandomPlacing = new JCheckBox("");
		chckbxUseRandomPlacing.setBounds(195, 63, 114, 23);
		getContentPane().add(chckbxUseRandomPlacing);
		
		JLabel lblUseRandomPlacing = new JLabel("Use random placing:");
		lblUseRandomPlacing.setBounds(10, 67, 140, 14);
		getContentPane().add(lblUseRandomPlacing);
		
		
		//custom initialization
		chckbxUseRandomPlacing.setSelected(useRandomPlacing);
		textFieldParameterN.setText(String.valueOf(N));
		textFieldParameterM.setText(String.valueOf(m));
		
		textFieldParameterN.setInputVerifier(new IntVerifier());
		textFieldParameterM.setInputVerifier(new IntVerifier());
		
		buttonOK.addActionListener(new ActionListener() 
		{
			@Override
		    public void actionPerformed(ActionEvent e) {
				SettingsChordEditor.this.setVisible(false);
		    }				
		});
		
	}
	
	public class Result{
		public int mValue;
		public int nValue; 
		public boolean useRandomPlacing; 		
	}
	
	public Result GetResult()
	{
		Result result = new Result();
		
		result.mValue =  new Integer(textFieldParameterM.getText());
		result.nValue =  new Integer(textFieldParameterN.getText());
		result.useRandomPlacing =  chckbxUseRandomPlacing.isSelected();
			
		return result;
	}
}
