package de.hsma.gentool.legacy;

public class Table{
	private String[] codons;
	
	private int[][] tablePair;
	
	public Table(String[] codons){
		this.codons = codons;
	}



	public int[][] getTablePair(int posA, int posB) {
		if(tablePair == null)
			createTablePair(posA, posB);
		return tablePair;
	}

	private void createTablePair(int posA, int posB) {
		tablePair = new int[4][4];
		fillWithNull(tablePair);
		for(int i = 0; i<codons.length; i++){
			int temp1 = convertCharToIndex(codons[i].charAt(posA));
			int temp2 = convertCharToIndex(codons[i].charAt(posB));
			tablePair[temp1][temp2]++;
		}		
	}
	
	private void fillWithNull(int[][] table){
		for(int i = 0; i<table.length; i++){
			for(int i2 = 0; i2<table[i].length;i2++)
				table[i][i2] = 0;
		}
	}
	
	private int convertCharToIndex(char x){
		if(x == 'A'){
			return 0;
		}if(x == 'C'){
			return 1;
		}if(x == 'G'){
			return 2;
		}else{
			return 3;
		}
	}
	
		
	
}
