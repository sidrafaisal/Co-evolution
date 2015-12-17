package Co_Evolution_Manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
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
	public static String SyncSrcAdd;
	public static String SyncSrcDel;
	
	public static long Triples_in_Source_Addition_Changeset = 0;
	public static long Triples_in_Source_Deletion_Changeset = 0;
	public static long Triples_in_Target_Addition_Changeset = 0;
	public static long Triples_in_Target_Deletion_Changeset = 0;
	public static long Triples_for_Sync_Source_AddChangeset = 0;
	public static long Triples_for_Sync_Source_DelChangeset = 0;
	
	private static OWLOntologyManager manager;

	public static Map<String, String> strategyforPredicate  = new HashMap<String, String>();	
	@Nonnull
	private static OWLOntology OWLOntology;

	public void configureFiles (String sa, String sd, String ta, String td, String t) {
System.out.println(sa+" "+sd+" " + ta+ " " + td + " " + t);
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
		
		setSyncSrcAdd("SyncSrcAdd");				

		nt = new File(SyncSrcAdd);
		if(!nt.exists())
			try {				
				nt.createNewFile();
			} catch (IOException e) {				
				e.printStackTrace();
			}
		
		setSyncSrcDel("SyncSrcDel");				

		nt = new File(SyncSrcDel);
		if(!nt.exists())
			try {				
				nt.createNewFile();
			} catch (IOException e) {				
				e.printStackTrace();
			}
		updateDatasetSize (); 
	}

	public configure (String synt, String p, String o) { //String strat, 

		// set the files to be used by other classes						
		predicateList = new ArrayList<String>();
		setfileSyntax(synt);
		setPredicates (p);
		setOntology(o);
		
		System.out.println("Allowed strategies: syncsourceNkeeplocalBnotconflicts, " + "syncsourceNkeeplocalWresolvedconflicts, " +
		"syncsourceNignorelocal, " + "nsyncsourceBkeeplocal," + "nsyncsourceNignorelocal");
		 
		System.out.println("Select strategy for each predicate");

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(Co_Evolution_Manager.configure.predicates));

			String line = null;
			while ((line = br.readLine()) != null) {
/*				System.out.println("\nEnter a strategy for this property: "+ line);

				String strategy = Co_Evolution_Manager.main.scanner.nextLine();
				Co_Evolution_Manager.configure.strategyforPredicate.put(line, strategy);					 					
	*/		}
			br.close();

			Co_Evolution_Manager.configure.strategyforPredicate.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "syncsourceNkeeplocalWresolvedconflicts");
			Co_Evolution_Manager.configure.strategyforPredicate.put("http://xmlns.com/foaf/0.1/depiction", "syncsourceNkeeplocalWresolvedconflicts");
			Co_Evolution_Manager.configure.strategyforPredicate.put("http://dbpedia.org/ontology/abstract", "syncsourceNkeeplocalWresolvedconflicts");
			Co_Evolution_Manager.configure.strategyforPredicate.put("http://xmlns.com/foaf/0.1/name", "syncsourceNkeeplocalWresolvedconflicts");
			Co_Evolution_Manager.configure.strategyforPredicate.put("http://dbpedia.org/ontology/nationality", "syncsourceNignorelocal");
			Co_Evolution_Manager.configure.strategyforPredicate.put("http://dbpedia.org/property/party", "nsyncsourceBkeeplocal");
			Co_Evolution_Manager.configure.strategyforPredicate.put("http://dbpedia.org/property/office", "nsyncsourceBkeeplocal");
			
			boolean isMixed = false;
			String found = null;
			List<String> temp_predicateList = new ArrayList<String>();
			List<String> temp1_predicateList = new ArrayList<String>();
			for (String predicate: Co_Evolution_Manager.configure.strategyforPredicate.keySet()){
				if (found==null)
					found = Co_Evolution_Manager.configure.strategyforPredicate.get(predicate);
				else if (!found.equals(Co_Evolution_Manager.configure.strategyforPredicate.get(predicate))) {
					isMixed = true;
					found = Co_Evolution_Manager.configure.strategyforPredicate.get(predicate);
				}
					if (found.equals("syncsourceNkeeplocalWresolvedconflicts")) 
						temp_predicateList.add(predicate);
					if (found.equals("syncsourceNkeeplocalBnotconflicts")) 
						temp1_predicateList.add(predicate);
					
				
			}
			if (!isMixed){
				if (found.equals("syncsourceNignorelocal"))
					Co_Evolution_Manager.strategy.setStrategy("syncsourceNignorelocal");
				else if (found.equals("syncsourceNkeeplocalBnotconflicts"))
					Co_Evolution_Manager.strategy.setStrategy("syncsourceNkeeplocalBnotconflicts");
				else if (found.equals("syncsourceNkeeplocalWresolvedconflicts"))
					Co_Evolution_Manager.strategy.setStrategy("syncsourceNkeeplocalWresolvedconflicts");
				else if (found.equals("nsyncsourceBkeeplocal"))	
					Co_Evolution_Manager.strategy.setStrategy("nsyncsourceBkeeplocal");

				if (found.equals("syncsourceNkeeplocalBnotconflicts") || found.equals("syncsourceNkeeplocalWresolvedconflicts")) {
					checkPredicateType ();
					if (found.equals("syncsourceNkeeplocalWresolvedconflicts")) {
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
			} else {
				checkPredicateType (); 
				predicateList = temp_predicateList;
				Co_Evolution_Manager.strategy.setStrategy("mixed");
				if (!predicateList.isEmpty()) {
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
				for (String str : temp1_predicateList) 
					predicateList.add(str);
			}		
			Conflict_Finder.source_Delta.setPredicateFunctionUseCounter ();	
		} catch (IOException e) {
			e.printStackTrace();
		}

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
	public static void setSyncSrcAdd(String s){
		SyncSrcAdd = s;
	}
	public static void setSyncSrcDel(String s){
		SyncSrcDel = s;
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
	public static long getDatasetSize (String filename) {
		long number_Of_Triples  = 0;
		if (filename != null) {
			Model model = FileManager.get().loadModel(filename, configure.fileSyntax);
			number_Of_Triples = model.size();
			model.close();
		}	
		return number_Of_Triples;
	}
	public static void updateDatasetSize(){
		Triples_in_Source_Addition_Changeset = Triples_in_Source_Addition_Changeset + getDatasetSize (sourceAdditionsChangeset);				
		Triples_in_Source_Deletion_Changeset = Triples_in_Source_Deletion_Changeset + getDatasetSize (sourceDeletionsChangeset);				
		Triples_in_Target_Addition_Changeset = Triples_in_Target_Addition_Changeset + getDatasetSize (targetAdditionsChangeset); 		
		Triples_in_Target_Deletion_Changeset = Triples_in_Target_Deletion_Changeset + getDatasetSize (targetDeletionsChangeset); 	
		Triples_for_Sync_Source_AddChangeset = getDatasetSize (SyncSrcAdd);
		Triples_for_Sync_Source_DelChangeset = getDatasetSize (SyncSrcDel);
	}

}