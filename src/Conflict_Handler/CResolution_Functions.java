package Conflict_Handler;

public interface CResolution_Functions {

	public double sum (String[] args);
	public double average (String[] args);
	public double median (String[] args);
	public double variance (String[] args);
	public double stdDev (String[] args);
	public double max (String[] args);
	public double min (String[] args);
	
	public String any (String[] args);
	public String first (String[] args);
	public String shortest (String[] args);
	public String longest (String[] args);	
	public String concatenation (String[] args);
	
	public String bestSource (String[] args);
	public String globalVote (String[] args);
	public String latest (String[] args);
	public String threshold (String[] args);
	public String best (String[] args);
	public String topN (String[] args);
	public String chooseDepending (String[] args);
	public String chooseCorresponding (String[] args);
	public String mostComplete (String[] args);
}
