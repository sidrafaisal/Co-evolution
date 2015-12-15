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

	public static boolean resolve;
	public static long identificationTime = 0;
	public static long resolutionTime = 0;
	
	public static void identifyConflicts(boolean r){

		resolve = r;

		source_Delta.apply();	
		
		long startTime = System.currentTimeMillis();
		
		applyDelTarget();								//Step 3
		applyAddTarget();								//Step 4
		applyInitialTarget();							//Step 5

		long endTime   = System.currentTimeMillis();
		identificationTime = identificationTime + source_Delta.identificationTime + (endTime - startTime);		
		source_Delta.identificationTime = 0;
		
		resolutionTime = resolutionTime + source_Delta.resolutionTime ;
		source_Delta.resolutionTime = 0;
	}	

	/*Apply rest of the changes directly*/

	public static void applyDelTarget() {
		if (configure.initialTarget!=null && configure.targetDeletionsChangeset!=null)	{
			try {
				Model itmodel = FileManager.get().loadModel(configure.initialTarget, configure.fileSyntax);			

				Model tmodel = FileManager.get().loadModel(configure.targetDeletionsChangeset, configure.fileSyntax);

				List<Triple> triplestoDelete = new ArrayList<Triple>();

				StmtIterator iter = tmodel.listStatements();

				while (iter.hasNext()) {
					Statement stmt = iter.nextStatement();  // get next statement		 
					triplestoDelete.add(stmt.asTriple());
				}

				for (Triple t : triplestoDelete) 	    
					itmodel.getGraph().delete(t);
				tmodel.close();	

				itmodel.write(new FileOutputStream(configure.initialTarget), configure.fileSyntax);		
				itmodel.close();	
			} catch (FileNotFoundException | org.apache.jena.riot.RiotException e) {
				System.out.println(""+e);
				e.printStackTrace();
			}
		}
	}

	/////////////////////////////////////////////////////Step4	
	public static void applyAddTarget() {
		if (configure.targetAdditionsChangeset!=null) {
			try	{
				Model tmodel = FileManager.get().loadModel(configure.targetAdditionsChangeset, configure.fileSyntax);

				Model ntmodel = FileManager.get().loadModel(configure.newTarget, configure.fileSyntax);			

				List<Triple> triplestoAdd = new ArrayList<Triple>();

				StmtIterator iter = tmodel.listStatements();

				while (iter.hasNext()) {
					Statement stmt = iter.nextStatement();  // get next statement		 
					triplestoAdd.add(stmt.asTriple());
				}

				for (Triple t : triplestoAdd) 		    
					ntmodel.getGraph().add(t);

				ntmodel.write(new FileOutputStream(configure.newTarget), configure.fileSyntax);

				tmodel.close();	
				ntmodel.close();	
			} catch (FileNotFoundException | org.apache.jena.riot.RiotException e) {
				System.out.println(""+e);
				e.printStackTrace();
			}
		}
	}

	/////////////////////////////////////////////////////Step5
	public static void applyInitialTarget() {
		if (configure.initialTarget!=null) {
			try {
				Model ntmodel = FileManager.get().loadModel(configure.newTarget, configure.fileSyntax);
				Model itmodel = FileManager.get().loadModel(configure.initialTarget, configure.fileSyntax);			

				StmtIterator iter = itmodel.listStatements();

				while (iter.hasNext()) {
					Statement stmt = iter.nextStatement();  // get next statement		 
					ntmodel.getGraph().add(stmt.asTriple());
				}

				ntmodel.write(new FileOutputStream(configure.newTarget), configure.fileSyntax);

				ntmodel.close();	
				itmodel.close();	
			} catch (FileNotFoundException | org.apache.jena.riot.RiotException e) {
				System.out.println(""+e);
				e.printStackTrace();
			}
		}
	}

	/*  		print triples				 */
	public static void printTriple (String filename, Resource subject, Property predicate, RDFNode object) {

		System.out.println("Triple in " + filename);
		System.out.print(subject.toString() + " " + predicate.toString() + " ");
		if (object instanceof Resource) 
			System.out.print(object.toString() + " .");
		else // object is a literal
			System.out.print(" \"" + object.toString() + "\"" + " .");

	}
}
