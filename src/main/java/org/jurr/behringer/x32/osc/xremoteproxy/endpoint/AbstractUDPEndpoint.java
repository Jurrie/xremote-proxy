package org.jurr.behringer.x32.osc.xremoteproxy.endpoint;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.net.UnixDomainSocketAddress;

import com.illposed.osc.OSCSerializeException;
import org.jurr.behringer.x32.osc.xremoteproxy.Bus;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.transports.MultiClientUDPTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUDPEndpoint<T extends AbstractOSCMessage> extends AbstractEndpoint<T, MultiClientUDPTransport>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	protected AbstractUDPEndpoint(final MultiClientUDPTransport transport)
	{
		super(transport);
	}

	@Override
	public final void start(final Bus bus)
	{
		super.start(bus);

		if (LOGGER.isInfoEnabled())
		{
			final String remotes = getTransport().getRemotes().stream().map(r -> {
				switch (r)
				{
				case InetSocketAddress isa:
					return isa.getAddress().getCanonicalHostName() + ":" + isa.getPort();
				case UnixDomainSocketAddress udsa:
					return udsa.getPath().toAbsolutePath().toString();
				default:
					return "Unknown socket";
				}
			}).reduce((a, b) -> a + ", " + b).map(r -> r + " and ").orElse("");
			LOGGER.info("{} started listening on port {} (sending to {}all clients that send us a message first)", getName(), getTransport().getLocal().getPort(), remotes);
		}
	}

	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}

	@Override
	public void send(final T message) throws IOException
	{
		LOGGER.debug("Sending message to {}: {}", getName(), message);

		try
		{
			getTransport().send(message.toOSCPacket(), message.getSource());
		}
		catch (OSCSerializeException e)
		{
			throw new IOException(e);
		}
	}
}
