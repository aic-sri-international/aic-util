package com.sri.ai.util;

import java.util.List;

/**
 * An explanation tree consisting of a header message, a sequence of sub-explanations, and a footer message.
 * 
 * @author braz
 *
 */
public interface ExplanationTree {

	String getHeader();

	List<? extends ExplanationTree> getSubExplanations();

	String getFooter();

	/** Gets indentation used by {@link #toString()}. */
	int getIndentation();

	/** Sets indentation used by {@link #toString()}. */
	void setIndentation(int indentation);

	/** Returns an indented representation of the explanation. */
	String toString();

}