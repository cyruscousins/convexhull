package complexity;

public class ComplexityTest {
	public static void main(String[] args){
		ComplexityAnalysis analysis = new ComplexityAnalysis();

		analysis.putOperationType("Generic Sort", ComplexityOperation.nlogn("n"), 1);
		analysis.putOperationType("Find Max", ComplexityOperation.linear("n"), .5);
		analysis.putOperationType("Find Median", ComplexityOperation.linear("n"), 2);
		
		analysis.putOperationType("Find Extreme Point", ComplexityOperation.linear("n"), .75);
		analysis.putOperationType("Calculate Convex Hull", ComplexityOperation.nlogk("n", "k"), 4);
		
		for(int i = 1; i < 16; i++){
			analysis.putData("Generic Sort", new int[]{i * 2});

			analysis.putData("Find Max", new int[]{64 / i});
			analysis.putData("Find Median", new int[]{64 / i});
			
			analysis.putData("Find Extreme Point", new int[]{128 / i});
			
			if(i % 4 == 0) analysis.putData("Calculate Convex Hull", new int[]{i * 4, i});
			
		}
		
//		String[] data = analysis.detailedInfo("Generic Sort");
//		for(int i = 0; i < data.length; i++){
//			System.out.println(data[i]);
//		}
		
		String[][] data = analysis.detailedInfoAll();
		for(int i = 0; i < data.length; i++){
			for(int j = 0; j < data[i].length; j++){
				if(j != 0) System.out.print("\t");
				System.out.println(data[i][j]);
			}
		}
	}
}
