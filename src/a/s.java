package a;

import javax.swing.JOptionPane;

public class s {
	public static String endl = std.string.endl;
	
	public static boolean writeConsole(String msg){
		boolean returner = std.calls.display(msg + endl);
		return returner;
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
