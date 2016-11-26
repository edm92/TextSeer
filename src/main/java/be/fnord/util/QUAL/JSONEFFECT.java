package be.fnord.util.QUAL;

import java.util.LinkedHashSet;

import com.google.gson.Gson;

public class JSONEFFECT {
	public String[] CONSTRAINT;
	public String[] EFFECT;

	public String[] GOAL;;

	public String Name = "";
	public Qos QOS = new Qos();

	public String[] RESOURCE;

	public String Type = "";

	
	
	public JSONEFFECT(String _incoming){
		Gson gson = new Gson();
		String json = gson.toJson(_incoming);
		if(_incoming.compareTo(a.e.EMPTY_EFFECT) == 0) { empty(); return; };
//		a.e.err(_incoming);
		
		JSONEFFECT e = gson.fromJson(_incoming, JSONEFFECT.class);
		this.CONSTRAINT = e.CONSTRAINT;
		this.EFFECT = e.EFFECT;
		this.GOAL = e.GOAL;
		this.Name = e.Name;
		this.QOS = e.QOS;
		this.Type = e.Type;
		
			
	}
	
	public JSONEFFECT() {
		empty();
	}
	public void empty(){
		Name = "";
		Type = "";
		QOS = new Qos();
		EFFECT = new String[] {};
		CONSTRAINT = new String[] {};
		GOAL = new String[] {};
		RESOURCE = new String[] {};
	}

	public String[] _cpStrArr(String[] input) {
		String[] _result = new String[EFFECT.length];
		int i = 0;
		for (String s : EFFECT) {
			_result[i++] = s;
		}

		return _result;

	}

	// Return a copy of this effect
	public JSONEFFECT copy() {
		JSONEFFECT _result = new JSONEFFECT();

		_result.Name = Name;
		_result.Type = Type;
		_result.QOS = QOS.copy();
		_result.EFFECT = _cpStrArr(EFFECT);
		_result.CONSTRAINT = _cpStrArr(CONSTRAINT);
		_result.GOAL = _cpStrArr(GOAL);
		_result.RESOURCE = _cpStrArr(RESOURCE);

		return _result;
	}

	public String getEffect() {
		String _result = "";
		if (EFFECT != null && EFFECT.length > 0) {
			for (String s : EFFECT) {
				if (s.length() > 0) {
					_result += "( " + s + " ) & ";
				}
			}
		}
		if (_result.length() > 3) {
			_result = _result.substring(0, _result.length() - 3);
		}

		return _result;
	}

	public boolean isEmpty() {
		if (Name.length() > 0 && Type.length() > 0) {
			return false;
		}
		
		return QOS.isEmpty();
	}

	// Accumulate
	public LinkedHashSet<JSONEFFECT> pairwise_acc(JSONEFFECT source,
			JSONEFFECT target, String KB) {
		JSONEFFECT _result = target.copy();

		// Leave Name and TYPE
		_result.QOS = QOS.pairwise_acc(source.QOS, target.QOS, source, target);

		// Copy the final effect
		LinkedHashSet<JSONEFFECT> rs = new LinkedHashSet<JSONEFFECT>();
		rs.add(_result);
		return rs;
	}

	public String toDSTring() {
		String _result = "";
		_result = "QOS:" + QOS + "; EFFECT:";
		for (String s : EFFECT) {
			_result += s + ",";
		}
		if (EFFECT.length > 0) {
			_result = _result.substring(0, _result.length() - 1);
		}
		_result += ";\n " + a.e.dent() + "CONSTRAINT: ";
		for (String s : CONSTRAINT) {
			_result += s + ",";
		}
		if (CONSTRAINT.length > 0) {
			_result = _result.substring(0, _result.length() - 1);
		}
		_result += ";\n" + a.e.dent() + "GOAL: ";
		for (String s : GOAL) {
			_result += s + ",";
		}
		if (GOAL.length > 0) {
			_result = _result.substring(0, _result.length() - 1);
		}
		_result += ";\n" + a.e.dent() + "RESOURCE: ";
		for (String s : GOAL) {
			_result += s + ",";
		}
		if (GOAL.length > 0) {
			_result = _result.substring(0, _result.length() - 1);
		}
		_result += ";";
		return _result;
	}

	@Override
	public String toString() {
		String _result = "";
		_result += "(" + Name + " " + Type + ")";
		_result = "QOS:" + QOS + "; EFFECT:";
		for (String s : EFFECT) {
			_result += s + ",";
		}
		if (EFFECT.length > 0) {
			_result = _result.substring(0, _result.length() - 1);
		}
		_result += "; CONSTRAINT: ";
		for (String s : CONSTRAINT) {
			_result += s + ",";
		}
		if (CONSTRAINT.length > 0) {
			_result = _result.substring(0, _result.length() - 1);
		}
		_result += "; GOAL: ";
		for (String s : GOAL) {
			_result += s + ",";
		}
		if (GOAL.length > 0) {
			_result = _result.substring(0, _result.length() - 1);
		}
		_result += "; RESOURCE: ";
		for (String s : GOAL) {
			_result += s + ",";
		}
		if (GOAL.length > 0) {
			_result = _result.substring(0, _result.length() - 1);
		}
		_result += ";";
		return _result;
	}

}
