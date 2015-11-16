package Co_Evolution_Manager;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

public class conflictsFinder {
	
	static boolean resolve;
	
	/*			Identify Conflicts 			*/
	
	public static void identifyConflicts(boolean r){
		
		resolve = r;
		
		conflictPatternFinderforAddSource();
		conflictPatternFinderforDelSource();	
		applyDelTarget();
		applyAddTarget();
		applyInitialTarget();
	}
	
	/////////////////////////////////////////////////////Step1
	/*Find conflicts for source additions changeset: Pick each triple s1,p1,o1 from source additions changeset and
	  check for s1,p1,o2 in target changesets and inital target*/
	
	public static void conflictPatternFinderforAddSource(){
		
		Model omodel = FileManager.get().loadModel(main.newTarget);		
		
		Model model = FileManager.get().loadModel(main.sourceAdditionsChangeset);
		
		StmtIterator iter = model.listStatements();
		
		while (iter.hasNext()) {
			
		    Statement stmt      = iter.nextStatement();  // get next statement
		    Resource  subject   = stmt.getSubject();     // get the subject
		    Property  predicate = stmt.getPredicate();   // get the predicate
		    RDFNode   object    = stmt.getObject();      // get the object
		    Conflict_Handler.functionforPredicate.set(predicate.toString(), "any");
		
		    String functionforPredicate = Conflict_Handler.functionforPredicate.get(predicate.toString()); // temporaily setting from here
		   // System.out.println(functionforPredicate);
		   // printTriple ("main.sourceAdditionsChangeset", subject, predicate, object);

			List<Triple> conflictingTriplesDeletionSource = findCorrespondingTriples(main.sourceDeletionsChangeset, subject, predicate, Node.ANY) ;
			
			List<Triple> conflictingTriplesTarget = findCorrespondingTriples(main.initialtarget, subject, predicate, Node.ANY) ;
			
			List<Triple> conflictingTriplesAdditionTarget = findCorrespondingTriples(main.targetAdditionsChangeset, subject, predicate, Node.ANY) ;
			
			List<Triple> conflictingTriplesDeletionTarget = findCorrespondingTriples(main.targetDeletionsChangeset, subject, predicate, Node.ANY) ;
			
			boolean flag_DS = false, flag_T = false, flag_AT = false, flag_DT = false; 
			
			if(conflictingTriplesDeletionSource.size() > 0)
				flag_DS = true;
			
			if(conflictingTriplesTarget.size() > 0)
				flag_T = true;
			
			if(conflictingTriplesAdditionTarget.size() > 0)
				flag_AT = true;
			
			if(conflictingTriplesDeletionTarget.size() > 0)
				flag_DT = true;
					
			
			
	/*		if ((!flag_DS && !flag_T && !flag_DT && !flag_AT) || (flag_DS && !flag_DT && !flag_AT) || 
					(flag_DS && flag_T && flag_DT && !flag_AT))
				omodel.add(stmt);
			else if ((!flag_DS && !flag_T && !flag_DT && flag_AT) || (flag_DS && !flag_DT && !flag_T && flag_AT) ||
					(flag_DS && flag_T && flag_DT && flag_AT))
			{					// added by source and target
				int conflictingTriplesAdditionsize = conflictingTriplesAdditionTarget.size();
				for (int i = 0; i < conflictingTriplesAdditionsize; i++)
				{
					Triple t = conflictingTriplesAdditionTarget.get(i);
					if(object.toString().equals(t.getObject().toString())){		//same values			
						omodel.add(stmt);
					} else if (resolve) {										//different values
						String [] args = {object.toString(), t.getObject().toString()};
						String rv = Conflict_Handler.resolutionFunctions.apply(functionforPredicate, args);
						
						omodel.getGraph().add(Triple.create(subject.asNode(), predicate.asNode(), NodeFactory.createLiteral(rv)));
						System.out.println("add after resolve...........");			
					}
				}
			}
			
		*/	
			
			
			//////////////// or
			if (!flag_DS && !flag_T && !flag_DT) { 
				if (!flag_AT) 			//added by source	
					omodel.add(stmt);	
				else {					// added by source and target
					int conflictingTriplesAdditionsize = conflictingTriplesAdditionTarget.size();
					for (int i = 0; i < conflictingTriplesAdditionsize; i++)
					{
						Triple t = conflictingTriplesAdditionTarget.get(i);
						if(object.toString().equals(t.getObject().toString())){		//same values			
							omodel.add(stmt);
						} else if (resolve) {														//different values
							String [] args = {object.toString(), t.getObject().toString()};
							String rv = Conflict_Handler.resolutionFunctions.apply(functionforPredicate, args);
							
							omodel.getGraph().add(Triple.create(subject.asNode(), predicate.asNode(), NodeFactory.createLiteral(rv)));
							System.out.println("add after resolve...........");			
						}
					}					
				}
			} else if (flag_DS && !flag_DT){ 		
				if(!flag_AT){					//modified by source
					omodel.add(stmt);							
				} else if (flag_AT && !flag_T) {			//modified by source and added by target
					int conflictingTriplesAdditionsize = conflictingTriplesAdditionTarget.size();		
					for (int i = 0; i < conflictingTriplesAdditionsize; i++)
					{
						Triple t = conflictingTriplesAdditionTarget.get(i);
						if(object.toString().equals(t.getObject().toString())){		//same values			
							omodel.add(stmt);
						} else if (resolve) {										//different values (directly use preference or resolved?)
							String [] args = {object.toString(), t.getObject().toString()};
							String rv = Conflict_Handler.resolutionFunctions.apply(functionforPredicate, args);
							
							omodel.getGraph().add(Triple.create(subject.asNode(), predicate.asNode(), NodeFactory.createLiteral(rv)));
							System.out.println("add after resolve...........");			
						}
					}
				}
			} else if (flag_DS && flag_T && flag_DT) { 
				if(!flag_AT){							//modified by source and deleted by target
					omodel.add(stmt);	//okaY?						
				} else {									//modified by source and target 
					int conflictingTriplesAdditionsize = conflictingTriplesAdditionTarget.size();
					for (int i = 0; i < conflictingTriplesAdditionsize; i++)
					{
						Triple t = conflictingTriplesAdditionTarget.get(i);
						if(object.toString().equals(t.getObject().toString())){		//same values			
							omodel.add(stmt);
						} else if (resolve) {										//different values
							String [] args = {object.toString(), t.getObject().toString()};
							String rv = Conflict_Handler.resolutionFunctions.apply(functionforPredicate, args);

							omodel.getGraph().add(Triple.create(subject.asNode(), predicate.asNode(), NodeFactory.createLiteral(rv)));
							System.out.println("add after resolve...........");		
						}
					}
				}
			} else if (!flag_DS && flag_T && flag_DT) { 
				if(!flag_AT){							//added by source and deleted by target
					;						
				} else {									//added by source and modified by target 
					omodel.getGraph().add(conflictingTriplesAdditionTarget.get(0));
				}
			}	
		} 

		try {				
			omodel.write(new FileOutputStream(main.newTarget),"NT");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		model.close();	
		
	}	
	
	/////////////////////////////////////////////////////Step2
	/*Find conflicts for source deletions changeset: Pick each triple s1,p1,o1 from source deletion changeset 
	and check for s1,p1,o2 in target changesets and initial target*/	
	
	public static void conflictPatternFinderforDelSource(){
		
		Model omodel = FileManager.get().loadModel(main.newTarget);		
		
		Model model = FileManager.get().loadModel(main.sourceDeletionsChangeset);
		
		StmtIterator iter = model.listStatements();
		
		while (iter.hasNext()) {
		    Statement stmt      = iter.nextStatement();  // get next statement
		    Resource  subject   = stmt.getSubject();     // get the subject
		    Property  predicate = stmt.getPredicate();   // get the predicate
		    RDFNode   object    = stmt.getObject();      // get the object
		    
		    printTriple ("main.sourceDeletionsChangeset", subject, predicate, object);

			List<Triple> conflictingTriplesTarget = findCorrespondingTriples(main.initialtarget, subject, predicate, Node.ANY) ;
		
			List<Triple> conflictingTriplesAdditionTarget = findCorrespondingTriples(main.targetAdditionsChangeset, subject, predicate, Node.ANY) ;
			
			List<Triple> conflictingTriplesDeletionTarget = findCorrespondingTriples(main.targetDeletionsChangeset, subject, predicate, Node.ANY) ;
			
			boolean flag_T = false, flag_AT = false, flag_DT = false; 
			
			if(conflictingTriplesTarget.size() > 0)
				flag_T = true;
			
			if(conflictingTriplesAdditionTarget.size() > 0)
				flag_AT = true;
			
			if(conflictingTriplesDeletionTarget.size() > 0)
				flag_DT = true;
					
			if (!flag_AT && flag_T && !flag_DT)  	//deleted by source
				; 									//already deleted from T during processing
			else if (!flag_AT && flag_T && flag_DT)  //deleted by source and target			
				;
			else if (flag_AT && !flag_T && !flag_DT)  //deleted by source and added by target			
			{
				omodel.getGraph().add(conflictingTriplesAdditionTarget.get(0));
			}
			else if (flag_AT && flag_T && flag_DT) { //deleted by source and modified by target
				omodel.getGraph().add(conflictingTriplesAdditionTarget.get(0));				
			}
		} 

		try {				
			omodel.write(new FileOutputStream(main.newTarget),"NT");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		model.close();		
		
	}	

	
	/////////////////////////////////////////////////////Step3
	/*Apply rest of the changes directly*/
	
	public static void applyDelTarget(){
		
		Model tmodel = FileManager.get().loadModel(main.targetDeletionsChangeset);
		Model itmodel = FileManager.get().loadModel(main.initialtarget);			
		List<Triple> triplestoDelete = new ArrayList<Triple>();
			
		StmtIterator iter = tmodel.listStatements();
		
		while (iter.hasNext()) {
		    Statement stmt = iter.nextStatement();  // get next statement		 
		    triplestoDelete.add(stmt.asTriple());
		}
				
		for (Triple t : triplestoDelete) 	    
		    itmodel.getGraph().delete(t);
		
		try {				
			itmodel.write(new FileOutputStream(main.initialtarget),"NT");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		tmodel.close();	
		itmodel.close();	
		
	}
	
	/////////////////////////////////////////////////////Step4	
	public static void applyAddTarget(){
		
		Model tmodel = FileManager.get().loadModel(main.targetAdditionsChangeset);
		Model itmodel = FileManager.get().loadModel(main.initialtarget);			
		List<Triple> triplestoAdd = new ArrayList<Triple>();
			
		StmtIterator iter = tmodel.listStatements();
		
		while (iter.hasNext()) {
		    Statement stmt = iter.nextStatement();  // get next statement		 
		    triplestoAdd.add(stmt.asTriple());
		}
				
		for (Triple t : triplestoAdd) 		    
		    itmodel.getGraph().add(t);

		try {				
			itmodel.write(new FileOutputStream(main.initialtarget),"NT");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		tmodel.close();	
		itmodel.close();	
		
	}

	/////////////////////////////////////////////////////Step5
	public static void applyInitialTarget(){
		
		Model ntmodel = FileManager.get().loadModel(main.newTarget);
		Model itmodel = FileManager.get().loadModel(main.initialtarget);			
			
		StmtIterator iter = itmodel.listStatements();
		
		while (iter.hasNext()) {
		    Statement stmt = iter.nextStatement();  // get next statement		 
		    ntmodel.getGraph().add(stmt.asTriple());
		}
		
		try {				
			ntmodel.write(new FileOutputStream(main.newTarget),"NT");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		ntmodel.close();	
		itmodel.close();	
		
	}
	
	 /*			Find conflicting triples in target*/
	
		public static List<Triple> findCorrespondingTriples(String filename, Resource subject, Property predicate, Node object) {

				Model model = FileManager.get().loadModel(filename,"NT");
				
				List<Triple> conflictingTriples = new ArrayList<Triple>();
				
				ExtendedIterator<Triple> results = model.getGraph().find(subject.asNode(), predicate.asNode(), object); 
				while (results.hasNext()) {
					Triple t = results.next();
					conflictingTriples.add(t); 
				}
			
				for (Triple deleteConflict : conflictingTriples) // Delete the conflicting triples from target
					model.getGraph().delete(deleteConflict);

				// After deletion
				/*results = GraphUtil.findAll(model.getGraph());
				while (results.hasNext()) {
					System.out.println(results.next());
				}*/
	
				try {				
					model.write(new FileOutputStream(filename),"NT");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				model.close();
				return conflictingTriples;
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
