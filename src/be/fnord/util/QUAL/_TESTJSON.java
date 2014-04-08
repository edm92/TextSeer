package be.fnord.util.QUAL;

import com.google.gson.Gson;

public class _TESTJSON {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 
		JSONEFFECT js = new JSONEFFECT();
		Gson gson = new Gson();
		String json = gson.toJson(js);  
		
		System.out.println("json = " + json);
		
		JSONEFFECT obj2 = gson.fromJson(json, JSONEFFECT.class);   

	}

}
