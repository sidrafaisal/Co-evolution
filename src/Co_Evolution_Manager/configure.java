package Co_Evolution_Manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Conflict_Finder.conflicts_Finder;
import Conflict_Resolver.resolver;
import Conflict_Resolver.statistics;

public class configure {
	public static List<String> predicateList;
	public static String initialTarget;
	public static String newTarget;
	public static String sourceAdditionsChangeset;
	public static String sourceDeletionsChangeset; 
	public static String targetAdditionsChangeset;
	public static String targetDeletionsChangeset;
	public static String fileSyntax;
	public static String predicates;
	public static String ontology;

	private static OWLOntologyManager manager;

	@Nonnull
	private static OWLOntology OWLOntology;
	
	public void configureFiles (String sa, String sd, String ta, String td, String t) {

		if( !isEmpty (t))
			setinitialTarget(t);
		else
			setinitialTarget(null);	

		if( !isEmpty (sa))
			setsourceAdditionsChangeset(sa);
		else
			setsourceAdditionsChangeset(null);

		if( !isEmpty (sd))
			setsourceDeletionsChangeset(sd);
		else
			setsourceDeletionsChangeset(null);

		if( !isEmpty (ta))
			settargetAdditionsChangeset(ta);
		else
			settargetAdditionsChangeset(null);

		if( !isEmpty (td))
			settargetDeletionsChangeset(td);
		else
			settargetDeletionsChangeset(null);	
		
		setnewTarget("newtarget");				

		File nt = new File(newTarget);
		if(!nt.exists())
			try {				
				nt.createNewFile();
			} catch (IOException e) {				
				e.printStackTrace();
			}
	
	}
	
	public configure (String strat, String synt, String p, String o) {

		// set the files to be used by other classes						
		predicateList = new ArrayList<String>();
		setfileSyntax(synt);
		setPredicates (p);
		setOntology(o);

		Co_Evolution_Manager.strategy.setStrategy(strat);
		if (strat.equals("syncsourceNkeeplocalBnotconflicts") || strat.equals("syncsourceNkeeplocalWresolvedconflicts")) {
			checkPredicateType ();
			if (strat.equals("syncsourceNkeeplocalWresolvedconflicts")) {
			System.out.println("For manual resolution, press 0. For auto resolution, press 1.");	
			String r = Co_Evolution_Manager.main.scanner.nextLine();

			if (r.equals("0")) {
				Conflict_Resolver.manual_Selector.select();
				resolver.manual_selector = true;
			}
			else if (r.equals("1")) {
				Conflict_Resolver.function_Auto_Selector.select();
				resolver.auto_selector = true;
			}
			}
		}
		
		Conflict_Finder.source_Delta.setPredicateFunctionUseCounter ();
		//save(strat);				
	}
	
	
	public static void checkPredicateType () {
		try{
			getPredicates (); 
			
			@Nonnull 
			File f = new File(Co_Evolution_Manager.configure.ontology);
			manager = OWLManager.createOWLOntologyManager();
			OWLOntology = manager.loadOntologyFromOntologyDocument(f);
			
			for (String predicate : predicateList) 									
				checkProperty(predicate);
					
		} catch (OWLException e) {
			e.printStackTrace();
		}
	}
	
	
	private static void checkProperty(String p) {
		OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(OWLOntology);
		OWLDataFactory fac = manager.getOWLDataFactory();
		IRI i = IRI.create(p); 

		OWLObjectProperty objprop = fac.getOWLObjectProperty(i);
		Set<OWLClassExpression> classexpr= objprop.getRanges(OWLOntology);
		
		if (!classexpr.isEmpty()){
			if (objprop.isFunctional(OWLOntology) ) //|| objprop.isInverseFunctional(OWLOntology)) 	
				statistics.predicateType.put(p, "F"); 
		} else {

			OWLDataProperty dataprop = fac.getOWLDataProperty(i);
			Set<OWLDataRange> datarange = dataprop.getRanges(OWLOntology);

			if (!datarange.isEmpty()){
				if (dataprop.isFunctional(OWLOntology))
					statistics.predicateType.put(p, "F"); 
			}
		}		
		reasoner.dispose();
	}
	
	public static void getPredicates () {

		BufferedReader br;
		try { 
			br = new BufferedReader(new FileReader(predicates));
			Conflict_Finder.source_Delta.predicateFunctionUseCounter = new HashMap<String, Integer>();
			String line = null;

		while ((line = br.readLine()) != null) 
			predicateList.add(line);		
		br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	public static void save (String strategy) {				
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Co-evolution");
			doc.appendChild(rootElement);
			rootElement.setAttribute("strategy", strategy);
			rootElement.setAttribute("inputTarget", initialTarget);
			rootElement.setAttribute("outputTarget", initialTarget);

			Element sd = doc.createElement("sourceDelta");
			sd.setAttribute("addition", sourceAdditionsChangeset);
			sd.setAttribute("deletion", sourceDeletionsChangeset);
			rootElement.appendChild(sd);

			Element td = doc.createElement("TargetDelta");
			td.setAttribute("addition", targetAdditionsChangeset);
			td.setAttribute("deletion", targetDeletionsChangeset);
			rootElement.appendChild(td);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("config.xml"));

			transformer.transform(source, result);

		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
	}

	public static void setfileSyntax(String s){
		fileSyntax = s;
	}

	public static void setinitialTarget(String s){
		initialTarget = s;
	}

	public static void setnewTarget(String s){
		newTarget = s;
	}

	public static void setsourceAdditionsChangeset(String s){
		sourceAdditionsChangeset = s;
	}
	
	public static void setPredicates (String p) {
		predicates = p;
	}

	public static void setsourceDeletionsChangeset(String s){
		sourceDeletionsChangeset = s;
	}

	public static void settargetAdditionsChangeset(String s){
		targetAdditionsChangeset = s;
	}

	public static void settargetDeletionsChangeset(String s){
		targetDeletionsChangeset = s;
	}
	
	public static void setOntology(String o) {
		ontology = o;
	}
	public static boolean isEmpty(String f) {
		if (f!=null){
		File file = new File(f);
		if(file.length()<=0)
			return true;
		else
			return false;
	} else
			return false;
}
}