package org.jurr.behringer.x32.osc.xremoteproxy.endpoints;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.illposed.osc.OSCSerializerAndParserBuilder;
import com.illposed.osc.transport.udp.UDPTransport;

/**
 * This class just extends {@link UDPTransport} to expose the local and remote {@link SocketAddress}es.
 */
public class LocalRemoteUDPTransport extends UDPTransport
{
	private final InetSocketAddress local;
	private final InetSocketAddress remote;

	public LocalRemoteUDPTransport(final InetSocketAddress local, final InetSocketAddress remote) throws IOException
	{
		super(local, remote);
		this.local = local;
		this.remote = remote;
	}

	public LocalRemoteUDPTransport(final InetSocketAddress local, final InetSocketAddress remote, final OSCSerializerAndParserBuilder serializerAndParserBuilder) throws IOException
	{
		super(local, remote, serializerAndParserBuilder);
		this.local = local;
		this.remote = remote;
	}

	public InetSocketAddress getLocal()
	{
		return local;
	}

	public InetSocketAddress getRemote()
	{
		return remote;
	}
}
