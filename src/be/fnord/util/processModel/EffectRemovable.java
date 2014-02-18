package be.fnord.util.processModel;

import be.fnord.util.logic.ClassicalLogicS;
import com.merriampark.Gilleland.CombinationGenerator;
import orbital.logic.imp.Formula;
import orbital.logic.imp.Interpretation;
import orbital.logic.imp.InterpretationBase;
import orbital.logic.sign.Signature;
import orbital.logic.sign.Symbol;
import orbital.logic.sign.SymbolBase;
import orbital.logic.sign.type.Types;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.*;

/**
 * The following class using orbital to store and process effects using propositional logic.
 * I have tried to keep classes here transient for future distributed processing.
 *
 * @author edm92
 * @-deprecated This class has been moved to be.fnord.util.logic
 */
public class EffectRemovable implements Serializable {
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

    public String getFormula() {
        if (formulaText == null || formulaText.compareTo("") == 0) formulaText = "eeee";
        return formulaText;
    }

    ;

    public void setFormula(String newFormula) {
        if (newFormula == null) formulaText = "eeee";
        else formulaText = newFormula;
        try {
            formula = (Formula) logic.createExpression(formulaText);
            sigma = logic.scanSignature(formulaText);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    ;


    public EffectRemovable() {
        this("");
    }

    public EffectRemovable(String formula) {
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
    public boolean eval(EffectRemovable vs) {
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
    public boolean eval(EffectRemovable vs, String KB) {
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
        if (getFormula().length() > 0) return "Effect:" + getFormula();
        else return "";
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
    public boolean entails(EffectRemovable target) {
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


}
