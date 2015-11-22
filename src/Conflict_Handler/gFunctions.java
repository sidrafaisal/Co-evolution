package Conflict_Handler;

import java.util.Arrays;
import java.util.Random;

public class gFunctions {
	/*								Resolution Functions				*/	
	
	public static String bestSource (String[] args) {

		return args[0];
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
