package Conflict_Handler;

import java.util.Arrays;
import java.util.Random;

public class Resolution_Functions implements CResolution_Functions {
	
	public String bestSource (String[] args) {
		Arrays.sort(args);
		return args[0];
	}	
	public String globalVote (String[] args) {
		Arrays.sort(args);
		return args[0];
	}	
	public String latest (String[] args) {
		Arrays.sort(args);
		return args[0];
	}	
	public String threshold (String[] args) {
		Arrays.sort(args);
		return args[0];
	}	
	public String best (String[] args) {
		Arrays.sort(args);
		return args[0];
	}	
	public String topN (String[] args) {
		Arrays.sort(args);
		return args[0];
	}	
	public String chooseDepending (String[] args) {
		Arrays.sort(args);
		return args[0];
	}	
	public String chooseCorresponding (String[] args) {
		Arrays.sort(args);
		return args[0];
	}	
	public String mostComplete (String[] args) {
		Arrays.sort(args);
		return args[0];
	}	
//requires metadata as well
	
//requires only data	
	public String first (String[] args) {
		Arrays.sort(args);
		return args[0];
	}	

	public String shortest (String[] args) {		
	    String shortest = any(args);

	    for (String value : args) {
	        if (value.length() < shortest.length()) 
	            shortest = value;
	    }
	    return shortest;    
	}
	
	public String longest (String[] args) {		
	    String longest = any(args);

	    for (String value : args) {
	        if (value.length() > longest.length()) 
	            longest = value;
	    }
	    return longest;    
	}
	
	public String any (String[] args) {
		int randomValue = new Random().nextInt(args.length);
	    return args[randomValue];		
	}

	public String concatenation (String[] args) {
		String concatenate = args[0];
		for (int i = 1; i < args.length; i++)
		{
			concatenate += ", ";
			concatenate += args[1];			
		}
		return concatenate;
	}
	
//Mathematical functions
	public double max (String[] args) {
		double max = Double.MIN_VALUE;
		for (String input : args)
		{
			double value = Double.parseDouble(input); 
			if( value > max) 
		         max = value;
		}
		return max;
	}
	
		public double min (String[] args) {
			double min = Double.MAX_VALUE;
			for (String input : args)
			{
				double value = Double.parseDouble(input); 
				if(value < min) 
			         min = value;
			}
			return min;
		}
		
		public double sum (String[] args) {
			double sum = 0;
			for (String input : args)
				sum += Double.parseDouble(input);
			return sum;
		}

		public double average (String[] args) {
			double average = 0;
			average = sum (args) / args.length;
			return average;
		}
		
		public double median (String[] args) {	
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
		
		public double variance(String[] args) {
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

		public double stdDev(String[] args) {
		    return Math.sqrt(variance(args));
		}
}
		
