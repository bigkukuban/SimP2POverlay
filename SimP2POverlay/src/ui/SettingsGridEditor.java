package ui;

import javax.swing.JFrame;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ui.inputVerifier.IntVerifier;
import java.awt.Rectangle;
import java.awt.Dimension;

public class SettingsGridEditor extends JDialog 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textFieldItemsInX;
	private JTextField textFieldItemsInY;
	
	public SettingsGridEditor(Frame parentFrame, int itemsInX, int itemsInY) 
	{
		super(parentFrame,"Grid settings", true);
		setResizable(false);
		setPreferredSize(new Dimension(350, 150));
		setMaximumSize(new Dimension(350, 100));
		setMinimumSize(new Dimension(350, 100));
		getContentPane().setMaximumSize(new Dimension(350, 100));
		getContentPane().setMinimumSize(new Dimension(350, 100));
		getContentPane().setPreferredSize(new Dimension(350, 100));
		getContentPane().setSize(new Dimension(350, 100));
		getContentPane().setBounds(new Rectangle(0, 0, 350, 150));
		setBounds(new Rectangle(0, 0, 350, 150));
		setTitle("Grid Settings");
		getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("OK");
		btnNewButton.setBounds(220, 67, 89, 23);
		getContentPane().add(btnNewButton);
		
		JLabel lblItemsInX = new JLabel("Items in X direction:");
		lblItemsInX.setBounds(10, 11, 156, 14);
		getContentPane().add(lblItemsInX);
		
		JLabel lblItemsInY = new JLabel("Items in Y direction:");
		lblItemsInY.setBounds(10, 36, 140, 14);
		getContentPane().add(lblItemsInY);
		
		textFieldItemsInX = new JTextField();
		textFieldItemsInX.setBounds(198, 11, 111, 20);
		getContentPane().add(textFieldItemsInX);
		textFieldItemsInX.setColumns(10);
		
		
		textFieldItemsInY = new JTextField();
		textFieldItemsInY.setBounds(198, 36, 111, 20);
		getContentPane().add(textFieldItemsInY);
		textFieldItemsInY.setColumns(10);
				
		
		///custom initialization 
		
		textFieldItemsInX.setText(String.valueOf(itemsInX));
		textFieldItemsInY.setText(String.valueOf(itemsInY));
		
		textFieldItemsInX.setInputVerifier(new IntVerifier());
		textFieldItemsInY.setInputVerifier(new IntVerifier());
		
		
		btnNewButton.addActionListener(new ActionListener() 
		{
			@Override
            public void actionPerformed(ActionEvent e) {
            	SettingsGridEditor.this.setVisible(false);
            }				
        });
		
	}
	
	public int[] GetResult()
	{
		int xItems =  new Integer(textFieldItemsInX.getText());
		int yItems =  new Integer(textFieldItemsInX.getText());
		
		return new int[]{xItems,yItems};
	}
	
	
	
}
