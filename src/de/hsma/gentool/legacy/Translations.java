package de.hsma.gentool.legacy;

public class Translations {

//                  Gui Übersetzungen

// Checkboxes GUI
public static final String allTransformationsCheckbox = "all transformations";
public static final String specialTransformationsCheckbox = "8 special transformations";
public static final String allOptions = "all options";

//Checkboxes CalculationGUI_2
public static final String symmetric 			= "symmetric";
public static final String zeroAndFourDiagonal 	= " '0' & '4' in diagonal ";
public static final String oneLineNotNull 		= "One line includes no '0'";
public static final String AUdifferentUA 		= "AU/CG are different form UA/GC";
public static final String _2square5 			= "2<= AA+AC+CA+CC >=5";
public static final String noRowOnly1_2_3 		= "No row consists only of 1, 2, 3";
public static final String ifRow3_2diagonal4	= "if row has three 2s, diagonal is 4";
public static final String ifRowthree0s_no4 	= "if row has three 0s, last is never 4";
public static final String noRowWithThree1sOr3s = "no row including Three 1s or 3s";
public static final String comparedSmallerFour  = "compared values <= 4";
public static final String fourTimes4inSquare	= "4*4 appears always as a square";

// Tool Name
public static final String toolName = "Genetic Code Analyzer";
public static final String calculateTableNr = "Calculate possible Nr of tables"; 

// Labels
public static final String transformationsHeader = "Transformations";
public static final String codonsHeader = "Codons";
public static final String lengthLabel = "length";
public static final String inputLabel = "Input field";
public static final String outputLabel = "Output field";
public static final String nLabel = "n";
public static final String numberLabel = "number";

//dropdown menus
public static final String[] constraintedCodesComboBox = {"choose constraints","C 3 CODES", "COMMA-FREE CODES"};

// Buttons
public static final String calculateNrPossibleTablesButton = "Tables";
public static final String trinucleotidesButton = "Input assistance";
public static final String checkForCommaFreeButton = "comma-free?";
public static final String checkForCircularButton = "(n)-circular?";
public static final String transformButton = "transform code";
public static final String splitButton = "split the code into 2 comma-free codes";
public static final String selfComplementaryButton = "self-complementary?";
public static final String aminoAcidsButton = "amino acids";
public static final String compareSetsButton = "intersection with transformation empty?";
public static final String startCommaFreeSearchButton = "start comma-free search";
public static final String splitInEqualPartsButton = "split the code into equal sized comma-free codes";
public static final String clearInputButton = "clear input";
public static final String clearOutputButton = "clear output";
public static final String splitInFourCommaFreeButtons = "split the code in 4 comma-free codes";

//Buttons CalculationGUI_2
public static final String calculate = "Calculate";
public static final String selectAll = "Select all";
public static final String print = "Calculate & Print";
public static final String convertCodeToTableButton = "create table";

// Messages
public static final String enterInputMessage = "Please enter code";
public static final String enterTableMessage = "Please enter table";
public static final String onlyMaximalMessage = "Possibilities can only be found for maximal codes";
public static final String selectTransformationsMessage = "Please select one or more transformations";
public static final String enterWrongLengthMessage = "Please enter a length between 1 and @length";
public static final String enterLengthMessage = "Please enter a length";
public static final String enterWrongNMessage = "n should be between 1 and @n";
public static final String splitResultEmptyMessage = "This code cannot be split into two comma-free codes";
public static final String commaFreeCodesFoundMessage = "comma-free codes found";
public static final String notValidCodonMessage = " is not a valid codon";
public static final String codeCanNotBeDividedInSameLengthMessage = "The code cannot be split into comma-free codes with the same length";
public static final String codeHasWrongLengthMessage = "The code must have 20 codons to be maximal";
public static final String codeNumberInvalidMessage = "Invalid code number";
public static final String enterNumberMessage = "Please enter a code number";
public static final String C3NoCircularMessage = "Circularity is mandatory for the C3 property";



//                   GuiMethods Übersetzungen

public static final String codeIsNotCommaFreeMessage = "The code is not comma-free since @codon is not valid";
public static final String codonIsNotValidMessage = "The code is not comma-free since @codon is not allowed";
public static final String codeIsCommaFreeMessage = "The code is comma-free";
public static final String codonsBelongToEquivalentClassMessage = "The code is not comma-free since @codons belong to the same equivalence class";
public static final String shiftedAt1CodonInCodeMessage = "The code is not comma-free since @shiftedCodon is in code and can be created through shifting the codons @codon1 and @codon2 once";
public static final String shiftedAt2CodonInCodeMessage = "The code is not comma-free since @shiftedCodon is in code and can be created through shifting the codons @codon1 and @codon2 twice";
public static final String codeIsSelfComplementaryMessage = "The code is self-complementary";
public static final String codeIsNotSelfComplementaryMessage = "The code is not self-complementary since the following codons @codons have no anti-codons in the code";
public static final String averageQuantityOfCodeAndTransformedCodeIsEmptyMessage = "The transformed code contains no codon from original code";
public static final String codonsIncludedInCodeAndTransformedCodeMessage = "The transformed and original code contain the following codon: ";
public static final String codeCanNotBeDividedMessage = "The code cannot be split";
public static final String codeIsNotCircularMessage = "The code is not circular since @codon is not valid";
public static final String codeIsNotC3Message = "The code is not a C3 code";
public static final String codonNotAllowedInCircularMessage = "The code is not circular since @codon is not allowed";
public static final String codeIsCiruclarMessage = "The code is circular";
public static final String codeIsC3Message = "The code is a C3 code";
public static final String shiftedAt1InCircularCodeMessage = "The code is not @n-circular since @shiftedCodons are in code and can be created through shifting the codons @codons once";
public static final String shiftedAt2InCircularCodeMessage = "The code is not @n-circular since @shiftedCodons are in the code and can be created through shifting the codons @codons twice";
public static final String codonsBelongToEquivalentClassForCircularMessage = "The code is not circular since @codons belong to the same equivalence class";
}