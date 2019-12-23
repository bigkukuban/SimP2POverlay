package ui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ui.inputVerifier.DoubleVerifier;
import ui.inputVerifier.IntVerifier;

import javax.swing.JButton;
import java.awt.Dimension;

public class SettingsPreferentialAttachmentEditor extends JDialog  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textFieldParameterm0;
	private JTextField textFieldParameterm;
	private JTextField textFieldParameterN;
	
	
	public SettingsPreferentialAttachmentEditor(Frame parentFrame, int m0, int m, int n)
	{
		super(parentFrame,"Preferential attachment settings", true);
		setPreferredSize(new Dimension(330, 150));
		setSize(new Dimension(330, 150));
		setResizable(false);
		getContentPane().setLayout(null);
		
		JLabel lblParameterN = new JLabel("Parameter N:");
		lblParameterN.setBounds(10, 11, 156, 14);
		getContentPane().add(lblParameterN);
		
		JLabel lblParameterM = new JLabel("Parameter m:");
		lblParameterM.setBounds(10, 36, 140, 14);
		getContentPane().add(lblParameterM);
		
		JLabel lblParameterM_1 = new JLabel("Parameter m0:");
		lblParameterM_1.setBounds(10, 61, 140, 14);
		getContentPane().add(lblParameterM_1);
		
		textFieldParameterm0 = new JTextField();
		textFieldParameterm0.setText("0");
		textFieldParameterm0.setColumns(10);
		textFieldParameterm0.setBounds(198, 61, 111, 20);
		getContentPane().add(textFieldParameterm0);
		
		textFieldParameterm = new JTextField();
		textFieldParameterm.setText("0");
		textFieldParameterm.setColumns(10);
		textFieldParameterm.setBounds(198, 36, 111, 20);
		getContentPane().add(textFieldParameterm);
		
		textFieldParameterN = new JTextField();
		textFieldParameterN.setText("0");
		textFieldParameterN.setColumns(10);
		textFieldParameterN.setBounds(198, 11, 111, 20);
		getContentPane().add(textFieldParameterN);
		
		JButton buttonOK = new JButton("OK");
		buttonOK.setBounds(220, 92, 89, 23);
		getContentPane().add(buttonOK);
		
		
		//custom initialization				
		textFieldParameterm0.setText(String.valueOf(m0));
		textFieldParameterm.setText(String.valueOf(m));
		textFieldParameterN.setText(String.valueOf(n));
		
				
		textFieldParameterm0.setInputVerifier(new IntVerifier());		
		textFieldParameterm.setInputVerifier(new IntVerifier());
		textFieldParameterN.setInputVerifier(new IntVerifier());
							
		buttonOK.addActionListener(new ActionListener() 
		{
			@Override
		    public void actionPerformed(ActionEvent e) {
				SettingsPreferentialAttachmentEditor.this.setVisible(false);
		    }				
		});							
	}
	
	public int[] GetResult()
	{
		int m0 	=  new Integer(textFieldParameterm0.getText());
		int m 	=  new Integer(textFieldParameterm.getText());
		int n 	=  new Integer(textFieldParameterN.getText());
								
		return new int[]{m0,m,n};
	}

}
