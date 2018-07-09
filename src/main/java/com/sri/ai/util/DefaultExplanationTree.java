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
	private List<? extends DefaultExplanationTree> subExplanations;
	private int indentation = DEFAULT_INDENTATION;

	public DefaultExplanationTree(String header, DefaultExplanationTree... subExplanations) {
		this(header, "", Arrays.asList(subExplanations));
	}
	
	public DefaultExplanationTree(String header, List<? extends DefaultExplanationTree> subExplanations) {
		this(header, "", subExplanations);
	}
	
	public DefaultExplanationTree(String header, String footer, DefaultExplanationTree... subExplanations) {
		this(header, footer, Arrays.asList(subExplanations));
	}
	
	public DefaultExplanationTree(String header, String footer, List<? extends DefaultExplanationTree> subExplanations) {
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
	public List<? extends DefaultExplanationTree> getSubExplanations() {
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
		return toString("1", 0);
	}

	public String toString(String prefix, int level) {
		StringBuilder builder = new StringBuilder();
		appendLineIfNotEmpty(builder, prefix, level, getHeader());
		int index = 1;
		for (DefaultExplanationTree subExplanation : getSubExplanations()) {
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
		println(DefaultExplanationTree.PLACEHOLDER);
	}
}
