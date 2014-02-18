package be.fnord.util.logic;

import a.e;
import com.merriampark.Gilleland.CombinationGenerator;
import orbital.logic.imp.Formula;
import orbital.logic.imp.Interpretation;
import orbital.logic.imp.InterpretationBase;
import orbital.logic.imp.Logic;
import orbital.logic.sign.Signature;
import orbital.logic.sign.Symbol;
import orbital.logic.sign.SymbolBase;
import orbital.logic.sign.type.Types;
import orbital.moon.logic.resolution.ClausalSet;
import orbital.moon.logic.resolution.DefaultClausalFactory;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.*;

/**
 * The following class using orbital to store and process effects using propositional logic.
 * I have tried to keep classes here transient for future distributed processing.
 *
 * @author edm92
 */
public class WFF implements Serializable {
    private static final long serialVersionUID = 1L;
    protected transient ClassicalLogicS logic;
    private transient Formula formula;
    private transient Formula formula2;
    private transient Signature sigma;
    private String formulaText = "";
    private transient UUID ID;

    public String getID() {
        return ID.toString();
    }

    ;
    protected transient static Logger logger = Logger.getLogger("EffectFunction");
    public int clauses = 0;

    public boolean isEmpty() {
        if (formulaText == null || formulaText.compareTo("") == 0 || formulaText.compareTo(a.e.EMPTY_EFFECT) == 0)
            return true;
        return false;
    }

    public String getFormula() {
        if (formulaText == null || formulaText.compareTo("") == 0) formulaText = a.e.EMPTY_EFFECT;
        return formulaText;
    }

    ;

