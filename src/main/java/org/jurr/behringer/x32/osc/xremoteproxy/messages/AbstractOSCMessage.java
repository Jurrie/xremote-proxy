package org.jurr.behringer.x32.osc.xremoteproxy.messages;

import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.AbstractEndpoint;

public abstract class AbstractOSCMessage
{
	private final AbstractEndpoint source;
	private final byte[] data;

	protected AbstractOSCMessage(final AbstractEndpoint source, final byte[] data)
	{
		this.source = source;
		this.data = data;
	}

	public AbstractEndpoint getSource()
	{
		return source;
	}

	public byte[] getData()
	{
		return data;
	}
}
