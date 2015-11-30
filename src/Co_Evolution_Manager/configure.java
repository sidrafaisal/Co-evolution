package Co_Evolution_Manager;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class configure {
	
	public static String strategy;
	public static String initialTarget;
	public static String newTarget;
	public static String sourceAdditionsChangeset;
	public static String sourceDeletionsChangeset; 
	public static String targetAdditionsChangeset;
	public static String targetDeletionsChangeset;
	public static String fileSyntax;
	
		public configure (String sa, String sd, String ta, String td, String t, String strat, String synt) {
	
			// set the files to be used by other classes						
			   setfileSyntax(synt);
			   setinitialTarget(t);
				setnewTarget("newtarget."+fileSyntax);
				
				setsourceAdditionsChangeset(sa);
				setsourceDeletionsChangeset(sd);
				
				settargetAdditionsChangeset(ta);
				settargetDeletionsChangeset(td);
				setStrategy(strat);
				
				try {
					File nt = new File(newTarget);
					if(!nt.exists()) 
					    nt.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				save();
		}
		public static void save () {
				
		  try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Co-evolution");
			doc.appendChild(rootElement);

			Element st = doc.createElement("strategy");
			rootElement.appendChild(st);

			st.setAttribute("id", strategy);

			Element sad = doc.createElement("sourceAddDelta");
			sad.appendChild(doc.createTextNode(sourceAdditionsChangeset));
			st.appendChild(sad);

			Element sdd = doc.createElement("SourceDelDelta");
			sdd.appendChild(doc.createTextNode(sourceDeletionsChangeset));
			st.appendChild(sdd);

			Element tad = doc.createElement("TargetAddDelta");
			tad.appendChild(doc.createTextNode(targetAdditionsChangeset));
			st.appendChild(tad);

			Element tdd = doc.createElement("TargetDelDelta");
			tdd.appendChild(doc.createTextNode(targetDeletionsChangeset));
			st.appendChild(tdd);

			Element it = doc.createElement("InitialTarget");
			it.appendChild(doc.createTextNode(initialTarget));
			st.appendChild(it);
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("config.xml"));

			transformer.transform(source, result);

		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
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
		
		public static void setsourceDeletionsChangeset(String s){
			sourceDeletionsChangeset = s;
		}
		
		public static void settargetAdditionsChangeset(String s){
			targetAdditionsChangeset = s;
		}

		public static void settargetDeletionsChangeset(String s){
			targetDeletionsChangeset = s;
		}

		public static void setStrategy(String s){
			strategy = s;
			Co_Evolution_Manager.strategy.setStrategy(s);
		}
}
