package Conflict_Finder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

import Co_Evolution_Manager.configure;

public class conflicts_Finder {
	
	static boolean resolve;
		
	public static void identifyConflicts(boolean r){
		
		resolve = r;
		
		source_Delta.apply(resolve);		
		applyDelTarget();								//Step 3
		applyAddTarget();								//Step 4
		applyInitialTarget();							//Step 5
	}	

	/*Apply rest of the changes directly*/
	
	public static void applyDelTarget(){
		
		Model tmodel = FileManager.get().loadModel(configure.targetDeletionsChangeset, configure.fileSyntax);
		Model itmodel = FileManager.get().loadModel(configure.initialTarget, configure.fileSyntax);			
		List<Triple> triplestoDelete = new ArrayList<Triple>();
			
		StmtIterator iter = tmodel.listStatements();
		
		while (iter.hasNext()) {
		    Statement stmt = iter.nextStatement();  // get next statement		 
		    triplestoDelete.add(stmt.asTriple());
		}
				
		for (Triple t : triplestoDelete) 	    
		    itmodel.getGraph().delete(t);
		
		try {				
			itmodel.write(new FileOutputStream(configure.initialTarget), configure.fileSyntax);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		tmodel.close();	
		itmodel.close();	
		
	}
	
	/////////////////////////////////////////////////////Step4	
	public static void applyAddTarget(){
		
		Model tmodel = FileManager.get().loadModel(configure.targetAdditionsChangeset, configure.fileSyntax);
		Model itmodel = FileManager.get().loadModel(configure.initialTarget, configure.fileSyntax);			
		List<Triple> triplestoAdd = new ArrayList<Triple>();
			
		StmtIterator iter = tmodel.listStatements();
		
		while (iter.hasNext()) {
		    Statement stmt = iter.nextStatement();  // get next statement		 
		    triplestoAdd.add(stmt.asTriple());
		}
				
		for (Triple t : triplestoAdd) 		    
		    itmodel.getGraph().add(t);

		try {				
			itmodel.write(new FileOutputStream(configure.initialTarget), configure.fileSyntax);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		tmodel.close();	
		itmodel.close();	
		
	}

	/////////////////////////////////////////////////////Step5
	public static void applyInitialTarget(){
		
		Model ntmodel = FileManager.get().loadModel(configure.newTarget, configure.fileSyntax);
		Model itmodel = FileManager.get().loadModel(configure.initialTarget, configure.fileSyntax);			
			
		StmtIterator iter = itmodel.listStatements();
		
		while (iter.hasNext()) {
		    Statement stmt = iter.nextStatement();  // get next statement		 
		    ntmodel.getGraph().add(stmt.asTriple());
		}
		
		try {				
			ntmodel.write(new FileOutputStream(configure.newTarget), configure.fileSyntax);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		ntmodel.close();	
		itmodel.close();	
		
	}

	/*  		print triples				 */
	public static void printTriple (String filename, Resource subject, Property predicate, RDFNode object){
		
	    System.out.println("Triple in " + filename);
	    System.out.print(subject.toString() + " " + predicate.toString() + " ");
	    if (object instanceof Resource) 
	       System.out.print(object.toString() + " .");
	    else // object is a literal
	        System.out.print(" \"" + object.toString() + "\"" + " .");
	    
	}
}
