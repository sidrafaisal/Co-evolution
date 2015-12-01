package Conflict_Resolver;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.util.FileManager;

import Co_Evolution_Manager.configure;

public class statistics {

	public static Map<String, String> preferedSourceforPredicate  = new HashMap<String, String>();
	public static Map<String, String> resolutionFunctionforPredicate  = new HashMap<String, String>();	
	public static Map<String, String> mostFrequentValue = new HashMap<String, String>();

	public static void globalVote ( String predicate ) {

		Map<String, Integer> max  = new HashMap<String, Integer>();
		Property p = ResourceFactory.createProperty(predicate);

		Model smodel = FileManager.get().loadModel(configure.sourceAdditionsChangeset, configure.fileSyntax);
		ResIterator r = smodel.listResourcesWithProperty(p);
		while (r.hasNext()) {
			Resource res = r.nextResource();
			NodeIterator sni = smodel.listObjectsOfProperty(res, p);
			while (sni.hasNext()){
				String val = sni.nextNode().asLiteral().getValue().toString();		
				if(!max.containsKey(val))
					max.put(val, 1);
				else
				{
					Integer i = max.get(val) ;
					max.remove(val,i);
					max.put(val, i + 1);		
				}
			}
		}
		smodel.close();	

		Model tmodel = FileManager.get().loadModel(configure.targetAdditionsChangeset, configure.fileSyntax);	
		ResIterator tr = tmodel.listResourcesWithProperty(p);
		while (tr.hasNext()) {
			Resource res = tr.nextResource();
			NodeIterator tni = tmodel.listObjectsOfProperty(res, p);
			while (tni.hasNext()){
				String val = tni.nextNode().asLiteral().getValue().toString();		
				if(!max.containsKey(val))
					max.put(val, 1);
				else
				{
					Integer i = max.get(val);
					max.remove(val, i);
					max.put(val, i + 1);		
				}
			}
		}
		tmodel.close();	

		Integer highest = 0;
		String value = "";

		for (String v : max.keySet()) {
			if (highest < max.get(v)) {
				highest = max.get(v);
				value = v;
			}
		}		
		mostFrequentValue.put(predicate, value);
	}

	public static String findBlankNodes ( String predicate ) {

		int blanksInSource = 0;
		int blanksInTarget = 0;

		Property p = ResourceFactory.createProperty(predicate);

		Model smodel = FileManager.get().loadModel(configure.sourceAdditionsChangeset, configure.fileSyntax);
		ResIterator r = smodel.listResourcesWithProperty(p);
		while (r.hasNext()) {
			Resource res = r.nextResource();
			NodeIterator sni = smodel.listObjectsOfProperty(res, p);
			while (sni.hasNext()){
				if (sni.nextNode().asNode().isBlank())
					blanksInSource++;
			}
		}
		smodel.close();	

		Model tmodel = FileManager.get().loadModel(configure.targetAdditionsChangeset, configure.fileSyntax);			
		ResIterator tr = tmodel.listResourcesWithProperty(p);
		while (tr.hasNext()) {
			Resource res = tr.nextResource();
			NodeIterator tni = tmodel.listObjectsOfProperty(res, p);
			while (tni.hasNext()){
				if (tni.nextNode().asNode().isBlank())		
					blanksInTarget++;
			}
		}
		tmodel.close();

		if(blanksInSource <= blanksInTarget)
			return "source";
		else
			return "target";
	}

}
