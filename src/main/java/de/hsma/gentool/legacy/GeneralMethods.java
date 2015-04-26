package de.hsma.gentool.legacy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GeneralMethods {
	
	public final static String[][] trinuncleotideClasses = { 
		{ "AAC", "ACA", "CAA" },
		{ "AAG", "AGA", "GAA" }, 
		{ "AAU", "AUA", "UAA" },
		{ "ACC", "CCA", "CAC" }, 
		{ "ACG", "CGA", "GAC" },
		{ "ACU", "CUA", "UAC" }, 
		{ "AGC", "GCA", "CAG" },
		{ "AGG", "GGA", "GAG" }, 
		{ "AGU", "GUA", "UAG" },
		{ "AUC", "UCA", "CAU" },
		{ "AUG", "UGA", "GAU" },
		{ "AUU", "UUA", "UAU" }, 
		{ "CCG", "CGC", "GCC" },
		{ "CCU", "CUC", "UCC" }, 
		{ "CGG", "GGC", "GCG" },
		{ "CGU", "GUC", "UCG" }, 
		{ "CUG", "UGC", "GCU" },
		{ "CUU", "UUC", "UCU" }, 
		{ "GGU", "GUG", "UGG" },
		{ "GUU", "UUG", "UGU" } 
	};

	/**
	 * Mit Hilfe dieser Methode kann überprüft werden ob der übergebene String ein Codon ist
	 * @param codon Zu prüfender String
	 * @return Codon oder nicht
	 */
	public boolean IsStringCodon(String codon) {
		if (codon.length() != 3) {
			return false;
		}
		
		for (String[] classes : trinuncleotideClasses) {
			for (String trununcleotide : classes) {
				if (trununcleotide.equals(codon) || "AAA".equals(codon) || "CCC".equals(codon) || "GGG".equals(codon) || "UUU".equals(codon)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Eine übergebene List von String wird auf comma-free getestet
	 * @param code Liste von String
	 * @return
	 */
	public String IsCodeCommaFree(ArrayList<String> code) {
		// test auf Codons
		for (String codon : code) {
			if (!IsStringCodon(codon)) {
				return Translations.codeIsNotCommaFreeMessage.replace("@codon", codon);
			}
			if ("AAA".equals(codon) || "CCC".equals(codon) || "GGG".equals(codon) || "UUU".equals(codon)) {
				return Translations.codonIsNotValidMessage.replace("@codon", codon);
			}
		}
		
		String resultClasses = EveryEquivalentClasseOneTime(code);
		String resultShifted = ShiftedCondonsInCode(code);
		
		if (resultClasses.equals("") && resultShifted.equals("")) {
			return Translations.codeIsCommaFreeMessage;
		}
		else if (!resultClasses.equals("")) {
			return Translations.codonsBelongToEquivalentClassMessage.replace("@codons", EveryEquivalentClasseOneTime(code).trim());
		}
		else {
			return resultShifted;
		}
	}
	
	/**
	 * Diese Methode testet die Codons auf äquivalenz
	 * @param code
	 * @return
	 */
	private String EveryEquivalentClasseOneTime(ArrayList<String> code) {
		String resultString = "";
		ArrayList<String> trinuncleotide;
		
		for (int i = 0; i < trinuncleotideClasses.length; i++) {
			trinuncleotide = new ArrayList<String>();
			
			for (int j = 0; j < trinuncleotideClasses[i].length; j++) {
				for (String codon : code) {
					if (codon.equals(trinuncleotideClasses[i][j])) {
						trinuncleotide.add(codon);
					}
				}
			}
			
			if (trinuncleotide.size() > 1) {
				resultString = "";
				
				for (String codon : trinuncleotide) {
					resultString += codon + " ";
				}
			}
		}
		
		return resultString;
	}

	/**
	 * Diese Methode testet Codons, ob sie durch eine Verschiebung im Code enthalten sind
	 * @param code Liste aller Codons
	 * @return
	 */
	private String ShiftedCondonsInCode(ArrayList<String> code) {
		String word = "";
		String shiftedAt1 = "";
		String shiftedAt2 = "";

		for (String codon1 : code) {
			for (String codon2 : code) {
				if (!codon1.equals(codon2)) {
					word = codon1 + codon2;

					shiftedAt1 = word.substring(1, 4);
					shiftedAt2 = word.substring(2, 5);

					for (String codon : code) {
						if (codon.equals(shiftedAt1)) {
							return Translations.shiftedAt1CodonInCodeMessage.replace("@shiftedCodon", shiftedAt1).replace("@codon1", codon1).replace("@codon2", codon2);
						}
						
						if (codon.equals(shiftedAt2)) {
							return Translations.shiftedAt2CodonInCodeMessage.replace("@shiftedCodon", shiftedAt1).replace("@codon1", codon1).replace("@codon2", codon2);
						}
					}
				}
			}
		}
		return "";
	}
	
	/**
	 * Mit Hilfe dieser Mehtode können mehrere transformationen auf einen Code angewand werden
	 * @param transformations 
	 * @param code 
	 * @return Eine Liste aller Transformationenen des Codes
	 */
	public ArrayList<String> Transformation(HashMap<String, HashMap<Character, Character>> transformations, ArrayList<String> code) {	
		char[] letter;
		String[] transformedCode;
		
		ArrayList<String> transformedCodes = new ArrayList<String>();

		String transformedString;

		for (Map.Entry<String, HashMap<Character, Character>> transformation : transformations.entrySet()) {
			transformedCode = new String[code.size()];
		
			for (int i = 0; i < transformedCode.length; i++) {
				letter = code.get(i).toCharArray();
				
				for (int j = 0; j < letter.length; j++) {
					switch(letter[j]) {
					case 'A': 
						letter[j] = transformation.getValue().get('A'); 
						break;
					case 'C' : 
						letter[j] = transformation.getValue().get('C'); 
						break;
					case 'G':
						letter[j] = transformation.getValue().get('G'); 
						break;
					default:
						letter[j] = transformation.getValue().get('U'); 
					}
				} 

				transformedCode[i] = "" + letter[0] + letter[1] + letter[2];
			}
			
			transformedString = transformation.getKey() + ": ";
			
			for (String codon : transformedCode) {
				transformedString += codon + " ";
			}
			transformedCodes.add(transformedString);
		}
		return transformedCodes;
	}
	
	/**
	 * Diese Methode versucht einen Code in zwei comma-free Codes aufzuteilen
	 * @param code Eine Liste von Strings
	 * @param length Die länge für einen der zwei comma-free Codes, die zweite wird durch max - length berechnet
	 * @param allPosibilities Ob alle Möglichkeiten für eine Aufteilung aufgelistet werden soll (true) oder nur eine (false)
	 * @return
	 */
	public ArrayList<String> SpitCode(ArrayList<String> code, int length, boolean allPosibilities) {
		if (length >= code.size()) {
			return new ArrayList<String>();
		}
		
		if (length > code.size() / 2) {
			length = code.size() - length;
		}
		
		Integer[] newCode = new Integer[length];
		for (int i = 0; i < length; i++) {
			newCode[i] = i;
		}

		boolean lastIteration = false;
		boolean containsElement;
		
		ArrayList<String> leftCode;
		ArrayList<String> rightCode;
		
		ArrayList<String> result = new ArrayList<String>();
		String resultString;
		
		ArrayList<Integer[]> codeInList = new ArrayList<Integer[]>();
		ArrayList<Integer> rightList;
		Integer[] rightListNumbers;
		boolean inList;

		do {
			leftCode = new ArrayList<String>();
			rightCode = new ArrayList<String>();
			
			for (int i = 0; i < code.size(); i++) {
				containsElement = false;
				
				for (int codon : newCode) {
					if (codon == i) {
						containsElement = true;
						break;
					}
				}
				
				if (containsElement) {
					leftCode.add(code.get(i));
				}
				else {
					rightCode.add(code.get(i));
				}
			}
			
			if (IsCodeCommaFree(leftCode).equals(Translations.codeIsCommaFreeMessage) && IsCodeCommaFree(rightCode).equals(Translations.codeIsCommaFreeMessage)) {
				if (!allPosibilities) {
					lastIteration = true;
				}
				
				Arrays.sort(newCode);
				inList = false;
				
				for (Integer[] codons : codeInList) {
					if (Arrays.deepEquals(newCode, codons)) {
						inList = true;
					}
				}
				
				if (!inList) {
					codeInList.add(newCode.clone());

					rightList = new ArrayList<Integer>();
					
					for (int i = 0; i < code.size(); i++) {
						containsElement = false;
						
						for (int codon : newCode) {
							if (codon == i) {
								containsElement = true;
								break;
							}
						}
						
						if (!containsElement) {
							rightList.add(i);
						}
					}
					
					rightListNumbers = new Integer[rightList.size()]; 
					for (int i = 0; i  < rightList.size(); i++) {
						rightListNumbers[i] = rightList.get(i);
					}

					Arrays.sort(rightListNumbers);
					codeInList.add(rightListNumbers.clone());
					
					resultString = "";
					for (String codon : rightCode) {
						resultString += codon + " ";
					}
					resultString += "| ";
					for (String codon : leftCode) {
						resultString += codon + " ";
					}
					
					
					result.add(resultString);
				}

			}
			
			for (int k = length - 1; k >= 0; k--) {
				newCode[k]++;
				
				if (newCode[0] == code.size() + 1 - length) {
					lastIteration = true;
					break;
				}
				else if (newCode[k] < (code.size() + 1 - length) + k) {
					if (k != length - 1) {
						for (int j = k + 1; j < length; j++) {
							newCode[j] = newCode[j - 1] + 1;
						}
					}
					
					break;
				}
			}				
		}while (!lastIteration);
		
		return result;
	}
	
	/**
	 * Diese Methode testet einen Code, ob er Selbstkomplementär ist
	 * @param code Eine Liste mit bestehend aus Codons
	 * @return
	 */
	public String IsCodeSelfComplementary(ArrayList<String> code) {
		String resultString = "";
		
		for (String codon : code) {
			char[] letter = codon.toCharArray();
			for (int i = 0; i < letter.length; i++) {
				switch(letter[i]) {
					case 'A': 
						letter[i] = 'U'; 
						break;
					case 'U' : 
						letter[i] = 'A';
						break;
					case 'G':
						letter[i] = 'C';
						break;
					default:
						letter[i] = 'G';
				}
			}
			String newCodon = "" + letter[2] + letter[1] + letter[0];	

			if (!CodeContainsCodon(code, newCodon)) {
				resultString += codon + " ";
				
			}	
		}
		
		if (resultString.equals("")) {
			resultString = Translations.codeIsSelfComplementaryMessage;
		}
		else {
			resultString = Translations.codeIsNotSelfComplementaryMessage.replace("@codons", resultString.trim());
		}
		
		return resultString;
	}
	
	/**
	 * Prüft ob eine Codon im Code beinhaltet ist
	 * @param commaFreeCode
	 * @param codon
	 * @return
	 */
	private boolean CodeContainsCodon(ArrayList<String> commaFreeCode, String codon) {
		for (String code : commaFreeCode) {
			if (code.equals(codon)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Mit dieser methode werden alle im Code beinhalteten Aminosäuren ausgegeben
	 * @param code
	 * @return
	 */
	public String AminoAcidsInCode(ArrayList<String> code) {
		Map<String, String> aminoAcids = new AminoAcids().allAminoAcids;
		String reusltString = "";
		HashMap<String, Integer> codeWithAminoAcidsCounter = new HashMap<String,Integer>();
		ArrayList<String> foundAcids = new ArrayList<String>();
		int counter;
		
		for (String codon : code) {
			if (aminoAcids.containsKey(codon)) {
				foundAcids.add(aminoAcids.get(codon));
			}
		}
		
		for (String acid : foundAcids) {
			counter = 0;
			if (!codeWithAminoAcidsCounter.containsKey(acid)) {
				for (String acid2 : foundAcids) {
					if (acid2.equals(acid)) {
						counter++;
					}
				}
				codeWithAminoAcidsCounter.put(acid, counter);
			}
		}
		
		for (Map.Entry<String, Integer> codeAcidsNumber : codeWithAminoAcidsCounter.entrySet()) {
			reusltString += codeAcidsNumber.getValue() + " x " + codeAcidsNumber.getKey() + " ";
		}
		
		return reusltString;
	}
	
	
	public String IsCodeC3Code(ArrayList<String> code) {
		if(!(IsCodeCircular(code,4).equals(Translations.codeIsCiruclarMessage))){
			return Translations.codeIsNotC3Message;
		}else{
			ArrayList<String> shifted1 =shift(code);
			if(!(IsCodeCircular(shifted1,4).equals(Translations.codeIsCiruclarMessage))){
				return Translations.codeIsNotC3Message;
			}else{
				if(!(IsCodeCircular(shift(shifted1),4).equals(Translations.codeIsCiruclarMessage))){
					return Translations.codeIsNotC3Message;
				}
			}
		}
		return Translations.codeIsC3Message;
	}
	
	private ArrayList<String> shift(ArrayList<String> code){
		ArrayList<String> codeShifted =  new ArrayList<String>();
		for (String i : code){
			char N1 = i.charAt(0);
			char N2 = i.charAt(1);
			char N3 = i.charAt(2);
			String codon = N2 + "" + "" + N3 + "" + N1;
			codeShifted.add(codon);
		}
		System.out.println(codeShifted.toString());
		return codeShifted;
	}
	
	/**
	 * Diese Methode sucht nach allen comma-free Codes mit der übergebenen länge
	 * @param length
	 * @return
	 */
	public ArrayList<String> FindAllCommaFreeCodesWithLength(int length) {
		if (length > 20 || length  <= 0) {
			return new ArrayList<String>();
		}
		
		int[] newCode = new int[length];
		for (int i = 0, j = 0; j < length; i += 3, j++) {
			newCode[j] = i;
		}
		
		boolean lastIteration = false;
		
		String commaFreeCodeString;
		
		ArrayList<String> commaFreeCodes = new ArrayList<String>();
		ArrayList<String> code;
		
		int line;
		int column;
		
		do {
			code = new ArrayList<String>();
			
			for (int i = 0; i < newCode.length; i++) {
				column = newCode[i] / 3;
				line = newCode[i] % 3;
				
				code.add(trinuncleotideClasses[column][line]);
			}
			
			if (ShiftedCondonsInCode(code).equals("")) {
				commaFreeCodeString = "";
				
				for (String codon : code) {
					commaFreeCodeString += codon + " ";
				}
				
				commaFreeCodes.add(commaFreeCodeString);
			}
			
			for (int i = length - 1, k = length - 1; i >= 0; i--, k -= 3) {
				newCode[i]++;
				
				if (newCode[0] == 60 - ((length - 1) * 3)) {
					lastIteration = true;
					break;
				}
				else if (newCode[i] < (60 + 1 - length) + k) {
					if (i != length - 1) {
						for (int j = i + 1; j < length; j++) {
							column = newCode[j - 1] / 3;
							newCode[j] = column * 3 + 3;
						}
					}
					break;
				}
			}
		}
		while(!lastIteration);
		
		return commaFreeCodes;
	}
	
	/**
	 * Diese Methode prüft ob ein Code und die übergebenen Transformationen eine leere Menge bilden oder nicht
	 * @param transformations
	 * @param code
	 * @return
	 */
	public ArrayList<String> IsSetAmountEmpyForTransformation(HashMap<String, HashMap<Character, Character>> transformations, ArrayList<String> code) {
		ArrayList<String> result = new ArrayList<String>();
		
		String[] transformedCode;
		char[] letter;
		
		for (Map.Entry<String, HashMap<Character, Character>> rule : transformations.entrySet()) {
			transformedCode = new String[code.size()];
		
			for (int i = 0; i < code.size(); i++) {
				letter = code.get(i).toCharArray();
				
				for (int j = 0; j < letter.length; j++) {
					switch(letter[j]) {
					case 'A': 
						letter[j] = rule.getValue().get('A'); 
						break;
					case 'C' : 
						letter[j] = rule.getValue().get('C'); 
						break;
					case 'G':
						letter[j] = rule.getValue().get('G'); 
						break;
					default:
						letter[j] = rule.getValue().get('U'); 
					}
				} 

				transformedCode[i] = "" + letter[0] + letter[1] + letter[2];
			}
			
			String inCommon = HaveCodonsInCommon(code, transformedCode);
			
			if (inCommon.equals("")) {
				result.add(rule.getKey() + ": " + Translations.averageQuantityOfCodeAndTransformedCodeIsEmptyMessage);
			}
			else {
				result.add(rule.getKey() + ": " +  Translations.codonsIncludedInCodeAndTransformedCodeMessage + inCommon);
			}
		}
		return result;
	}
	
	/**
	 * Mit Hilfe dieser Methode werden zwei Code auf gleiche Codons verglichen
	 * @param code1
	 * @param code2
	 * @return
	 */
	private String HaveCodonsInCommon(ArrayList<String> code1, String[] code2) {
		for (String codon1 : code1) {
			for (String codon2 : code2) {
				if (codon1.equals(codon2)) {
					return codon1;
				}
			}
		}
		return "";
	}
	
	/**
	 * Diese Funktion versucht einen Code in gleich große Comma-free Codes zu zerlegen, dabei werden zuerst alle möglichen Comma-free Codes gesucht 
	 * und dann wird versucht eine Kombination gesucht, in der jeder Codon einmal vorkommt
	 * @param code Eine Liste mit Codons
	 * @param length Länge der Teile
	 * @return
	 */
	public String DivideCodeInCommaFreeCodesWithEqualLength(ArrayList<String> code, int length) {
		int parts = code.size() / length;
		
		String resultString = "";
		
		ArrayList<Integer> newCode = new ArrayList<Integer>();
		for (int i = 0; i < length; i++) {
			newCode.add(i);
		}
		
		ArrayList<String> codonCode;
		ArrayList<ArrayList<String>> commaFreeCodes = new ArrayList<ArrayList<String>>();
		
		do {
			codonCode = new ArrayList<String>();
			for (int codonNumber : newCode) {
				codonCode.add(code.get(codonNumber));
			}
			
			if (IsCodeCommaFree(codonCode).equals(Translations.codeIsCommaFreeMessage)) {
				commaFreeCodes.add(codonCode);
			}
			
			newCode = Increment(newCode, code.size());
		}
		while(newCode.get(0) != code.size() - length + 1);
				
		if (commaFreeCodes.size() >= parts) {
			
			int[] dividedCodes = new int[parts];
			for (int i = 0; i < parts; i++) {
				dividedCodes[i] = i;
			}
			
			boolean lastIteration = false;
			
			ArrayList<ArrayList<String>> usedCommaFreeCodes;
			
			do {
				usedCommaFreeCodes = new ArrayList<ArrayList<String>>();
				for (int codonNumber : dividedCodes) {
					usedCommaFreeCodes.add(commaFreeCodes.get(codonNumber));
				}
				
				if (IsEveryCodonUniqe(usedCommaFreeCodes)) {
					lastIteration = true;
					
					for (ArrayList<String> commaFreeCode : usedCommaFreeCodes) {
						for (String codon : commaFreeCode) {
							resultString += codon + " ";
						}
						
						if (commaFreeCode != usedCommaFreeCodes.get(usedCommaFreeCodes.size() - 1)) {
							resultString += "| ";
						}
					}
				}
				else {
					for (int i = parts - 1; i >= 0; i--) {
						dividedCodes[i]++;
						
						if (dividedCodes[i] < commaFreeCodes.size() - parts + i + 1) {
							if (i != commaFreeCodes.size() - 1) {
								for (int j = i + 1; j < parts; j++) {
									dividedCodes[j] = dividedCodes[j - 1] + 1;
								}
							}
							break;
						}
					}
					
					if (dividedCodes[0] == commaFreeCodes.size() - parts + 1) {
						lastIteration = true;
					}
				}
			}
			while(!lastIteration);
		}
		
		if (resultString.equals("")) {
			resultString = Translations.codeCanNotBeDividedMessage;
		}
		
		return resultString;
	}
	
	/**
	 * Durchsucht eine Liste von comma-free Codes ob alle Codons einmalig sind
	 * @param commaFreeCodes Eine Liste vom commma-free Codes
	 * @return
	 */
	private boolean IsEveryCodonUniqe(ArrayList<ArrayList<String>> commaFreeCodes) {
		ArrayList<String> codons = new ArrayList<String>();
		
		for (ArrayList<String> commaFreeCode : commaFreeCodes) {
			for (String codon : commaFreeCode) {
				if (codons.contains(codon)) {
					return false;
				}
				else {
					codons.add(codon);
				}
			}
		}
		
		return true;
	}

	/**
	 *  Eine Variante der Zerlegung eines Codes in 4 gleiche Teile der länge 5
	 *  wird derzeitig nicht verwendet
	 * @param code
	 * @return
	 */
	public String SplitInFourCommaFreeCodes(ArrayList<String> code) {
		String resultString = "";

		ArrayList<String> code2 = new ArrayList<String>();
		ArrayList<String> code3 = new ArrayList<String>();
		
		ArrayList<Integer> codonsNumber1 = new ArrayList<Integer>();
		ArrayList<Integer> codonsNumber2 = new ArrayList<Integer>();
		
		for(int i = 0; i < 5; i++) {
			codonsNumber1.add(i);
		}
		
		boolean lastIteration = false;
		boolean lastIteration2 = false;
		boolean codeSplitable = false;
		
		ArrayList<String> codePart; 
		
		ArrayList<String> result5To5Split = new ArrayList<String>();
		
		do {
			codePart = new ArrayList<String>();
			for (int codonNumber : codonsNumber1) {
				codePart.add(code.get(codonNumber));
			}
			
			if (IsCodeCommaFree(codePart).equals(Translations.codeIsCommaFreeMessage)) {
				lastIteration2 = false;
				
				code2 = new ArrayList<String>();
				for (int i = 0; i < code.size(); i++) {
					if (!codonsNumber1.contains(i)) {
						code2.add(code.get(i));
					}
				}
				
				codonsNumber2 = new ArrayList<Integer>();
				for (int i = 0; i < 5; i++) {
					codonsNumber2.add(i);
				}
				
				do {
					codePart = new ArrayList<String>();
					for (int codonNumber : codonsNumber2) {
						codePart.add(code2.get(codonNumber));
					}
					
					if (IsCodeCommaFree(codePart).equals(Translations.codeIsCommaFreeMessage)) {						
						code3 = new ArrayList<String>();
						for (int i = 0; i < code2.size(); i++) {
							if (!codonsNumber2.contains(i)) {
								code3.add(code2.get(i));
							}
						}
						
						result5To5Split = SpitCode(code3, 5, false);
						
						if (result5To5Split.size() > 0) {
							codeSplitable = true;
							
							for (int codonNumber : codonsNumber1) {
								resultString += code.get(codonNumber) + " ";
							}
							resultString += " | ";
							for (int codonNumber : codonsNumber2) {
								resultString += code2.get(codonNumber) + " ";
							}
							resultString += " | ";
							resultString += result5To5Split.get(0);
						}
					}		
					
					codonsNumber2 = Increment(codonsNumber2, 15);
				
					if (codonsNumber2.get(0) == 11) {
						lastIteration2 = true;
					}
				}
				while(!lastIteration2 && !codeSplitable);
			
			}		
			
			codonsNumber1 = Increment(codonsNumber1, 20);
		
			if (codonsNumber1.get(0) == 1) {
				lastIteration = true;
			}
		}
		while(!lastIteration && !codeSplitable);
	
		if (resultString.equals("")) {
			resultString = Translations.codeCanNotBeDividedMessage;
		}
		
		return resultString;
	}
	
	

	public String getConstraintedCodes(int restriction, int number){
		String result = "";
		String file = "";
		String space ="";
		
		switch (restriction){
		//C3 Codes
			case 1: 
				file ="2016C3Codes.txt";
				space = ":";
				break;
				
		//comma-freeCodes
			case 2:
				file ="408commaFreeCodes.txt";
				space = "	";
				break;
		}
		
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String zeile = null;
			while ((zeile = in.readLine()) != null) {
			//	System.out.println("Gelesene Zeile: " + zeile);
				 if (zeile.startsWith(number+ space)) {
					 result = zeile.replaceAll( "[0-9|:]", "").trim();
				 }
			}
			 in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * Wandelt code in Tabelle um
	 * @param code
	 * @return table
	 */
	public String convertCodeToTable(ArrayList<String> code) {
		
		String[] codeArray = code.toArray(new String[0]);
		Table table = new Table(codeArray); 
		int[][] tableArray = table.getTablePair(0,2);

		return (  "        A   C   G   T \n"
				+ "  \n"
				+ "A      "  + tableArray[0][0]+ "   " + tableArray[0][1]+ "   " + tableArray[0][2]+ "   " + tableArray[0][3]+ "\n" 
				+ "C     "  + tableArray[1][0]+ "   " + tableArray[1][1]+ "   " + tableArray[1][2]+ "   " + tableArray[1][3]+ "\n"
				+ "G     "  + tableArray[2][0]+ "   " + tableArray[2][1]+ "   " + tableArray[2][2]+ "   " + tableArray[2][3]+  "\n"
				+ "T      " + tableArray[3][0]+ "   " + tableArray[3][1]+ "   " + tableArray[3][2]+ "   " + tableArray[3][3]+  "\n"
			
					);
	}
	
	
/*	public String countNucleotids(ArrayList<String> code, char first, char third){
		int counter = 0;
		char[] letter;
		for (int i = 0; i < code.size(); i++) {
			letter = code.get(i).toCharArray();
			
				if (letter[0] == first && letter[2] == third){
					counter++;
				}
			}
		
		return Integer.toString(counter);
	}
	*/
	
	/**
	 * Erhöht eine Liste von Numern um 1
	 * @param code Derzeitiger Stand der Liste
	 * @param max Amzahl für die maximale Zahl in der Liste
	 * @return neue Liste mit Numern
	 */
	private ArrayList<Integer> Increment(ArrayList<Integer> code, int max) {
		
		for (int i = code.size() - 1; i >= 0; i--) {
			code.set(i,  code.get(i) + 1);
			
			if (code.get(i) < max - code.size() + i + 1) {
				if (i != code.size() - 1) {
					for (int j = i + 1; j < code.size(); j++) {
						code.set(j,  code.get(j - 1) + 1);
					}
				}
				break;
			}
		}
		
		return code;
	}
	
	/**
	 * Diese Methode testet einen übergebenen Code, ob dieser circular für die länge ist
	 * @param code Liste vond Codons
	 * @param length n-circular Zahl
	 * @return
	 */
	public String IsCodeCircular(ArrayList<String> code, int length) {
		// test auf Codons
		for (String codon : code) {
			if (!IsStringCodon(codon)) {
				return Translations.codeIsNotCircularMessage.replace("@codon", codon);
			}
			if ("AAA".equals(codon) || "CCC".equals(codon) || "GGG".equals(codon) || "UUU".equals(codon)) {
				return Translations.codonNotAllowedInCircularMessage.replace("@codon", codon);
			}
		}
		
		String equivalentClassesResult = EveryEquivalentClasseOneTime(code);
		
		if (!equivalentClassesResult.equals("")) {

			return Translations.codonsBelongToEquivalentClassForCircularMessage.replace("@codons", equivalentClassesResult);
		}
		
		// Jedes Codon gehört zu einer anderen Äquvivalentklasse 
		if (length == 1) {
			return Translations.codeIsCiruclarMessage;
			
		}
		
		if (!IsCodeCircular(code, length - 1).equals(Translations.codeIsCiruclarMessage)) {
			return IsCodeCircular(code, length - 1);
		}
		
		String resultString;
		
		int[] codons = new int[length]; 
		for (int i = 0; i < length; i++) {
			codons[i] = 0;
		}
		
		boolean lastIteration = false;
		
		ArrayList<String> codePart;
		
		do {
			codePart = new ArrayList<String>();
			for (int codonNumber : codons) {
				codePart.add(code.get(codonNumber));
			}
			
			resultString = ShiftedCodonsInCode(code, codePart);
			
			if (!resultString.equals("")) {
				lastIteration = true;
			}
			
			for (int i = length - 1; i >= 0; i--) {
				codons[i]++;
				
				if (codons[i] != code.size()) {
					break;
				}
				else {
					for (int j = i; j < length; j++) {
						if (i != 0) {
							codons[j] = 0;
						}
						else {
							lastIteration = true;
						}
					}
				}
			}
		}
		while(!lastIteration);
		
		if (resultString.equals("")) {
			resultString = Translations.codeIsCiruclarMessage.replace("@n", length + "");
		}
		
		return resultString;
	}
	
	/**
	 * Diese Methode bekommt eine Liste von Codons und setzt sie in jeder möglichen Kombinations zusammen, um auf Circularität zu testen
	 * @param code Liste aller Codons im Code
	 * @param codons Liste einer gewissen Länge von Codons
	 * @return
	 */
	private String ShiftedCodonsInCode(ArrayList<String> code, ArrayList<String> codons) {
		
		String resultString = "";
		
		int[] codonNumbers = new int[codons.size()];
		for (int i = 0; i < codons.size(); i++) {
			codonNumbers[i] = i;
		}
		
		String orderedCodons;
		String resultCodons;
		String shiftedAt1;
		String shiftedAt2;
		
		boolean lastIteration = false;
		
		do {
			orderedCodons = "";
			resultCodons = "";
			for (int codonNumber : codonNumbers) {
				orderedCodons += codons.get(codonNumber);
				resultCodons += codons.get(codonNumber) + " ";
			}
			
			if (NumbersUnique(codonNumbers)) {
				shiftedAt1 = orderedCodons.substring(1) + orderedCodons.substring(0, 1);
				shiftedAt2 = orderedCodons.substring(2) + orderedCodons.substring(0, 2);
				
				if (CodonsInCode(code, shiftedAt1.split("(?<=\\G...)"))) {
					return Translations.shiftedAt1InCircularCodeMessage.replace("@n", codons.size() + "").replace("@codons", resultCodons.trim()).replace("@shiftedCodons", shiftedAt1.replaceAll("(.{3})(?!$)", "$1 "));
				}
				
				if (CodonsInCode(code, shiftedAt2.split("(?<=\\G...)"))) {
					return Translations.shiftedAt2InCircularCodeMessage.replace("@n", codons.size() + "").replace("@codons", resultCodons.trim()).replace("@shiftedCodons", shiftedAt2.replaceAll("(.{3})(?!$)", "$1 "));
				}
			}
			
			for (int i = codonNumbers.length - 1; i >= 0; i--) {
				codonNumbers[i]++;
				
				if (codonNumbers[0] == codonNumbers.length ) {
					lastIteration = true;
					break;
				}
				
				if (codonNumbers[i] == codonNumbers.length) {
					codonNumbers[i] = 0;
				}
				else {
					break;
				}
			}
		}
		while(!lastIteration);
		
		return resultString;
	}
	
	private boolean NumbersUnique(int[] codons) {
		ArrayList<Integer> usedNumbers = new ArrayList<Integer>();
		
		for (int codon : codons) {
			if (usedNumbers.contains(codon)) {
				return false;
			}
			usedNumbers.add(codon);
		}
		
		return true;
	}
	
	/**
	 * Testet ob ein Array von Codons im Code enthalten ist
	 * @param code Liste mit allen Codons
	 * @param codons  Liste von geshifteten Codons, die getestet werden sollen
	 * @return 
	 */
	private boolean CodonsInCode(ArrayList<String> code, String[] codons) {
		int counter = 0;
		
		for (String codon : codons) {
			for (String codonInCode : code) {
				if (codon.equals(codonInCode)) {
					counter++;
				}
			}
		}
		
		if (counter == codons.length) {
			return true;
		}
		else {
			return false;
		}
	}
}
