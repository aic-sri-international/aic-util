package com.sri.ai.util.gnuplot;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.sri.ai.util.Util;

public class GnuplotPipe extends BufferedOutputStream {
	public GnuplotPipe(boolean persist) {
		super(getGnuplotOutputStream(persist));
	}

	private static OutputStream getGnuplotOutputStream(boolean persist) {
		String persistFlag = persist? " -persist" : " ";
		Process process = null;
		try {
			String executableName = System.getProperty("os.name").contains("Windows")? "pgnuplot.exe" : "gnuplot";
			process = Runtime.getRuntime().exec(executableName + persistFlag);
		} catch (IOException e) { Util.fatalError("Could not launch gnuplot", e); }
		return process.getOutputStream();
	}

	public void send(String command) throws IOException {
		write((command + "\n").getBytes());
		flush();
	}
}