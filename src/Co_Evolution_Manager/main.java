package Co_Evolution_Manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
import Co_Evolution_Manager.configure;

public class main {

	public static Scanner scanner;
	static configure config;
	public static long Triples_in_Source_Addition_Changeset = 0;
	public static long Triples_in_Source_Deletion_Changeset = 0;
	public static long Triples_in_Target_Addition_Changeset = 0;
	public static long Triples_in_Target_Deletion_Changeset = 0;

	public static void iterate(File srcadd, int i, String srcdel, String taradd, String tardel){
		List<String> arr = new ArrayList<String>();
		if(srcadd.isDirectory()){
			String[] srcaddList = srcadd.list();
			for(String filename : srcaddList){
				iterate(new File(srcadd, filename), i, srcdel, taradd, tardel);				
			}
			if (srcadd.getAbsolutePath().length()>i) {
				File sdfile = new File(srcdel + (srcadd.getAbsolutePath()).substring(i));
				if(sdfile.isDirectory()){
					String[] addList = sdfile.list();
					for(String filename : addList){
						if(filename.contains(".removed.nt")) {
							arr.add(0, null);
							arr.add(1, sdfile+"/"+filename);							

							String tanode = taradd + (srcadd.getAbsolutePath()).substring(i)+ "/" + filename;
							int tindex = tanode.indexOf(".removed.nt");
							tanode = tanode.substring(0, tindex)+".added.nt";
							File tafile = new File(tanode);
							if (tafile.exists()) 
								arr.add(2, tafile.getAbsolutePath());
							else 
								arr.add(2, null);
							
							String tdnode = tardel + (srcadd.getAbsolutePath()).substring(i)+ "/" + filename;
							File tdfile = new File(tdnode);
							if (tdfile.exists()) 
								arr.add(3, tdfile.getAbsolutePath());
							else 
								arr.add(3, null);
							
							config.configureFiles (arr.get(0), arr.get(1), arr.get(2), arr.get(3), "t");

							Triples_in_Source_Deletion_Changeset = Triples_in_Source_Deletion_Changeset + getDatasetSize (configure.sourceDeletionsChangeset);				
							Triples_in_Target_Addition_Changeset = Triples_in_Target_Addition_Changeset + getDatasetSize (configure.targetAdditionsChangeset); 		
							Triples_in_Target_Deletion_Changeset = Triples_in_Target_Deletion_Changeset + getDatasetSize (configure.targetDeletionsChangeset); 		

							strategy.apply ();
							emptyResources (arr);
							renameOutput ("t");
						}
					}
				}
			}
		} else {
			if (srcadd.getName().contains("added.nt")) {

				String parent = srcadd.getAbsolutePath();
				arr.add(0, parent);
				//	System.out.println(parent);

				String sdnode = srcdel + parent.substring(i);
				int sindex = sdnode.indexOf(".added.nt");
				sdnode = sdnode.substring(0, sindex)+".removed.nt";
				File sdfile = new File(sdnode);
				if (sdfile.exists()) 
					arr.add(1, sdfile.getAbsolutePath());
				else 
					arr.add(1, null);

				String tanode = taradd + parent.substring(i);
				File tafile = new File(tanode);
				if (tafile.exists()) 
					arr.add(2, tafile.getAbsolutePath());
				else 
					arr.add(2, null);

				String tdnode = tardel + parent.substring(i);
				int tindex = tdnode.indexOf(".added.nt");
				tdnode = tdnode.substring(0, tindex)+".removed.nt";
				File tdfile = new File(tdnode);
				if (tdfile.exists()) 
					arr.add(3, tdfile.getAbsolutePath());
				else 
					arr.add(3, null);


				///////////////////////////////// perform strategy

				config.configureFiles (arr.get(0), arr.get(1), arr.get(2), arr.get(3), "t");

				Triples_in_Source_Addition_Changeset = Triples_in_Source_Addition_Changeset + getDatasetSize (configure.sourceAdditionsChangeset);
				Triples_in_Source_Deletion_Changeset = Triples_in_Source_Deletion_Changeset + getDatasetSize (configure.sourceDeletionsChangeset);				
				Triples_in_Target_Addition_Changeset = Triples_in_Target_Addition_Changeset + getDatasetSize (configure.targetAdditionsChangeset); 		
				Triples_in_Target_Deletion_Changeset = Triples_in_Target_Deletion_Changeset + getDatasetSize (configure.targetDeletionsChangeset); 		

				strategy.apply ();
				emptyResources (arr);
				renameOutput ("t");
			} else if (srcadd.getName().contains("removed.nt")) {

			}
		}
	}



