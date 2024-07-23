package org.jurr.behringer.x32.osc.xremoteproxy.endpoints;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.illposed.osc.BufferBytesReceiver;
import com.illposed.osc.LibraryInfo;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCParseException;
import com.illposed.osc.OSCParser;
import com.illposed.osc.OSCSerializeException;
import com.illposed.osc.OSCSerializer;
import com.illposed.osc.OSCSerializerAndParserBuilder;
import com.illposed.osc.transport.Transport;
import com.illposed.osc.transport.channel.OSCDatagramChannel;
import com.illposed.osc.transport.udp.UDPTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a substitute for {@link UDPTransport} that allows for multiple clients.
 * Whenever a client sends a message to us, we will remember the client's address.
 * Whenever a {@link #send(com.illposed.osc.OSCPacket)} is performed, we will send the message to all remembered clients.
 */
public class MultiClientUDPTransport implements Transport
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * Buffers were 1500 bytes in size, but were increased to 1536, as this
	 * is a common MTU, and then increased to 65507, as this is the maximum
	 * incoming datagram data size.
	 */
	public static final int BUFFER_SIZE = 65507;
	private final ByteBuffer recvBuffer = ByteBuffer.allocate(BUFFER_SIZE);
	private final ByteBuffer sendBuffer = ByteBuffer.allocate(BUFFER_SIZE);

	private final List<InetSocketAddress> remotes;
	private final InetSocketAddress local;
	private final DatagramChannel channel;
	private final MultiClientOSCDatagramChannel oscChannel;

	public MultiClientUDPTransport(final InetSocketAddress local) throws IOException
	{
		this(local, Collections.emptyList(), new OSCSerializerAndParserBuilder());
	}

	public MultiClientUDPTransport(final InetSocketAddress local, final List<InetSocketAddress> remotes) throws IOException
	{
		this(local, remotes, new OSCSerializerAndParserBuilder());
	}

	public MultiClientUDPTransport(final InetSocketAddress local, final OSCSerializerAndParserBuilder serializerAndParserBuilder) throws IOException
	{
		this(local, Collections.emptyList(), serializerAndParserBuilder);
	}

	public MultiClientUDPTransport(final InetSocketAddress local, final List<InetSocketAddress> remotes, final OSCSerializerAndParserBuilder serializerAndParserBuilder) throws IOException
	{
		this.local = local;
		this.remotes = Collections.synchronizedList(new ArrayList<>(remotes));

		final DatagramChannel tmpChannel;
		if (LibraryInfo.hasStandardProtocolFamily())
		{
			final InetSocketAddress localIsa = local;
			localIsa.getAddress().getClass();

			if (localIsa.getAddress() instanceof Inet4Address)
			{
				tmpChannel = DatagramChannel.open(StandardProtocolFamily.INET);
			}
			else if (localIsa.getAddress() instanceof Inet6Address)
			{
				tmpChannel = DatagramChannel.open(StandardProtocolFamily.INET6);
			}
			else
			{
				throw new IllegalArgumentException("Unknown address type: " + localIsa.getAddress().getClass().getCanonicalName());
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
		oscChannel = new MultiClientOSCDatagramChannel(channel, serializerAndParserBuilder);
	}

	public InetSocketAddress getLocal()
	{
		return local;
	}

	/**
	 * Get the list of remote clients that we will {@link #send(OSCPacket)} to.
	 *
	 * The list is modifiable and thread safe.
	 *
	 * @return The list of remote clients
	 */
	public List<InetSocketAddress> getRemotes()
	{
		return remotes;
	}

	@Override
	public void send(final OSCPacket packet) throws IOException, OSCSerializeException
	{
		oscChannel.send(sendBuffer, packet, remotes);
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
	public void connect()
	{
		// By nature, this class will not call channel.connect().
	}

	@Override
	public void disconnect()
	{
		// By nature, this class will not call channel.connect(). So we do not need to call channel.disconnect() here.
	}

	@Override
	public boolean isConnected()
	{
		// By nature, this class will not call channel.connect(). So we always return false here.
		return false;
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

	private class MultiClientOSCDatagramChannel extends OSCDatagramChannel
	{
		private final DatagramChannel underlyingChannel;
		private final OSCParser parser;
		private final OSCSerializerAndParserBuilder serializerBuilder;

		private MultiClientOSCDatagramChannel(final DatagramChannel underlyingChannel, final OSCSerializerAndParserBuilder serializerAndParserBuilder)
		{
			super(underlyingChannel, serializerAndParserBuilder);
			this.underlyingChannel = underlyingChannel;
			parser = serializerAndParserBuilder.buildParser();
			serializerBuilder = serializerAndParserBuilder;
		}

		@Override
		public OSCPacket read(final ByteBuffer recvBuffer) throws IOException, OSCParseException
		{
			boolean completed = false;
			OSCPacket oscPacket;
			try
			{
				begin();

				recvBuffer.clear();
				final SocketAddress clientAddress = underlyingChannel.receive(recvBuffer);
				if (!remotes.contains(clientAddress))
				{
					LOGGER.debug("New client detected on {}:{}. Client {} has sent us its first message.", local.getAddress().getCanonicalHostName(), local.getPort(), clientAddress);
					remotes.add((InetSocketAddress) clientAddress);
				}

				recvBuffer.flip();
				if (recvBuffer.limit() == 0)
				{
					throw new OSCParseException("Received a packet without any data", recvBuffer);
				}
				else
				{
					oscPacket = parser.convert(recvBuffer);
					completed = true;
				}
				recvBuffer.flip();
			}
			finally
			{
				end(completed);
			}

			return oscPacket;
		}

		public void send(final ByteBuffer sendBuffer, final OSCPacket packet, final List<InetSocketAddress> remoteAddresses) throws IOException, OSCSerializeException
		{
			boolean completed = false;
			try
			{
				begin();

				final OSCSerializer serializer = serializerBuilder.buildSerializer(new BufferBytesReceiver(sendBuffer));
				sendBuffer.rewind();
				serializer.write(packet);
				sendBuffer.flip();
				for (final SocketAddress remoteAddress : remoteAddresses)
				{
					sendBuffer.rewind();
					underlyingChannel.send(sendBuffer, remoteAddress);
				}
				sendBuffer.flip();
				completed = true;
			}
			finally
			{
				end(completed);
			}
		}
	}
}
