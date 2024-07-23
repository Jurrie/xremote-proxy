package org.slf4j.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class LoggerFactory implements ILoggerFactory
{
	private Map<String, Logger> loggerMap = new HashMap<>();

	/*
	 * (non-Javadoc)
	 *
	 * @see org.slf4j.ILoggerFactory#getLogger(java.lang.String)
	 */
	@Override
	public synchronized Logger getLogger(final String name)
	{
		Logger ulogger = null;
		// protect against concurrent access of loggerMap
		synchronized (this)
		{
			ulogger = loggerMap.get(name);
			if (ulogger == null)
			{
				ulogger = createNewLogger(name);
				loggerMap.put(name, ulogger);
			}
		}
		return ulogger;
	}

	private Logger createNewLogger(final String name)
	{
		return new ConsoleLogger(name);
	}
}