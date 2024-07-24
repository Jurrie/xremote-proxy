package org.jurr.behringer.x32.osc.xremoteproxy.routers;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import org.jurr.behringer.x32.osc.xremoteproxy.Bus;
import org.jurr.behringer.x32.osc.xremoteproxy.BusManaged;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.AbstractEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRouter implements BusManaged
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private Bus bus;

	public enum ReceiveResult
	{
		DID_NOT_HANDLE, HANDLED_STOP_ROUTING, HANDLED_CONTINUE_ROUTING
	}

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

	public abstract ReceiveResult onMessageReceived(AbstractEndpoint<?> source, AbstractOSCMessage message);

	protected <T extends AbstractOSCMessage> void send(final AbstractEndpoint<T> endpoint, final T command)
	{
		try
		{
			endpoint.send(command);
		}
		catch (IOException e)
		{
			LOGGER.error("Error sending command to endpoint.", e);
		}
	}
}
