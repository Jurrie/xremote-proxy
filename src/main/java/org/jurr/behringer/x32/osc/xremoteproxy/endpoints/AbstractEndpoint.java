package org.jurr.behringer.x32.osc.xremoteproxy.endpoints;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.net.UnixDomainSocketAddress;

import com.illposed.osc.OSCSerializeException;
import org.jurr.behringer.x32.osc.xremoteproxy.Bus;
import org.jurr.behringer.x32.osc.xremoteproxy.BusManaged;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEndpoint<T extends AbstractOSCMessage> implements Runnable, BusManaged
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final MultiClientUDPTransport transport;

	private Thread thread;
	private Bus bus;
	private boolean running;

	protected AbstractEndpoint(final MultiClientUDPTransport transport)
	{
		this.transport = transport;
	}

	@Override
	public final void start(final Bus bus)
	{
		this.bus = bus;

		running = true;

		thread = new Thread(this);
		thread.start();

		if (LOGGER.isDebugEnabled())
		{
			final String remotes = transport.getRemotes().stream().map(r -> {
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
			LOGGER.debug("{} started listening on port {} (sending to {}all clients that send us a message first)", getName(), transport.getLocal().getPort(), remotes);
		}
	}

	public String getName()
	{
		return this.getClass().getSimpleName();
	}

	protected MultiClientUDPTransport getTransport()
	{
		return transport;
	}

	protected Bus getBus()
	{
		return bus;
	}

	protected boolean isRunning()
	{
		return running;
	}

	@Override
	public void signalStop() throws IOException
	{
		LOGGER.debug("{} stopping.", getName());
		running = false;
		transport.close();
		thread.interrupt();
	}

	@Override
	public void waitUntilStopped() throws InterruptedException
	{
		thread.join();
		LOGGER.debug("{} stopped.", getName());
	}

	@Override
	public final void waitUntilStopped(final long millis) throws InterruptedException
	{
		this.waitUntilStopped(millis, 0);
	}

	@Override
	public void waitUntilStopped(final long millis, final int nanos) throws InterruptedException
	{
		thread.join(millis, nanos);
		LOGGER.debug("{} stopped.", getName());
	}

	public void send(T message) throws IOException
	{
		LOGGER.debug("Sending message to {}: {}", getName(), message);

		try
		{
			transport.send(message.toOSCPacket(), message.getSource());
		}
		catch (OSCSerializeException e)
		{
			throw new IOException(e);
		}
	}
}