    public void setFormula(String newFormula) {
        if (newFormula == null) formulaText = e.EMPTY_FORMULA;
        else formulaText = newFormula;
        try {
            formula = (Formula) logic.createExpression(formulaText);
            sigma = logic.scanSignature(formulaText);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    ;


    public WFF() {
        this("");
    }

    public WFF(String formula) {
        super();
        if (formula != null)
            formulaText = formula;
        logic = new ClassicalLogicS();
        ID = UUID.randomUUID();
    }

    /**
     * Evaluate if effect is satisfiable
     *
     * @return
     */
    public boolean eval() {
        return eval("");
    }

    /**
     * Evaluate if effect is satisfiable when unioned with the input effect
     *
     * @param vs
     * @return
     */
    public boolean eval(WFF vs) {
        return eval(vs.getFormula());
    }

    /**
     * Evaluate if effect is satisfiable given an input formula (as a string)
     *
     * @param vs
     * @return
     */
    protected boolean eval(String vs) {
        return eval(vs, "");
    }

    /**
     * Evaluate if union of all inputs and this effect formula are satisfiable.
     *
     * @param vs input formula string
     * @param KB knowledge base string
     * @return
     */
    public boolean eval(WFF vs, String KB) {
        return eval(vs.getFormula(), KB);
    }

    ;

    /**
     * Evaluate if union of all inputs and this effect formula are satisfiable.
     *
     * @param vs input formula string
     * @param KB knowledge base string
     * @return
     */
    protected boolean eval(String vs, String KB) {
        // Make interpretation:
        vs = getEffect(vs, KB);
        if (vs.length() == 0 || vs.compareTo("") == 0)
            return true;
        if (KB.length() > 0) vs = "( " + KB + " ) & ( " + vs + " )";
        return issat(vs);

    }

    public String getEffect(String vs, String KB) {
        vs = vs.trim();
        if (vs.compareTo("") == 0 || vs.length() == 0) vs = getFormula();
        else {
            if (!(getFormula().compareTo("") == 0 || getFormula().length() == 0))
                vs = "(" + getFormula() + ") & (" + vs + ")";
        }
        return vs;
    }

    public boolean isConsistent() {
        return issat();
    }

    public boolean isConsistent(String _with) {
        return issat(getFormula() + " " + a.e.AND + " " + _with);
    }

    public boolean issat() {
        return issat(getFormula());
//    	try{
//    		logic = new ClassicalLogicS();
//	    	sigma = logic.scanSignature(getFormula());
//	        formula = (Formula) logic.createExpression(getFormula());
//	        
//	        Set<String> symbols = new HashSet<String>(); 
//	        
//	    	for (Iterator<?> i = sigma.iterator(); i.hasNext(); ) {
//	    		Symbol o = (Symbol) i.next();
//	            
//	    		symbols.add(o.toString());
//	    	}
//	    	System.err.println("Setting " + symbols);
//	    	if(computeAssignments(symbols)) return true;
//	    	
//    	}catch(Exception e){
//    		logger.error("Error with effects eval():" + e);
//    		e.printStackTrace();
//    		
//    	}
//    	return false;
    }

    public boolean issat(String vs) {
        try {
            logic = new ClassicalLogicS();
            sigma = logic.scanSignature(vs);
            formula = (Formula) logic.createExpression(vs);

            Set<String> symbols = new HashSet<String>();

            for (Iterator<?> i = sigma.iterator(); i.hasNext(); ) {
                Symbol o = (Symbol) i.next();

                symbols.add(o.toString());
            }
            if (computeAssignments(symbols)) return true;

        } catch (Exception e) {
            logger.error("Error with effects eval():" + e);
            e.printStackTrace();

        }
        return false;
    }

    public String toString() {
        String _result = "";
        if (getFormula().length() > 0) _result = "" + getFormula();
        else _result = "";
        _result = _result.replace("(" + a.e.EMPTY_EFFECT + ") & ", "");
        _result = _result.replace("& (" + a.e.EMPTY_EFFECT + ")", "");
        _result = _result.replace("" + a.e.EMPTY_EFFECT + " & ", "");
        _result = _result.replace("&" + a.e.EMPTY_EFFECT + "", "");

        return _result;
    }

    public boolean computeAssignments(Set<String> symbols) {
        String[] elements = new String[symbols.size() * 2];
        int k = 0;
        int j = symbols.size();
        for (String s : symbols) {
            elements[k] = s;
            elements[k + j] = "~" + s;
            k++;
        }
        int[] indices;
        CombinationGenerator x = new CombinationGenerator(elements.length, symbols.size());
        StringBuffer combination;
        while (x.hasMore()) {
            combination = new StringBuffer();
            Set<String> _sym = new HashSet<String>();
            int eleCount = 0;
            indices = x.getNext();
            for (int i = 0; i < indices.length; i++) {
                if (!combination.toString().contains(elements[indices[i]].replace("~", ""))) {
                    combination.append(elements[indices[i]] + " ");
                    _sym.add(elements[indices[i]]);
                    eleCount++;
                }
            }
            if (eleCount == symbols.size()) {
                boolean result = issat(_sym);
                if (result)
                    return true;
            }

        }
        return false;
    }

    public boolean issat(Set<String> s) {
        //System.err.println("Checking out " + s);
        Map<SymbolBase, Boolean> intermap = new HashMap<SymbolBase, Boolean>();
        for (String _symbolBase : s) {
            // Removed because it was stuffing up worst cases
//    		if(intermap.containsKey(_symbolBase.replace("~", "")))
//    			if(intermap.get(_symbolBase.replace("~", "")) != _symbolBase.contains("~")) return false;
            if (_symbolBase.contains("~"))
                intermap.put(new SymbolBase(_symbolBase.replace("~", ""), Types.TRUTH), Boolean.FALSE);
            else
                intermap.put(new SymbolBase(_symbolBase, Types.TRUTH), Boolean.TRUE);
        }

        Interpretation interpretation = new InterpretationBase(sigma, intermap);
        boolean satisfied = logic.satisfy(interpretation, formula);

        return satisfied;
    }

    /**
     * Check if this effect entails the input effect
     *
     * @param target the entailee
     * @return
     */
    public boolean entails(WFF target) {
        if (!issat() || !target.issat()) return false; // Can't have entailment from non-sat.

        return entails(getFormula(), target.getFormula());
    }

    private boolean entails(String s1, String s2) {
        boolean deduce = false;

        if (s1.length() < 1) return false;
        if (s2.length() < 1) return true;
        try {
            this.sigma = logic.scanSignature(s1);
            formula = (Formula) logic.createExpression(s1);
            this.sigma = logic.scanSignature(s2);
            formula2 = (Formula) logic.createExpression(s2);

            deduce = logic.inference().infer(new Formula[]{formula}, formula2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deduce;
    }

    /**
     * Uhm.... Yes I know what this is supposed to do and no it doesn't do it.
     * Will it be implemented???
     *
     * Uhmmm, aha yeah, sure.... one day?! Someone else is welcome to it if I don't get to it first.
     *
     * edm
     * @return deductive closure
     */
    public String getClosure() {
        try {
            Logic logic = new ClassicalLogicS();
            if (getFormula().length() < 1) return "";
            formula = (Formula) logic.createExpression(getFormula());
            Formula result = ClassicalLogicS.Utilities.conjunctiveForm(formula, true);
            DefaultClausalFactory myFacts = new DefaultClausalFactory();
            ClausalSet myClauses = myFacts.asClausalSet(result);
            Formula f = myClauses.toFormula();
            return f.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
