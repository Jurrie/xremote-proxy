package org.jurr.behringer.x32.osc.xremoteproxy.wapi.messages;

import com.illposed.osc.OSCMessage;

public class CorruptWapiMessageException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	private final OSCMessage oscMessage;

	public CorruptWapiMessageException(final String message, final OSCMessage oscMessage)
	{
		super(message);
		this.oscMessage = oscMessage;
	}

	public OSCMessage getOSCMessage()
	{
		return oscMessage;
	}
}
