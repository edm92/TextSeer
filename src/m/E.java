package m;

public class E {
	public boolean writeConsole(String msg){
		return std.calls.display(msg);
	}

	public boolean writeGUI(String msg){
		return std.calls.showResult(msg);
	}
}
