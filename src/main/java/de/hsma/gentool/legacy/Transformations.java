package de.hsma.gentool.legacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Transformations {
	
	private HashMap<String, HashMap<Character, Character>> normalRules = new HashMap<String, HashMap<Character, Character>>();
	
	private HashMap<String, HashMap<Character, Character>> specialRules = new HashMap<String, HashMap<Character, Character>>();
	
	public Transformations() {
		HashMap<Character, Character> transformations;
		
		// special Rules
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'A');
		transformations.put('C', 'C');
		transformations.put('G', 'G');
		transformations.put('U', 'U');
		specialRules.put("id", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'U');
		transformations.put('C', 'G');
		transformations.put('G', 'C');
		transformations.put('U', 'A');
		specialRules.put("c", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'G');
		transformations.put('C', 'U');
		transformations.put('G', 'A');
		transformations.put('U', 'C');
		specialRules.put("p", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'C');
		transformations.put('C', 'A');
		transformations.put('G', 'U');
		transformations.put('U', 'G');
		specialRules.put("r", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'A');
		transformations.put('C', 'G');
		transformations.put('G', 'C');
		transformations.put('U', 'U');
		specialRules.put("\u03c0CG", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'U');
		transformations.put('C', 'C');
		transformations.put('G', 'G');
		transformations.put('U', 'A');
		specialRules.put("\u03c0AU", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'C');
		transformations.put('C', 'U');
		transformations.put('G', 'A');
		transformations.put('U', 'G');
		specialRules.put("\u03c0ACUG", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'G');
		transformations.put('C', 'A');
		transformations.put('G', 'U');
		transformations.put('U', 'C');
		specialRules.put("\u03c0AGUC", transformations);
		
		// normal Rules
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'C');
		transformations.put('C', 'A');
		transformations.put('G', 'G');
		transformations.put('U', 'U');
		normalRules.put("\u03c0AC", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'G');
		transformations.put('C', 'C');
		transformations.put('G', 'A');
		transformations.put('U', 'U');
		normalRules.put("\u03c0AG", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'A');
		transformations.put('C', 'C');
		transformations.put('G', 'U');
		transformations.put('U', 'G');
		normalRules.put("\u03c0UG", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'A');
		transformations.put('C', 'U');
		transformations.put('G', 'G');
		transformations.put('U', 'C');
		normalRules.put("\u03c0UC", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'U');
		transformations.put('C', 'G');
		transformations.put('G', 'A');
		transformations.put('U', 'C');
		normalRules.put("\u03c0AUCG", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'U');
		transformations.put('C', 'A');
		transformations.put('G', 'C');
		transformations.put('U', 'G');
		normalRules.put("\u03c0AUGC", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'C');
		transformations.put('C', 'G');
		transformations.put('G', 'U');
		transformations.put('U', 'A');
		normalRules.put("\u03c0UACG", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'G');
		transformations.put('C', 'U');
		transformations.put('G', 'C');
		transformations.put('U', 'A');
		normalRules.put("\u03c0UAGC", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'U');
		transformations.put('C', 'A');
		transformations.put('G', 'G');
		transformations.put('U', 'C');
		normalRules.put("\u03c0AUC", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'C');
		transformations.put('C', 'U');
		transformations.put('G', 'G');
		transformations.put('U', 'A');
		normalRules.put("\u03c0UAC", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'U');
		transformations.put('C', 'C');
		transformations.put('G', 'A');
		transformations.put('U', 'G');
		normalRules.put("\u03c0AUG", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'G');
		transformations.put('C', 'C');
		transformations.put('G', 'U');
		transformations.put('U', 'A');
		normalRules.put("\u03c0UAG", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'A');
		transformations.put('C', 'G');
		transformations.put('G', 'U');
		transformations.put('U', 'C');
		normalRules.put("\u03c0GUC", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'A');
		transformations.put('C', 'U');
		transformations.put('G', 'C');
		transformations.put('U', 'G');
		normalRules.put("\u03c0UGC", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'G');
		transformations.put('C', 'A');
		transformations.put('G', 'C');
		transformations.put('U', 'U');
		normalRules.put("\u03c0AGC", transformations);
		
		transformations = new HashMap<Character, Character>();
		transformations.put('A', 'C');
		transformations.put('C', 'G');
		transformations.put('G', 'A');
		transformations.put('U', 'U');
		normalRules.put("\u03c0GAC", transformations);
	}
	
	public HashMap<String,HashMap<Character, Character>> GetSpecialRules() {
		return specialRules;
	}
	
	public HashMap<String, HashMap<Character, Character>> GetAllRules() {
		HashMap<String, HashMap<Character, Character>> rules = normalRules;
		
		for (Map.Entry<String, HashMap<Character, Character>> rule : specialRules.entrySet()) {
			rules.put(rule.getKey(), rule.getValue());
		}
		
		return rules;
	}
	
	public HashMap<String, HashMap<Character, Character>> GetSelectedRules(ArrayList<String> rules) {
		HashMap<String, HashMap<Character, Character>> selectedRules = new HashMap<String, HashMap<Character, Character>>();
		
		for (Map.Entry<String, HashMap<Character, Character>> rule : GetAllRules().entrySet()) {
			for (String selectedRule : rules) {
				if (selectedRule.equals(rule.getKey())) {
					selectedRules.put(rule.getKey(), rule.getValue());
				}
			}
		}
		
		return selectedRules;
	}
}
