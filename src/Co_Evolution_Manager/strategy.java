package Co_Evolution_Manager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

import Conflict_Finder.conflicts_Finder;

public class strategy {

	public static long time_S1 = 0;
	public static long time_S2 = 0;
	
	public static int S1 = 0;
	public static int S2 = 0;
	public static int S3 = 0; //todo for s4
	
	static String strategy;	

	public static void setStrategy(String s){
		strategy = s;
	}

	public static void apply (){		
		switch (strategy)
		{
		case "syncsourceNignorelocal":
			syncsourceNignorelocal();
			break;
		case "syncsourceNkeeplocalBnotconflicts":
			syncsourceNkeeplocalBnotconflicts();
			break;
		case "syncsourceNkeeplocalWresolvedconflicts": 
			syncsourceNkeeplocalWresolvedconflicts();	
			break; 	
		case "nsyncsourceBkeeplocal":
			nsyncsourceBkeeplocal();
			break;
		case "nsyncsourceNignorelocal":
			nsyncsourceNignorelocal();		
			break;
		case "mixed":
			mixed();
			break;
		}		
	}


	public static void mixed () {
		try {
			for (String p: Co_Evolution_Manager.configure.strategyforPredicate.keySet())
			{
				String strategy = Co_Evolution_Manager.configure.strategyforPredicate.get(p);
				Property property =ResourceFactory.createProperty(p);

				if (strategy.equals("syncsourceNignorelocal")) {
					long startTime   = System.currentTimeMillis();
					SSIL (configure.sourceAdditionsChangeset, configure.newTarget, property);
					long endTime   = System.currentTimeMillis();
					time_S1 = time_S1 + (endTime - startTime);
					
				}

				else if (strategy.equals("nsyncsourceBkeeplocal")) {
					long startTime   = System.currentTimeMillis();
					NSKL (configure.targetAdditionsChangeset, configure.newTarget, property);
					long endTime   = System.currentTimeMillis();
					time_S2 = time_S2 + (endTime - startTime);
					
				}


			}
			
			for (String p: Co_Evolution_Manager.configure.strategyforPredicate.keySet())
			{
				String strategy = Co_Evolution_Manager.configure.strategyforPredicate.get(p);
				//Property property =ResourceFactory.createProperty(p);

				if (strategy.equals("syncsourceNkeeplocalWresolvedconflicts")) {
					syncsourceNkeeplocalWresolvedconflicts();
				break;
				}
				//else if (strategy.equals("syncsourceNkeeplocalBnotconflicts()")) 
				// todo : 	
			}
			
			System.out.println(configure.getDatasetSize(configure.newTarget));
			if (configure.initialTarget!=null) {
			Model omodel = FileManager.get().loadModel( configure.newTarget, configure.fileSyntax);
			Model imodel = FileManager.get().loadModel(configure.initialTarget, configure.fileSyntax);
			StmtIterator iter = imodel.listStatements();
			while (iter.hasNext()){
				Statement stmt = iter.nextStatement();  		
				omodel.add(stmt);			 
			}

			omodel.write(new FileOutputStream( configure.newTarget), configure.fileSyntax);
			omodel.close();	
			}
			
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}	
	}
	
	//Ti+1 = delta (Si) + Ti	
	public static void syncsourceNignorelocal() {

		deleteTriples (configure.initialTarget, configure.sourceDeletionsChangeset, configure.initialTarget);
		writeTriples (configure.initialTarget, configure.newTarget);			
		writeTriples (configure.sourceAdditionsChangeset, configure.newTarget);		

	}
	
