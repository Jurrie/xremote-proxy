package org.jurr.behringer.x32.osc.xremoteproxy.messages;

import java.net.SocketAddress;

import com.illposed.osc.OSCPacket;

public abstract class AbstractOSCMessage
{
	private final SocketAddress source;

	protected AbstractOSCMessage(final SocketAddress source)
	{
		this.source = source;
	}

	public SocketAddress getSource()
	{
		return source;
	}

	public abstract OSCPacket toOSCPacket();
}
