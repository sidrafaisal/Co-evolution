package Co_Evolution_Manager;

public class main {
	
	static String initialtarget;
	static String newTarget;
	static String sourceAdditionsChangeset;
	static String sourceDeletionsChangeset; 
	static String targetAdditionsChangeset;
	static String targetDeletionsChangeset;
	
	public static void main(String[] args){
		
		setinitialtarget("t.nt");
		setnewtarget("newtarget.nt");
		setsourceAdditionsChangeset("sa.nt");
		setsourceDeletionsChangeset("sd.nt");
		settargetAdditionsChangeset("ta.nt");
		settargetDeletionsChangeset("td.nt");
		
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
