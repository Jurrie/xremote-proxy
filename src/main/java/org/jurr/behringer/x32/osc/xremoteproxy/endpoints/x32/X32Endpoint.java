package org.jurr.behringer.x32.osc.xremoteproxy.endpoints.x32;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCSerializeException;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.MultiClientUDPTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class X32Endpoint extends AbstractX32Endpoint
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// This class is responsible for receiving commands sent by the X32. It is also responsible for sending /xremote calls every 8 seconds.

	private static final OSCMessage XREMOTE_OSC_MESSAGE = new OSCMessage("/xremote");
	private static final int X32_PORT = 10023;

	private final InetSocketAddress x32Address;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public X32Endpoint(final InetAddress x32Address) throws IOException
	{
		this(new InetSocketAddress(0), new InetSocketAddress(x32Address, X32_PORT));
	}

	private X32Endpoint(final InetSocketAddress local, final InetSocketAddress x32Address) throws IOException
	{
		super(new MultiClientUDPTransport(local, Collections.singletonList(x32Address), new X32OSCSerializerAndParserBuilder()));
		this.x32Address = x32Address;
	}

	@Override
	public void run()
	{
		scheduler.scheduleAtFixedRate(new X32RemoteSender(), 1, 8, TimeUnit.SECONDS);
		super.run();
	}

	@Override
	public void signalStop() throws IOException
	{
		scheduler.shutdown();
		super.signalStop();
	}

	@Override
	public void waitUntilStopped() throws InterruptedException
	{
		scheduler.awaitTermination(1, TimeUnit.HOURS);
		super.waitUntilStopped();
	}

	@Override
	public void waitUntilStopped(final long millis, final int nanos) throws InterruptedException
	{
		scheduler.awaitTermination(millis * 1000 + nanos, TimeUnit.NANOSECONDS);
		super.waitUntilStopped(millis, nanos);
	}

	// We reuse the UDP socket here, so we receive the UDP messages from the X32 on the same port.
	class X32RemoteSender implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				getTransport().send(XREMOTE_OSC_MESSAGE);

				LOGGER.debug("Sent /xremote to {}.", x32Address.getAddress().getCanonicalHostName());
			}
			catch (IOException | OSCSerializeException e)
			{
				e.printStackTrace();
			}
		}
	}
}
