package Co_Evolution_Manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class main {
	
	static String initialtarget;
	static String newTarget;
	static String sourceAdditionsChangeset;
	static String sourceDeletionsChangeset; 
	static String targetAdditionsChangeset;
	static String targetDeletionsChangeset;
	
	public static void main(String[] args){
		
		setinitialtarget("t.nt");
		setnewtarget("newtarget1m1.nt");
		
		setsourceAdditionsChangeset("sa.nt");
		setsourceDeletionsChangeset("sd.nt");
		
		settargetAdditionsChangeset("ta.nt");
		settargetDeletionsChangeset("td.nt");
		
		try {
			File nt = new File(newTarget);
			if(!nt.exists()) 
			    nt.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		chooseStrategy.setStrategy("syncsourceNkeeplocalBnotconflicts"); 
		chooseStrategy.applyStrategy();
		
		}
	
	//				Helper functions
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
