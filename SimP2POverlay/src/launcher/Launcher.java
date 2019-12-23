package launcher;

import java.awt.EventQueue;

import ui.AppWindowActions;
import ui.ApplicationWindow;
import ui.OpenGLView;

public class Launcher 
{
	

	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					OpenGLView oglView = new OpenGLView(); 
					
					new ApplicationWindow(oglView._glcanvas, new AppWindowActions(oglView));
					
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
		
	}

}
