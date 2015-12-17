package Co_Evolution_Manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import Co_Evolution_Manager.configure;

public class main {

	public static Scanner scanner;
	static configure config;
	
	public static void write(String f, String content) {
		try {
content = "\n"+ content ;
		//	String content = "This is the content to write into file";

			File file = new File("/Users/sidra/Desktop/"+f);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main (String[] args) {		

		String srcadd = "/Users/sidra/Desktop/source/added/",
			srcdel = "/Users/sidra/Desktop/source/removed/",
			taradd = "/Users/sidra/Desktop/target/added/",
			tardel = "/Users/sidra/Desktop/target/removed/",
			pred = "/Users/sidra/Desktop/predicates.txt",
			ont = "/Users/sidra/Desktop/dbpedia_2014.owl",
			t = "/Users/sidra/Desktop/coevo-pol-slice.nt";
		
		scanner = new Scanner(System.in);			
		config = new configure ("NT",pred, ont);

		Conflict_Finder.source_Delta.usedFunctions.put("any", 0);

		Conflict_Finder.source_Delta.usedFunctions.put("first", 0);
		Conflict_Finder.source_Delta.usedFunctions.put("longest", 0);
			System.out.println("t = " + config.getDatasetSize(t));
		
		crawl(new File(srcadd), srcadd.length(), srcdel, taradd, tardel,t);
		recrawl(new File(taradd),t);
		recrawl(new File(tardel),t);

		scanner.close();
		if (t!=null) {
		File file = new File (t);
			file.delete();
		}
		File file = new File ("SyncSrcAdd");
			file.delete();

		file = new File ("SyncSrcDel");
			file.delete();

	}
	
	public static void printStats(){
		write("triplesa", Long.toString(configure.Triples_in_Source_Addition_Changeset));
		
		write("triplesd", Long.toString(configure.Triples_in_Source_Deletion_Changeset));
			
		write("tripleta", Long.toString(configure.Triples_in_Target_Addition_Changeset));
		write("tripletd", Long.toString(configure.Triples_in_Target_Deletion_Changeset));
		write("triplessa", Long.toString(configure.Triples_for_Sync_Source_AddChangeset));
		write("triplessd", Long.toString(configure.Triples_for_Sync_Source_DelChangeset));
		write("triples1", Integer.toString(strategy.S1));
		write("triples2", Integer.toString(strategy.S2));
		write("triplect", Long.toString(Conflict_Finder.source_Delta.number_Of_ConflictingTriples));
		write("triplert", Long.toString(Conflict_Finder.source_Delta.number_Of_ResolvedTriples));
		write("triplest", Long.toString(configure.getDatasetSize("/Users/sidra/Desktop/coevo-pol-slice.nt")));
		write("triples3", Long.toString(Conflict_Finder.source_Delta.s3));

				write("fany", Long.toString(Conflict_Finder.source_Delta.usedFunctions.get("any")));
		write("flong", Long.toString(Conflict_Finder.source_Delta.usedFunctions.get("longest")));
		
		write("first", Long.toString(Conflict_Finder.source_Delta.usedFunctions.get("first")));

		String Resolved_in_time = String.format("%d min, %d sec", 
				TimeUnit.MILLISECONDS.toMinutes(Conflict_Finder.conflicts_Finder.resolutionTime + Conflict_Finder.conflicts_Finder.identificationTime),
				TimeUnit.MILLISECONDS.toSeconds(Conflict_Finder.conflicts_Finder.resolutionTime + Conflict_Finder.conflicts_Finder.identificationTime) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Conflict_Finder.conflicts_Finder.resolutionTime + Conflict_Finder.conflicts_Finder.identificationTime)));

		write("etr", Resolved_in_time);

		Resolved_in_time = String.format("%d min, %d sec", 
				TimeUnit.MILLISECONDS.toMinutes(strategy.time_S1),
				TimeUnit.MILLISECONDS.toSeconds(strategy.time_S1) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(strategy.time_S1)));
		
		write("ets1", Resolved_in_time);

		Resolved_in_time = String.format("%d min, %d sec", 
				TimeUnit.MILLISECONDS.toMinutes(strategy.time_S2),
				TimeUnit.MILLISECONDS.toSeconds(strategy.time_S2) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(strategy.time_S2)));
		write("ets2", Resolved_in_time);
		write("cases1", Long.toString(Conflict_Finder.source_Delta.number_Of_case123Triples));	

		write("cases4", Long.toString(Conflict_Finder.source_Delta.number_Of_case4Triples));	
		write("cases6", Long.toString(Conflict_Finder.source_Delta.number_Of_case678Triples));	
	
		
		
		
	}
	
	public static void crawl(File srcadd, int i, String srcdel, String taradd, String tardel, String t){
		

		
		List<String> arr = new ArrayList<String>();
		if(srcadd.isDirectory()){
			String[] srcaddList = srcadd.list();
			for(String filename : srcaddList){
				
				crawl(new File(srcadd, filename), i, srcdel, taradd, tardel, t);	
				
								
					
				/*if(srcadd.equals ("/Users/sidra/Desktop/source/added/2015/09/01/") || srcadd.equals ("/Users/sidra/Desktop/source/added/2015/09/15/") ||
						srcadd.equals ("/Users/sidra/Desktop/source/added/2015/09/30/") || srcadd.equals ("/Users/sidra/Desktop/source/added/2015/10/01/") ||
						srcadd.equals ("/Users/sidra/Desktop/source/added/2015/10/15/") || srcadd.equals ("/Users/sidra/Desktop/source/added/2015/10/30/")) {
					System.out.println("------------------------------");
					System.out.println("t = " + config.getDatasetSize(t));
				}*/
				
//System.out.println(srcadd.getPath());
				
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
							
							config.configureFiles (arr.get(0), arr.get(1), arr.get(2), arr.get(3), t);	
						
							strategy.apply ();
							emptyResources (arr);
							renameOutput (t);
							printStats();	
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

				config.configureFiles (arr.get(0), arr.get(1), arr.get(2), arr.get(3), t);

				strategy.apply ();
				emptyResources (arr);
				renameOutput (t);
				printStats();	
			} else if (srcadd.getName().contains("removed.nt")) {

			}
		}
	}


	public static void recrawl(File tar, String t){
		List<String> arr = new ArrayList<String>();
		if(tar.isDirectory()){
			String[] srcaddList = tar.list();
			for(String filename : srcaddList){

				
				recrawl(new File(tar, filename), t);
				
			}
		} else if (tar.getName().contains("added.nt")) {
				String parent = tar.getAbsolutePath();
				arr.add(0, null);
				arr.add(1, null);
				arr.add(2, parent);
				arr.add(3, null);
				
				///////////////////////////////// perform strategy

				config.configureFiles (arr.get(0), arr.get(1), arr.get(2), arr.get(3), t);

				strategy.apply ();
				emptyResources (arr);
				renameOutput (t);
				printStats();	
			} else if (tar.getName().contains("removed.nt")) {
				String parent = tar.getAbsolutePath();
				arr.add(0, null);
				arr.add(1, null);
				arr.add(2, null);
				arr.add(3, parent);
				
				///////////////////////////////// perform strategy

				config.configureFiles (arr.get(0), arr.get(1), arr.get(2), arr.get(3), t);

				strategy.apply ();
				emptyResources (arr);
				renameOutput (t);	
				printStats();	
		}
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
