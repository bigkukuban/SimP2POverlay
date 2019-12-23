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
import java.awt.Dialog.ModalityType;

public class SettingsSmallWorldEditor  extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textFieldXItems;
	private JTextField textFieldYItems;
	private JTextField textFieldQParameter;
	private JTextField textFieldPParameter;
	private JTextField textFieldRParameter;
	
	public SettingsSmallWorldEditor(Frame parentFrame, int itemsInX, int itemsInY, int qParameter, int pParameter, double rParameter) 
	{
		setModalityType(ModalityType.DOCUMENT_MODAL);
		this.setTitle("Small-World settings");
		setPreferredSize(new Dimension(320, 200));
		setSize(new Dimension(320, 200));
		setResizable(false);
		getContentPane().setLayout(null);
		
		JLabel label = new JLabel("Items in X direction:");
		label.setBounds(10, 11, 156, 14);
		getContentPane().add(label);
		
		JLabel label_1 = new JLabel("Items in Y direction:");
		label_1.setBounds(10, 36, 140, 14);
		getContentPane().add(label_1);
		
		textFieldXItems = new JTextField();
		textFieldXItems.setText("0");
		textFieldXItems.setColumns(10);
		textFieldXItems.setBounds(198, 11, 111, 20);
		getContentPane().add(textFieldXItems);
		
		textFieldYItems = new JTextField();
		textFieldYItems.setText("0");
		textFieldYItems.setColumns(10);
		textFieldYItems.setBounds(198, 36, 111, 20);
		getContentPane().add(textFieldYItems);
		
		JButton buttonOK = new JButton("OK");
		buttonOK.setBounds(220, 140, 89, 23);
		getContentPane().add(buttonOK);
		
		JLabel lblParameterQ = new JLabel("Parameter q:");
		lblParameterQ.setBounds(10, 61, 140, 14);
		getContentPane().add(lblParameterQ);
		
		textFieldQParameter = new JTextField();
		textFieldQParameter.setText("0");
		textFieldQParameter.setColumns(10);
		textFieldQParameter.setBounds(198, 61, 111, 20);
		getContentPane().add(textFieldQParameter);
		
		JLabel lblPrameterP = new JLabel("Parameter p:");
		lblPrameterP.setBounds(10, 86, 140, 14);
		getContentPane().add(lblPrameterP);
		
		textFieldPParameter = new JTextField();
		textFieldPParameter.setText("0");
		textFieldPParameter.setColumns(10);
		textFieldPParameter.setBounds(198, 86, 111, 20);
		getContentPane().add(textFieldPParameter);
		
		JLabel lblParameterR = new JLabel("Parameter r:");
		lblParameterR.setBounds(10, 111, 140, 14);
		getContentPane().add(lblParameterR);
		
		textFieldRParameter = new JTextField();
		textFieldRParameter.setText("0");
		textFieldRParameter.setColumns(10);
		textFieldRParameter.setBounds(198, 111, 111, 20);
		getContentPane().add(textFieldRParameter);
		
		
		///custom initialization 
		
		textFieldXItems.setText(String.valueOf(itemsInX));
		textFieldYItems.setText(String.valueOf(itemsInY));
		textFieldQParameter.setText(String.valueOf(qParameter));
		textFieldPParameter.setText(String.valueOf(pParameter));
		textFieldRParameter.setText(String.valueOf(rParameter));
		
				
		textFieldXItems.setInputVerifier(new IntVerifier());		
		textFieldYItems.setInputVerifier(new IntVerifier());
		
		textFieldQParameter.setInputVerifier(new IntVerifier());
		textFieldPParameter.setInputVerifier(new IntVerifier());
		textFieldRParameter.setInputVerifier(new DoubleVerifier());
				
				
		buttonOK.addActionListener(new ActionListener() 
		{
			@Override
		    public void actionPerformed(ActionEvent e) {
				SettingsSmallWorldEditor.this.setVisible(false);
		    }				
		});
		
		
	}

	public class Result{
		public int xItems;
		public int yItems; 
		public int qParam; 
		public int pParam;
		public double rParam;
	}
	
	public Result GetResult()
	{
		Result result = new Result();
		
		result.xItems =  new Integer(textFieldXItems.getText());
		result.yItems =  new Integer(textFieldYItems.getText());
		result.qParam =  new Integer(textFieldQParameter.getText());
		result.pParam =  new Integer(textFieldPParameter.getText());
		result.rParam =  new Double(textFieldRParameter.getText());
	
		return result;
	}
}
