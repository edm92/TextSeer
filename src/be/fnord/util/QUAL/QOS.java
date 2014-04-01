package be.fnord.util.QUAL;

/**
 * Created by edm92 on 25/03/2014.
 */
public class Qos {
	public String COST;
	public String TIME;
	public String SKILL;
	
	public Qos(){
		COST = "";
		TIME = "";
		SKILL = "";
	}
	
	public String getCOST() {
		return COST;
	}
	public void setCOST(String cOST) {
		COST = cOST;
	}
	public String getTIME() {
		return TIME;
	}
	public void setTIME(String tIME) {
		TIME = tIME;
	}
	public String getSKILL() {
		return SKILL;
	}
	public void setSKILL(String sKILL) {
		SKILL = sKILL;
	}
	
}
