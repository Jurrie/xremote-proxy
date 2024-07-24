package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;

public class SLF4JServiceProvider implements org.slf4j.spi.SLF4JServiceProvider
{
	/**
	 * Declare the version of the SLF4J API this implementation is compiled against.
	 * The value of this field is modified with each major release.
	 */
	// to avoid constant folding by the compiler, this field must *not* be final
	public static String REQUESTED_API_VERSION = "2.0.99"; // !final

	private LoggerFactory defaultLoggerContext = new LoggerFactory();
	private final IMarkerFactory markerFactory = new BasicMarkerFactory();
	private final MDCAdapter mdcAdapter = new NOPMDCAdapter();

	@Override
	public void initialize()
	{
		// already initialized
	}

	@Override
	public ILoggerFactory getLoggerFactory()
	{
		return defaultLoggerContext;
	}

	@Override
	public IMarkerFactory getMarkerFactory()
	{
		return markerFactory;
	}

	@Override
	public MDCAdapter getMDCAdapter()
	{
		return mdcAdapter;
	}

	@Override
	public String getRequestedApiVersion()
	{
		return REQUESTED_API_VERSION;
	}
}
