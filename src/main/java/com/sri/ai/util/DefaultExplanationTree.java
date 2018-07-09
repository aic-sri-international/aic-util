package com.sri.ai.util;

import static com.sri.ai.util.Util.println;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import joptsimple.internal.Strings;

public class DefaultExplanationTree implements ExplanationTree {

	public static final ExplanationTree PLACEHOLDER = new DefaultExplanationTree(
	"This is the header of an explanation consisting of three steps (this version should not have fractions and hard proc. attachments).",
	"This is the footer of an explanation consisting of three steps.",
	new DefaultExplanationTree("This is the header of first step, which has two sub-steps and no footer.",
			new DefaultExplanationTree("This is the first sub-step, which has no sub-sub-steps."),
			new DefaultExplanationTree("This is the second sub-step, which has no sub-sub-steps.")),
	new DefaultExplanationTree("This is header of the second step, which has no sub-steps and no footer."),
	new DefaultExplanationTree("This is the header of third step, which has one sub-step.",
			"This is the footer of first step, which has one sub-step.",
			new DefaultExplanationTree("This is the only sub-step, which has no sub-sub-steps.")));

	private static final int DEFAULT_INDENTATION = 4;

	private String header;
	private String footer;
	private List<? extends ExplanationTree> subExplanations;
	private int indentation = DEFAULT_INDENTATION;

	public DefaultExplanationTree(String header, ExplanationTree... subExplanations) {
		this(header, "", Arrays.asList(subExplanations));
	}
	
	public DefaultExplanationTree(String header, List<? extends ExplanationTree> subExplanations) {
		this(header, "", subExplanations);
	}
	
	public DefaultExplanationTree(String header, String footer, ExplanationTree... subExplanations) {
		this(header, footer, Arrays.asList(subExplanations));
	}
	
	public DefaultExplanationTree(String header, String footer, List<? extends ExplanationTree> subExplanations) {
		super();
		this.header = header;
		this.footer = footer;
		this.subExplanations = subExplanations;
	}

	@Override
	public String getHeader() {
		return header;
	}

	@Override
	public List<? extends ExplanationTree> getSubExplanations() {
		return Collections.unmodifiableList(subExplanations);
	}
	
	@Override
	public String getFooter() {
		return footer;
	}

	@Override
	public int getIndentation() {
		return indentation;
	}

	@Override
	public void setIndentation(int indentation) {
		this.indentation = indentation;
	}

	@Override
	public String toString() {
		return toString(this, "1", 0);
	}

	public static String toString(ExplanationTree explanation, String prefix, int level) {
		StringBuilder builder = new StringBuilder();
		appendLineIfNotEmpty(explanation, builder, prefix, level, explanation.getHeader());
		int index = 1;
		for (ExplanationTree subExplanation : explanation.getSubExplanations()) {
			builder.append(toString(subExplanation, prefix + "." + index, level + 1));
			index++;
		}
		appendLineIfNotEmpty(explanation, builder, prefix, level, explanation.getFooter());
		return builder.toString();
	}

	private static void appendLineIfNotEmpty(ExplanationTree explanation, StringBuilder builder, String prefix, int level, String line) {
		if (line.length() != 0) {
			appendLine(explanation, builder, prefix, level, line);
		}
	}

	private static StringBuilder appendLine(ExplanationTree explanation, StringBuilder builder, String prefix, int level, String line) {
		StringBuilder result = builder.append(makeLine(explanation, prefix, level, line));
		return result;
	}

	private static String makeLine(ExplanationTree explanation, String prefix, int level, String line) {
		String result = Strings.repeat(' ', level*explanation.getIndentation()) + prefix + " " + line + "\n";
		return result;
	}
	
	public static void main(String[] args) {
		println(DefaultExplanationTree.PLACEHOLDER);
	}
}
