package Conflict_Resolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import Conflict_Resolver.Label_Extractor;

@SuppressWarnings("javadoc")
public class function_Auto_Selector {

	private static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	private final int INDENT = 4;
	@Nonnull
	private static OWLReasonerFactory reasonerFactory;
	@Nonnull
	private static OWLOntology ontology;
	private final static PrintStream out = System.out;
	
	public static List<String> getPredicates () {
		List<String> predicateList = new ArrayList<String>();

		BufferedReader br;
		try { Co_Evolution_Manager.configure.predicates="predicates.txt";
			br = new BufferedReader(new FileReader(Co_Evolution_Manager.configure.predicates));
		String line = null;

		while ((line = br.readLine()) != null) {
			predicateList.add(line);
		}
		br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return predicateList;
	}	

	public static void select() {
		try{
			Map<String, String> resolutionFunctionforPredicate  = new HashMap<String, String>();
			List<String> predicateList = getPredicates (); // get the required predicates to be extracted from auto_selector file
			Co_Evolution_Manager.configure.ontology = "dbpedia_2014.owl";
			@Nonnull 
			File documentIRI = new File(Co_Evolution_Manager.configure.ontology);
			//    String x = "http://protege.cim3.net/file/pub/ontologies/travel/travel.owl";
			//   System.out.println(x);
			//  IRI documentIRI = IRI.create(x);

			ontology = manager.loadOntologyFromOntologyDocument(documentIRI);
			
			reasonerFactory = new Reasoner.ReasonerFactory();


			OWLClass clazz = manager.getOWLDataFactory().getOWLThing();
			for (String predicate : predicateList) {				
				if (predicate.contains("owl:sameAs") || predicate.contains("rdfs:label") || predicate.contains("rdf:type") ) {
					resolutionFunctionforPredicate.put(predicate, "0");
				} else {				
			String prefferedfname = checkProperty(clazz, predicate);
			resolutionFunctionforPredicate.put(predicate, prefferedfname); 
				}
			}
			manual_Selector.filename = "manual_FunctionSelector_"+ Co_Evolution_Manager.configure.newTarget+".xml";
			manual_Selector.create (resolutionFunctionforPredicate);
			
			// http://dbpedia.org/ontology/otherOccupation obj property
			// http://dbpedia.org/ontology/goalsInLeague data property			

		} catch (OWLException e)
		{
			e.printStackTrace();
		}
	}
	private static String checkProperty(@Nonnull OWLClass clazz, String p) {
		String value = null;
		OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);
		OWLDataFactory fac = manager.getOWLDataFactory();
		IRI i = IRI.create(p); 

		OWLObjectProperty objprop = fac.getOWLObjectProperty(i);
		Set<OWLClassExpression> classexpr= objprop.getRanges(ontology);
		Iterator<OWLClassExpression> expr = classexpr.iterator();
		
		if (!classexpr.isEmpty()){
			if (objprop.isFunctional(ontology) || objprop.isInverseFunctional(ontology))
				 value = "0";
			while (expr.hasNext())
				out.println(expr.next()+"object property");
			
		} else {

			OWLDataProperty dataprop = fac.getOWLDataProperty(i);
			Set<OWLDataRange> datarange = dataprop.getRanges(ontology);
			Iterator<OWLDataRange> range = datarange.iterator();

			if (!datarange.isEmpty()){
				if (dataprop.isFunctional(ontology))
					value = "0";
			}
			while (range.hasNext())
				out.println(range.next()+"data Property");
		}
		
		reasoner.dispose();
		return value;
	}
	
	private static void printHierarchy(@Nonnull OWLClass clazz, String p) {
		OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);

		OWLDataFactory fac = manager.getOWLDataFactory();
		//IRI i=   	IRI.create("http://dbpedia.org/ontology/goalsInLeague");
		IRI i=   	IRI.create(p); 

		OWLObjectProperty ope = fac.getOWLObjectProperty(i);
		Set<OWLClassExpression> z= ope.getRanges(ontology);
		Iterator<OWLClassExpression> y = z.iterator();
		out.println(z.isEmpty());
		
		if (!z.isEmpty()){
			if (ope.isFunctional(ontology))
				out.println("Functional property");
			else if (ope.isInverseFunctional(ontology))
				out.println("Inverse functional property");
			while (y.hasNext())
			{

				out.println(y.next()+"object property");
			}
		} else {

			// http://dbpedia.org/ontology/otherOccupation obj property
			// http://dbpedia.org/ontology/goalsInLeague data property

			OWLDataProperty odp = fac.getOWLDataProperty(i);


			Set<OWLDataRange> odr= odp.getRanges(ontology);

			Iterator<OWLDataRange> odi = odr.iterator();
			out.println(odr.isEmpty());
			if (!odr.isEmpty()){
				if (odp.isFunctional(ontology))
					out.println("Functional property");
			}
			while (odi.hasNext())
			{
				out.println(odi.next()+"data Property");
			}
		}


		//NodeSet <OWLClass> a = reasoner.getObjectPropertyRanges(ope, false);
		/*NodeSet<OWLDataProperty> a = reasoner.getSubDataProperties(op, false);
   	  Set<OWLDataProperty> values = a.getFlattened();
   	  for(OWLDataProperty ind : values) {
   		  if (ind.isOWLObjectProperty())
   		  {
   			  OWLObjectProperty v = ind.asOWLObjectProperty();

             out.println("isitso " + ind);
   	  }
   		  out.println("isitso " + ind); 
         }*/
		//   printHierarchy(reasoner, clazz, 0);
		/* Now print out any unsatisfiable classes */
		/*    for (OWLClass cl : ontology.getClassesInSignature()) {
               assert cl != null;

               if (!reasoner.isSatisfiable(cl)) {
                   out.println("XXX: " + labelFor(cl));
               }
           }
		 */  reasoner.dispose();
	}
	private void printHierarchy(@Nonnull OWLReasoner reasoner, @Nonnull OWLClass clazz, int level) throws OWLException {
		/*
		 * Only print satisfiable classes -- otherwise we end up with bottom
		 * everywhere
		 */
		if (reasoner.isSatisfiable(clazz)) {
			for (int i = 0; i < level * INDENT; i++) {
				out.print(" ");
			}
			out.println("temp"+labelFor(clazz));

		}

		/* Find the children and recurse */
		for (OWLClass child : reasoner.getSubClasses(clazz, true).getFlattened()) {
			if (!child.equals(clazz)) {
				printHierarchy(reasoner, child, level + 1);


			}
		}
	}

    private String labelFor(@Nonnull OWLClass clazz) {
        
         // Use a visitor to extract label annotations
         
        Label_Extractor le = new Label_Extractor();
     //   for (OWLAnnotation anno : getAnnotationObjects(clazz, ontology)) {
       //     anno.accept(le);
      //  }
       
        //Print out the label if there is one. If not, just use the class URI 
        if (le.getResult() != null) {
            return le.getResult();
        } else {
            return clazz.getIRI().toString();
        }
    }
}
