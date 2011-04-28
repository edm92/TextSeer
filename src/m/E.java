package m;

import javax.swing.JOptionPane;

public class E {
	public static String endl = std.string.endl;
	
	public static boolean writeConsole(String msg){
		return std.calls.display(msg);
	}

	public static boolean writeGUI(String msg){
		return std.calls.showResult(msg);
	}
	
	public static boolean writeMsg(String msg){
		JOptionPane.showMessageDialog(null,
				msg, "TextSeerAlert", JOptionPane.PLAIN_MESSAGE);
		return true;
	}
}
