package org.jurr.behringer.x32.osc.xremoteproxy.messages;

import com.illposed.osc.OSCMessage;

public class CorruptOSCMessageException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	private final OSCMessage oscMessage;

	public CorruptOSCMessageException(final String message, final OSCMessage oscMessage)
	{
		super(message);
		this.oscMessage = oscMessage;
	}

	public OSCMessage getOSCMessage()
	{
		return oscMessage;
	}
}
