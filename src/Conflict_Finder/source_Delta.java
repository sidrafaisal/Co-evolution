package Conflict_Finder;

import Conflict_Finder.conflicts_Finder;
import Conflict_Resolver.statistics;
import Co_Evolution_Manager.configure;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

public class source_Delta {

	public static String current_Predicate = "";

	public static void apply (boolean resolve){

		additions_changeset(resolve);		//Step 1
		deletions_changeset();				//Step 2

	}

	/*Find conflicts for source additions changeset: Pick each triple s1,p1,o1 from source additions changeset and
	  check for s1,p1,o2 in target changesets and inital target*/

	public static void additions_changeset (boolean resolve) {		
		try {			
			Model omodel = ModelFactory.createDefaultModel();

			//FileManager.get().loadModel(configure.newTarget, configure.fileSyntax);		
			if (configure.sourceAdditionsChangeset != null) {
				Model model = FileManager.get().loadModel(configure.sourceAdditionsChangeset, configure.fileSyntax);

				StmtIterator iter = model.listStatements();

				while (iter.hasNext()) {

					Statement stmt      = iter.nextStatement();  // get next statement
					Resource  subject   = stmt.getSubject();     // get the subject
					Property  predicate = stmt.getPredicate();   // get the predicate
					RDFNode   object    = stmt.getObject();      // get the object

					String functionforPredicate =statistics.resolutionFunctionforPredicate.get(predicate.toString()); 
					current_Predicate = predicate.toString();
					// printTriple ("configure.sourceAdditionsChangeset", subject, predicate, object);

					List<Triple> conflictingTriplesDeletionSource = findCorrespondingTriples(configure.sourceDeletionsChangeset, subject, predicate, Node.ANY) ;

					List<Triple> conflictingTriplesTarget = findCorrespondingTriples(configure.initialTarget, subject, predicate, Node.ANY) ;

					List<Triple> conflictingTriplesAdditionTarget = findCorrespondingTriples(configure.targetAdditionsChangeset, subject, predicate, Node.ANY) ;

					List<Triple> conflictingTriplesDeletionTarget = findCorrespondingTriples(configure.targetDeletionsChangeset, subject, predicate, Node.ANY) ;

					boolean flag_DS = false, flag_T = false, flag_AT = false, flag_DT = false; 

					if(conflictingTriplesDeletionSource.size() > 0)
						flag_DS = true;

					if(conflictingTriplesTarget.size() > 0)
						flag_T = true;

					if(conflictingTriplesAdditionTarget.size() > 0)
						flag_AT = true;

					if(conflictingTriplesDeletionTarget.size() > 0)
						flag_DT = true;

					//added by source || modified by source || modified by source and target

					if ( (!flag_DS && !flag_T && !flag_DT && !flag_AT) ||	(flag_DS && !flag_DT && !flag_AT)	||
							(flag_DS && flag_T && flag_DT && flag_AT))	

						omodel.add(stmt);

					// added by source and target ||  modified by source and added by target || modified by source and deleted by target

					else if ( (!flag_DS && !flag_T && !flag_DT && flag_AT) ||  (flag_DS && !flag_DT && flag_AT && !flag_T) ||
							(flag_DS && flag_T && flag_DT && flag_AT)) {	

						int conflictingTriplesAdditionsize = conflictingTriplesAdditionTarget.size();

						for (int i = 0; i < conflictingTriplesAdditionsize; i++)
						{
							Triple t = conflictingTriplesAdditionTarget.get(i);
							if(object.toString().equals(t.getObject().toString())){		//same values			
								omodel.add(stmt);
							} else if (resolve) {	//different values (directly use preference or resolved?)
								String type, rv = "";

								//	if (object.isLiteral())
								//	{	
								String [] args = { object.asLiteral().getValue().toString(),
										t.getObject().getLiteralValue().toString() };								
								type = getType(object.asLiteral().getDatatypeURI());
								//	} else
								//		val1 = object.toString();

								rv = Conflict_Resolver.resolver.apply(functionforPredicate, args, type);
								omodel.getGraph().add(Triple.create(subject.asNode(), predicate.asNode(), NodeFactory.createLiteral(rv, object.asLiteral().getDatatype())));
								System.out.println("add after resolve...........");			
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
				model.close();
			}
			omodel.write(new FileOutputStream(configure.newTarget), configure.fileSyntax);

			omodel.close();	
		} catch (FileNotFoundException | org.apache.jena.riot.RiotException e) {
			System.out.println(""+e);
			e.printStackTrace();
		}
	}	

	/*Find conflicts for source deletions changeset: Pick each triple s1,p1,o1 from source deletion changeset 
and check for s1,p1,o2 in target changesets and initial target*/	

	public static void deletions_changeset(){
		if (configure.sourceDeletionsChangeset != null) {
			try {
				//	ModelFactory.createDefaultModel()
				Model omodel = FileManager.get().loadModel(configure.newTarget, configure.fileSyntax);		

				Model model = FileManager.get().loadModel(configure.sourceDeletionsChangeset, configure.fileSyntax);

				StmtIterator iter = model.listStatements();

				while (iter.hasNext()) {
					Statement stmt      = iter.nextStatement();  // get next statement
					Resource  subject   = stmt.getSubject();     // get the subject
					Property  predicate = stmt.getPredicate();   // get the predicate
					RDFNode   object    = stmt.getObject();      // get the object

					conflicts_Finder.printTriple ("configure.sourceDeletionsChangeset", subject, predicate, object);

					List<Triple> conflictingTriplesTarget = findCorrespondingTriples(configure.initialTarget, subject, predicate, Node.ANY) ;

					List<Triple> conflictingTriplesAdditionTarget = findCorrespondingTriples(configure.targetAdditionsChangeset, subject, predicate, Node.ANY) ;

					List<Triple> conflictingTriplesDeletionTarget = findCorrespondingTriples(configure.targetDeletionsChangeset, subject, predicate, Node.ANY) ;

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
				omodel.write(new FileOutputStream(configure.newTarget), configure.fileSyntax);
				model.close();		
			} catch (FileNotFoundException | org.apache.jena.riot.RiotException e) {
				System.out.println(""+e);
				e.printStackTrace();
			}
		}
	}	


	/*			Find conflicting triples in target*/

	public static List<Triple> findCorrespondingTriples(String filename, Resource subject, Property predicate, Node object) {
		if (filename!=null){
			try {
				Model model = FileManager.get().loadModel(filename, configure.fileSyntax);

				List<Triple> conflictingTriples = new ArrayList<Triple>();

				ExtendedIterator<Triple> results = model.getGraph().find(subject.asNode(), predicate.asNode(), object); 
				while (results.hasNext()) {
					Triple t = results.next();
					conflictingTriples.add(t); 
				}

				for (Triple deleteConflict : conflictingTriples) // Delete the conflicting triples from target
					model.getGraph().delete(deleteConflict);

				model.write(new FileOutputStream(filename), configure.fileSyntax);
				model.close();
				return conflictingTriples;

			} catch (FileNotFoundException | org.apache.jena.riot.RiotException e) {
				System.out.println(""+e);
				e.printStackTrace();
			}
		}			return null;
	}

	public static String getType(String type) {

		int index = type.indexOf("#");
		if (index == -1)
			index = type.indexOf(":");
		index = index + 1;

		int size = type.length();
		return type.substring(index, size);
	}

}
