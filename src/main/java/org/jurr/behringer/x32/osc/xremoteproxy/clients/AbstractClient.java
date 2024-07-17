package org.jurr.behringer.x32.osc.xremoteproxy.clients;

import java.io.IOException;

import org.jurr.behringer.x32.osc.xremoteproxy.Bus;
import org.jurr.behringer.x32.osc.xremoteproxy.BusManaged;
import org.jurr.behringer.x32.osc.xremoteproxy.commands.AbstractOSCCommand;

public abstract class AbstractClient<T extends AbstractOSCCommand> implements BusManaged
{
	private Bus bus;

	@Override
	public void start(final Bus bus)
	{
		this.bus = bus;
	}

	protected Bus getBus()
	{
		return bus;
	}

	@Override
	public void signalStop()
	{
	}

	@Override
	public void waitUntilStopped() throws InterruptedException
	{
	}

	@Override
	public void waitUntilStopped(long millis) throws InterruptedException
	{
	}

	@Override
	public void waitUntilStopped(long millis, int nanos) throws InterruptedException
	{
	}

	public abstract void send(T message) throws IOException;
}
