package org.jurr.behringer.x32.osc.xremoteproxy.endpoints.x32;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class X32Endpoint extends AbstractX32Endpoint
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// This class is responsible for receiving commands sent by the X32. It is also responsible for sending /xremote calls every 8 seconds.

	private static final byte[] XREMOTE_CMD = "/xremote".getBytes();
	private static final int X32_PORT = 10023;

	private final InetAddress x32Address;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public X32Endpoint(final InetAddress x32Address) throws SocketException
	{
		super(new DatagramSocket());

		this.x32Address = x32Address;
	}

	@Override
	public void run()
	{
		scheduler.scheduleAtFixedRate(new X32RemoteSender(), 1, 8, TimeUnit.SECONDS);
		LOGGER.debug("Endpoint started on random port {}, listening to X32 on {}:{}.", getDatagramSocket().getLocalPort(), x32Address.getCanonicalHostName(), X32_PORT);

		super.run();
	}

	@Override
	public void signalStop()
	{
		LOGGER.debug("Endpoint stopping.");
		scheduler.shutdown();
		super.signalStop();
	}

	@Override
	public void waitUntilStopped() throws InterruptedException
	{
		scheduler.awaitTermination(1, TimeUnit.HOURS);
		super.waitUntilStopped();
		LOGGER.debug("Endpoint stopped.");
	}

	@Override
	public void waitUntilStopped(final long millis) throws InterruptedException
	{
		scheduler.awaitTermination(millis, TimeUnit.MILLISECONDS);
		super.waitUntilStopped(millis);
		LOGGER.debug("Endpoint stopped.");
	}

	@Override
	public void waitUntilStopped(final long millis, final int nanos) throws InterruptedException
	{
		scheduler.awaitTermination(millis * 1000 + nanos, TimeUnit.NANOSECONDS);
		super.waitUntilStopped(millis, nanos);
		LOGGER.debug("Endpoint stopped.");
	}

	// We reuse the UDP socket here, so we receive the UDP messages from the X32 on the same port.
	class X32RemoteSender implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				final var dp = new DatagramPacket(XREMOTE_CMD, XREMOTE_CMD.length, x32Address, X32_PORT);
				getDatagramSocket().send(dp);

				LOGGER.debug("Sent /xremote to {}.", x32Address.getCanonicalHostName());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
