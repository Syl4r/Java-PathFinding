package run;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class Main 
{
	public static void main(String[] args) 
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			        if ("Nimbus".equals(info.getName())) {
			            UIManager.setLookAndFeel(info.getClassName());
			            break;
			        }
			    }
		}
		catch (Exception e){}
		@SuppressWarnings("unused")
		Gui g = new Gui("A* Path finding demo", 800, 600, true);
	}
}
