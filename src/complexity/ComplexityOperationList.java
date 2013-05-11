package complexity;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ComplexityOperationList extends ArrayList<int[]> {
	
	ComplexityOperation complexity;
	double weight;

	public ComplexityOperationList(ComplexityOperation complexity, double weight) {
		super();
		this.complexity = complexity;
		this.weight = weight;
	}
	
	public double evaluateTotalCost(){
		double costSum = 0;
		
		for(int[] vars : this){
			costSum += complexity.evaluateCost(vars, weight);
		}
		
		return costSum;
	}
	
	public double evaluateCost(int index){
		return complexity.evaluateCost(get(index), weight);
	}

	public String asymptoticCostString(){
		return complexity.asymptoticCostString();
	}
	
	public String costString(int index){
		return complexity.costString(get(index), weight);
	}
}
