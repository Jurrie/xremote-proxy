package org.jurr.behringer.x32.osc.xremoteproxy.messages;

import java.net.SocketAddress;

public abstract class AbstractMessage
{
	private final SocketAddress source;

	protected AbstractMessage(final SocketAddress source)
	{
		this.source = source;
	}

	public SocketAddress getSource()
	{
		return source;
	}
}
