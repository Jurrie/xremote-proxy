package org.slf4j.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;
import org.jurr.behringer.x32.osc.xremoteproxy.Settings;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

public class ConsoleLogger extends MarkerIgnoringBase
{
	private static final long serialVersionUID = 1L;

	private static DateTimeFormatter dateTimeFormatter;

	public ConsoleLogger(final String name)
	{
		this.name = name;
	}

	@Override
	public boolean isTraceEnabled()
	{
		return Settings.INSTANCE.isTraceOutput();
	}

	@Override
	public void trace(final String msg)
	{
		if (!isTraceEnabled())
		{
			return;
		}

		trace(msg, null, null);
	}

	@Override
	public void trace(final String format, final Object arg)
	{
		if (!isTraceEnabled())
		{
			return;
		}

		trace(format, new Object[] { arg }, null);
	}

	@Override
	public void trace(final String format, final Object arg1, final Object arg2)
	{
		if (!isTraceEnabled())
		{
			return;
		}

		trace(format, new Object[] { arg1, arg2 }, null);
	}

	@Override
	public void trace(final String format, final Object[] argArray)
	{
		if (!isTraceEnabled())
		{
			return;
		}

		trace(format, argArray, null);
	}

	@Override
	public void trace(final String msg, final Throwable t)
	{
		if (!isTraceEnabled())
		{
			return;
		}

		trace(msg, null, t);
	}

	protected void trace(final String format, final Object[] argArray, final Throwable t)
	{
		final String message = argArray == null ? format : MessageFormatter.arrayFormat(format, argArray).getMessage();
		AnsiConsole.out().println(Ansi.ansi().fg(Color.WHITE) + nowAsString() + " " + "TRACE" + ": " + message + Ansi.ansi().reset());
		if (t != null)
		{
			t.printStackTrace(System.out);
		}
	}

	@Override
	public boolean isDebugEnabled()
	{
		return Settings.INSTANCE.isVerboseOutput();
	}

	@Override
	public void debug(final String msg)
	{
		if (!isDebugEnabled())
		{
			return;
		}

		debug(msg, null, null);
	}

	@Override
	public void debug(final String format, final Object arg)
	{
		if (!isDebugEnabled())
		{
			return;
		}

		debug(format, new Object[] { arg }, null);
	}

	@Override
	public void debug(final String format, final Object arg1, final Object arg2)
	{
		if (!isDebugEnabled())
		{
			return;
		}

		debug(format, new Object[] { arg1, arg2 }, null);
	}

	@Override
	public void debug(final String format, final Object[] argArray)
	{
		if (!isDebugEnabled())
		{
			return;
		}

		debug(format, argArray, null);
	}

	@Override
	public void debug(final String msg, final Throwable t)
	{
		if (!isDebugEnabled())
		{
			return;
		}

		debug(msg, null, t);
	}

	protected void debug(final String format, final Object[] argArray, final Throwable t)
	{
		final String message = argArray == null ? format : MessageFormatter.arrayFormat(format, argArray).getMessage();
		AnsiConsole.out().println(Ansi.ansi().fg(Color.WHITE) + nowAsString() + " " + "DEBUG" + ": " + message + Ansi.ansi().reset());
		if (t != null)
		{
			t.printStackTrace(System.out);
		}
	}

	@Override
	public boolean isInfoEnabled()
	{
		return true;
	}

	@Override
	public void info(final String msg)
	{
		if (!isInfoEnabled())
		{
			return;
		}

		info(msg, null, null);
	}

	@Override
	public void info(final String format, final Object arg)
	{
		if (!isInfoEnabled())
		{
			return;
		}

		info(format, new Object[] { arg }, null);
	}

	@Override
	public void info(final String format, final Object arg1, final Object arg2)
	{
		if (!isInfoEnabled())
		{
			return;
		}

		info(format, new Object[] { arg1, arg2 }, null);
	}

	@Override
	public void info(final String format, final Object[] argArray)
	{
		if (!isInfoEnabled())
		{
			return;
		}

		info(format, argArray, null);
	}

	@Override
	public void info(final String msg, final Throwable t)
	{
		if (!isInfoEnabled())
		{
			return;
		}

		info(msg, null, t);
	}

	protected void info(final String format, final Object[] argArray, final Throwable t)
	{
		final String message = argArray == null ? format : MessageFormatter.arrayFormat(format, argArray).getMessage();
		AnsiConsole.out().println(nowAsString() + " " + Ansi.ansi().bold() + Ansi.ansi().fgBright(Color.WHITE) + "INFO " + Ansi.ansi().boldOff() + ": " + message + Ansi.ansi().reset());
		if (t != null)
		{
			t.printStackTrace(System.out);
		}
	}

	@Override
	public boolean isWarnEnabled()
	{
		return true;
	}

	@Override
	public void warn(final String msg)
	{
		if (!isWarnEnabled())
		{
			return;
		}

		warn(msg, null, null);
	}

	@Override
	public void warn(final String format, final Object arg)
	{
		if (!isWarnEnabled())
		{
			return;
		}

		warn(format, new Object[] { arg }, null);
	}

	@Override
	public void warn(final String format, final Object arg1, final Object arg2)
	{
		if (!isWarnEnabled())
		{
			return;
		}

		warn(format, new Object[] { arg1, arg2 }, null);
	}

	@Override
	public void warn(final String format, final Object[] argArray)
	{
		if (!isWarnEnabled())
		{
			return;
		}

		warn(format, argArray, null);
	}

	@Override
	public void warn(final String msg, final Throwable t)
	{
		if (!isWarnEnabled())
		{
			return;
		}

		warn(msg, null, t);
	}

	protected void warn(final String format, final Object[] argArray, final Throwable t)
	{
		final String message = argArray == null ? format : MessageFormatter.arrayFormat(format, argArray).getMessage();
		AnsiConsole.out().println(nowAsString() + " " + Ansi.ansi().bold() + Ansi.ansi().fgBright(Color.YELLOW) + "WARN " + Ansi.ansi().boldOff() + ": " + message + Ansi.ansi().reset());
		if (t != null)
		{
			t.printStackTrace(System.out);
		}
	}

	@Override
	public boolean isErrorEnabled()
	{
		return true;
	}

	@Override
	public void error(final String msg)
	{
		if (!isErrorEnabled())
		{
			return;
		}

		error(msg, null, null);
	}

	@Override
	public void error(final String format, final Object arg)
	{
		if (!isErrorEnabled())
		{
			return;
		}

		error(format, new Object[] { arg }, null);
	}

	@Override
	public void error(final String format, final Object arg1, final Object arg2)
	{
		if (!isErrorEnabled())
		{
			return;
		}

		error(format, new Object[] { arg1, arg2 }, null);
	}

	@Override
	public void error(final String format, final Object[] argArray)
	{
		if (!isErrorEnabled())
		{
			return;
		}

		error(format, argArray, null);
	}

	@Override
	public void error(final String msg, final Throwable t)
	{
		if (!isErrorEnabled())
		{
			return;
		}

		error(msg, null, t);
	}

	protected void error(final String format, final Object[] argArray, final Throwable t)
	{
		final String message = argArray == null ? format : MessageFormatter.arrayFormat(format, argArray).getMessage();
		AnsiConsole.out().println(nowAsString() + " " + Ansi.ansi().bold() + Ansi.ansi().fgBright(Color.RED) + "ERROR" + Ansi.ansi().boldOff() + ": " + message + Ansi.ansi().reset());
		if (t != null)
		{
			t.printStackTrace(System.out);
		}
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