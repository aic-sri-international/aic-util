package com.sri.ai.util.planning.core;

import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.map;
import static com.sri.ai.util.Util.putInListValue;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.sri.ai.util.planning.api.IndexedRules;
import com.sri.ai.util.Util;
import com.sri.ai.util.planning.api.Goal;
import com.sri.ai.util.planning.api.Rule;

public class DefaultIndexedRules implements IndexedRules {
	
	private Map<Goal, List<Rule>> fromGoalToListOfRules;
	
	public DefaultIndexedRules(Iterable<? extends Rule> rules) {
		fromGoalToListOfRules = map();
		for (Rule rule : rules) {
			compileRule(rule);
		}
	}

	private void compileRule(Rule rule) {
		for (Goal consequent: rule.getConsequents()) {
			putInListValue(fromGoalToListOfRules, consequent, rule);
		}
	}

	@Override
	public Collection<? extends Goal> getGoals() {
		return fromGoalToListOfRules.keySet();
	}

	@Override
	public List<Rule> getRulesFor(Goal goal) {
		List<Rule> result = Util.getOrUseDefault(fromGoalToListOfRules, goal, list());
		return result;
	}

	@Override
	public String toString() {
		return join("\n", fromGoalToListOfRules);
	}

}
