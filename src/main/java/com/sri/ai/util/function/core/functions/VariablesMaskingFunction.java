package com.sri.ai.util.function.core.functions;

import static com.sri.ai.util.Util.getFirst;
import static com.sri.ai.util.Util.mapIntoList;
import static com.sri.ai.util.Util.myAssert;
import static com.sri.ai.util.Util.thereExists;

import java.util.Collection;
import java.util.List;

import com.sri.ai.util.collect.DefaultManyToManyRelation;
import com.sri.ai.util.collect.ManyToManyRelation;
import com.sri.ai.util.function.api.functions.Function;
import com.sri.ai.util.function.api.functions.SingleInputFunction;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.function.core.variables.DefaultAssignment;
import com.sri.ai.util.function.core.variables.DefaultSetOfVariables;

public class VariablesMaskingFunction extends AbstractFunction {

	private Function baseFunction;

	///////////////////////
	
	public VariablesMaskingFunction(Function baseFunction, Variable... oldAndNewVariablePairs) {
		this(baseFunction, makeNewToOldRelation(oldAndNewVariablePairs, baseFunction));
	}
	
	private VariablesMaskingFunction(Function baseFunction, ManyToManyRelation<Variable, Variable> newToOldVariables) {
		this.baseFunction = baseFunction;
		this.newToOldVariables = newToOldVariables;
	}
	
	///////////////////////
	
	private String name;

	@Override
	public String getName() {
		if (name == null) {
			name = baseFunction.getName();
			for (Variable newVariable : newToOldVariables.getAs()) {
				Variable oldVariable = getOldVariable(newVariable);
				name = name.replace(oldVariable.getName(), newVariable.getName());
			}
		}
		return name;
	}

	///////////////////////
	
	private Variable outputVariable;

	@Override
	public Variable getOutputVariable() {
		if (outputVariable == null) {
			Variable baseOutputVariable = baseFunction.getOutputVariable();
			if (isOldVariable(baseOutputVariable)) {
				outputVariable = getNewVariable(baseOutputVariable);
			} else {
				outputVariable = baseOutputVariable;
				String newOutputVariableName = outputVariable.getName();
				newOutputVariableName = replaceOldVariablesNamesbyNewNames(newOutputVariableName);
				outputVariable = outputVariable.copyWithNewName(newOutputVariableName);
			}
		}
		return outputVariable;
	}

	private String replaceOldVariablesNamesbyNewNames(String string) {
		for (Variable oldVariable : getOldVariables()) {
			String oldName = oldVariable.getName();
			String newName = getNewVariable(oldVariable).getName();
			string = string.replaceAll(oldName, newName);
		}
		return string;
	}

	///////////////////////
	
	private SetOfVariables setOfInputVariables;

	@Override
	public SetOfVariables getSetOfInputVariables() {
		if (setOfInputVariables == null) {
			setOfInputVariables = getEffectiveSetOfNewVariables(baseFunction.getSetOfInputVariables());
		}
		return setOfInputVariables;
	}

	///////////////////////
	
	@Override
	public Value evaluate(Assignment assignmentToInputVariables) {
		Assignment effectiveAssignment = getEffectiveAssignmentOnOldVariables(assignmentToInputVariables);
		return baseFunction.evaluate(effectiveAssignment);
	}

	///////////////////////
	
	@Override
	protected SingleInputFunction projectIfNeeded(Variable variable, Assignment assignmentToRemainingVariables) {
		SingleInputFunction baseProjection = baseFunction.project(variable, assignmentToRemainingVariables);
		Function maskedBasedProjection = new VariablesMaskingFunction(baseProjection, newToOldVariables);
		SingleInputFunction maskedBaseProjectionSingleInputFunction = new FunctionToSingleInputFunctionAdapter(maskedBasedProjection);
		return maskedBaseProjectionSingleInputFunction;
	}

	///////////////////////
	
	private SetOfVariables getEffectiveSetOfNewVariables(SetOfVariables setOfBaseVariables) {
		if (thereExists(setOfBaseVariables.getVariables(), this::isOldVariable)) {
			List<Variable> effectiveVariables = mapIntoList(setOfBaseVariables.getVariables(), this::getEffectiveNewVariable);
			DefaultSetOfVariables result = new DefaultSetOfVariables(effectiveVariables);
			return result;
		}
		else {
			return setOfBaseVariables;
		}
	}

	private SetOfVariables getEffectiveSetOfOldVariables(SetOfVariables setOfBaseVariables) {
		if (thereExists(setOfBaseVariables.getVariables(), this::isNewVariable)) {
			List<Variable> effectiveVariables = mapIntoList(setOfBaseVariables.getVariables(), this::getEffectiveOldVariable);
			DefaultSetOfVariables result = new DefaultSetOfVariables(effectiveVariables);
			return result;
		}
		else {
			return setOfBaseVariables;
		}
	}

	private Variable getEffectiveNewVariable(Variable baseVariable) {
		if (isOldVariable(baseVariable)) {
			return getNewVariable(baseVariable);
		} else {
			return baseVariable;
		}
	}

