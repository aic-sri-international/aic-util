package com.sri.ai.util;

import static com.sri.ai.util.Util.println;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import joptsimple.internal.Strings;

/**
 * An explanation tree consisting of a header message, a sequence of sub-explanations, and a footer message.
 * 
 * @author braz
 *
 */
public class ExplanationTree {

	private static final int DEFAULT_INDENTATION = 4;

	public static final ExplanationTree PLACEHOLDER =
			new ExplanationTree(
					"This is the header of an explanation consisting of three steps.",
					"This is the footer of an explanation consisting of three steps.",
					new ExplanationTree(
							"This is the header of first step, which has two sub-steps and no footer.",
							new ExplanationTree("This is the first sub-step, which has no sub-sub-steps."),
							new ExplanationTree("This is the second sub-step, which has no sub-sub-steps.")
							),
					new ExplanationTree("This is header of the second step, which has no sub-steps and no footer."),
					new ExplanationTree(
							"This is the header of first step, which has one sub-step.",
							"This is the footer of first step, which has one sub-step.",
							new ExplanationTree("This is the only sub-step, which has no sub-sub-steps.")
							)
					);
	
	private String header;
	private String footer;
	private List<? extends ExplanationTree> subExplanations;
	private int indentation = DEFAULT_INDENTATION;
	
	public ExplanationTree(String header, ExplanationTree... subExplanations) {
		this(header, "", Arrays.asList(subExplanations));
	}
	
	public ExplanationTree(String header, List<? extends ExplanationTree> subExplanations) {
		this(header, "", subExplanations);
	}
	
	public ExplanationTree(String header, String footer, ExplanationTree... subExplanations) {
		this(header, footer, Arrays.asList(subExplanations));
	}
	
	public ExplanationTree(String header, String footer, List<? extends ExplanationTree> subExplanations) {
		super();
		this.header = header;
		this.footer = footer;
		this.subExplanations = subExplanations;
	}

	public String getHeader() {
		return header;
	}

	public String getFooter() {
		return footer;
	}

	public List<? extends ExplanationTree> getSubExplanations() {
		return Collections.unmodifiableList(subExplanations);
	}
	
	public int getIndentation() {
		return indentation;
	}

	public void setIndentation(int indentation) {
		this.indentation = indentation;
	}

	@Override
	public String toString() {
		return toString("1", 0);
	}

	public String toString(String prefix, int level) {
		StringBuilder builder = new StringBuilder();
		appendLineIfNotEmpty(builder, prefix, level, getHeader());
		int index = 1;
		for (ExplanationTree subExplanation : getSubExplanations()) {
			builder.append(subExplanation.toString(prefix + "." + index, level + 1));
			index++;
		}
		appendLineIfNotEmpty(builder, prefix, level, getFooter());
		return builder.toString();
	}

	private void appendLineIfNotEmpty(StringBuilder builder, String prefix, int level, String line) {
		if (line.length() != 0) {
			appendLine(builder, prefix, level, line);
		}
	}

	private StringBuilder appendLine(StringBuilder builder, String prefix, int level, String line) {
		StringBuilder result = builder.append(makeLine(prefix, level, line));
		return result;
	}

	private String makeLine(String prefix, int level, String line) {
		String result = Strings.repeat(' ', level*getIndentation()) + prefix + " " + line + "\n";
		return result;
	}
	
	public static void main(String[] args) {
		println(PLACEHOLDER);
	}
}
