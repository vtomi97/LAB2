package hu.bme.mit.yakindu.analysis.workhere;

import org.eclipse.emf.common.util.TreeIterator;

import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;

import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;
import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	static List<EventDefinition> events = new ArrayList<EventDefinition>();
	static List<VariableDefinition> variables = new ArrayList<VariableDefinition>();
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		EObject root = manager.loadModel("model_input/example.sct");
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof EventDefinition) {
				EventDefinition ed =(EventDefinition) content;
				events.add(ed);
			}
			if(content instanceof VariableDefinition) {
				VariableDefinition vd = (VariableDefinition) content;
				variables.add(vd);
			}
		}
		String content = model2gml.transform(root);
		manager.saveFile("model_output/graph.gml", content);
		print();
	}
	
	public static void print() {
		System.out.println("package hu.bme.mit.yakindu.analysis.workhere;\nimport java.io.IOException;\nimport hu.bme.mit.yakindu.analysis.RuntimeService;\n"
				+ "import hu.bme.mit.yakindu.analysis.TimerService;\nimport hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;\n"
				+ "import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;\nimport java.util.Scanner;");
		System.out.println("public class RunStatechart {\npublic static void main(String[] args) throws IOException {\n"
				+ "ExampleStatemachine s = new ExampleStatemachine();\ns.setTimer(new TimerService());\n"
				+ "RuntimeService.getInstance().registerStatemachine(s, 200);\ns.init();\ns.enter();\nBoolean run = true;\nScanner sc = new Scanner(System.in);"
				+ "while(run) {\nString input = sc.nextLine();\nswitch(input) {");
		for(EventDefinition e : events) {
			String cap = e.getName().substring(0,1).toUpperCase() + e.getName().substring(1);
			System.out.println("case \"" + e.getName() + "\": s.raise" + cap + "(); break;");
		}
		System.out.println("case \"exit\": run = false; break;\n}\ns.runCycle();\nprint(s);\n}\nsc.close();\nSystem.exit(0);\n}");
		System.out.println("public static void print(IExampleStatemachine s) {");
		for(VariableDefinition v : variables) {
			String cap = v.getName().substring(0,1).toUpperCase() + v.getName().substring(1);
			System.out.println("System.out.println(\"W = \" + s.getSCInterface().get" + cap);
		}
		System.out.println("}\n}");
	}
	
	/*public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();

		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof State) {
				//
			}
			if(content instanceof State) {
				State state = (State) content;
				for(int i = 0; i < state.getOutgoingTransitions().size() ; i++) {
					System.out.println(state.getName() + " -> " + state.getOutgoingTransitions().get(i).getTarget().getName());
				}
				if(state.getOutgoingTransitions().size() == 0) {
					System.out.println(state.getName());
				}
				if(state.getName() == "") {
					System.out.println(state.getSpecification());
				}
			}
		}
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}*/
}
