package std;

public class type {
	
	/* NODE BASICS */
	
	public enum Node {
	    ACTIVITY, GATEWAY, EVENT 
	}
	
	public enum ActivityType {
	    SUBPROCESS, LOOPING, NORMAL 
	}
	
	public enum GatewayType {
		XOR, AND, OR, XOR_JOIN
	}
	
	public enum EventType {
		START, END, INTERMEDIATE
	}
	
	public static String NodeToString(Node compare){
		String retVal = "";
		switch (compare){
		case ACTIVITY: 
			retVal = "(ACTIVITY)"; break;
		case GATEWAY:
			retVal = "(GATEWAY)"; break;
		case EVENT:
			retVal = "(EVENT)"; break;
		}
		
		return retVal;
	}
	
	public static String SubNodeToString(Node type, Object compare){
		String retVal = "";
		switch (type){
		case ACTIVITY: 
			retVal = "(ACTIVITY)"; 
			switch ((ActivityType)compare){
			case SUBPROCESS: 
				retVal += ".(SUBPROCESS)"; break;
			case LOOPING: 
				retVal += ".(LOOPING)"; break;
			case NORMAL: 
				retVal += ".(NORMAL)"; break;
			}
			break;
		case GATEWAY:
			retVal = "(GATEWAY)"; 
			switch ((GatewayType)compare){
			case XOR: 
				retVal += ".(XOR)"; break;
			case AND: 
				retVal += ".(AND)"; break;
			case OR: 
				retVal += ".(OR)"; break;
			case XOR_JOIN:
				retVal += ".(XOR_JOIN)"; break;
			}
			break;
		case EVENT:
			retVal = "(EVENT)"; 
			switch ((EventType)compare){
			case START: 
				retVal += ".(START)"; break;
			case END: 
				retVal += ".(END)"; break;
			case INTERMEDIATE: 
				retVal += ".(INTERMEDIATE)"; break;
			}
			break;
		}
		
		return retVal;
	}
	
	/* EDGE BASIC */
	
	public enum Edge {
		SEQUENCE, MESSAGE
	}
	
	public static String EdgeToString(Edge compare){
		String retVal = "";
		switch (compare){
		case MESSAGE: 
			retVal = "(Message- - >)"; break;
		case SEQUENCE:
			retVal = "(Sequence--->)"; break;
		}
		
		return retVal;
	}
	
	
	
}


