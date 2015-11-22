package Conflict_Handler;

import java.math.BigInteger;
import java.util.Arrays;

public class mathFunctions {
	
	public static String mathforBI (String function, String [] args){
		
		String val = "";
		
		BigInteger o = new BigInteger(args[0]);
		
		if (function.equals("sum"))	{
			for (int i = 1; i< args.length; i++)
			{
				BigInteger o1 = new BigInteger(args[i]);
				o = o.add(o1);
			}
			val = o.toString();			
		} else if (function.equals("average")) {
			BigInteger size = new BigInteger(args.length+"");
			for (int i = 1; i < args.length; i++)
			{
				BigInteger o1 = new BigInteger(args[i]);
				o = o.add(o1);
			}
			val = o.divide(size).toString();

		} else if (function.equals("max")) {
			for (int i = 1; i < args.length; i++)
			{
				BigInteger o1 = new BigInteger(args[i]);
				o = o.max(o1);
			}
			val = o.toString();
		} else if (function.equals("min")) {
			for (int i = 1; i < args.length; i++)
			{
				BigInteger o1 = new BigInteger(args[i]);
				o = o.min(o1);
			}
			val = o.toString();
		} else if (function.equals("median")) {

				int size = args.length;
				BigInteger  input [] = new BigInteger [size];
				BigInteger median = new BigInteger("0");
				BigInteger divider = new BigInteger("2");
				
				for (int i = 0; i < size; i++)
					input [i] = new BigInteger(args[i]);		
				
				Arrays.sort(input);
				
				if (size % 2 == 0)
					median = (input[size/2].add(input[size/2 - 1])).divide(divider);
				else
					median = input[size/2];						
			
			val = median.toString();
		} else if (function.equals("variance")) {
			BigInteger size = new BigInteger(args.length+"");
			for (int i = 1; i < args.length; i++)	{
				BigInteger o1 = new BigInteger(args[i]);
				o = o.add(o1);
			}
			BigInteger average = o.divide(size);
			BigInteger variance = new BigInteger("0");
			
			BigInteger  input [] = new BigInteger [args.length];
			
			for (int i = 0; i < args.length; i++)
				input [i] = new BigInteger(args[i]);

		    for(BigInteger value : input)
		    	variance = variance.add((average.subtract(value)).multiply(average.subtract(value)));
			val = variance.divide(size).toString();
		} else if (function.equals("stdDev")) {
			BigInteger size = new BigInteger(args.length+"");
			for (int i = 1; i < args.length; i++)
			{
				BigInteger o1 = new BigInteger(args[i]);
				o = o.add(o1);
			}
			BigInteger average = o.divide(size);
			BigInteger variance = new BigInteger("0");
			
			BigInteger  input [] = new BigInteger [args.length];
			
			for (int i = 0; i < args.length; i++)
				input [i] = new BigInteger(args[i]);

		    for(BigInteger value : input)
		    	variance = variance.add((average.subtract(value)).multiply(average.subtract(value)));
		    
			val = sqrt(variance.divide(size)).toString();

		}
		return val;
	}

	//Mathematical functions

			public static double max(String [] args) {			
				double max = Double.MIN_VALUE;
				for (String input : args)
				{
					double value = Double.parseDouble(input); 
					if(value > max) 
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
				
				public static BigInteger sqrt(BigInteger n) {
					  BigInteger a = BigInteger.ONE;
					  BigInteger b = new BigInteger(n.shiftRight(5).add(new BigInteger("8")).toString());
					  while(b.compareTo(a) >= 0) {
					    BigInteger mid = new BigInteger(a.add(b).shiftRight(1).toString());
					    if(mid.multiply(mid).compareTo(n) > 0) b = mid.subtract(BigInteger.ONE);
					    else a = mid.add(BigInteger.ONE);
					  }
					  return a.subtract(BigInteger.ONE);
					}
}
