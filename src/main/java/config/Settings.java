package config;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class Settings {
	public static enum OS {
		ALT, LIN, MAC, WIN
	}

	public static String currentDir = System.getProperty("user.dir");
	public static boolean sysDEBUG = true;
	public static String myHomeDir = System.getProperty("user.home");
	public static String myUserName = System.getProperty("user.name");
	public static String myRepository = "";
	public static OS myOSsys = OS.ALT;
	public static int currentIndentLvl = 0;
	public static String currentProp = "";
	public static boolean initialized = false;

	public static String l = System.getProperty("line.separator");
	public static boolean LoadFromDB = false;
	public static boolean LoadFromRepo = false;
	protected transient static Logger logger = Logger.getLogger("Settings");
	public static String mainDB = "base.db";
	public static transient TreeMap<String, String> PROP = new TreeMap<String, String>();
	public static String propertiesBase = "base.properties";
	public static boolean RECOMPUTE_SCENARIOS = false;

	public static String s = System.getProperty("file.separator");
	public static String sharredModelKey = "sharedModels";
	public static boolean StoreInDB = false;
	public static String StrategyDB = "";// "models/strategy.sqlite";

	public static String t = "";
	public static String tab = "\t";

	public static boolean visualEnabled = false;

	public static String _toString() {
		String _r = "";
		_r += "File seperator : " + s + l + "Username: " + myUserName + l
				+ "HomeDir:" + myHomeDir + l + "CurrentDir: " + currentDir + l
				+ "System: " + sysString() + l;

		return _r;
	}

	public static void decreaseIndent() {
		currentIndentLvl--;
		getInTabs();
	}

	public static String getInTabs() {
		String tt = "";
		for (int i = 0; i < currentIndentLvl; i++) {
			tt += tab;
		}
		t = tt;
		return tt;
	}

	public static void increaseIndent() {
		currentIndentLvl++;
		getInTabs();
	}

	public static void init() {
		if (initialized) {
			System.err.println("Init done");
			return;
		}
		// System.err.println("Doing init");

		initialized = true;
		t = getInTabs();

		// Get OS if that is important
		if (isWindows()) {
			myOSsys = OS.WIN;
		} else if (isMac()) {
			myOSsys = OS.MAC;
		} else if (isUnix()) {
			myOSsys = OS.LIN;
		} else {
			myOSsys = OS.ALT;
		}

		// Load main properites
		Properties props = new Properties();

		// try retrieve data from file
		try {
			props.load(new FileInputStream(propertiesBase));
			mainDB = props.getProperty("mainDB");
			myRepository = props.getProperty("Repository");
			if (myRepository.contains("$CURRENTDIR$")) {
				myRepository = myRepository.replace("$CURRENTDIR$", currentDir);
			}
			LoadFromRepo = Integer.parseInt(props.getProperty("loadFromRepo")) == 1 ? true
					: false;
			RECOMPUTE_SCENARIOS = Integer.parseInt(props
					.getProperty("recomputeScenarios")) == 1 ? true : false;
			StrategyDB = myRepository + "\\" + props.getProperty("StrategyDB");

			// LoadFromDB = Integer.parseInt(props.getProperty("loadFromDB")) ==
			// 1 ? true : false;
			// StoreInDB = Integer.parseInt(props.getProperty("storeInDB")) == 1
			// ? true : false;
			// LoadFromRepo =
			// Integer.parseInt(props.getProperty("loadFromRepo")) == 1 ? true :
			// false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Fatal Error: Problem loading properties - init():"
					+ e);
			System.exit(1);
		}

		// System.err.println("_repository:" + _repository);

	}

	public static boolean isMac() {

		String os = System.getProperty("os.name").toLowerCase();
		// Mac
		return os.indexOf("mac") >= 0;

	}

	public static boolean isUnix() {

		String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0;

	}

	public static boolean isWindows() {

		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return os.indexOf("win") >= 0;

	}

	public static String makeNewProp() {
		String results = "";
		return results;
	}

	public static String sysString() {
		if (myOSsys == OS.WIN) {
			return "Windows";
		}
		if (myOSsys == OS.MAC) {
			return "Mac";
		}
		if (myOSsys == OS.LIN) {
			return "Linux";
		}
		return "Not known";
	}

}
