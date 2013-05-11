package complexity;

import java.text.DecimalFormat;

//This class is pretty simple, it holds multiplicative complexity classes, with log and linear terms taken to arbitrary powers.
//Future work would deal with defining operations in terms of each other, and performing text processing instead of hardcoding complexity types.
public class ComplexityOperation {

	public static final DecimalFormat decFormat = new DecimalFormat("000.0000");
	
	
	
	String[] varnames;
	
	public static final int LIN=0, LOG=1;

	int[] functionTypes;
	int[] variables;
	double[] exponents;
	
	public double evaluateCost(int[] varvalues, double weight){
		double value = 1;
		for(int i = 0; i < functionTypes.length; i++){
			int varval = varvalues[variables[i]];
			double thisVal = 0;
			switch(functionTypes[i]){
				case LIN:
					thisVal = varval;
				break;
				case LOG:
					thisVal = Math.log(varval) / Math.log(2); //change of base formula, log base 2
				break;
			}
			thisVal = Math.pow(thisVal, exponents[i]);
			value *= thisVal;
		}
		value *= weight;
		return value;
	}
	
	//Get useful information as string
	
	//Big O notation
	public String asymptoticCostString(){
		String cost = "";
		for(int i = 0; i < functionTypes.length; i++){
			int varIndex = variables[i];
			switch(functionTypes[i]){
				case LIN:
					cost += varnames[varIndex];
					if(exponents[i] != 1){
						cost += "^" + exponents[i];
					}
				break;
				case LOG:
					cost += "log";
					if(exponents[i] != 1){
						cost += "^" + exponents[i];
					}
					cost += "(" + varnames[varIndex] + ")";
				break;
			}
		}
		if(cost == ""){ //Constant function
			cost = "1";
		}
		return "O( " + cost + " )";
	}
	
	public String costString(int[] data, double weight){
		String retval = "";
		for(int i = 0; i < varnames.length; i++){
			retval += varnames[i] + " = " + data[i];
			if(i == varnames.length - 1){
				retval += ".  \t";
			}
			else retval += ", ";
		}
		retval += " cost: " + decFormat.format(evaluateCost(data, weight)).replaceAll("\\G0", " ");
		return retval;
	}
	
//	public String detailedInfoString(){
//		String info = "";
//		for(int i = 0; i < varnames.length; i++){
//			info += varnames[i] + " = " + varvalues[i];
//			if(i == varnames.length - 1){
//				info += ".  ";
//			}
//			else{
//				info += ", ";
//			}
//		}
//		info += "Total cost: " + evaluateCost();
//		return info;
//	}
	
	//Constructors

	public static ComplexityOperation constant(){
		ComplexityOperation operation = new ComplexityOperation();
		
		operation.varnames = new String[]{};
		operation.functionTypes = new int[]{};
		operation.variables = new int[]{};
		operation.exponents = new double[]{};
		
		return operation;
	}
	public static ComplexityOperation linear(String varname){
		ComplexityOperation operation = new ComplexityOperation();
		
		operation.varnames = new String[]{varname};
		
		operation.functionTypes = new int[]{LIN};
		operation.variables = new int[]{0};
		operation.exponents = new double[]{1};
		
		return operation;
	}
	
	public static ComplexityOperation quadratic(String varname){
		ComplexityOperation operation = new ComplexityOperation();
		
		operation.varnames = new String[]{varname};
		
		operation.functionTypes = new int[]{LIN};
		operation.variables = new int[]{0};
		operation.exponents = new double[]{2};
		
		return operation;
	}
	
	public static ComplexityOperation log(String varname){
		ComplexityOperation operation = new ComplexityOperation();
		
		operation.varnames = new String[]{varname};
		
		operation.functionTypes = new int[]{LOG};
		operation.variables = new int[]{0};
		operation.exponents = new double[]{1};
		
		return operation;
	}
	
	public static ComplexityOperation nlogn(String varname){
		ComplexityOperation operation = new ComplexityOperation();
		
		operation.varnames = new String[]{varname};

		operation.functionTypes = new int[]{LIN, LOG};
		operation.variables = new int[]{0, 0};
		operation.exponents = new double[]{1, 1};
		
		return operation;
	}
	
	public static ComplexityOperation nlogk(String var1, String var2){
		ComplexityOperation operation = new ComplexityOperation();
		
		operation.varnames = new String[]{var1, var2};
		
		operation.functionTypes = new int[]{LIN, LOG};
		operation.variables = new int[]{0, 1};
		operation.exponents = new double[]{1, 1};
		
		return operation;
	}
}
