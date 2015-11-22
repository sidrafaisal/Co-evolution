package Conflict_Handler;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import Conflict_Handler.gFunctions;
import Conflict_Handler.mathFunctions;

public class resolutionFunctions {
	
		public static String [] availableResolutionFunctions = {"sum", "average", "median", "variance", "stdDev", "max", "min", "any", "first", 
			"shortest", "longest", "concatenation", "bestSource", "globalVote", "latest", "threshold", "best", "topN", "chooseDepending", 
			"chooseCorresponding", "mostComplete"};	
		
		public static String apply (String function, String [] args, String type){

			String val = "";	

			if(function.equals("sum") || function.equals("average") || function.equals("max") || function.equals("min") ||
					function.equals("stdDev") || function.equals("variance") || function.equals("median")) {

				if (type.equals("int") || type.equals("unsignedShort")) 
				{
					String s = functions (function, args, type); 
					Double d = Double.parseDouble(s);
					val = d.intValue() + "";	

				} else if (type.equals("unsignedLong") || type.equals("positiveInteger") || type.equals("nonPositiveInteger") || 
						type.equals("nonNegativeInteger") || type.equals("negativeInteger") || type.equals("integer")) {

					val = mathFunctions.mathforBI (function, args);

				} else if (type.equals("float")) {

					String s = functions (function, args, type); 
					Double d = Double.parseDouble(s);
					val = d.floatValue() + "";

				} else if (type.equals("unsignedByte") || type.equals("short")) {

					String s = functions (function, args, type); 
					Double d = Double.parseDouble(s);
					val = d.shortValue() + "";

				} else if (type.equals("unsignedInt") || type.equals("long")) {

					String s = functions (function, args, type); 
					Double d = Double.parseDouble(s);
					val = d.longValue() + "";

				}/*else if (type.equals("anyURI") || type.equals("anySimpleType") || type.equals("ENTITIES") || type.equals("ENTITY") || 
					type.equals("token") || type.equals("string") || type.equals("normalizedString") || type.equals("NMTOKENS") || 
					type.equals("NMTOKEN") || type.equals("NCName") || type.equals("Name") || type.equals("IDREFS") || 
					type.equals("ID") || type.equals("IDREF") || type.equals("language")){
				   val = rv;
			} else if (type.equals("double")) {
				   val = rv;
			} */
			} else {

				val = functions (function, args, type); 
			}		
			return val;	
		}

				
		public static String functions (String function, String [] args, String type){
			switch (function) {
				case "sum": {
					Double d = mathFunctions.sum(args);
					return (String.valueOf(d));
					}
				
				case "average":{
					Double d = mathFunctions.average(args);
					return (String.valueOf(d));
					}
				
				case "median": {
					Double d = mathFunctions.median(args);
					return (String.valueOf(d));
					}
				
				case "variance": {
					Double d = mathFunctions.variance(args);
					return (String.valueOf(d));
					}
				
				case "stdDev": {
					Double d = mathFunctions.stdDev(args);
					return (String.valueOf(d));
					}
				
				case "max": {
					Double d = mathFunctions.max(args);
					return (String.valueOf(d));
					}
				
				case "min": {
					Double d = mathFunctions.min(args);
					return (String.valueOf(d));
					}
				
				case "any":
					return gFunctions.any(args);
				
				case "first":
					return gFunctions.first(args); 
				
				case "shortest":
					return gFunctions.shortest(args);

				case "longest":
					return gFunctions.longest(args);
				
				case "concatenation":
					return gFunctions.concatenation(args);	
				
				case "bestSource":
					return gFunctions.bestSource(args);
				
				case "globalVote":
					return gFunctions.globalVote(args);
				
				case "latest":
					return gFunctions.latest(args);
				
				case "threshold":
					return gFunctions.threshold(args);
				
				case "best":
					return gFunctions.best(args);
				
				case "topN":
					return gFunctions.topN(args);
				
				case "chooseDepending":
					return gFunctions.chooseDepending(args);
				
				case "chooseCorresponding":
					return gFunctions.chooseCorresponding(args);
				
				case "mostComplete":
					return gFunctions.mostComplete(args);
				
				default:
					return " ";
			}
		}
	

		
	
}
