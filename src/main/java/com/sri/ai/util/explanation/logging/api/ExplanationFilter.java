package com.sri.ai.util.explanation.logging.api;

import java.util.function.Predicate;

@FunctionalInterface
public interface ExplanationFilter extends Predicate<ExplanationRecord> {

}
