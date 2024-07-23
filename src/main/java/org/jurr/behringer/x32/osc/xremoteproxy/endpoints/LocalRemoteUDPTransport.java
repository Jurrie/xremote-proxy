package org.jurr.behringer.x32.osc.xremoteproxy.endpoints;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import com.illposed.osc.LibraryInfo;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCParseException;
import com.illposed.osc.OSCSerializeException;
import com.illposed.osc.OSCSerializerAndParserBuilder;
import com.illposed.osc.transport.Transport;
import com.illposed.osc.transport.channel.OSCDatagramChannel;
import com.illposed.osc.transport.udp.UDPTransport;

/**
 * This class just "extends" {@link UDPTransport} to expose the local and remote {@link SocketAddress}es.
 * It actually copies it because {@link UDPTransport} contains a bug: https://github.com/hoijui/JavaOSC/pull/73/
 */
public class LocalRemoteUDPTransport implements Transport
{
	/**
	 * Buffers were 1500 bytes in size, but were increased to 1536, as this
	 * is a common MTU, and then increased to 65507, as this is the maximum
	 * incoming datagram data size.
	 */
	public static final int BUFFER_SIZE = 65507;
	private final ByteBuffer recvBuffer = ByteBuffer.allocate(BUFFER_SIZE);
	private final ByteBuffer sendBuffer = ByteBuffer.allocate(BUFFER_SIZE);

	private final InetSocketAddress local;
	private final InetSocketAddress remote;
	private final DatagramChannel channel;
	private final OSCDatagramChannel oscChannel;

	public LocalRemoteUDPTransport(final InetSocketAddress local, final InetSocketAddress remote) throws IOException
	{
		this(local, remote, new OSCSerializerAndParserBuilder());
	}

	public LocalRemoteUDPTransport(final InetSocketAddress local, final InetSocketAddress remote, final OSCSerializerAndParserBuilder serializerAndParserBuilder) throws IOException
	{
		this.local = local;
		this.remote = remote;
		final DatagramChannel tmpChannel;
		if (LibraryInfo.hasStandardProtocolFamily())
		{
			final Class<?> localClass = local.getAddress().getClass();
			final Class<?> remoteClass = remote.getAddress().getClass();

			if (!localClass.equals(remoteClass))
			{
				throw new IllegalArgumentException("local and remote addresses are not of the same family" + " (IP v4 vs v6)");
			}
			if (local.getAddress() instanceof Inet4Address)
			{
				tmpChannel = DatagramChannel.open(StandardProtocolFamily.INET);
			}
			else if (local.getAddress() instanceof Inet6Address)
			{
				tmpChannel = DatagramChannel.open(StandardProtocolFamily.INET6);
			}
			else
			{
				throw new IllegalArgumentException("Unknown address type: " + local.getAddress().getClass().getCanonicalName());
			}
		}
		else
		{
			tmpChannel = DatagramChannel.open();
		}
		channel = tmpChannel;
		if (LibraryInfo.hasStandardProtocolFamily())
		{
			channel.setOption(StandardSocketOptions.SO_SNDBUF, BUFFER_SIZE);
			// NOTE So far, we never saw an issue with the receive-buffer size,
			// thus we leave it at its default.
			channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			channel.setOption(StandardSocketOptions.SO_BROADCAST, true);
		}
		else
		{
			channel.socket().setSendBufferSize(BUFFER_SIZE);
			// NOTE So far, we never saw an issue with the receive-buffer size,
			// thus we leave it at its default.
			channel.socket().setReuseAddress(true);
			channel.socket().setBroadcast(true);
		}
		channel.socket().bind(local);
		oscChannel = new OSCDatagramChannel(channel, serializerAndParserBuilder);
	}

	public InetSocketAddress getLocal()
	{
		return local;
	}

	public InetSocketAddress getRemote()
	{
		return remote;
	}

	@Override
	public void connect() throws IOException
	{
		if (remote == null)
		{
			throw new IllegalStateException("Can not connect a socket without a remote address specified");
		}
		channel.connect(remote);
	}

	@Override
	public void disconnect() throws IOException
	{
		channel.disconnect();
	}

	@Override
	public boolean isConnected()
	{
		return channel.isConnected();
	}

	/**
	 * Close the socket and free-up resources.
	 * It is recommended that clients call this when they are done with the port.
	 *
	 * @throws IOException If an I/O error occurs on the channel
	 */
	@Override
	public void close() throws IOException
	{
		channel.close();
	}

	@Override
	public void send(final OSCPacket packet) throws IOException, OSCSerializeException
	{
		oscChannel.send(sendBuffer, packet, remote);
	}

	@Override
	public OSCPacket receive() throws IOException, OSCParseException
	{
		return oscChannel.read(recvBuffer);
	}

	@Override
	public boolean isBlocking()
	{
		return channel.isBlocking();
	}

	@Override
	public String toString()
	{
		return String.format("%s: local=%s, remote=%s", getClass().getSimpleName(), local, remote);
	}
}
