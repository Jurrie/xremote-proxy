package org.jurr.behringer.x32.osc.xremoteproxy.routers;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import org.jurr.behringer.x32.osc.xremoteproxy.Bus;
import org.jurr.behringer.x32.osc.xremoteproxy.BusManaged;
import org.jurr.behringer.x32.osc.xremoteproxy.clients.AbstractClient;
import org.jurr.behringer.x32.osc.xremoteproxy.commands.AbstractOSCCommand;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRouter implements BusManaged
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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

	public abstract void onMessageReceived(AbstractOSCMessage message);

	protected <T extends AbstractOSCCommand> void send(final AbstractClient<T> client, final T command)
	{
		try
		{
			client.send(command);
		}
		catch (IOException e)
		{
			LOGGER.error("Error send command to client.", e);
		}
	}
}
