package Co_Evolution_Manager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;

public class chooseStrategy {
	
	static String strategy;	
	
	public static void setStrategy(String s){
		strategy = s;
	}
	
	public static void applyStrategy (){		
		switch (strategy)
		{
		case "syncsourceNignorelocal":
			syncsourceNignorelocal();
			break;
		case "syncsourceNkeeplocalBnotconflicts":
			syncsourceNkeeplocalBnotconflicts();
			break;
		case "syncsourceNkeeplocalWresolvedconflicts": {
			Conflict_Handler.functionforPredicate.select();
			syncsourceNkeeplocalWresolvedconflicts();	
			break; }
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
		
		deleteTriples (main.initialTarget, main.sourceDeletionsChangeset, main.initialTarget);
		
		String [] inputfilename = {main.initialTarget, main.sourceAdditionsChangeset};
		writeTriples (main.initialTarget, main.newTarget);			
		writeTriples (main.sourceAdditionsChangeset, main.newTarget);		
		
	}
	
	//Ti+1 = delta (Si) + delta (Ti) + Ti - X
	public static void syncsourceNkeeplocalBnotconflicts(){
		
		conflictsFinder.identifyConflicts(false);
		
	}
	
	//Ti+1 = delta (Si) + delta (Ti) + Ti - X + NGT + ERT
	public static void syncsourceNkeeplocalWresolvedconflicts(){
		
		conflictsFinder.identifyConflicts(true);
		
	}
	
	//Ti+1 = delta (Ti) + Ti
	public static void nsyncsourceBkeeplocal(){
		
		deleteTriples (main.initialTarget, main.targetDeletionsChangeset, main.initialTarget);
		
		String [] inputfilename = {main.initialTarget, main.targetAdditionsChangeset};
		writeTriples (main.initialTarget, main.newTarget);			
		writeTriples (main.targetAdditionsChangeset, main.newTarget);		
	}
	
	//Ti+1 = Ti
	public static void nsyncsourceNignorelocal(){
		
		String [] inputfilename = {main.initialTarget};
		writeTriples (main.initialTarget, main.newTarget);	
		
	}
	
	// delete the triples for final output
	public static void deleteTriples (String initialtarget, String targetDeletionsChangeset, String outputfilename){
		
		Model imodel = //RDFDataMgr.loadModel(initialtarget);
				FileManager.get().loadModel(initialtarget);		
		Model tmodel = //RDFDataMgr.loadModel(targetDeletionsChangeset);
				FileManager.get().loadModel(targetDeletionsChangeset);		
		
		StmtIterator iter = tmodel.listStatements();
		
		while (iter.hasNext()) {
		    Statement stmt      = iter.nextStatement();  // get next statement 
		    imodel.getGraph().delete(stmt.asTriple());	// Delete the triples of target from initial		    					   
		}
		
		try {	
			imodel.write(new FileOutputStream(outputfilename), main.fileSyntax);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		imodel.close();
		tmodel.close();
	}
	
	// write in final output file
	public static void writeTriples(String inputfilename, String outputfilename) {
		//Model model =  ModelFactory.createDefaultModel();;
		//for (String input : inputfilename) {		
			//RDFDataMgr.loadModel(input);
		Model model = //RDFDataMgr.loadModel(inputfilename);
					FileManager.get().loadModel(inputfilename);
		try {				
			model.write(new FileOutputStream(outputfilename, true), main.fileSyntax);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		model.close();
		//}
}
	
	public String getStrategy(){
		return strategy;
	}
}
