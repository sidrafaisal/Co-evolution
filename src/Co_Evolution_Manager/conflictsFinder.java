package Co_Evolution_Manager;

import Conflict_Handler.CResolution_Functions;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.GraphUtil;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

public class conflictsFinder {
	
	/*			Identify Conflicts 			*/
	
	public static void identifyConflicts(boolean resolve){
		conflictPatternFinderforAddSource();
		conflictPatternFinderforDelSource();	
		applyDelTarget();
		applyAddTarget();
		applyInitialTarget();

		//	if (!resolve)
			//	model.getGraph().delete(t);	
		
		//	Conflict_Handler.CResolution_Functions c = new Conflict_Handler.Resolution_Functions();
		//String[] s={"5.57","5.6"};
		//System.out.print(c.median(s));

	}
	
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
		    
		    printTriple ("main.sourceAdditionsChangeset", subject, predicate, object);

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
					
			if (!flag_DS && !flag_T && !flag_DT) { 
				if (!flag_AT) 			//added by source	
					omodel.add(stmt);	
				else {					// added by source and target
					//System.out.println("s1,p1,o2 found in target addition changeset" );
					int conflictingTriplesAdditionsize = conflictingTriplesAdditionTarget.size();
					for (int i = 0; i < conflictingTriplesAdditionsize; i++)
					{
						Triple t = conflictingTriplesAdditionTarget.get(i);
						if(object.toString().equals(t.getObject().toString())){		//same values			
						//	System.out.println("same............"+t.toString());
							conflictingTriplesAdditionTarget.remove(t);
							omodel.add(stmt);
						} else {														//different values
							System.out.println("add after resolve...........");		
//						if (!resolve)
	//						model.getGraph().delete(t);		
						}
					}					
				}
			} else if (flag_DS && !flag_DT){
				if(!flag_AT){					//modified by source
					omodel.add(stmt);							
				} else if (!flag_T) {			//modified by source and added by target
					System.out.println("add after resolve...........");
				}
			} else if (flag_DS && flag_T && flag_DT) { 
				if(!flag_AT){							//modified by source and deleted by target
					omodel.add(stmt);							
				} else {									//modified by source and target 
					System.out.println("add after resolve...........");
				}
			} else if (!flag_DS && flag_T && flag_DT) { 
				if(!flag_AT){							//added by source and deleted by target
					System.out.println("add after resolve...........");						
				} else {									//added by source and modified by target 
					System.out.println("add after resolve...........");
				}
			}
		//	model.getGraph().delete(stmt.asTriple());	
		} 

		try {				
//			model.write(new FileOutputStream(main.sourceAdditionsChangeset),"NT");
			omodel.write(new FileOutputStream(main.newTarget),"NT");
	//		if (model.getGraph().size() == 0 )
		//		System.out.println("Finished for source...........");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		model.close();	
		
	}	
	
	
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
					
			if (!flag_AT && flag_T && !flag_DT)  //deleted by source
				; //already deleted from T during processing
			else if (!flag_AT && flag_T && flag_DT)  //deleted by source and target			
				;
			else if (flag_AT && !flag_T && !flag_DT)  //deleted by source and added by target			
			{
				omodel.add(createStatement(omodel,conflictingTriplesAdditionTarget.get(0)));
			}
			else if (flag_AT && flag_T && flag_DT) { //deleted by source and modified by target
				System.out.println("add after resolve...........");				
			}
		//	model.getGraph().delete(stmt.asTriple());	
		} 

		try {				
		//	model.write(new FileOutputStream(main.sourceDeletionsChangeset, true),"NT");
			omodel.write(new FileOutputStream(main.newTarget),"NT");
		//	if (model.getGraph().size() == 0 )
			//	System.out.println("Finished for source...........");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		model.close();			
	}	

	
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
				
		for (Triple t : triplestoDelete) {
		//	tmodel.getGraph().delete(t);		    
		    itmodel.getGraph().delete(t);
		}
		
		try {				
			itmodel.write(new FileOutputStream(main.initialtarget),"NT");
		//	if (tmodel.getGraph().size() == 0 )
			//	System.out.println("Finished for target...........");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		tmodel.close();	
		itmodel.close();	
	}
	
	public static void applyAddTarget(){
		Model tmodel = FileManager.get().loadModel(main.targetAdditionsChangeset);
		Model itmodel = FileManager.get().loadModel(main.initialtarget);			
		List<Triple> triplestoAdd = new ArrayList<Triple>();
			
		StmtIterator iter = tmodel.listStatements();
		
		while (iter.hasNext()) {
		    Statement stmt = iter.nextStatement();  // get next statement		 
		    triplestoAdd.add(stmt.asTriple());
		}
				
		for (Triple t : triplestoAdd) {
		//	tmodel.getGraph().delete(t);		    
		    itmodel.getGraph().add(t);
		}
		
		try {				
			itmodel.write(new FileOutputStream(main.initialtarget),"NT");
			//if (tmodel.getGraph().size() == 0 )
				//System.out.println("Finished for target...........");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		tmodel.close();	
		itmodel.close();	
	}
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
				
				System.out.println("Total triples in file " + model.getGraph().size());
				ExtendedIterator<Triple> results = model.getGraph().find(subject.asNode(), predicate.asNode(), object); 
				while (results.hasNext()) {
					Triple t = results.next();
					conflictingTriples.add(t); 
				}
				
				// Delete the conflicting triples from target
				
				for (Triple deleteConflict : conflictingTriples) {
					model.getGraph().delete(deleteConflict);
				}
				System.out.println("Total triples after deletion " + model.getGraph().size());

				// After deletion
				results = GraphUtil.findAll(model.getGraph());
				while (results.hasNext()) {
					System.out.println(results.next());
				}
	
				try {				
					model.write(new FileOutputStream(filename),"NT");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				model.close();
				return conflictingTriples;
		}
		
	/*			Create statement 			*/
public static Statement createStatement(Model m, Triple t){
	Resource s = m.getResource(t.getSubject().toString());
	Property p = m.getProperty(t.getPredicate().toString());
	RDFNode o = m.getRDFNode(t.getObject());

	return ResourceFactory.createStatement(s, p, o);
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
