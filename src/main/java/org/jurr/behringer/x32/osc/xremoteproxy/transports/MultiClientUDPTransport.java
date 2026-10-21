package org.jurr.behringer.x32.osc.xremoteproxy.transports;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.illposed.osc.BufferBytesReceiver;
import com.illposed.osc.LibraryInfo;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCParseException;
import com.illposed.osc.OSCParser;
import com.illposed.osc.OSCSerializeException;
import com.illposed.osc.OSCSerializer;
import com.illposed.osc.OSCSerializerAndParserBuilder;
import com.illposed.osc.transport.channel.OSCDatagramChannel;
import com.illposed.osc.transport.udp.UDPTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a substitute for {@link UDPTransport} that allows for multiple clients.
 * Whenever a client sends a message to us, we will remember the client's address.
 * Whenever a {@link #send(com.illposed.osc.OSCPacket)} is performed, we will send the message to all remembered clients.
 */
public class MultiClientUDPTransport extends Transport
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
		LOGGER.debug("Binding to {}", local);

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
	 * Sends to all known remotes, except the one given as <code>source</code>.
	 *
	 * @param packet The OSC packet to send
	 * @param source The address to exclude from sending (can be NULL in which case we send to all remotes known)
	 * @throws IOException
	 * @throws OSCSerializeException
	 */
	public void send(final OSCPacket packet, final SocketAddress source) throws IOException, OSCSerializeException
	{
		final List<SocketAddress> destinations = remotes.stream().filter(destination -> {
			if (sameAddress(source).test(destination))
			{
				LOGGER.debug("Not sending to {}, as it is the same as the source address {}", destination, source);
				return false;
			}
			else
			{
				return true;
			}
		}).toList();
		oscChannel.send(sendBuffer, packet, destinations);
	}

	@Override
	public OSCPacketAndSource receive() throws IOException, ParseException
	{
		OSCPacketAndSource oscPacketAndSource;
		try
		{
			oscPacketAndSource = oscChannel.read(recvBuffer);
		}
		catch (OSCParseException e)
		{
			LOGGER.error("Error parsing OSC packet.", e);
			throw new ParseException(e);
		}
		logReceivedOSCPacket(oscPacketAndSource);
		return oscPacketAndSource;
	}

	private static void logReceivedOSCPacket(final OSCPacketAndSource received)
	{
		if (LOGGER.isTraceEnabled())
		{
			if (received.getWrappedPacket() instanceof OSCBundle oscBundle)
			{
				LOGGER.trace("Received OSC bundle containing {} messages from {}:", oscBundle.getPackets().size(), received.getSource());
				for (final OSCPacket packet : oscBundle.getPackets())
				{
					if (packet instanceof OSCMessage message)
					{
						LOGGER.trace("  {}", oscPacketToString(message));
					}
					else
					{
						LOGGER.trace("  {}", packet);
					}
				}
			}
			else if (received.getWrappedPacket() instanceof OSCMessage message)
			{
				LOGGER.trace("Received OSC message from {}.", received.getSource());
				LOGGER.trace("  {}", oscPacketToString(message));
			}
			else
			{
				LOGGER.trace("Received unknown OSC packet {} from {}.", received.getWrappedPacket(), received.getSource());
			}
		}
	}

	private static String oscPacketToString(final OSCMessage message)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(message.getAddress());
		sb.append(", args: [");
		for (int i = 0; i < message.getArguments().size(); i++)
		{
			final Object arg = message.getArguments().get(i);

			if (i > 0)
			{
				sb.append(", ");
			}
			sb.append(arg);
		}
		return sb.append("]").toString();
	}

	@Override
	public boolean isBlocking()
	{
		return channel.isBlocking();
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
					LOGGER.trace("Sending to {}", remoteAddress);
					sendBuffer.rewind();
					try
					{
						underlyingChannel.send(sendBuffer, remoteAddress);
					}
					catch (SocketException e)
					{
						final String errorMessage = e.getMessage();
						if (errorMessage != null && errorMessage.contains("Network is unreachable"))
						{
							LOGGER.warn("Could not send to {} because that network was unreachable", remoteAddress);
						}
						else
						{
							throw e;
						}
					}
				}
				sendBuffer.clear();
				completed = true;
			}
			finally
			{
				end(completed);
			}
		}
	}
}
