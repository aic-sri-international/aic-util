package com.sri.ai.util.planning.core;

import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.map;
import static com.sri.ai.util.Util.putInListValue;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.sri.ai.util.Util;
import com.sri.ai.util.planning.api.Goal;
import com.sri.ai.util.planning.api.IndexedSetOfRules;
import com.sri.ai.util.planning.api.Rule;

public class DefaultIndexedRules<R extends Rule<G>, G extends Goal> implements IndexedSetOfRules<R, G> {
	
	private Map<G, List<R>> fromGoalToListOfRules;
	
	public DefaultIndexedRules(Iterable<? extends R> rules) {
		fromGoalToListOfRules = map();
		for (R rule : rules) {
			compileRule(rule);
		}
	}

	private void compileRule(R rule) {
		for (G consequent: rule.getConsequents()) {
			putInListValue(fromGoalToListOfRules, consequent, rule);
		}
	}

	@Override
	public Collection<? extends G> getGoals() {
		return fromGoalToListOfRules.keySet();
	}

	@Override
	public List<R> getRulesFor(G goal) {
		List<R> result = Util.getOrUseDefault(fromGoalToListOfRules, goal, list());
		return result;
	}

	@Override
	public String toString() {
		return join("\n", fromGoalToListOfRules);
	}

}
