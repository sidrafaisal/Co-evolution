package Co_Evolution_Manager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

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
		deleteTriples (main.initialtarget, main.sourceDeletionsChangeset, main.initialtarget);
		
		String [] inputfilename = {main.initialtarget, main.sourceAdditionsChangeset};
		writeTriples (inputfilename, main.newTarget);	
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
		
		deleteTriples (main.initialtarget, main.targetDeletionsChangeset, main.initialtarget);
		
		String [] inputfilename = {main.initialtarget, main.targetAdditionsChangeset};
		writeTriples (inputfilename, main.newTarget);			
	}
	
	//Ti+1 = Ti
	public static void nsyncsourceNignorelocal(){
		
		String [] inputfilename = {main.initialtarget};
		writeTriples (inputfilename, main.newTarget);		
	}
	
	// delete the triples for final output
	public static void deleteTriples (String initialtarget, String targetDeletionsChangeset, String outputfilename){
		Model imodel = FileManager.get().loadModel(initialtarget,"NT");		
		Model tmodel = FileManager.get().loadModel(targetDeletionsChangeset,"NT");		
		
		StmtIterator iter = tmodel.listStatements();
		
		while (iter.hasNext()) {
		    Statement stmt      = iter.nextStatement();  // get next statement
		/*    Resource  subject   = stmt.getSubject();     // get the subject
		    Property  predicate = stmt.getPredicate();   // get the predicate
		    RDFNode   object    = stmt.getObject();      // get the object
		  */  
		    imodel.getGraph().delete(stmt.asTriple());	// Delete the triples of target from initial
		    
					    
			/*ExtendedIterator<Triple> results = imodel.getGraph().find(subject.asNode(), predicate.asNode(), object.asNode()); 
			while (results.hasNext()) {
				Triple t = results.next();
				imodel.getGraph().delete(t);
			}*/
		System.out.println("Total triples after deletion " + imodel.getGraph().size());
		}
		
		try {				
			imodel.write(new FileOutputStream(outputfilename),"NT");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		imodel.close();
		tmodel.close();
	}
	
	// write in final output file
	public static void writeTriples(String [] inputfilename, String outputfilename) {

	//	Model omodel = FileManager.get().loadModel(outputfilename,"NT");
		for (String input : inputfilename) {		
		Model model = FileManager.get().loadModel(input,"NT");
		try {				
			model.write(new FileOutputStream(outputfilename, true),"NT");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		model.close();
		}
	//	omodel.close();
}
	
	public String getStrategy(){
		return strategy;
	}
}
