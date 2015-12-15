package Conflict_Resolver;

import Conflict_Resolver.statistics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class manual_Selector {		

	public static String filename = "";

	public static void select () {
		
		Map<String, String> resolutionFunctionforPredicate  = new HashMap<String, String>();	
		
		//int pos	= Co_Evolution_Manager.configure.newTarget.indexOf(".");.substring(0, pos)
		filename = "manual_FunctionSelector_"+ Co_Evolution_Manager.configure.newTarget+".xml";
		File file = new File(filename);

		if(!file.exists()) 	{
			int size = resolver.availableResolutionFunctions.length;
			System.out.println("Avaialable resolution functions:");
			for (int i = 0; i < size; i++) 
				System.out.print(resolver.availableResolutionFunctions [i] + " . ");
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(Co_Evolution_Manager.configure.predicates));

				String line = null;
				while ((line = br.readLine()) != null) {
					System.out.println("\nEnter a resolution function for this property: "+ line);

					String rf = Co_Evolution_Manager.main.scanner.nextLine();
					resolutionFunctionforPredicate.put(line, rf);					 					
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			create(resolutionFunctionforPredicate);		
		}
		else 
			populate();	
	}

	public static void create (Map<String, String> resolutionFunctionforPredicate) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Predicate_Function");
			doc.appendChild(rootElement);

			Iterator<String> keySetIterator = resolutionFunctionforPredicate.keySet().iterator();
			while(keySetIterator.hasNext()){	
				String key = keySetIterator.next();
				String value = resolutionFunctionforPredicate.get(key);

				Element st = doc.createElement("Predicate");
				rootElement.appendChild(st);
				st.setAttribute("name", key);					
				st.setAttribute("function", value);

				//set additional info required by some specific predicates
				if (value.equals("bestSource")) {

					System.out.println("\nEnter your first preference: source or target");
					String prf = Co_Evolution_Manager.main.scanner.nextLine();			
					st.setAttribute("preference", prf);
					statistics.preferedSourceforPredicate.put(key, prf);

				} else if (value.equals("mostComplete")) {

					String sourceWithFewerBlanks = Conflict_Resolver.statistics.findBlankNodes(value);
					statistics.preferedSourceforPredicate.put(value, sourceWithFewerBlanks);

				} else if (value.equals("globalVote")) {
					Conflict_Resolver.statistics.globalVote(value);					
				}

				statistics.resolutionFunctionforPredicate.put (key, value);			
			}		
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);

			StreamResult result = new StreamResult(new File(filename));
			transformer.transform(source, result);

			auto_Selector.record ( );

		} catch (DOMException|ParserConfigurationException|TransformerException e) {
			System.out.println(""+e);
			e.printStackTrace();
		}

	}

	public static void populate(){		
		try {
			File fXmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList predList = doc.getElementsByTagName("Predicate");

			for (int temp = 0; temp < predList.getLength(); temp++) {
				Node pred = predList.item(temp);	    				

				if (pred.getNodeType() == Node.ELEMENT_NODE) {
					Element p = (Element) pred;
					String predicate = p.getAttribute("name");
					String function = p.getAttribute("function");

					if (function.equals("bestSource"))
						statistics.preferedSourceforPredicate.put(predicate, p.getAttribute("preference"));

					else if (function.equals("mostComplete")) {

						String sourceWithFewerBlanks = Conflict_Resolver.statistics.findBlankNodes(predicate);
						statistics.preferedSourceforPredicate.put(predicate, sourceWithFewerBlanks);

					} else if (function.equals("globalVote")) {
						Conflict_Resolver.statistics.globalVote(predicate);					
					}

					statistics.resolutionFunctionforPredicate.put(predicate, function);
				}
			}
		} catch (Exception e) {
			System.out.println(""+e);
			e.printStackTrace();
		}
	}

	public static void set (String p, String rf){	
		statistics.resolutionFunctionforPredicate.put(p, rf);  
	}

	public static String get (String p){		
		return statistics.resolutionFunctionforPredicate.get(p);  
	}

}

