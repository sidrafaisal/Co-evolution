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
		new configure ("sa", "sd", "ta", "td", "t", "syncsourceNignorelocal"/*"syncsourceNkeeplocalWresolvedconflicts"*/, "RDF/XML");   
		strategy.apply ();
		emptyResources ();
		renameOutput ("t");
		//Conflict_Resolver.statistics.findBlankNodes("rdf:Description"); 
		scanner.close();
	}


	public static void emptyResources () {
		try {
			File f;

			if (configure.initialTarget != null) {
				f = new File (configure.initialTarget);
				f.delete();
			}
			if (configure.sourceAdditionsChangeset != null) {			
				f = new File (configure.sourceAdditionsChangeset);
				f.delete();
			}
			if (configure.sourceDeletionsChangeset != null) {
				f = new File (configure.sourceDeletionsChangeset);
				f.delete();
			}
			if (configure.targetAdditionsChangeset != null) {
				f = new File (configure.targetAdditionsChangeset);
				f.delete();
			}
			if (configure.targetDeletionsChangeset != null) {
				f = new File (configure.targetDeletionsChangeset);
				f.delete();			
			}
		} catch(Exception e){  		
			e.printStackTrace();
		}
	}

	// Output target will act as initial input for next time synchronization, so rename it at end of synchronization
	public static void renameOutput (String outputfilename) 	{
		if (configure.newTarget != null) {
			File ifile = new File(configure.newTarget);
			File ofile = new File(outputfilename);

			if (ofile.exists()){
				System.out.println("file already exists");
			} 
			ifile.renameTo(ofile);
		}
	}

}
