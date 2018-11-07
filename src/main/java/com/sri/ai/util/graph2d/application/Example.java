package com.sri.ai.util.graph2d.application;

import static com.sri.ai.util.Util.map;
import static com.sri.ai.util.Util.println;
import static com.sri.ai.util.graph2d.api.functions.Function.function;
import static com.sri.ai.util.graph2d.api.functions.Functions.functions;
import static com.sri.ai.util.graph2d.api.graph.GraphSetMaker.graphSetMaker;
import static com.sri.ai.util.graph2d.api.variables.SetOfVariables.setOfVariables;
import static com.sri.ai.util.graph2d.api.variables.Value.value;
import static com.sri.ai.util.graph2d.api.variables.Variable.enumVariable;
import static com.sri.ai.util.graph2d.api.variables.Variable.integerVariable;
import static com.sri.ai.util.graph2d.api.variables.Variable.realVariable;
import static com.sri.ai.util.graph2d.core.values.SetOfEnumValues.setOfEnumValues;
import static com.sri.ai.util.graph2d.core.values.SetOfIntegerValues.setOfIntegerValues;

import com.sri.ai.util.graph2d.api.functions.Function;
import com.sri.ai.util.graph2d.api.functions.Functions;
import com.sri.ai.util.graph2d.api.graph.GraphSet;
import com.sri.ai.util.graph2d.api.graph.GraphSetMaker;
import com.sri.ai.util.graph2d.api.variables.Unit;
import com.sri.ai.util.graph2d.api.variables.Variable;

public class Example {

	public static void main(String[] args) {
		
		Variable continent = enumVariable("Continent");
		Variable age = integerVariable("Age", Unit.YEAR);
		Variable occupation = enumVariable("Occupation");
		Variable income = realVariable("Income", Unit.DOLLAR);
		Variable expense = realVariable("Expense", Unit.DOLLAR);

		Function incomeFunction = 
				function("Income", income, setOfVariables(continent, age, occupation),
						(assignment) -> {
							String continentValue = assignment.get(continent).stringValue();
							String occupationValue = assignment.get(occupation).stringValue();
							
							if (continentValue.equals("North America") || continentValue.equals("Europe")) {
								if (occupationValue.equals("Doctor")) {
									int ageValue = assignment.get(age).intValue();
									return ageValue > 40? value(200000) : value(150000);
								}
								else {
									return value(100000);
								}
							}
							else {
								return value(50000);
							}
						}
				);
		
		Function expenseFunction = 
				function("Expense", expense, setOfVariables(continent, age, occupation),
						(assignment) -> value(incomeFunction.evaluate(assignment).doubleValue() * 0.75)
				);
		
		Functions functions = functions(incomeFunction, expenseFunction);

		GraphSetMaker graphSetMaker = graphSetMaker();

		graphSetMaker.setFunctions(functions);
		graphSetMaker.setFromVariableToSetOfValues(
				map(
						continent, setOfEnumValues("North America", "Africa", "Europe"),
						age, setOfIntegerValues(18, 99),
						occupation, setOfEnumValues("Driver", "CEO", "Doctor")
						)
				);

		GraphSet graphSet = graphSetMaker.make(age);
		
		println(graphSet);
	}
}