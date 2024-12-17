package org.jurr.behringer.x32.osc.xremoteproxy.endpoints;

import java.net.SocketAddress;

import com.illposed.osc.OSCPacket;

/**
 * A wrapper class for {@link OSCPacket} where we also store the source address from which we received the OSC packet.
 */
public class OSCPacketAndSource implements OSCPacket
{
	private static final long serialVersionUID = 1L;

	private final OSCPacket wrappedPacket;
	private final SocketAddress source;

	OSCPacketAndSource(final OSCPacket wrappedPacket, final SocketAddress source)
	{
		this.wrappedPacket = wrappedPacket;
		this.source = source;
	}

	public OSCPacket getWrappedPacket()
	{
		return wrappedPacket;
	}

	public SocketAddress getSource()
	{
		return source;
	}
}