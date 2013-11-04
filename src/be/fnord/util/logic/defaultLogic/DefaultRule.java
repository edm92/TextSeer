package be.fnord.util.logic.defaultLogic;


// Default rule consists of prerequisite, justification, consequence 

public class DefaultRule {

	
	private String prerequisite = a.e.EMPTY_FORMULA;
	private String justificatoin = a.e.EMPTY_FORMULA;
	private String consequence = a.e.EMPTY_FORMULA; 
	
	
	public String getPrerequisite() {
		return prerequisite;
	}
	public void setPrerequisite(String prerequisite) {
		this.prerequisite = prerequisite;
	}
	public String getJustificatoin() {
		return justificatoin;
	}
	public void setJustificatoin(String justificatoin) {
		this.justificatoin = justificatoin;
	}
	public String getConsequence() {
		return consequence;
	}
	public void setConsequence(String consequence) {
		this.consequence = consequence;
	}
	
	public String toString(){
		String _results = "";
		_results = "[" + prerequisite + ":" + justificatoin + "->" + consequence + "]";
		return _results;
	}
	
	
}
