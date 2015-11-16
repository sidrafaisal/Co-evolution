package Co_Evolution_Manager;

import java.io.File;
import java.io.IOException;

public class main {
	
	static String initialtarget;
	static String newTarget;
	static String sourceAdditionsChangeset;
	static String sourceDeletionsChangeset; 
	static String targetAdditionsChangeset;
	static String targetDeletionsChangeset;
	
	   public static void main (String[] args) {
		   
			setinitialtarget("t.nt");
			setnewtarget("newtarget.nt");
			
			setsourceAdditionsChangeset("sa.nt");
			setsourceDeletionsChangeset("sd.nt");
			
			settargetAdditionsChangeset("ta.nt");
			settargetDeletionsChangeset("td.nt");
			
			try {
				File nt = new File(newTarget);
				if(!nt.exists()) 
				    nt.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Conflict_Handler.functionforPredicate.select();
			chooseStrategy.setStrategy("syncsourceNkeeplocalBnotconflicts"); 
			//chooseStrategy.setStrategy("syncsourceNkeeplocalWresolvedconflicts");			
			//chooseStrategy.setStrategy("syncsourceNignorelocal");

			//chooseStrategy.setStrategy("nsyncsourceBkeeplocal");
			//chooseStrategy.setStrategy("nsyncsourceNignorelocal");
			chooseStrategy.applyStrategy ();
			emptyResources ();
	   }
	   
	//				Helper functions
	public static void emptyResources () {
		try {
			File f = new File (initialtarget);
		    f.delete();

		    f = new File (sourceAdditionsChangeset);
		    f.delete();
		    
		    f = new File (sourceDeletionsChangeset);
		    f.delete();
		    
		    f = new File (targetAdditionsChangeset);
		    f.delete();
			
		    f = new File (targetDeletionsChangeset);
		    f.delete();
    	} catch(Exception e){  		
    		e.printStackTrace();
    	}
	}
	public static void setinitialtarget(String s){
		initialtarget = s;
	}
	
	public static void setnewtarget(String s){
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
