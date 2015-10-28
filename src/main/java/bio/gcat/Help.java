package bio.gcat;

import java.util.HashMap;
import java.util.Map;

public class Help {
	public static final String
		OVERVIEW = "1. Overview",
		GETTING_STARTED = "2. Getting started",
		OPERATIONS = "3. Operations",
		WHATS_NEW = "4. What's new",
		LEGAL = "5. Legal";
	
	public static final String
		GENERAL = "3.1. General",
		ANALYSES = "3.2. Analyses",
		SPLITS = "3.3. Splits",
		TESTS = "3.4. Tests",
		TRANSFORMATIONS = "3.5. Transformations";
	
	// Add any general help pages to this map
	public static final Map<String[],String>
		GENERAL_HELP_PAGES = new HashMap<>();
	
	// Or use this method to add help pages
	public static void addPage(String title, String resource, String... category) {
		GENERAL_HELP_PAGES.put(Utilities.add(category,title),resource); }
	
	static {
		// Predefined help pages
		addPage(OVERVIEW, "help/overview.html");
		addPage(LEGAL, "help/legal.html");
		
		addPage("2.1. Analysis Tool", "help/getting_started/analysis.html", GETTING_STARTED);
		addPage("2.2. Batch Tool", "help/getting_started/batch.html", GETTING_STARTED);
		addPage("2.3. BDA Tool", "help/getting_started/bda.html", GETTING_STARTED);
		
		addPage("Version 1.x (Alpha)", "help/whats_new/1.html", WHATS_NEW);
		addPage("Version 2.0 (Beta)", "help/whats_new/2_0.html", WHATS_NEW);
	}
}
