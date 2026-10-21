package org.jurr.behringer.x32.osc.xremoteproxy.transports;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnixDomainSocketAddress;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Transport
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * Receive a packet.
	 *
	 * @return the packet received
	 * @throws IOException if an I/O error occurs while trying to read from the
	 * @throws ParseException if the packet fails to parse
	 */
	public abstract Packet receive() throws IOException, ParseException;

	/**
	 * Close the socket and free-up resources.
	 * It is recommended that clients call this when they are done with the
	 * port.
	 *
	 * @throws IOException If an I/O error occurs
	 */
	public abstract void close() throws IOException;

	public abstract boolean isBlocking();

	public static Predicate<? super SocketAddress> sameAddress(final SocketAddress source)
	{
		return switch (source)
		{
		case null -> destination -> false;
		case InetSocketAddress isa -> sameAddress(isa);
		case UnixDomainSocketAddress udsa -> destination -> destination.equals(udsa);
		default -> destination -> {
			LOGGER.warn("Unknown socket address type {}", source.getClass().getCanonicalName());
			return destination.equals(source);
		};
		};
	}

	private static Predicate<? super SocketAddress> sameAddress(final InetSocketAddress source)
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

				return result;
			}
			else
			{
				return false;
			}
		};
	}
}
