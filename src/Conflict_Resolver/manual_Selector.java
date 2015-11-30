package Conflict_Resolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class manual_Selector {		

	public static Map<String, String> resolutionFunctionforPredicate  = new HashMap<String, String>();
	public static String filename = "";
	
	public static void select () {

		int pos	= Co_Evolution_Manager.configure.newTarget.indexOf(".");
		filename = "Function"+ Co_Evolution_Manager.configure.newTarget.substring(0, pos)+".xml";
		File file = new File(filename);

		if(!file.exists()) 	
			create();									
		else 
			populate();	
	}

	public static void create () {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Predicate_Function");
			doc.appendChild(rootElement);

			int size = resolver.availableResolutionFunctions.length;
			System.out.println("Avaialable resolution functions");
			for (int i = 0; i < size; i++) 
				System.out.print(resolver.availableResolutionFunctions [i] + " . ");

			BufferedReader br = new BufferedReader(new FileReader("predicates.txt"));

			String line = null;

			while ((line = br.readLine()) != null) {
				System.out.println("\nEnter a resolution function for this property: "+ line);

				String rf = Co_Evolution_Manager.main.scanner.nextLine();

				set (line, rf);	
				Element st = doc.createElement("Predicate");
				rootElement.appendChild(st);
				st.setAttribute("name", line);					
				st.setAttribute("function", rf);			
			}

			br.close();

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			
			StreamResult result = new StreamResult(new File(filename));
			transformer.transform(source, result);
			
			auto_Selector.record ( resolutionFunctionforPredicate );
			
		} catch (IOException|DOMException|ParserConfigurationException|TransformerException e) {
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
	
					set (predicate, function);
					System.out.println(predicate+function);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void set (String p, String rf){	
		resolutionFunctionforPredicate.put(p, rf);  
	}

	public static String get (String p){		
		return resolutionFunctionforPredicate.get(p);  
	}

}

