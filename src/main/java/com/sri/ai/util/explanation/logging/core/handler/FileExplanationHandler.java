package com.sri.ai.util.explanation.logging.core.handler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.sri.ai.util.base.NullaryFunction;

public class FileExplanationHandler extends AbstractWriterExplanationHandler implements AutoCloseable {

	private String filename;
	
	public FileExplanationHandler(String filename) throws IOException {
		super(writerMaker(filename)); // lazy so that inactive loggers do not touch the file
		this.filename = filename;
	}

	private static NullaryFunction<Writer> writerMaker(String filename) {
		return () ->  {
			try {
				return new BufferedWriter(new FileWriter(filename));
			} catch (IOException e) {
				throw new Error(e);
			}
		};
	}
	
	public String getFilename() {
		return filename;
	}

	@Override
	public String toString() {
		return "File explanation handler for " + filename;
	}

	@Override
	public void close() throws Exception {
		if (writerHasBeenCreated()) {
			getWriter().close();
		}
	}
}
