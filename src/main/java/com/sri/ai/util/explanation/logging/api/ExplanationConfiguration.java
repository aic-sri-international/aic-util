package com.sri.ai.util.explanation.logging.api;

import com.sri.ai.util.Configuration;
import com.sri.ai.util.explanation.logging.core.handler.FileExplanationHandler;
import com.sri.ai.util.explanation.logging.core.handler.TabNestedFileExplanationHandler;

public class ExplanationConfiguration {

	/**
	 * Indicates whether explanation loggers are active or not by default;
	 * comes from property <code>explanation.active</code> or is <code>false</code> by default;
	 * 
	 */
	public static boolean WHETHER_EXPLANATION_LOGGERS_ARE_ACTIVE_BY_DEFAULT = Configuration.getBoolean("explanation.active", false);

	public static boolean setWhetherExplanationLoggersAreActiveByDefaultAndReturnOldValue(boolean newValue) {
		boolean oldValue = WHETHER_EXPLANATION_LOGGERS_ARE_ACTIVE_BY_DEFAULT;
		WHETHER_EXPLANATION_LOGGERS_ARE_ACTIVE_BY_DEFAULT = newValue;
		return oldValue;
	}
	
	/**
	 * String concatenated at beginning of line, once per nesting level;
	 * comes from property <code>explanation.nesting.block</code> or is <code>TAB</code> by default;
	 * 
	 */
	public static final String DEFAULT_NESTING_BLOCK = Configuration.getString("explanation.nesting.block", "\t");
	
	/**
	 * String concatenated after nesting blocks;
	 * comes from property <code>explanation.nesting.postfix</code> or is the empty string by default;
	 * 
	 */
	public static final String DEFAULT_NESTING_POSTFIX = Configuration.getString("explanation.nesting.postfix", "");
	
	/**
	 * Whether explanations include a timestamp;
	 * comes from property <code>explanation.include.timestamp</code> or is <code>false</code> by default;
	 * 
	 */
	public static final boolean DEFAULT_INCLUDE_TIMESTAMP = Configuration.getBoolean("explanation.include.timestamp", false);
	
	/**
	 * Whether nesting blocks' closing explanation includes the time it took to execute;
	 * comes from property <code>explanation.include.block.time</code> or is <code>true</code> by default;
	 * 
	 */
	public static final boolean DEFAULT_INCLUDE_BLOCK_TIME = Configuration.getBoolean("explanation.include.block.time", true);

	/**
	 * Whether record ids are included in their description;
	 * comes from property <code>explanation.include.record.id</code> or is <code>false</code> by default;
	 * 
	 */
	public static final boolean DEFAULT_INCLUDE_RECORD_ID = Configuration.getBoolean("explanation.include.record.id", false);

	/**
	 * The default class for file explanation handlers;
	 * comes from property <code>explanation.file.explanation.handler</code> or is <code>TabNestedFileExplanationHandler.class</code> by default;
	 * 
	 */
	public static final Class<? extends FileExplanationHandler> DEFAULT_FILE_EXPLANATION_HANDLER_CLASS = getFileExplanationHandlerClass();

	@SuppressWarnings("unchecked")
	private static Class<? extends FileExplanationHandler> getFileExplanationHandlerClass() {
		String classNameFromProperties = "";
		try {
			Class<? extends FileExplanationHandler> result;
			classNameFromProperties = Configuration.getString("explanation.file.explanation.handler", "");
			if (classNameFromProperties.isEmpty()) {
				result = TabNestedFileExplanationHandler.class;
			}
			else {
				result = (Class<? extends FileExplanationHandler>) ClassLoader.getSystemClassLoader().loadClass(classNameFromProperties);
			}
			return result;
		} catch (ClassNotFoundException e) {
			throw new Error("File explanation handler class " + classNameFromProperties + " not found", e);
		} catch (ClassCastException e) {
			throw new Error("File explanation handler class " + classNameFromProperties + " must extend " + FileExplanationHandler.class, e);
		}
	}
	
	public static boolean explanationFileHasBeenRequested() {
		String classNameFromProperties = Configuration.getString("explanation.file.explanation.handler", "");
		return !classNameFromProperties.isEmpty();
	}
}
