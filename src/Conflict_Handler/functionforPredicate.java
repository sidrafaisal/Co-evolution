package Conflict_Handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class functionforPredicate {

	public static Map<String, String> resolutionFunctionforPredicate  = new HashMap<String, String>();

	public static void select(){
		try {
			
			File file = new File("resolutionFunctionforPredicate.txt");
			
			if(!file.exists()) {
			
				file.createNewFile();
				String content="";   
				
				int size = resolutionFunctions.availableResolutionFunctions.length;
				System.out.println("Avaialable resolution functions");
				for (int i = 0; i < size; i++) {
					System.out.print(resolutionFunctions.availableResolutionFunctions [i] + " . ");
				}

				BufferedReader br = new BufferedReader(new FileReader("predicates.txt"));
				Scanner scanner = new Scanner(System.in);
				String line = null;
				while ((line = br.readLine()) != null) {
					System.out.println("\nEnter a resolution function for this property: "+ line);
					
					String rf = scanner.nextLine();

					set (line, rf);	
					content += line + "," + rf + "\n"; 
				}
				scanner.close();
				br.close();
				saveinFile(file, content);
			} else {
				populate();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void populate(){
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("resolutionFunctionforPredicate.txt"));
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					int separator = line.indexOf(",");
					String p = line.substring(0, separator);
					String rf = line.substring(separator+1, line.length());
					set (p, rf);	
				}
				br.close();	
			} catch (IOException e) {
				e.printStackTrace();
			}	 } catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
	}
	
	public static void saveinFile(File file, String content) {
		try (FileOutputStream fop = new FileOutputStream(file)) {
			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void set (String p, String rf){	
		resolutionFunctionforPredicate.put(p, rf);  
	}

	public static String get (String p){		
		return resolutionFunctionforPredicate.get(p);  
	}

}

