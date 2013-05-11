package complexity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ComplexityAnalysis {
	
	HashMap<String, ComplexityOperationList> data = new HashMap<String, ComplexityOperationList>();
	List<String> keys = new ArrayList<String>();
	
	List<String> log = new ArrayList<String>();
	
	public void putOperationType(String key, ComplexityOperation operation, double weight){
		keys.add(key);
		ComplexityOperationList newList = new ComplexityOperationList(operation, weight);
		data.put(key, newList);
	}
	
	public void putData(String key, int[] complexity){
		key = key.intern();
		
		ComplexityOperationList op = data.get(key);
		//If the cost of this operation is nonzero, add it.  This check is to filter out costless sorts of single element lists.
		if(op.complexity.evaluateCost(complexity, op.weight) > .001){
			op.add(complexity);
		}
		
		log.add("Added " + key + " operation: " + op.complexity.asymptoticCostString() + ".  " + op.complexity.costString(complexity, op.weight));
	}
	
	public double totalCost(String group){
		group = group.intern();
		ComplexityOperationList operation = data.get(group);
		return operation.evaluateTotalCost();
	}
	
	public double totalCost(int index){
		return totalCost(keys.get(index));
	}
	
	public double totalCostAll(){
		double cost = 0;
		for(int i = 0; i < keys.size(); i++){
			cost += totalCost(i);
		}
		return cost;
	}
	
	public String[] detailedInfo(String group){
		group = group.intern();
		
		ComplexityOperationList operations = data.get(group);
		ComplexityOperation complexity = operations.complexity;
		
		String[] retval = new String[operations.size() + 1];
		retval[0] = group + ": Asymptotic Cost = " + complexity.asymptoticCostString() + ".  For " + operations.size() + " operations, total cost of this operation is = " + totalCost(group);
		
		for(int i = 0; i < operations.size(); i++){
			retval[i + 1] = operations.costString(i);
		}
		
		return retval;
	}
	
	public String[][] detailedInfoAll(){
		String[][] retval = new String[keys.size() + 1][];
		retval[0] = new String[1];
		retval[0][0] = "Total cost of all operations = " + totalCostAll();
		for(int i = 0; i < keys.size(); i++){
			retval[i + 1] = detailedInfo(keys.get(i));
		}
		
		return retval;
	}
	
	//Gets rid of all info held by this datatype
	public void reset(){
		data.clear();
		keys.clear();
		log.clear();
	}
	
	public void sort(){
		sortGroups();
		for(int i = 0; i < keys.size(); i++){
			sortWithinGroup(i);
		}
	}
	
	public void sortGroups(){
		Object[] keyStrings = keys.toArray();
		double[] costValues = new double[keyStrings.length];
		for(int i = 0; i < costValues.length; i++){
			costValues[i] = totalCost(i);
		}
		int[] indices = getSortIndices(costValues);
		for(int i = 0; i < keyStrings.length; i++){
			//Reorder the data.
			//Code for ascending:
//			keys.set(indices[i], (String)keyStrings[i]);
			
			//Code for descending:
			keys.set(keyStrings.length - indices[i] - 1, (String)keyStrings[i]);
		}
	}
	
	public void sortWithinGroup(int index){
		ComplexityOperationList group = data.get(keys.get(index));

		Object[] groupData = group.toArray();
		double[] costValues = new double[group.size()];
		for(int i = 0; i < group.size(); i++){
			costValues[i] = group.evaluateCost(i);
		}

		int[] indices = getSortIndices(costValues);
		for(int i = 0; i < group.size(); i++){
			//Reorder the data.
			//Code for ascending:
//			keys.set(indices[i], (String)keyStrings[i]);
			
			//Code for descending:
			group.set(group.size() - indices[i] - 1, (int[])groupData[i]);
		}
		
	}
	
	
	
	//Sort function.  A terrible sorting algorithm that I made up myself!
	//Gives the indices that each item in data would take if it were sorted.  
	//Resolves ties in such a way that each index is unique, and crawling through
	//data in the provided order is monotonic.
	private static int[] getSortIndices(double[] data){
		int[] sortPositions = new int[data.length];
		
		//find how many are less than each value.
		for(int i = 0; i < data.length; i++){
			int lessCount = 0;
			for(int j = 0; j < data.length; j++){
				if(data[j] < data[i]){
					lessCount++;
				}
			}
			sortPositions[i] = lessCount;
		}
		
		//Now get rid of repeats
		for(int i = 0; i < data.length; i++){
			boolean foundThisNum = false;
			for(int j = 0; j < data.length; j++){
				if(sortPositions[j] == i){
					if(foundThisNum){
						sortPositions[j]++;
					}
					else{
						foundThisNum = true;
					}
				}
			}
		}
		return sortPositions;
	}
	
	/*
	//Test sortPositions functions
	public static void main(String[] args){
		double[] data = new double[]{0, 1, 2, -1, 3, 4, 5};
		int[] positions = getSortIndices(data);
		for(int i = 0; i < data.length; i++){
//			System.out.println(positions[i] + ": " + data[i]);

			for(int j = 0; j < data.length; j++){
				if(positions[j] == i) System.out.println(data[j]);
			}
		}
	}
	*/
}
