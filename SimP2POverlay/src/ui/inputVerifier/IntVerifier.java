package ui.inputVerifier;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

import ui.SettingsGridEditor;

public class IntVerifier extends InputVerifier {
  

	@Override
    public boolean verify(JComponent input) {
        String text = ((JTextField) input).getText();
        try {
        	Integer value = new Integer(text);
            return value < 3000 & value > 0; 
        } catch (NumberFormatException e) {
            return false;
        }
    }
}