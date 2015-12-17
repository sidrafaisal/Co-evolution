package Conflict_Resolver;

//todo auto selection
/*We prefer function longest for string to avoid loss of information. 
For numbers data type, we prefer function max to keep the up-to-date value.
For URIs, we pick the first value.
*/
import java.io.BufferedReader;
import java.io.File;
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
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

@SuppressWarnings("javadoc")
public class function_Auto_Selector {

	private static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	@Nonnull
	private static OWLReasonerFactory reasonerFactory;
	@Nonnull
	private static OWLOntology ontology;
	private final static PrintStream out = System.out;
	
	/*public static List<String> getPredicatest () {
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
	}*/	

	public static void select() {
		try{
			Map<String, String> resolutionFunctionforPredicate  = new HashMap<String, String>();
			List<String> predicateList = Co_Evolution_Manager.configure.predicateList; // get the required predicates to be extracted from auto_selector file

			@Nonnull 
			File documentIRI = new File(Co_Evolution_Manager.configure.ontology);
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
			//while (expr.hasNext())
				//out.println(expr.next()+"object property");
			
		} else {

			OWLDataProperty dataprop = fac.getOWLDataProperty(i);
			Set<OWLDataRange> datarange = dataprop.getRanges(ontology);
			Iterator<OWLDataRange> range = datarange.iterator();

			if (!datarange.isEmpty()){
				if (dataprop.isFunctional(ontology))
					value = "0";
			}
		//	while (range.hasNext())
			//	out.println(range.next()+"data Property");
		}
		
		reasoner.dispose();
		return value;
	}
}
