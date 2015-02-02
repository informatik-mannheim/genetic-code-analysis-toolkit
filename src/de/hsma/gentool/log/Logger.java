package de.hsma.gentool.log;

public interface Logger {
	public void log(String format, Object... arguments);
	public void log(String message, Throwable throwable);
}