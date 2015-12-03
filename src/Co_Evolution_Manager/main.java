package Co_Evolution_Manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class main {

	public static Scanner scanner;

	public static void main (String[] args) {

		/*	Allowed strategies
		 * "syncsourceNkeeplocalBnotconflicts" "syncsourceNkeeplocalWresolvedconflicts" "syncsourceNignorelocal" "nsyncsourceBkeeplocal" 
		 * "nsyncsourceNignorelocal"
		 * */		
		
		scanner = new Scanner(System.in);		
		String [] arr = {"sa", "sd", "ta", "td", "t"};
		new configure (arr[0], arr[1], arr[2], arr[3], arr[4], /*"syncsourceNignorelocal"*/"syncsourceNkeeplocalWresolvedconflicts", "NT");   
		strategy.apply ();
		emptyResources (arr);
		renameOutput ("t");
		scanner.close();
	}


	public static void emptyResources (String [] f) {
		try {
			File file;

			for (int i = 0; i < f.length ; i++ ) {
				file = new File (f[i]);
				file.delete();
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
