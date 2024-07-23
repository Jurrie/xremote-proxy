package org.jurr.behringer.x32.osc.xremoteproxy.endpoints;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import com.illposed.osc.OSCSerializeException;
import com.illposed.osc.transport.Transport;
import org.jurr.behringer.x32.osc.xremoteproxy.Bus;
import org.jurr.behringer.x32.osc.xremoteproxy.BusManaged;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEndpoint<T extends AbstractOSCMessage> implements Runnable, BusManaged
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final Transport transport;

	private Thread thread;
	private Bus bus;
	private boolean running;

	protected AbstractEndpoint(final Transport transport)
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
			switch (transport)
			{
			case MultiClientUDPTransport mcUDPTransport -> {
				final String remotes = mcUDPTransport.getRemotes().stream().map(r -> r.getAddress().getCanonicalHostName() + ":" + r.getPort()).reduce((a, b) -> a + ", " + b).map(r -> r + " and ").orElse("");
				LOGGER.debug("Endpoint {} started listening on port {} (sending to {}all clients that send to us first)", getName(), mcUDPTransport.getLocal().getPort(), remotes);
			}
			default -> LOGGER.debug("Endpoint {} started listening", getName());
			}
		}
	}

	public String getName()
	{
		return this.getClass().getSimpleName();
	}

	protected Transport getTransport()
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
		LOGGER.debug("Endpoint {} stopping.", getName());
		running = false;
		transport.close();
		thread.interrupt();
	}

	@Override
	public void waitUntilStopped() throws InterruptedException
	{
		thread.join();
		LOGGER.debug("Endpoint {} stopped.", getName());
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
		LOGGER.debug("Endpoint {} stopped.", getName());
	}

	public void send(T message) throws IOException
	{
		LOGGER.debug("Sending message to {}: {}", getName(), message);

		try
		{
			transport.send(message.toOSCPacket());
		}
		catch (OSCSerializeException e)
		{
			throw new IOException(e);
		}
	}
}
