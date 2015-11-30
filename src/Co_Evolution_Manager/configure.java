package Co_Evolution_Manager;

import java.io.File;
import java.io.IOException;

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

public class configure {

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

		setsourceAdditionsChangeset(sa);
		setsourceDeletionsChangeset(sd);

		settargetAdditionsChangeset(ta);
		settargetDeletionsChangeset(td);

		Co_Evolution_Manager.strategy.setStrategy(strat);

		setnewTarget("newtarget."+fileSyntax);
		File nt = new File(newTarget);
		if(!nt.exists())
			try {
				nt.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

		save(strat);				
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
}
