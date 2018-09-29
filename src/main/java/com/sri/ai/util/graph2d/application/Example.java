package com.sri.ai.util.graph2d.application;

import static com.sri.ai.util.Util.println;
import static com.sri.ai.util.graph2d.api.functions.Functions.functions;
import static com.sri.ai.util.graph2d.api.variables.SetOfVariables.setOfVariables;
import static com.sri.ai.util.graph2d.api.variables.Value.value;
import static com.sri.ai.util.graph2d.api.variables.Variable.enumVariable;
import static com.sri.ai.util.graph2d.api.variables.Variable.integerVariable;
import static com.sri.ai.util.graph2d.api.variables.Variable.realVariable;

import java.math.BigDecimal;

import com.sri.ai.util.graph2d.api.functions.Function;
import com.sri.ai.util.graph2d.api.functions.Functions;
import com.sri.ai.util.graph2d.api.graph.GraphSet;
import com.sri.ai.util.graph2d.api.graph.GraphSetMaker;
import com.sri.ai.util.graph2d.api.variables.Unit;
import com.sri.ai.util.graph2d.api.variables.Variable;
import com.sri.ai.util.graph2d.core.DefaultFunction;

public class Example {

	public static void main(String[] args) {
		
		Variable continent = enumVariable("Continent", "North America", "Africa", "Europe");
		Variable age = integerVariable("Age", Unit.YEAR, 18, 99);
		Variable occupation = enumVariable("Occupation", "Driver", "CEO", "Doctor");
		Variable income = realVariable("Income", Unit.DOLLAR, 0, new BigDecimal("0.1"), 100);
		Variable expense = realVariable("Expense", Unit.DOLLAR, 0, new BigDecimal("0.1"), 100);

		Function incomeFunction = 
				new DefaultFunction("Income", income, setOfVariables(continent, age, occupation),
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
				new DefaultFunction("Expense", expense, setOfVariables(continent, age, occupation),
						(assignment) -> {
							String continentValue = assignment.get(continent).stringValue();
							String occupationValue = assignment.get(occupation).stringValue();
							
							if (continentValue.equals("North America") || continentValue.equals("Europe")) {
								if (occupationValue.equals("Doctor")) {
									int ageValue = assignment.get(age).intValue();
									return ageValue > 40? value(150000) : value(100000);
								}
								else {
									return value(70000);
								}
							}
							else {
								return value(25000);
							}
						}
				);
		
		Functions functions = functions(incomeFunction, expenseFunction);
		
		GraphSetMaker graphSetMaker = GraphSetMaker.graphSetMaker();
		
		graphSetMaker.setFunctions(functions);
		GraphSet graphSet = graphSetMaker.make(age);
		
		println(graphSet);
	}
}