	public static void SSIL (String ifilename, String ofilename, Property property) throws FileNotFoundException {	
		List<Triple> triples2delete = new ArrayList<Triple>();
		StmtIterator iter ;
		
		//////////////////////////
		triples2delete = new ArrayList<Triple>();
		
		if (configure.initialTarget!=null && configure.sourceDeletionsChangeset!=null) {
		Model tmodel = FileManager.get().loadModel(configure.initialTarget, configure.fileSyntax);
		Model sdmodel = FileManager.get().loadModel( configure.sourceDeletionsChangeset, configure.fileSyntax);
		iter = sdmodel.listStatements((Resource)null, property, (RDFNode)null);
		while (iter.hasNext()){	
			Statement stmt = iter.nextStatement();
			triples2delete.add(stmt.asTriple());

		}
		for (Triple t : triples2delete) {
			sdmodel.getGraph().delete(t);		
			tmodel.getGraph().delete(t);
		}
		sdmodel.write(new FileOutputStream(configure.sourceDeletionsChangeset), configure.fileSyntax);
		sdmodel.close();	
		tmodel.write(new FileOutputStream(configure.initialTarget), configure.fileSyntax);
		tmodel.close();	
		}
		/////////////////////////
		triples2delete = new ArrayList<Triple>();

		if (configure.targetAdditionsChangeset!=null) {
		Model tamodel = FileManager.get().loadModel( configure.targetAdditionsChangeset, configure.fileSyntax);
		iter = tamodel.listStatements((Resource)null, property, (RDFNode)null);
		while (iter.hasNext()){		
			Statement stmt = iter.nextStatement(); 
			triples2delete.add(stmt.asTriple());		 
		}
		for (Triple t : triples2delete)
			tamodel.getGraph().delete(t);
		
		tamodel.write(new FileOutputStream(configure.targetAdditionsChangeset), configure.fileSyntax);
		tamodel.close();	
		}
		////////////////////////
		triples2delete = new ArrayList<Triple>();

		if (configure.targetDeletionsChangeset!=null) {
		Model tdmodel = FileManager.get().loadModel( configure.targetDeletionsChangeset, configure.fileSyntax);
		iter = tdmodel.listStatements((Resource)null, property, (RDFNode)null);
		while (iter.hasNext()){		
			Statement stmt = iter.nextStatement(); 
			triples2delete.add(stmt.asTriple());		 
		}
		for (Triple t : triples2delete)
			tdmodel.getGraph().delete(t);

		tdmodel.write(new FileOutputStream(configure.targetDeletionsChangeset), configure.fileSyntax);
		tdmodel.close();				
	}
		///////////////////////
		if (ifilename!=null) {
		Model omodel = FileManager.get().loadModel( ofilename, configure.fileSyntax);
		Model imodel = FileManager.get().loadModel( ifilename, configure.fileSyntax);
	
		iter = imodel.listStatements((Resource)null, property, (RDFNode)null);
		while (iter.hasNext()){
			Statement stmt = iter.nextStatement();  
			triples2delete.add(stmt.asTriple());			
			omodel.add(stmt);
			S1++;
		}
		for (Triple t : triples2delete)
			imodel.getGraph().delete(t);
		
		imodel.write(new FileOutputStream(ifilename), configure.fileSyntax);
		imodel.close();	
		omodel.write(new FileOutputStream(ofilename), configure.fileSyntax);
		omodel.close();	
	
		}
	}

	//Ti+1 = delta (Si) + delta (Ti) + Ti - X
	public static void syncsourceNkeeplocalBnotconflicts(){
		conflicts_Finder.identifyConflicts(false);

	}


	//Ti+1 = delta (Si) + delta (Ti) + Ti - X + NGT + ERT
	public static void syncsourceNkeeplocalWresolvedconflicts(){

		conflicts_Finder.identifyConflicts(true);		

		/*	For auto_Selector using scores
		 * File file = new File("auto_FunctionSelector.xml");

			if(!file.exists()) {
				System.out.println("Auto resolution not possible, please select manually.");
				Conflict_Resolver.manual_Selector.select();
				resolver.manual_selector = true;
				conflicts_Finder.identifyConflicts(true);
			}
			else {
				Conflict_Resolver.auto_Selector.select();
				resolver.auto_selector = true;
				conflicts_Finder.identifyConflicts(true);
			}*/


	}

	//Ti+1 = delta (Ti) + Ti
	public static void nsyncsourceBkeeplocal(){

		deleteTriples (configure.initialTarget, configure.targetDeletionsChangeset, configure.initialTarget);		
		writeTriples (configure.initialTarget, configure.newTarget);			
		writeTriples (configure.targetAdditionsChangeset, configure.newTarget);		

	}
	
