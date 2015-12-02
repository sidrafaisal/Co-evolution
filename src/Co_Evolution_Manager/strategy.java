package Co_Evolution_Manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

import Conflict_Finder.conflicts_Finder;
import Conflict_Resolver.resolver;

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

		conflicts_Finder.identifyConflicts(false);

	}

	//Ti+1 = delta (Si) + delta (Ti) + Ti - X + NGT + ERT
	public static void syncsourceNkeeplocalWresolvedconflicts(){
		System.out.println("For manual resolution, press 0. For auto resolution, press 1.");	
		String r = Co_Evolution_Manager.main.scanner.nextLine();

		if (r.equals("0")) {
			Conflict_Resolver.manual_Selector.select();
			resolver.manual_selector = true;
			conflicts_Finder.identifyConflicts(true);
		}
		else if (r.equals("1")) {
			File file = new File("auto_FunctionSelector.xml");

			if(!file.exists()) {
				System.out.println("Auto resolution not possible, please select manually.");
				Conflict_Resolver.manual_Selector.select();
				resolver.manual_selector = true;
				conflicts_Finder.identifyConflicts(true);
			}
			else {
				Conflict_Resolver.auto_Selector.select();
				resolver.auto_selector = true;
				conflicts_Finder.identifyConflicts(true);
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

		if (initialtarget!=null) {	
			try {
				Model imodel = FileManager.get().loadModel(initialtarget, configure.fileSyntax);	

				if (!targetDeletionsChangeset.equals(null)) {
					Model tmodel = FileManager.get().loadModel(targetDeletionsChangeset, configure.fileSyntax);		

					StmtIterator iter = tmodel.listStatements();

					while (iter.hasNext()) {
						Statement stmt = iter.nextStatement();  // get next statement 
						imodel.getGraph().delete(stmt.asTriple());	// Delete the triples of target from initial		    					   
					}
					tmodel.close();
				} 

				imodel.write(new FileOutputStream(outputfilename), configure.fileSyntax);

				imodel.close();

			} catch (FileNotFoundException | org.apache.jena.riot.RiotException e) {
				System.out.println(""+e);
				e.printStackTrace();
			}
		} else 
			;
	}

	// write in output file
	public static void writeTriples(String inputfilename, String outputfilename) {
		if (inputfilename!=null)
		{
			try {
				Model model = FileManager.get().loadModel(inputfilename, configure.fileSyntax);			
				model.write(new FileOutputStream(outputfilename, true), configure.fileSyntax);
				model.close();
			} catch (FileNotFoundException | org.apache.jena.riot.RiotException e) {
				System.out.println(""+e);
				e.printStackTrace();
			}}
	}

	public String getStrategy(){
		return strategy;
	}
}
