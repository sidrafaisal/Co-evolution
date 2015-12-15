package Conflict_Finder;

import Conflict_Finder.conflicts_Finder;
import Conflict_Resolver.statistics;
import Co_Evolution_Manager.configure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class source_Delta {

	public static String current_Predicate = "";
	public static String current_Predicate_Type = "";	
	public static long number_Of_ConflictingTriples = 0;
	public static long number_Of_ResolvedTriples = 0;

	public static long number_Of_case123Triples = 0;
	public static long number_Of_case4Triples = 0;
	public static long number_Of_case5Triples = 0;
	public static long number_Of_case678Triples = 0;


	public static long resolutionTime = 0;
	public static long identificationTime = 0;
	public static Map <String, Integer> predicateFunctionUseCounter;

	public static void apply (){

		additions_changeset();				//Step 1
		deletions_changeset();				//Step 2

		identificationTime = identificationTime - resolutionTime;
	}

	/*Find conflicts for source additions changeset: Pick each triple s1,p1,o1 from source additions changeset and
	  check for s1,p1,o2 in target changesets and inital target*/

	public static void additions_changeset () {		
		try {			
			long startTime = System.currentTimeMillis();
			Model omodel = FileManager.get().loadModel(configure.newTarget, configure.fileSyntax);		

			if (configure.sourceAdditionsChangeset != null) {
				Model model = FileManager.get().loadModel(configure.sourceAdditionsChangeset, configure.fileSyntax);
				model.size();
				StmtIterator iter = model.listStatements();

				while (iter.hasNext()) {

					Statement stmt      = iter.nextStatement();  // get next statement
					Resource  subject   = stmt.getSubject();     // get the subject
					Property  predicate = stmt.getPredicate();   // get the predicate
					RDFNode   object    = stmt.getObject();      // get the object

					//pick first value if we have multiple in source
					String functionforPredicate = statistics.resolutionFunctionforPredicate.get(predicate.toString()); 
					current_Predicate = predicate.toString();
					List<Triple> conflictingTriplesDeletionSource = null, 
							conflictingTriplesTarget = null, 
							conflictingTriplesAdditionTarget = null, 
							conflictingTriplesDeletionTarget = null, 
							conflictingTriplesSource = null;

					boolean flag_DS = false, flag_T = false, flag_AT = false, flag_DT = false, flag_MS = false;

					if(statistics.predicateType.get(current_Predicate)!=null)
						current_Predicate_Type = statistics.predicateType.get(current_Predicate);

					// printTriple ("configure.sourceAdditionsChangeset", subject, predicate, object);
					if (omodel.contains(subject, predicate)) {
						if (!current_Predicate_Type.equals("F"))
							omodel.add(stmt);
						number_Of_case123Triples++;
					}
					else {
						conflictingTriplesDeletionSource = findCorrespondingTriples(configure.sourceDeletionsChangeset, subject, predicate, Node.ANY) ;
						conflictingTriplesTarget = findCorrespondingTriples(configure.initialTarget, subject, predicate, Node.ANY) ;
						conflictingTriplesAdditionTarget = findCorrespondingTriples(configure.targetAdditionsChangeset, subject, predicate, Node.ANY) ;
						conflictingTriplesDeletionTarget = findCorrespondingTriples(configure.targetDeletionsChangeset, subject, predicate, Node.ANY) ;

						if(conflictingTriplesDeletionSource.size() > 1) {
							conflictingTriplesSource = findCorrespondingTriples(configure.sourceAdditionsChangeset, subject, predicate, Node.ANY) ;	
							if (conflictingTriplesSource!=null) {
								Triple t = conflictingTriplesSource.get(conflictingTriplesSource.size()-1); 

								subject = ResourceFactory.createResource(t.getSubject().toString());
								predicate = ResourceFactory.createProperty(t.getPredicate().toString());

								object = ResourceFactory.createPlainLiteral(t.getObject().toString());
								stmt = ResourceFactory.createStatement(subject, predicate, object);
								flag_MS = true;
							}
						}

						if(conflictingTriplesDeletionSource.size() > 0)
							flag_DS = true;
						if(conflictingTriplesTarget.size() > 0)
							flag_T = true;
						if(conflictingTriplesAdditionTarget.size() > 0)
							flag_AT = true;
						if(conflictingTriplesDeletionTarget.size() > 0)
							flag_DT = true;


						//added by source || modified by source || modified by source and deleted by target
						// In second case of (flag_DS && !flag_DT && !flag_AT), it does not matter whether this triple exists in initial target or not. 					
						// New value will be added even if the target has previously deleted this triple					

						if ( (!flag_DS && !flag_T && !flag_DT && !flag_AT) ||	(flag_DS && !flag_DT && !flag_AT)	||
								(flag_DS && flag_T && flag_DT && !flag_AT))	{
							omodel.add(stmt);
							number_Of_case123Triples++;
						}
						//added by source and deleted by target, default case is to ignore this triple
						else if (!flag_DS && flag_T && flag_DT && !flag_AT) 
							number_Of_case4Triples++;

						//added by source and modified by target, prefer target here 
						else if (!flag_DS && flag_T && flag_DT && flag_AT) { 
							omodel.getGraph().add(conflictingTriplesAdditionTarget.get(0));		
							number_Of_case5Triples++;
						}
						// added by source and target ||  modified by source and added by target || modified by source and modified by target				

						else if ((!flag_DS && !flag_T && !flag_DT && flag_AT) ||  (flag_DS && !flag_DT && flag_AT && !flag_T) ||
								(flag_DS && flag_T && flag_DT && flag_AT)) {
							number_Of_case678Triples++;

							int conflictingTriplesAdditionsize = conflictingTriplesAdditionTarget.size();

							for (int i = 0; i < conflictingTriplesAdditionsize; i++) {
								Triple t = conflictingTriplesAdditionTarget.get(i);
								if(stmt.asTriple().sameAs(t.getSubject(), t.getPredicate(), t.getObject())) {		//same values			
									if (conflictingTriplesAdditionsize == i+1) //current_Predicate_Type
										omodel.add(stmt);					
								} else if (current_Predicate.equals("http://www.w3.org/2000/01/rdf-schema#label")) {
									boolean is_conflict = resolveLabels(omodel, stmt, conflictingTriplesAdditionTarget, functionforPredicate);
									if (is_conflict) {									
										if (flag_MS)
											number_Of_ConflictingTriples += conflictingTriplesSource.size();
										number_Of_ConflictingTriples += conflictingTriplesDeletionSource.size() + conflictingTriplesTarget.size() + 
												conflictingTriplesAdditionTarget.size() + conflictingTriplesDeletionTarget.size() + 1; 
										number_Of_ResolvedTriples++;
									}
									break;								
								} else if (current_Predicate.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") ) {
									t = conflictingTriplesAdditionTarget.get(i);
									if (!isDisjoint(t.getObject().getURI(),stmt.asTriple().getObject().getURI())) {
										if (i==0)
											omodel.getGraph().add(Triple.create(subject.asNode(), predicate.asNode(), object.asNode()));
										omodel.getGraph().add(Triple.create(subject.asNode(), predicate.asNode(), t.getObject()));
									} else {
										boolean resolved = false;
										for (int j = 1; j < conflictingTriplesAdditionsize; j++)
										{
											t = conflictingTriplesAdditionTarget.get(j);
											if (!isDisjoint(t.getObject().getURI(),stmt.asTriple().getObject().getURI())) {
												Triple triple =Triple.create(subject.asNode(), predicate.asNode(), object.asNode());
												if (!contains (omodel, triple)) 
													omodel.getGraph().add(triple);
												omodel.getGraph().add(Triple.create(subject.asNode(), predicate.asNode(), t.getObject()));
												resolved = true;
											}
										}
										if (!resolved) {

											long srt = System.currentTimeMillis();
											String rv = Conflict_Resolver.resolver.apply(functionforPredicate, getURIstoResolve (object, conflictingTriplesAdditionTarget), "String"); 

											long ert   = System.currentTimeMillis();
											resolutionTime = resolutionTime + (ert - srt);

											Triple triple = Triple.create(subject.asNode(), predicate.asNode(), NodeFactory.createURI(rv));	
											omodel.getGraph().add(triple);	
											int counter = predicateFunctionUseCounter.get(current_Predicate) + 1;
											predicateFunctionUseCounter.put(current_Predicate, counter);

											if (flag_MS)
												number_Of_ConflictingTriples += conflictingTriplesSource.size();
											number_Of_ConflictingTriples += conflictingTriplesDeletionSource.size() + conflictingTriplesTarget.size() + 
													conflictingTriplesAdditionTarget.size() + conflictingTriplesDeletionTarget.size() + 1; 
											number_Of_ResolvedTriples++;
										}									
										break;
									}
								}/* else if ( ((Co_Evolution_Manager.configure.predicateList.contains("http://www.w3.org/2002/07/owl#sameAs")	
									&& object.isLiteral()) ||
									!Co_Evolution_Manager.configure.predicateList.contains("http://www.w3.org/2002/07/owl#sameAs"))) {
								if (Conflict_Finder.conflicts_Finder.resolve)
									notResolvableUsingSameAs (omodel, stmt, conflictingTriplesAdditionTarget, functionforPredicate);							
								break;
							}*/ else if (current_Predicate.equals("http://www.w3.org/2002/07/owl#sameAs")) {
								if (i==0)
									omodel.getGraph().add(Triple.create(subject.asNode(), predicate.asNode(), object.asNode()));
								omodel.getGraph().add(Triple.create(subject.asNode(), predicate.asNode(), t.getObject()));

							} else if (!current_Predicate.equals("http://www.w3.org/2002/07/owl#sameAs") &&
									Co_Evolution_Manager.configure.predicateList.contains("http://www.w3.org/2002/07/owl#sameAs")	
									&& object.isURIResource()) { // if we have sameAs triples, check if both objects are in sameas binding
								Property p = ResourceFactory.createProperty("http://www.w3.org/2002/07/owl#sameAs");

								List<Triple> sameAsInTargetAdd = findCorrespondingTriples(configure.targetAdditionsChangeset, object.asResource(), p, t.getObject()) ;
								List<Triple> sameAsInTargetDel = findCorrespondingTriples(configure.targetDeletionsChangeset, object.asResource(), p, t.getObject()) ;	
								List<Triple> sameAsInSourceDel = findCorrespondingTriples(configure.sourceDeletionsChangeset, object.asResource(), p, t.getObject()) ;	
								List<Triple> sameAsInTarget = findCorrespondingTriples(configure.initialTarget, object.asResource(), p, t.getObject()) ;	

								if (!sameAsInTargetAdd.isEmpty() || !sameAsInTargetDel.isEmpty() || !sameAsInSourceDel.isEmpty() || !sameAsInTarget.isEmpty()) {									
									if (i==0)
										omodel.getGraph().add(Triple.create(subject.asNode(), predicate.asNode(), object.asNode()));

									omodel.getGraph().add(Triple.create(object.asNode(), p.asNode(),  t.getObject()));									
									omodel.getGraph().add(Triple.create(subject.asNode(), predicate.asNode(), t.getObject()));
								} else	{
									Resource r = ResourceFactory.createResource(t.getObject().toString());
									sameAsInTargetAdd = findCorrespondingTriples(configure.targetAdditionsChangeset, r, p, object.asNode()) ;
									sameAsInTarget = findCorrespondingTriples(configure.initialTarget, r, p, object.asNode()) ;
									sameAsInTargetDel = findCorrespondingTriples(configure.targetDeletionsChangeset, r, p, object.asNode()) ; 
									sameAsInSourceDel = findCorrespondingTriples(configure.sourceDeletionsChangeset, r, p, object.asNode()) ;	

									if (!sameAsInTargetAdd.isEmpty() || !sameAsInTargetDel.isEmpty() || !sameAsInSourceDel.isEmpty() || !sameAsInTarget.isEmpty()) {

										if (i==0)
											omodel.getGraph().add(Triple.create(subject.asNode(), predicate.asNode(), object.asNode()));

										omodel.getGraph().add(Triple.create(r.asNode(), p.asNode(), object.asNode()));	
										omodel.getGraph().add(Triple.create(subject.asNode(), predicate.asNode(), t.getObject()));	
									} else if (Conflict_Finder.conflicts_Finder.resolve) {
										String [] args = {"",""};
										args [0] = object.asResource().getURI().toString();
										args [1] = t.getObject().getURI().toString() ;	
										long srt = System.currentTimeMillis();
										String rv = Conflict_Resolver.resolver.apply(functionforPredicate, args, "String"); 
										long ert   = System.currentTimeMillis();
										resolutionTime = resolutionTime + (ert - srt);

										Triple triple = Triple.create(subject.asNode(), predicate.asNode(), NodeFactory.createURI(rv));
										if (!contains (omodel, triple)) 
											omodel.getGraph().add(triple);
										int counter = predicateFunctionUseCounter.get(current_Predicate) + 1;
										predicateFunctionUseCounter.put(current_Predicate, counter);

										number_Of_ResolvedTriples++;
										if (i==0) {
											number_Of_ConflictingTriples += conflictingTriplesDeletionSource.size() + conflictingTriplesTarget.size() + 
													conflictingTriplesDeletionTarget.size() + 2;// 1 for add target, 1 for src add
											if (flag_MS)
												number_Of_ConflictingTriples += conflictingTriplesSource.size();
										}
										else
											number_Of_ConflictingTriples += 1;

									}
								}
							} // prev case of sameAs works only for object URIs, whereas we can also have literals
							else if (Conflict_Finder.conflicts_Finder.resolve) {
								boolean is_conflict = resolveGenerally (omodel, stmt, conflictingTriplesAdditionTarget, functionforPredicate);	
								if (is_conflict) {
									if (flag_MS)
										number_Of_ConflictingTriples += conflictingTriplesSource.size();
									number_Of_ConflictingTriples += conflictingTriplesDeletionSource.size() + conflictingTriplesTarget.size() + 
											conflictingTriplesAdditionTarget.size() + conflictingTriplesDeletionTarget.size() + 1; 
									number_Of_ResolvedTriples++;
								}
								break;
							}
							}				
						}
					}
				}
				model.close();

			}
			omodel.write(new FileOutputStream(configure.newTarget), configure.fileSyntax);

			omodel.close();	
			long endTime   = System.currentTimeMillis();
			identificationTime = identificationTime + (endTime - startTime);

		} catch (FileNotFoundException | org.apache.jena.riot.RiotException e) {
			e.printStackTrace();
		}
	}	


	public static boolean resolveGenerally (Model omodel, Statement stmt, List<Triple> conflictingTriplesAdditionTarget, String functionforPredicate) {							

		Resource  subject   = stmt.getSubject();     // get the subject
		Property  predicate = stmt.getPredicate();   // get the predicate
		RDFNode   object    = stmt.getObject(); 
		//if (current_Predicate_Type.equals("F")) 
		boolean is_conflict = false;
		if (object.isURIResource()) {

			long srt = System.currentTimeMillis();
			String rv = Conflict_Resolver.resolver.apply(functionforPredicate, getURIstoResolve (object, conflictingTriplesAdditionTarget), "String"); 

			long ert   = System.currentTimeMillis();
			resolutionTime = resolutionTime + (ert - srt);
			is_conflict = true;
			Triple triple = Triple.create(subject.asNode(), predicate.asNode(), NodeFactory.createURI(rv));	
			if (!contains (omodel, triple)) 
				omodel.getGraph().add(triple);
			int counter = predicateFunctionUseCounter.get(current_Predicate) + 1;
			predicateFunctionUseCounter.put(current_Predicate, counter);

		} else if (object.isLiteral()) {
			String type = getType(object.asLiteral().getDatatypeURI());
			long srt = System.currentTimeMillis();
			String rv = Conflict_Resolver.resolver.apply(functionforPredicate, getLiteralstoResolve (object, conflictingTriplesAdditionTarget), type); 
			is_conflict = true;
			if (conflictingTriplesAdditionTarget.contains(rv)) {
				Node temp = conflictingTriplesAdditionTarget.get(conflictingTriplesAdditionTarget.indexOf(rv)).getObject();
				if (temp.getLiteralLanguage()!=null)
					type = temp.getLiteralLanguage();
				else {
					String slang = object.asLiteral().getLanguage();
					if (slang!=null)
						type = slang;
				}					
			} else {
				String slang = object.asLiteral().getLanguage();
				if (slang!=null)
					type = slang;
			}

			long ert   = System.currentTimeMillis();
			resolutionTime = resolutionTime + (ert - srt);

			Triple triple = Triple.create(subject.asNode(), predicate.asNode(), NodeFactory.createLiteral(rv, type));
			//if (!contains (omodel, triple)) 
			omodel.getGraph().add(triple);
			int counter = predicateFunctionUseCounter.get(current_Predicate) + 1;
			predicateFunctionUseCounter.put(current_Predicate, counter);
		}	
		return is_conflict;
		//		number_Of_ConflictingTriples++;	
	}

	/*	public static void notResolvableUsingSameAs (Model omodel, Statement stmt, List<Triple> conflictingTriplesAdditionTarget, String functionforPredicate) {							

		Resource  subject   = stmt.getSubject();     // get the subject
		Property  predicate = stmt.getPredicate();   // get the predicate
		RDFNode   object    = stmt.getObject(); 
		//if (current_Predicate_Type.equals("F")) 

		if (object.isURIResource()) {
			long srt = System.currentTimeMillis();
			String rv = Conflict_Resolver.resolver.apply(functionforPredicate, getURIstoResolve (object, conflictingTriplesAdditionTarget), "String"); 

			long ert   = System.currentTimeMillis();
			resolutionTime = resolutionTime + (ert - srt);
			Triple triple = Triple.create(subject.asNode(), predicate.asNode(), NodeFactory.createURI(rv));	
			//	if (!contains (omodel, triple)) 
			omodel.getGraph().add(triple);		
			int counter = predicateFunctionUseCounter.get(current_Predicate) + 1;
			predicateFunctionUseCounter.put(current_Predicate, counter);

		} else if (object.isLiteral()) {
			String type = getType(object.asLiteral().getDatatypeURI());
			long srt = System.currentTimeMillis();
			String rv = Conflict_Resolver.resolver.apply(functionforPredicate, getLiteralstoResolve (object, conflictingTriplesAdditionTarget), type); 
			if (conflictingTriplesAdditionTarget.contains(rv)) {
				Node temp = conflictingTriplesAdditionTarget.get(conflictingTriplesAdditionTarget.indexOf(rv)).getObject();
				if (temp.getLiteralLanguage()!=null)
					type = temp.getLiteralLanguage();
				else {
					String slang = object.asLiteral().getLanguage();
					if (slang!=null)
						type = slang;
				}					
			} else {
				String slang = object.asLiteral().getLanguage();
				if (slang!=null)
					type = slang;
			}
			long ert   = System.currentTimeMillis();
			resolutionTime = resolutionTime + (ert - srt);

			Triple triple = Triple.create(subject.asNode(), predicate.asNode(), NodeFactory.createLiteral(rv, type));
			//	if (!contains (omodel, triple)) 
			omodel.getGraph().add(triple);		
			int counter = predicateFunctionUseCounter.get(current_Predicate) + 1;
			predicateFunctionUseCounter.put(current_Predicate, counter);
		}		
	//	number_Of_ConflictingTriples++; 	
	}
	 */
	public static boolean resolveLabels(Model omodel, Statement stmt, List<Triple> conflictingTriplesAdditionTarget, String functionforPredicate) {
		int maxdiff = 0;
		Triple t;
		boolean is_conflict = false;
		Resource  subject   = stmt.getSubject();     // get the subject
		Property  predicate = stmt.getPredicate();   // get the predicate
		RDFNode   object    = stmt.getObject(); 

		int iteration = 0;
		for (int j = 0; j < conflictingTriplesAdditionTarget.size(); j++) {
			t = conflictingTriplesAdditionTarget.get(j);
			String s_value = object.asNode().toString();
			String t_value = t.getObject().toString();
			int threshold = Math.max(s_value.length(), t_value.length())/2;
			int diff = StringUtils.getLevenshteinDistance(s_value, t_value); //greater the diff,lesser the similarity

			if (diff >= threshold && diff > maxdiff) {		
				maxdiff = diff;
				iteration = j;	
			}
		}									
		if (maxdiff > 0) {							// pick least similar string
			t = conflictingTriplesAdditionTarget.get(iteration);
			Triple triple = Triple.create(subject.asNode(), predicate.asNode(), t.getObject());
			//if (!contains (omodel, triple)) 
			omodel.getGraph().add(triple);	
			omodel.getGraph().add(Triple.create(subject.asNode(), predicate.asNode(), object.asNode()));

		} else if (Conflict_Finder.conflicts_Finder.resolve) {

			String type = getType(object.asLiteral().getDatatypeURI());

			long srt = System.currentTimeMillis();
			String rv = Conflict_Resolver.resolver.apply(functionforPredicate, getLiteralstoResolve (object, conflictingTriplesAdditionTarget), type); 

			long ert   = System.currentTimeMillis();
			resolutionTime = resolutionTime + (ert - srt);
			is_conflict = true;
			//	number_Of_ResolvedTriples++;
			if (conflictingTriplesAdditionTarget.contains(rv)) {
				Node temp = conflictingTriplesAdditionTarget.get(conflictingTriplesAdditionTarget.indexOf(rv)).getObject();
				if (temp.getLiteralLanguage()!=null)
					type = temp.getLiteralLanguage();
				else {
					String slang = object.asLiteral().getLanguage();
					if (slang!=null)
						type = slang;
				}					
			} else {
				String slang = object.asLiteral().getLanguage();
				if (slang!=null)
					type = slang;
			}

			Triple triple =Triple.create(subject.asNode(), predicate.asNode(), NodeFactory.createLiteral(rv, type));
			//if (!contains (omodel, triple)) 
			omodel.getGraph().add(triple);		
			int counter = predicateFunctionUseCounter.get(current_Predicate) + 1;
			predicateFunctionUseCounter.put(current_Predicate, counter);
			//	number_Of_ConflictingTriples++;
		}	
		return is_conflict;
	}

	public static String [] getLiteralstoResolve (RDFNode object, List<Triple> conflictingTriplesAdditionTarget) {
		String [] args = new String [conflictingTriplesAdditionTarget.size() + 1];
		args [0] = object.asLiteral().getValue().toString();

		for (int j = 0; j < conflictingTriplesAdditionTarget.size(); j++) {
			Triple t = conflictingTriplesAdditionTarget.get(j);
			args [j+1] = t.getObject().getLiteralValue().toString();
		}
		return args;
	}

	public static String [] getURIstoResolve (RDFNode object, List<Triple> conflictingTriplesAdditionTarget) {
		String [] args = new String [conflictingTriplesAdditionTarget.size() + 1];	
		args [0] = object.asResource().getURI().toString();
		for (int j = 0; j < conflictingTriplesAdditionTarget.size(); j++)
		{
			Triple t = conflictingTriplesAdditionTarget.get(j);
			args [j+1] = t.getObject().getURI().toString();
		}
		return args;
	}

	public static boolean isDisjoint(String tar, String src) {
		boolean isDisjoint = false;
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		try {
			@Nonnull 
			File f = new File(Co_Evolution_Manager.configure.ontology);
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(f);

			OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
			OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);
			OWLDataFactory fac = manager.getOWLDataFactory();		

			OWLClass sclass = fac.getOWLClass(IRI.create(src));
			OWLClass tclass = fac.getOWLClass(IRI.create(tar));

			NodeSet<OWLClass> c = reasoner.getDisjointClasses(sclass);
			if (c.containsEntity(tclass))
				isDisjoint = true;

			reasoner.dispose();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}		
		return isDisjoint;
	}

	/*Find conflicts for source deletions changeset: Pick each triple s1,p1,o1 from source deletion changeset 
and check for s1,p1,o2 in target changesets and initial target*/	

	public static void deletions_changeset(){
		if (configure.sourceDeletionsChangeset != null) {
			try {
				long startTime = System.currentTimeMillis();
				Model omodel = FileManager.get().loadModel(configure.newTarget, configure.fileSyntax);		

				Model model = FileManager.get().loadModel(configure.sourceDeletionsChangeset, configure.fileSyntax);

				StmtIterator iter = model.listStatements();

				while (iter.hasNext()) {
					Statement stmt      = iter.nextStatement();  // get next statement
					Resource  subject   = stmt.getSubject();     // get the subject
					Property  predicate = stmt.getPredicate();   // get the predicate
					RDFNode   object    = stmt.getObject();      // get the object

					//					conflicts_Finder.printTriple ("configure.sourceDeletionsChangeset", subject, predicate, object);

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
				long endTime   = System.currentTimeMillis();
				identificationTime = identificationTime + (endTime - startTime);
			} catch (FileNotFoundException | org.apache.jena.riot.RiotException e) {
				e.printStackTrace();
			}
		}
	}	


	/*			Find conflicting triples in target*/

	public static List<Triple> findCorrespondingTriples(String filename, Resource subject, Property predicate, Node object) {

		List<Triple> conflictingTriples = new ArrayList<Triple>();

		if (filename!=null){
			try {
				Model model = FileManager.get().loadModel(filename, configure.fileSyntax);

				ExtendedIterator<Triple> results = model.getGraph().find(subject.asNode(), predicate.asNode(), object); 
				while (results.hasNext()) {
					Triple t = results.next();
					conflictingTriples.add(t); 
				}

				for (Triple deleteConflict : conflictingTriples) // Delete the conflicting triples from input file
					model.getGraph().delete(deleteConflict);

				model.write(new FileOutputStream(filename), configure.fileSyntax);
				model.close();

			} catch (FileNotFoundException | org.apache.jena.riot.RiotException e) {
				e.printStackTrace();
			}
		}
		return conflictingTriples;
	}

	public static boolean contains (Model model, Triple t) {
		boolean contains = false;
		if (model!=null) {
			try {		
				ExtendedIterator<Triple> results = model.getGraph().find(t.getSubject(), t.getPredicate(), t.getObject()); 
				if (results.hasNext())
					contains = true;
				else 
					contains = false;				
			} catch (org.apache.jena.riot.RiotException e) {
				e.printStackTrace();
			}
		}
		return contains;
	}


	public static String getType(String type) {

		int index = type.indexOf("#");
		if (index == -1)
			index = type.indexOf(":");
		index = index + 1;

		int size = type.length();
		return type.substring(index, size);
	}

	public static void setPredicateFunctionUseCounter () {
		Iterator<String> predicateList = Co_Evolution_Manager.configure.predicateList.iterator();
		while (predicateList.hasNext()) {
			Conflict_Finder.source_Delta.predicateFunctionUseCounter.put(predicateList.next(), 0);
		}
	}	

	public static void getPredicateFunctionUseCounter () {
		Map<String, Integer> usedFunction = new HashMap<String, Integer>();
		Iterator<String> predicateList = Co_Evolution_Manager.configure.predicateList.iterator();
		while (predicateList.hasNext()) {
			String predicate = predicateList.next();
			int counter = Conflict_Finder.source_Delta.predicateFunctionUseCounter.get(predicate);
			String functionforPredicate = statistics.resolutionFunctionforPredicate.get(predicate);
			if(usedFunction.containsKey(functionforPredicate)) {
				counter = usedFunction.get(functionforPredicate) + counter;
				usedFunction.remove(functionforPredicate); 
			}
			usedFunction.put(functionforPredicate, counter);
		}
		System.out.println("Function, #of triples resolved using this function");
		Set <String> funList= usedFunction.keySet();		
		for (String fun : funList) {
			System.out.println(fun+ ", " + usedFunction.get(fun));
		}
	}	
}
