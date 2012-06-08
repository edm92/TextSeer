import java.util.HashSet;

import config.Settings;
import au.edu.dsl.dlab.processtools.Edge;
import au.edu.dsl.dlab.processtools.Effect;
import au.edu.dsl.dlab.processtools.Vertex;
import au.edu.dsl.dlab.processtools.parser.ModelLoader;
import au.edu.dsl.dlab.processtools.parser.bpmn.visualize;
import au.edu.dsl.dlab.processtools.scenario.ComputeScenario;


public class ModelLoadingExample {

	
	/**
	 * To load models, place into models directory (configurable in file 'base.properties') 
	 * See readme for details on model reading implementation
	 */
	
	public static void main(String [] args){
		ModelLoader myLoader = new ModelLoader();
		for(String file : myLoader.getModelIDs()){
			System.out.println("Loaded file: " + file);
			HashSet<Effect> results = ComputeScenario.makeCleanSave(myLoader.getModel(file), myLoader.getKB(file));
			for(Effect tst : results){
				System.out.println("Effect Scenario:" + tst);
			}
			
			
			System.out.println("##############Showing Paths ###################");
			myLoader.getModel(file).printPaths();		// Requesting graph from class as an example - you can use either way.
			
			if(Settings.visualEnabled ){
				visualize<Vertex,Edge> myViewer = new visualize<Vertex,Edge>();
				myViewer.showModel(myLoader.getModel(file));
			}else{
				//System.err.println("Not visual");
			}
		}
		
	}
}
