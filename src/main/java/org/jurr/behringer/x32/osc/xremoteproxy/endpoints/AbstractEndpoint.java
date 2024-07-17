package org.jurr.behringer.x32.osc.xremoteproxy.endpoints;

import org.jurr.behringer.x32.osc.xremoteproxy.Bus;
import org.jurr.behringer.x32.osc.xremoteproxy.BusManaged;

public abstract class AbstractEndpoint implements Runnable, BusManaged
{
	private Thread thread;
	private Bus bus;
	private boolean running;

	@Override
	public final void start(final Bus bus)
	{
		this.bus = bus;

		running = true;

		thread = new Thread(this);
		thread.start();
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
	public void signalStop()
	{
		running = false;
		thread.interrupt();
	}

	@Override
	public void waitUntilStopped() throws InterruptedException
	{
		thread.join();
	}

	@Override
	public void waitUntilStopped(final long millis) throws InterruptedException
	{
		thread.join(millis);
	}

	@Override
	public void waitUntilStopped(final long millis, final int nanos) throws InterruptedException
	{
		thread.join(millis, nanos);
	}
}
