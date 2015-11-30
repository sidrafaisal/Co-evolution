package Co_Evolution_Manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

import Conflict_Finder.conflicts_Finder;

public class strategy {

	static String strategy;	

	public static void setStrategy(String s){
		strategy = s;
	}

	public static void apply (){		
		switch (strategy)
		{
		case "syncsourceNignorelocal":
			syncsourceNignorelocal();
			break;
		case "syncsourceNkeeplocalBnotconflicts":
			syncsourceNkeeplocalBnotconflicts();
			break;
		case "syncsourceNkeeplocalWresolvedconflicts": 
			syncsourceNkeeplocalWresolvedconflicts();	
			break; 	
		case "nsyncsourceBkeeplocal":
			nsyncsourceBkeeplocal();
			break;
		case "nsyncsourceNignorelocal":
			nsyncsourceNignorelocal();		
			break;
		}		
	}

	//Ti+1 = delta (Si) + Ti	
	public static void syncsourceNignorelocal() {

		deleteTriples (configure.initialTarget, configure.sourceDeletionsChangeset, configure.initialTarget);
		writeTriples (configure.initialTarget, configure.newTarget);			
		writeTriples (configure.sourceAdditionsChangeset, configure.newTarget);		

	}

	//Ti+1 = delta (Si) + delta (Ti) + Ti - X
	public static void syncsourceNkeeplocalBnotconflicts(){

		conflicts_Finder.identifyConflicts(0, false);

	}

	//Ti+1 = delta (Si) + delta (Ti) + Ti - X + NGT + ERT
	public static void syncsourceNkeeplocalWresolvedconflicts(){
		System.out.println("For manual resolution, press 0. For auto resolution, press 1.");	
		String r = Co_Evolution_Manager.main.scanner.nextLine();

		if (r.equals("0")) {
			Conflict_Resolver.manual_Selector.select();
			conflicts_Finder.identifyConflicts(0, true);
		}
		else if (r.equals("1")) {
			File file = new File("auto_FunctionSelector.xml");

			if(!file.exists()) {
				System.out.println("Auto resolution not possible, please select manually.");
				Conflict_Resolver.manual_Selector.select();
				conflicts_Finder.identifyConflicts(0, true);
			}
			else {
				Conflict_Resolver.auto_Selector.select();
				conflicts_Finder.identifyConflicts(1, true);
			}
		}

	}

	//Ti+1 = delta (Ti) + Ti
	public static void nsyncsourceBkeeplocal(){

		deleteTriples (configure.initialTarget, configure.targetDeletionsChangeset, configure.initialTarget);		
		writeTriples (configure.initialTarget, configure.newTarget);			
		writeTriples (configure.targetAdditionsChangeset, configure.newTarget);		

	}

	//Ti+1 = Ti
	public static void nsyncsourceNignorelocal(){

		writeTriples (configure.initialTarget, configure.newTarget);	

	}

	// delete the triples for final output
	public static void deleteTriples (String initialtarget, String targetDeletionsChangeset, String outputfilename){

		Model imodel = FileManager.get().loadModel(initialtarget);		
		Model tmodel = FileManager.get().loadModel(targetDeletionsChangeset);		

		StmtIterator iter = tmodel.listStatements();

		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();  // get next statement 
			imodel.getGraph().delete(stmt.asTriple());	// Delete the triples of target from initial		    					   
		}

		try {	
			imodel.write(new FileOutputStream(outputfilename), configure.fileSyntax);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		imodel.close();
		tmodel.close();
	}

	// write in output file
	public static void writeTriples(String inputfilename, String outputfilename) {
		Model model = FileManager.get().loadModel(inputfilename);
		try {				
			model.write(new FileOutputStream(outputfilename, true), configure.fileSyntax);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		model.close();
	}

	public String getStrategy(){
		return strategy;
	}
}