	public static void NSKL (String ifilename, String ofilename, Property property) throws FileNotFoundException {	
		List<Triple> triples2delete = new ArrayList<Triple>();
		StmtIterator iter ;
		
		if (ifilename!=null) {
		Model omodel = FileManager.get().loadModel( ofilename, configure.fileSyntax);
		Model imodel = FileManager.get().loadModel( ifilename, configure.fileSyntax);

		iter = imodel.listStatements((Resource)null, property, (RDFNode)null);
		while (iter.hasNext()){
			Statement stmt = iter.nextStatement(); 
			triples2delete.add(stmt.asTriple());
			omodel.add(stmt);
			S2++;
		}
		for (Triple t : triples2delete)
			imodel.getGraph().delete(t);

		imodel.write(new FileOutputStream(ifilename), configure.fileSyntax);
		imodel.close();	
		omodel.write(new FileOutputStream(ofilename), configure.fileSyntax);
		omodel.close();	
		}
		//////////////////////////

		if (configure.initialTarget!=null && configure.targetDeletionsChangeset!=null) {
			triples2delete = new ArrayList<Triple>();
		Model tmodel = FileManager.get().loadModel( configure.initialTarget, configure.fileSyntax);
		Model sdmodel = FileManager.get().loadModel( configure.targetDeletionsChangeset, configure.fileSyntax);
		iter = sdmodel.listStatements((Resource)null, property, (RDFNode)null);
		while (iter.hasNext()){
			Statement stmt = iter.nextStatement(); 
			triples2delete.add(stmt.asTriple());
		}
		for (Triple t : triples2delete) {			
			sdmodel.getGraph().delete(t);		
			tmodel.getGraph().delete(t);
		}
		sdmodel.write(new FileOutputStream(configure.targetDeletionsChangeset), configure.fileSyntax);
		sdmodel.close();	
		tmodel.write(new FileOutputStream(configure.initialTarget), configure.fileSyntax);
		tmodel.close();	
		}
		///////////////////////////
		
		if (configure.sourceAdditionsChangeset!=null) {
		triples2delete = new ArrayList<Triple>();
		Model tamodel = FileManager.get().loadModel( configure.sourceAdditionsChangeset, configure.fileSyntax);
		iter = tamodel.listStatements((Resource)null, property, (RDFNode)null);
		while (iter.hasNext()){
			Statement stmt = iter.nextStatement(); 
			triples2delete.add(stmt.asTriple());
		}
		for (Triple t : triples2delete) 			
			tamodel.getGraph().delete(t);			 

		tamodel.write(new FileOutputStream(configure.sourceAdditionsChangeset), configure.fileSyntax);
		tamodel.close();	
		}
		///////////////////////////
		if (configure.sourceDeletionsChangeset!=null) {
		triples2delete = new ArrayList<Triple>();
		Model tdmodel = FileManager.get().loadModel( configure.sourceDeletionsChangeset, configure.fileSyntax);
		iter = tdmodel.listStatements((Resource)null, property, (RDFNode)null);
		while (iter.hasNext()){
			Statement stmt = iter.nextStatement(); 
			triples2delete.add(stmt.asTriple()); 
		}
		for (Triple t : triples2delete) 			
			tdmodel.getGraph().delete(t);
		tdmodel.write(new FileOutputStream(configure.sourceDeletionsChangeset), configure.fileSyntax);
		tdmodel.close();		
		}
	}
	//Ti+1 = Ti
	public static void nsyncsourceNignorelocal(){

		writeTriples (configure.initialTarget, configure.newTarget);	

	}

	// delete the triples for final output
	public static void deleteTriples (String initialtarget, String targetDeletionsChangeset, String outputfilename){

		if (initialtarget!=null) {	
			try {
				Model imodel = FileManager.get().loadModel(initialtarget, configure.fileSyntax);	

				if (targetDeletionsChangeset!=null) {
					Model tmodel = FileManager.get().loadModel(targetDeletionsChangeset, configure.fileSyntax);		

					StmtIterator iter = tmodel.listStatements();

					while (iter.hasNext()) {
						Statement stmt = iter.nextStatement();  // get next statement 
						imodel.getGraph().delete(stmt.asTriple());	// Delete the triples of target from initial		    					   
					}
					tmodel.close();
				} 

				imodel.write(new FileOutputStream(outputfilename), configure.fileSyntax);

				imodel.close();

			} catch (FileNotFoundException | org.apache.jena.riot.RiotException e) {
				System.out.println(""+e);
				e.printStackTrace();
			}
		} else 
			;
	}

	// write in output file
	public static void writeTriples(String inputfilename, String outputfilename) {
		if (inputfilename!=null)
		{
			try {
				Model model = FileManager.get().loadModel(inputfilename, configure.fileSyntax);			
				model.write(new FileOutputStream(outputfilename, true), configure.fileSyntax);
				model.close();
			} catch (FileNotFoundException | org.apache.jena.riot.RiotException e) {
				System.out.println(""+e);
				e.printStackTrace();
			}}
	}

	public String getStrategy(){
		return strategy;
	}
}
