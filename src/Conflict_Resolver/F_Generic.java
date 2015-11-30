package Conflict_Resolver;

import java.util.Arrays;
import java.util.Random;

import Conflict_Finder.source_Delta;

public class F_Generic {
	
	public static String Compute (String function, String [] args){
		switch (function) {
			
			case "any":
				return any(args);
			
			case "first":
				return first(args); 
			
			case "shortest":
				return shortest(args);

			case "longest":
				return longest(args);
			
			case "concatenation":
				return concatenation(args);	
			
			case "bestSource":
				return bestSource(args);
			
			case "globalVote":
				return globalVote(args);
			
			case "latest":
				return latest(args);
			
			case "threshold":
				return threshold(args);
			
			case "best":
				return best(args);
			
			case "topN":
				return topN(args);
			
			case "chooseDepending":
				return chooseDepending(args);
			
			case "chooseCorresponding":
				return chooseCorresponding(args);
			
			case "mostComplete":
				return mostComplete(args);
			
			default:
				return " ";
		}
	}
	
	/*								Resolution Functions				*/	
	
	public static String bestSource (String[] args) {
		
		String p = source_Delta.current_Predicate;
		String result, preferedsource;
		if (resolver.manual_selector == true)
			preferedsource = manual_Selector.preferedSourceforPredicate.get(p);
		else 
			preferedsource = auto_Selector.preferedSourceforPredicate.get(p);
		
		if (preferedsource.equals("source"))
			result = args[0];
		else
			result = args [1];
		
			return result;
	}	
	public static String globalVote (String[] args) {

		return args[0];
	}	
	public static String latest (String[] args) {

		return args[0];
	}	
	public static String threshold (String[] args) {

		return args[0];
	}	
	public static String best (String[] args) {

		return args[0];
	}	
	public static String topN (String[] args) {
	
		return args[0];
	}	
	public static String chooseDepending (String[] args) {

		return args[0];
	}	
	public static String chooseCorresponding (String[] args) {

		return args[0];
	}	
	public static String mostComplete (String[] args) {

		return args[0];
	}	
//requires metadata as well
	
//requires only data	
	public static String first (String[] args) {
		Arrays.sort(args);
		return args[0];
	}	

	public static String shortest (String[] args) {		
	    String shortest = any(args);

	    for (String value : args) {
	        if (value.length() < shortest.length()) 
	            shortest = value;
	    }
	    return shortest;    
	}
	
	public static String longest (String[] args) {		
	    String longest = any(args);

	    for (String value : args) {
	        if (value.length() > longest.length()) 
	            longest = value;
	    }
	    return longest;    
	}
	
	public static String any (String[] args) {
		int randomValue = new Random().nextInt(args.length);
	    return args[randomValue];		
	}

	public static String concatenation (String[] args) {
		String concatenate = args[0];
		for (int i = 1; i < args.length; i++)
		{
			concatenate += ", ";
			concatenate += args[1];			
		}
		return concatenate;
	}
}