	private Variable getEffectiveOldVariable(Variable baseVariable) {
		if (isNewVariable(baseVariable)) {
			return getOldVariable(baseVariable);
		} else {
			return baseVariable;
		}
	}

	@SuppressWarnings("unused")
	private Assignment getEffectiveAssignmentOnNewVariables(Assignment assignmentToInputVariables) {
		SetOfVariables effectiveAssignmentNewVariables = getEffectiveSetOfNewVariables(assignmentToInputVariables.getSetOfVariables());
		if (effectiveAssignmentNewVariables != assignmentToInputVariables.getSetOfVariables()) {
			return makeAssignmentOnEffectiveVariables(assignmentToInputVariables, effectiveAssignmentNewVariables);
		}
		else {
			return assignmentToInputVariables;
		}
	}

	private Assignment getEffectiveAssignmentOnOldVariables(Assignment assignmentToInputVariables) {
		SetOfVariables effectiveAssignmentOldVariables = getEffectiveSetOfOldVariables(assignmentToInputVariables.getSetOfVariables());
		if (effectiveAssignmentOldVariables != assignmentToInputVariables.getSetOfVariables()) {
			return makeAssignmentOnEffectiveVariables(assignmentToInputVariables, effectiveAssignmentOldVariables);
		}
		else {
			return assignmentToInputVariables;
		}
	}

	private Assignment makeAssignmentOnEffectiveVariables(Assignment assignment, SetOfVariables effectiveAssignmentVariables) {
		List<Value> values = mapIntoList(assignment.getSetOfVariables().getVariables(), assignment::get);
		Assignment effectiveAssignmentOnNewVariables = new DefaultAssignment(effectiveAssignmentVariables, values);
		return effectiveAssignmentOnNewVariables;
	}

	///////////////////////
	
	private ManyToManyRelation<Variable, Variable> newToOldVariables;
	// 'A's are new variables, 'B's are old variables
	// Even though we are using "many-to-many", each 'A' has a single corresponding 'B'.
	
	private static ManyToManyRelation<Variable, Variable> makeNewToOldRelation(Variable[] oldAndNewVariablePairs, Function baseFunction) {
		myAssert(oldAndNewVariablePairs.length % 2 == 0, () -> VariablesMaskingFunction.class + " requires an even number of variables to be giving, corresponding to new and old variable pairs in sequence");
		ManyToManyRelation<Variable, Variable> newToOldVariables = new DefaultManyToManyRelation<>();
		for (int i = 0; i != oldAndNewVariablePairs.length; i = i + 2) {
			collectPair(oldAndNewVariablePairs, i, newToOldVariables, baseFunction);
		}

		checkOneToOne(newToOldVariables);
		
		return newToOldVariables;
	}

	private static void collectPair(Variable[] newAndOldVariablePairs, int i, ManyToManyRelation<Variable, Variable> newToOldVariables, Function baseFunction) {
		Variable oldVariable = newAndOldVariablePairs[i];
		Variable newVariable = newAndOldVariablePairs[i + 1];
		myAssert( ! baseFunction.getSetOfInputVariables().getVariables().contains(newVariable), () -> VariablesMaskingFunction.class + " requires new variables to not be variables of the given base function, but " + newVariable + " is one of the variables " + baseFunction.getSetOfInputVariables().getVariables() + " of the base function " + baseFunction);
		myAssert(   baseFunction.getSetOfInputVariables().getVariables().contains(oldVariable), () -> VariablesMaskingFunction.class + " requires old variables to be variables of the given base function, but " + oldVariable + " is not one of the variables " + baseFunction.getSetOfInputVariables().getVariables() + " of the base function " + baseFunction);
		newToOldVariables.add(newVariable, oldVariable);
	}

	private static void checkOneToOne(ManyToManyRelation<Variable, Variable> newToOldVariables) {
		Variable faultyA = getFirst(newToOldVariables.getAs(), a -> newToOldVariables.getBsOfA(a).size() != 1);
		myAssert(faultyA == null, () -> VariablesMaskingFunction.class + " requires a one-to-one relationship between new and old variables, but found " + faultyA + " with correspondents " + newToOldVariables.getBsOfA(faultyA));
		Variable faultyB = getFirst(newToOldVariables.getBs(), b -> newToOldVariables.getAsOfB(b).size() != 1);
		myAssert(faultyA == null, () -> VariablesMaskingFunction.class + " requires a one-to-one relationship between new and old variables, but found " + faultyB + " with correspondents " + newToOldVariables.getAsOfB(faultyB));
	}

	private boolean isOldVariable(Variable v) {
		return newToOldVariables.containsB(v);
	}

	private boolean isNewVariable(Variable v) {
		return newToOldVariables.containsA(v);
	}

	private Variable getNewVariable(Variable oldVariable) {
		return getFirst(newToOldVariables.getAsOfB(oldVariable));
	}

	private Variable getOldVariable(Variable newVariable) {
		return getFirst(newToOldVariables.getBsOfA(newVariable));
	}
	
	private Collection<? extends Variable> getOldVariables() {
		return newToOldVariables.getBs();
	}

}
