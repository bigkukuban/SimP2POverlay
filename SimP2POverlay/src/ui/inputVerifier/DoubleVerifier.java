package ui.inputVerifier;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

public class DoubleVerifier extends InputVerifier {
	  

	@Override
    public boolean verify(JComponent input) {
        String text = ((JTextField) input).getText();
        try {
        	Double value = new Double(text);
            return value > 0; 
        } catch (NumberFormatException e) {
            return false;
        }
    }
}