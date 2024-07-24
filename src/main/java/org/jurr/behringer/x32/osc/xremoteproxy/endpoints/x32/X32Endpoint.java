package org.jurr.behringer.x32.osc.xremoteproxy.endpoints.x32;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCParseException;
import com.illposed.osc.OSCSerializeException;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.AbstractEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.MultiClientUDPTransport;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.x32.AbstractX32OSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.x32.X32OSCMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class X32Endpoint extends AbstractEndpoint<AbstractX32OSCMessage>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static final int X32_PORT = 10023;
	private static final OSCMessage XREMOTE_OSC_MESSAGE = new OSCMessage("/xremote");

	private final List<InetSocketAddress> x32Addresses;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	/**
	 * This class is responsible for receiving commands sent by the X32. It is also responsible for sending /xremote calls every 8 seconds.
	 *
	 * We will only send /xremote to real X32 devices, not to the fake X32 clients.
	 *
	 * @param local our local address we listen on for messages
	 * @param x32Addresses the real X32 devices (the ones we need to send /xremote to)
	 * @param fakeX32Addresses the fake X32 clients (the ones that do accept X32 OSC commands, but do not need /xremote)
	 * @throws IOException
	 */
	public X32Endpoint(final InetSocketAddress local, final List<InetSocketAddress> x32Addresses, final List<InetSocketAddress> fakeX32Addresses) throws IOException
	{
		super(new MultiClientUDPTransport(local, Stream.concat(x32Addresses.stream(), fakeX32Addresses.stream()).toList(), new X32OSCSerializerAndParserBuilder()));
		this.x32Addresses = x32Addresses;
	}

	@Override
	public String getName()
	{
		return "X32 endpoint";
	}

	@Override
	public void run()
	{
		scheduler.scheduleAtFixedRate(new X32RemoteSender(), 1, 8, TimeUnit.SECONDS);

		while (isRunning())
		{
			try
			{
				final OSCPacket received = getTransport().receive();

				final AbstractOSCMessage message = X32OSCMessageFactory.fromData(this, received);

				getBus().messageReceived(this, message);
			}
			catch (IOException e)
			{
				if (isRunning())
				{
					LOGGER.error("Error receiving data from X32.", e);
				}
			}
			catch (OSCParseException e)
			{
				if (isRunning())
				{
					LOGGER.error("Error parsing OSC data from X32.", e);
				}
			}
		}
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
			for (final InetSocketAddress x32Address : x32Addresses)
			{
				try
				{
					getTransport().send(XREMOTE_OSC_MESSAGE);
					LOGGER.debug("Sent /xremote to {}.", x32Address.getAddress().getCanonicalHostName());
				}
				catch (IOException | OSCSerializeException e)
				{
					LOGGER.error("Error sending /xremote to X32 at " + x32Address.getAddress().getCanonicalHostName(), e);
				}
			}
		}
	}
}
