package Co_Evolution_Manager;

import java.io.File;
import java.util.Scanner;

public class main {
	
	public static Scanner scanner;
	
	public static void main (String[] args) {

		/*	Allowed strategies
		 * "syncsourceNkeeplocalBnotconflicts" "syncsourceNkeeplocalWresolvedconflicts" "syncsourceNignorelocal" "nsyncsourceBkeeplocal" 
		 * "nsyncsourceNignorelocal"
		 * */		
		scanner = new Scanner(System.in);		
		new configure ("sa.nt", "sd.nt", "ta.nt", "td.nt", "t.nt", "syncsourceNkeeplocalWresolvedconflicts", "NT");   
		strategy.apply ();
		emptyResources ();
		scanner.close();
	}


	public static void emptyResources () {
		try {
			File f = new File (configure.initialTarget);
			f.delete();

			f = new File (configure.sourceAdditionsChangeset);
			f.delete();

			f = new File (configure.sourceDeletionsChangeset);
			f.delete();

			f = new File (configure.targetAdditionsChangeset);
			f.delete();

			f = new File (configure.targetDeletionsChangeset);
			f.delete();
		} catch(Exception e){  		
			e.printStackTrace();
		}
	}


}
