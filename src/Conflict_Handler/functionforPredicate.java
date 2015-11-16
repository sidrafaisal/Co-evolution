package Conflict_Handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//import org.apache.jena.rdf.model.Property;
//import org.apache.jena.rdf.model.ResourceFactory;

public class functionforPredicate {

	public static Map<String, String> resolutionFunctionforPredicate  = new HashMap<String, String>();
	
	public static void select(){
//	    Property p = ResourceFactory.createProperty("<http://dbpedia.org/property/goals>");
	    
	    int size = resolutionFunctions.availableResolutionFunctions.length;
	    System.out.println("Avaialable resolution functions");
		for (int i = 0; i < size; i++) {
			System.out.print(resolutionFunctions.availableResolutionFunctions [i] + " . ");
		}
		
		System.out.println("\nEnter a resolution function for this property");
		Scanner scanner = new Scanner(System.in);
		String rf = scanner.nextLine();
		scanner.close();
				 
		set ("<http://dbpedia.org/property/goals>", rf);
	}
	
	public static void set (String p, String rf){		
		resolutionFunctionforPredicate.put(p, rf);  
	}
	
	public static String get (String p){		
		return resolutionFunctionforPredicate.get(p);  
	}
	
}
	
