package examples;

/**
 * The following class is used in the build file to demonstrate all examples.
 *
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be Apache
 *         License, Version 2.0, Apache License Version 2.0, January 2004
 *         http://www.apache.org/licenses/
 */
public class RunAll {
	public static void main(String[] args) {
		a.e.println("Text Seer Process Tools - Demonstration -");
		a.e.println("For more information please refer to readme.md");
		a.e.println("There is currently an error in the process model loading functions - due to static references");

		AbductiveExample.main(args);
		AccumulationExample.main(args);
		BPMN2ModelLoadingExample.main(args);
		DecisionFreeGraphConversion.main(args);
		DefaultLogicExample.main(args);
		LogicExample.main(args);
		OrderConstrainedPermutationExample.main(args);
		// SemanticTracing.main(args);

		a.e.println("------------------------ \n End of Examples");
	}
}
