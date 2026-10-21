package org.jurr.behringer.x32.osc.xremoteproxy.transports;

public class ParseException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ParseException(final String message)
	{
		super(message);
	}

	public ParseException(final Throwable cause)
	{
		super(cause);
	}

	public ParseException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
