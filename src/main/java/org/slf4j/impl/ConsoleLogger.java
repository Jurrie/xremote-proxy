package org.slf4j.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;
import org.jurr.behringer.x32.osc.xremoteproxy.Settings;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.LegacyAbstractLogger;
import org.slf4j.helpers.MessageFormatter;

public class ConsoleLogger extends LegacyAbstractLogger
{
	private static final long serialVersionUID = 1L;

	private static DateTimeFormatter dateTimeFormatter;

	public ConsoleLogger(final String name)
	{
		this.name = name;
	}

	@Override
	protected String getFullyQualifiedCallerName()
	{
		// ???
		return name;
	}

	@Override
	protected void handleNormalizedLoggingCall(Level level, Marker marker, String format, Object[] argArray, Throwable t)
	{
		final String message = argArray == null ? format : MessageFormatter.arrayFormat(format, argArray).getMessage();
		switch (level)
		{
		case TRACE:
			AnsiConsole.out().println(Ansi.ansi().fg(Color.WHITE) + nowAsString() + " " + "TRACE" + ": " + message + Ansi.ansi().reset());
			break;
		case DEBUG:
			AnsiConsole.out().println(Ansi.ansi().fg(Color.WHITE) + nowAsString() + " " + "DEBUG" + ": " + message + Ansi.ansi().reset());
			break;
		case INFO:
			AnsiConsole.out().println(nowAsString() + " " + Ansi.ansi().bold() + Ansi.ansi().fgBright(Color.WHITE) + "INFO " + Ansi.ansi().boldOff() + ": " + message + Ansi.ansi().reset());
			break;
		case WARN:
			AnsiConsole.out().println(nowAsString() + " " + Ansi.ansi().bold() + Ansi.ansi().fgBright(Color.YELLOW) + "WARN " + Ansi.ansi().boldOff() + ": " + message + Ansi.ansi().reset());
			break;
		case ERROR:
			AnsiConsole.out().println(nowAsString() + " " + Ansi.ansi().bold() + Ansi.ansi().fgBright(Color.RED) + "ERROR" + Ansi.ansi().boldOff() + ": " + message + Ansi.ansi().reset());
			break;
		}
		if (t != null)
		{
			t.printStackTrace(System.out);
		}
	}

	@Override
	public boolean isTraceEnabled()
	{
		return Settings.INSTANCE.isTraceOutput();
	}

	@Override
	public boolean isDebugEnabled()
	{
		if (name.equals("com.illposed.osc.argument.handler.Activator"))
		{
			// This class keeps spamming us with a message "java.lang.ClassNotFoundException: com.illposed.osc.argument.handler.AwtColorArgumentHandler".
			// Shut up that message here.
			return false;
		}
		return Settings.INSTANCE.isVerboseOutput();
	}

	@Override
	public boolean isInfoEnabled()
	{
		return true;
	}

	@Override
	public boolean isWarnEnabled()
	{
		return true;
	}

	@Override
	public boolean isErrorEnabled()
	{
		return true;
	}

	protected static String nowAsString()
	{
		if (dateTimeFormatter == null)
		{
			dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSSS");
		}
		return dateTimeFormatter.format(LocalDateTime.now());
	}
}