package Conflict_Resolver;

import java.io.File;
import java.util.HashMap;
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


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class auto_Selector {

	public static int numberofIterations;
	public static Map<String, String> preferedSourceforPredicate  = new HashMap<String, String>();
	public static Map<String, String> resolutionFunctionforPredicate  = new HashMap<String, String>();

	public static void record (Map<String, String> r) {

		File file = new File("auto_FunctionSelector.xml");

		if(!file.exists()) 	
			create(r);									
		else 
			modify(r);			
	}

	public static void set (String p, String rf){	
		resolutionFunctionforPredicate.put(p, rf);  
	}


	public static void select () {	
		try {
			//create config file for future use
			DocumentBuilderFactory mFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder mBuilder = mFactory.newDocumentBuilder();

			Document mdoc = mBuilder.newDocument();
			Element rootElement = mdoc.createElement("Predicate_Function");
			mdoc.appendChild(rootElement);
			
			// read auto-selector
			File fXmlFile = new File("auto_FunctionSelector.xml");
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
					NodeList fList = p.getElementsByTagName("Function");

					Node fun = fList.item(0);
					Element f = (Element) fun;

					String prefferedfname = f.getAttribute("name");					
					Double maxscore = Double.parseDouble(f.getAttribute("score"));

					for (int temps = 1; temps < fList.getLength(); temps++) {
						fun = fList.item(temps);
						f = (Element) fun;

						Double score = Double.parseDouble(f.getAttribute("score"));
						if (score > maxscore){
							maxscore = score;
							prefferedfname = f.getAttribute("name");
						}
					}
					
					Element st = mdoc.createElement("Predicate");
					rootElement.appendChild(st);
					st.setAttribute("name", predicate);					
					st.setAttribute("function", prefferedfname);
					
					if (prefferedfname.equals("bestSource")){
						System.out.println("\nEnter your first preference: source or target");
						String prf = Co_Evolution_Manager.main.scanner.nextLine();
						
						preferedSourceforPredicate.put(predicate, prf);
						st.setAttribute ("preference", prf);
					}
					set (predicate, prefferedfname);
				}
			}
			// write the content into xml file
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						transformer.setOutputProperty(OutputKeys.INDENT, "yes");
						transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
						DOMSource source = new DOMSource(mdoc);

						int pos	= Co_Evolution_Manager.configure.newTarget.indexOf(".");
						String filename = "manual_FunctionSelector_"+ Co_Evolution_Manager.configure.newTarget.substring(0, pos)+".xml";
						
						StreamResult result = new StreamResult(new File(filename));
						transformer.transform(source, result);					
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void modify (Map<String, String> r) {
		try {
			File fXmlFile = new File("auto_FunctionSelector.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			int numberofIterations = Integer.parseInt(doc.getDocumentElement().getAttribute("numberofIterations")) ;	 

			NodeList predList = doc.getElementsByTagName("Predicate");

			for (int temp = 0; temp < predList.getLength(); temp++) {
				Node pred = predList.item(temp);	    				
				if (pred.getNodeType() == Node.ELEMENT_NODE) {
					Element p = (Element) pred;
					String predicate = p.getAttribute("name");
					String ifunction = r.get(predicate);

					NodeList fList = p.getElementsByTagName("Function");

					for (int temps = 0; temps < fList.getLength(); temps++) {
						Node fun = fList.item(temps);
						Element f = (Element) fun;
						Double score = Double.parseDouble(f.getAttribute("score"));

						String function= f.getAttribute("name");
						if (function.equals(ifunction))
							score = (score * numberofIterations + 1) / (numberofIterations + 1);
						else
							score = (score * numberofIterations ) / (numberofIterations + 1);
						f.setAttribute("score", score.toString());
					}
				}
			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();


			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("auto_FunctionSelector.xml"));

			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void create (Map<String, String> r) {
		numberofIterations = 1;

		int size = resolver.availableResolutionFunctions.length;

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("auto_FunctionSelector");
			rootElement.setAttribute("numberofIterations", Integer.toString(numberofIterations));
			doc.appendChild(rootElement);

			for (String key: r.keySet()) {
				String predicate = key;

				Element st = doc.createElement("Predicate");
				rootElement.appendChild(st);

				st.setAttribute("name", predicate);					

				String selectedFunction = r.get(key);
				for (int i = 0; i< size; i++) {
					Double score;
					String availableFunction = resolver.availableResolutionFunctions[i];
					if (selectedFunction.equals(availableFunction))
						score = 1.0;
					else
						score =	0.0; 						

					Element af = doc.createElement("Function");
					af.setAttribute("name", availableFunction);
					af.setAttribute("score", score.toString());
					st.appendChild(af);
				}
			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("auto_FunctionSelector.xml"));

			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

	}
}
