package Conflict_Handler;

import java.util.Arrays;
import java.util.Random;

public class resolutionFunctions {
	
		public static String [] availableResolutionFunctions = {"sum", "average", "median", "variance", "stdDev", "max", "min", "any", "first", 
			"shortest", "longest", "concatenation", "bestSource", "globalVote", "latest", "threshold", "best", "topN", "chooseDepending", 
			"chooseCorresponding", "mostComplete"};	
		
		public static String apply (String function, String [] args){
			switch (function) {
				case "sum": {
					Double d = sum(args);
					return (String.valueOf(d));
					}
				
				case "average":{
					Double d = average(args);
					return (String.valueOf(d));
					}
				
				case "median": {
					Double d = median(args);
					return (String.valueOf(d));
					}
				
				case "variance": {
					Double d = variance(args);
					return (String.valueOf(d));
					}
				
				case "stdDev": {
					Double d = stdDev(args);
					return (String.valueOf(d));
					}
				
				case "max": {
					Double d = max(args);
					return (String.valueOf(d));
					}
				
				case "min": {
					Double d = min(args);
					return (String.valueOf(d));
					}
				
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
		
	//Mathematical functions
		public static double max (String[] args) {
			double max = Double.MIN_VALUE;
			for (String input : args)
			{
				double value = Double.parseDouble(input); 
				if( value > max) 
			         max = value;
			}
			return max;
		}
		
			public static double min (String[] args) {
				double min = Double.MAX_VALUE;
				for (String input : args)
				{
					double value = Double.parseDouble(input); 
					if(value < min) 
				         min = value;
				}
				return min;
			}
			
			public static double sum (String[] args) {
				double sum = 0;
				for (String input : args)
					sum += Double.parseDouble(input);
				return sum;
			}

			public static double average (String[] args) {
				double average = 0;
				average = sum (args) / args.length;
				return average;
			}
			
			public static double median (String[] args) {	
				int size = args.length;
				double input [] = new double [size];
				double median = 0;
				
				for (int i = 0; i < size; i++)
					input [i] = Double.parseDouble(args[i]);		
				
				Arrays.sort(input);
				
				if (size % 2 == 0)
					median = ((double)input[size/2] + (double)input[size/2 - 1])/2;
				else
					median = (double) input[size/2];
				return median;
			}
			
			public static double variance(String[] args) {
				int size = args.length;
				double input [] = new double [size];			
				for (int i = 0; i < size; i++)
					input [i] = Double.parseDouble(args[i]);	
				
			    double avg = average(args);
			    double variance = 0;
			    for(double value : input)
			    	variance += (avg-value)*(avg-value);
			    return variance/size;
			}

			public static double stdDev(String[] args) {
			    return Math.sqrt(variance(args));
			}
}