	public static void main (String[] args) {
		
		/*	Allowed strategies
		 * "syncsourceNkeeplocalBnotconflicts" "syncsourceNkeeplocalWresolvedconflicts" "syncsourceNignorelocal" "nsyncsourceBkeeplocal" 
		 * "nsyncsourceNignorelocal"
		 * */

		String srcadd = "/Users/sidra/Desktop/2015/";
		String srcdel = "/Users/sidra/Desktop/20152/";
		String taradd = "/Users/sidra/Desktop/20153/";
		String tardel = "/Users/sidra/Desktop/20154/";

		scanner = new Scanner(System.in);			
		System.out.println("Processing...");
		config = new configure ("syncsourceNkeeplocalWresolvedconflicts", "NT","/Users/sidra/Desktop/predicates.txt", "/Users/sidra/Desktop/dbpedia_2014.owl");  
		iterate(new File(srcadd), srcadd.length(), srcdel, taradd, tardel);

		System.out.println("------------------------------");
		System.out.println("Triples in Source Addition Changeset = " + Triples_in_Source_Addition_Changeset);
		System.out.println("Triples in Source Deletion Changeset = " + Triples_in_Source_Deletion_Changeset);			
		System.out.println("Triples in Target Addition Changeset = " + Triples_in_Target_Addition_Changeset);
		System.out.println("Triples in Target Deletion Changeset = " + Triples_in_Target_Deletion_Changeset);
		System.out.println("Triples in Synchronized Target = " + getDatasetSize ("t"));
		System.out.println("------------------------------");
		if (Conflict_Finder.conflicts_Finder.resolve) {
			System.out.println("Number of Conflicting triples = " + Conflict_Finder.source_Delta.number_Of_ConflictingTriples);
			System.out.println("Number of Resolved triples = " + Conflict_Finder.source_Delta.number_Of_ResolvedTriples);
			System.out.println("------------------------------");
			System.out.println("Number of Case123 triples = " + Conflict_Finder.source_Delta.number_Of_case123Triples); 
			System.out.println("Number of Case4 triples = " + Conflict_Finder.source_Delta.number_Of_case4Triples);
			System.out.println("Number of Case5 triples = " + Conflict_Finder.source_Delta.number_Of_case5Triples);
			System.out.println("Number of Case678 triples = " + Conflict_Finder.source_Delta.number_Of_case678Triples);
			System.out.println("------------------------------");
			
			String Resolved_in_time = String.format("%d min, %d sec", 
					TimeUnit.MILLISECONDS.toMinutes(Conflict_Finder.conflicts_Finder.resolutionTime),
					TimeUnit.MILLISECONDS.toSeconds(Conflict_Finder.conflicts_Finder.resolutionTime) - 
					TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Conflict_Finder.conflicts_Finder.resolutionTime)));
			System.out.println("Resolved in time = " + Resolved_in_time);

			String Identified_in_time = String.format("%d min, %d sec", 
					TimeUnit.MILLISECONDS.toMinutes(Conflict_Finder.conflicts_Finder.identificationTime),
					TimeUnit.MILLISECONDS.toSeconds(Conflict_Finder.conflicts_Finder.identificationTime) - 
					TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Conflict_Finder.conflicts_Finder.identificationTime)));			
			System.out.println("Identified in time = " + Identified_in_time);

			System.out.println("------------------------------");
			Conflict_Finder.source_Delta.getPredicateFunctionUseCounter ();
		}
		scanner.close();
	}

	public static long getDatasetSize (String filename) {
		long number_Of_Triples  = 0;
		if (filename != null) {
			Model model = FileManager.get().loadModel(filename, configure.fileSyntax);
			number_Of_Triples = model.size();
			model.close();
		}	
		return number_Of_Triples;
	}

	public static void emptyResources (List<String> f) {
		try {
			File file;

			for (int i = 0; i < f.size() ; i++ ) {
				if (f.get(i)!=null) {
					file = new File (f.get(i));
					file.delete();
				}		
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

			ifile.renameTo(ofile);
		}
	}

}
