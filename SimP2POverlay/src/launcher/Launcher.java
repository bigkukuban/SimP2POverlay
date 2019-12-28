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
					AppWindowActions appActions = new AppWindowActions();
					
					OpenGLView oglView = new OpenGLView(appActions); 
					appActions._openGlView = oglView;
					
					ApplicationWindow appWindow = new ApplicationWindow(oglView._glcanvas, appActions);
					appActions._applicationWindow = appWindow;
					appWindow.frame.setVisible(true);
					
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
		
	}

}
