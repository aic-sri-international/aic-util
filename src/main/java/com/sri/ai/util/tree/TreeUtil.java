package com.sri.ai.util.tree;

import static com.sri.ai.util.Util.fill;
import static com.sri.ai.util.Util.getLast;
import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.listFrom;
import static com.sri.ai.util.Util.mapIntoList;

import java.util.List;

public class TreeUtil {
	
	/**
	 * Receives a tree of strings and add a given string to the very last leaf in it.
	 * @param stringTree
	 * @param string
	 * @return
	 */
	public static Tree<String> addAtTheVeryEnd(Tree<? extends String> stringTree, String string) {
		if (stringTree.getChildren().hasNext()) {
			List<Tree<? extends String>> children = listFrom(stringTree.getChildren());
			Tree<? extends String> newLast = addAtTheVeryEnd(getLast(children), string);
			children.set(children.size() - 1, newLast);
			return new DefaultTree<String>(stringTree.getInformation(), children);
		}
		else {
			return new DefaultTree<String>(stringTree.getInformation() + string);
		}
	}
	
	public static String indentedString(Tree<? extends String> stringTree, int level, int paddingPerLevel) {
		String padding = fill(level*paddingPerLevel, ' ');
		return
				padding + stringTree.getInformation()
				+ join("", mapIntoList(stringTree.getChildren(), c -> "\n" + indentedString(c, level + 1, paddingPerLevel)))
				;
	}

}
