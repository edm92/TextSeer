package processBuilding;

// This module requires the xpdlLoader (will be added to git shortly), seperated from main due to dependencies.

import xpdlLoader.OpenModel;


public class LoadExternal {

	public static process loadFile(String filename){
		return OpenModel.readXPDL(filename);
	}
}
