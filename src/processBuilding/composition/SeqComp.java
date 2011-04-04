package processBuilding.composition;

import java.util.List;

import processBuilding.process;

import std.prover.PairwiseChecker;
import textSeer.Model.Effect;
import textSeer.Model.Graph;
import textSeer.Model.Predicate;
import textSeer.Model.Vertex;
import textSeer.Model.functions.ScenarioBuilder;

public class SeqComp {
	
	
	public static ScenarioBuilder SeqCompEff(List<Graph> endEffectScenarios,
			process myProcess2) {
		Graph g = new Graph(myProcess2.structure);
		process newProcess = new process();
		newProcess.structure = g;
		ScenarioBuilder newProcessScenario = new ScenarioBuilder(newProcess.structure);
		g.finalize();
		ScenarioBuilder myProcessBuilder = new ScenarioBuilder(newProcess.structure);
		for(Graph scene: endEffectScenarios){
			for(Vertex v : g.startNodes){
				Effect ceffect = new Effect();
				ceffect.addPredicate(new Predicate(ScenarioBuilder.cummulativeEffect(scene),true));
				v.addEffect(ceffect);
				
				PairwiseChecker scenarioChecker = new PairwiseChecker(g);
				if(scenarioChecker.isConsistent){
					ScenarioBuilder.redoGraph(g);
					myProcessBuilder.processEffects.add(g);				
					myProcess2.endEffectScenarios.add(g);
				}else{
					std.calls.showResult("Inconsistent Scenario: " + ScenarioBuilder.graphString(g));
				}
			}
		}
		return newProcessScenario;
		
	}

}
