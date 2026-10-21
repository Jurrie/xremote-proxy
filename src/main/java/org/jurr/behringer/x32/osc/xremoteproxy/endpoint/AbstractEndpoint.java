package org.jurr.behringer.x32.osc.xremoteproxy.endpoint;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import org.jurr.behringer.x32.osc.xremoteproxy.Bus;
import org.jurr.behringer.x32.osc.xremoteproxy.BusManaged;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.transports.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEndpoint<T extends AbstractMessage, U extends Transport> implements Runnable, BusManaged
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final U transport;

	private Thread thread;
	private Bus bus;
	private boolean running;

	protected AbstractEndpoint(final U transport)
	{
		this.transport = transport;
	}

	@Override
	public void start(final Bus bus)
	{
		this.bus = bus;

		running = true;

		thread = new Thread(this);
		thread.start();
	}

	public String getName()
	{
		return this.getClass().getSimpleName();
	}

	protected U getTransport()
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

	public abstract void send(T message) throws IOException;
}
