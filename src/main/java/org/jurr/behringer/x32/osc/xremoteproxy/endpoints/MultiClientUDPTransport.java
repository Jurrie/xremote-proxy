package org.jurr.behringer.x32.osc.xremoteproxy.endpoints;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

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

	private final List<SocketAddress> remotes;
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
	public List<SocketAddress> getRemotes()
	{
		return remotes;
	}

	/**
	 * @deprecated Better use {@link #send(OSCPacket, SocketAddress)} with the second argument <code>null</code>.
	 */
	@Deprecated
	@Override
	public void send(final OSCPacket packet) throws IOException, OSCSerializeException
	{
		LOGGER.warn("Sending packet to all known sources, even the source that orignally sent us the message!");
		oscChannel.send(sendBuffer, packet, remotes);
	}

	/**
	 * Sends to all known remotes, except the one given as <code>source</code>.
	 *
	 * @param packet The OSC packet to send
	 * @param source The address to exclude from sending (can be NULL in which case we send to all remotes known)
	 * @throws IOException
	 * @throws OSCSerializeException
	 */
	public void send(final OSCPacket packet, final SocketAddress source) throws IOException, OSCSerializeException
	{
		oscChannel.send(sendBuffer, packet, remotes.stream().filter(sameAddress(source).negate()).toList());
	}

	private Predicate<? super SocketAddress> sameAddress(final SocketAddress source)
	{
		return switch (source)
		{
		case InetSocketAddress isa -> sameAddress(isa);
		case UnixDomainSocketAddress udsa -> destination -> destination.equals(udsa);
		default -> destination -> {
			LOGGER.warn("Unknown socket address type {}", source.getClass().getCanonicalName());
			return destination.equals(source);
		};
		};
	}

	private Predicate<? super SocketAddress> sameAddress(final InetSocketAddress source)
	{
		return destination -> {
			if (destination instanceof InetSocketAddress destinationISA)
			{
				final InetAddress sourceAddress = source.getAddress();
				final String sourceHostname = source.getHostName();
				final InetAddress destinationAddress = destinationISA.getAddress();
				final String destinationHostname = destinationISA.getHostName();

				boolean result = false;
				if (sourceAddress != null && destinationAddress != null)
				{
					result = sourceAddress.equals(destinationAddress);
				}
				else if (sourceHostname != null && destinationHostname != null)
				{
					result = sourceHostname.equals(destinationHostname);
				}

				if (LOGGER.isDebugEnabled())
				{
					if (result)
					{
						LOGGER.debug("InetSocketAddresses (excluding ports) are the same: source {} ({}) = dest {} ({})", sourceAddress, sourceHostname, destinationAddress, destinationHostname);
					}
					else
					{
						LOGGER.debug("InetSocketAddresses (excluding ports) are NOT the same: source {} ({}) != dest {} ({})", sourceAddress, sourceHostname, destinationAddress, destinationHostname);
					}
				}

				return result;
			}
			else
			{
				return false;
			}
		};
	}

	@Override
	public OSCPacketAndSource receive() throws IOException, OSCParseException
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
		public OSCPacketAndSource read(final ByteBuffer recvBuffer) throws IOException, OSCParseException
		{
			boolean completed = false;
			final OSCPacket oscPacket;
			final SocketAddress clientAddress;
			try
			{
				begin();

				recvBuffer.clear();
				clientAddress = underlyingChannel.receive(recvBuffer);

				if (remotes.stream().noneMatch(sameAddress(clientAddress)))
				{
					LOGGER.info("New client detected on {}:{}. Client {} has sent us its first message.", local.getAddress().getCanonicalHostName(), local.getPort(), clientAddress);
					remotes.add(clientAddress);
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
			catch (Exception e)
			{
				throw new OSCParseException("Unknown error receiving packet", recvBuffer);
			}
			finally
			{
				end(completed);
			}

			return new OSCPacketAndSource(oscPacket, clientAddress);
		}

		public void send(final ByteBuffer sendBuffer, final OSCPacket packet, final List<SocketAddress> remoteAddresses) throws IOException, OSCSerializeException
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
					LOGGER.trace("Sending to {}", remoteAddress.toString());
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
