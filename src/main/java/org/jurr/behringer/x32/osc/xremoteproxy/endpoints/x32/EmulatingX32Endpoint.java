package org.jurr.behringer.x32.osc.xremoteproxy.endpoints.x32;

import java.lang.invoke.MethodHandles;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmulatingX32Endpoint extends AbstractX32Endpoint
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public EmulatingX32Endpoint(final int listenPort) throws SocketException
	{
		super(new DatagramSocket(listenPort));
	}

	@Override
	public void run()
	{
		LOGGER.debug("Endpoint started on port {}", getDatagramSocket().getLocalPort());
		super.run();
	}

	@Override
	public void signalStop()
	{
		LOGGER.debug("Endpoint stopping.");
		super.signalStop();
	}

	@Override
	public void waitUntilStopped() throws InterruptedException
	{
		super.waitUntilStopped();
		LOGGER.debug("Endpoint stopped.");
	}

	@Override
	public void waitUntilStopped(long millis) throws InterruptedException
	{
		super.waitUntilStopped(millis);
		LOGGER.debug("Endpoint stopped.");
	}

	@Override
	public void waitUntilStopped(long millis, int nanos) throws InterruptedException
	{
		super.waitUntilStopped(millis, nanos);
		LOGGER.debug("Endpoint stopped.");
	}
}
