package m;

public class E {
	public static boolean writeConsole(String msg){
		return std.calls.display(msg);
	}

	public static boolean writeGUI(String msg){
		return std.calls.showResult(msg);
	}
}
