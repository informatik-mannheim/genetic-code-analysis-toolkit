package de.hsma.gentool.legacy;

import java.io.FileWriter;
import java.io.IOException;

public class IO {
	// zähle max(0), max(1), max(2), max(3), max(4)
	public int[] numbers = new int[5];

	

	public void writeTablePair(int iLine, String line, int[][] table,
			String fileName) {
		try {
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(iLine + ": " + line + "\n");
			writer.write("     A   C   G   T \n");
			for (int i = 0; i < table.length; i++) {
				writer.write(convertIndexToString(i) + ":");
				for (int i2 = 0; i2 < table[i].length; i2++) {
					writer.write("  " + IntToString(table[i][i2]));
					numbers[table[i][i2]]++;
				}
				writer.write("\n");
			}
			writer.write("\n");
			writer.close();
			setNumbers(numbers, this.numbers);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void writeManTablePair(String trans, int[][] table, String fileName) {
		try {
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(trans + ": " + "\n");
			writer.write("     A   C   G   T \n");
			for (int i = 0; i < table.length; i++) {
				writer.write(convertIndexToString(i) + ":");
				for (int i2 = 0; i2 < table[i].length; i2++) {
					writer.write("  " + IntToString(table[i][i2]));
					numbers[table[i][i2]]++;
				}
				writer.write("\n");
			}
			writer.write("\n");
			writer.close();
			setNumbers(numbers, this.numbers);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String IntToString(int x) {
		if (x < 10)
			return " " + x;
		return "" + x;
	}

	private String convertIndexToString(int i) {
		if (i == 0)
			return "A";
		if (i == 1)
			return "C";
		if (i == 2)
			return "G";
		return "T";
	}

	private void setNumbers(int[] currentNumbers, int[] staticNumbers) {
		for (int i = 0; i < currentNumbers.length; i++) {
			if (currentNumbers[i] > staticNumbers[i]) {
				staticNumbers[i] = currentNumbers[i];
			}
		}
	}
}
