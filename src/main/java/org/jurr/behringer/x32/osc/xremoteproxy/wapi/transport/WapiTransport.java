package org.jurr.behringer.x32.osc.xremoteproxy.wapi.transport;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jurr.behringer.x32.osc.xremoteproxy.transports.ParseException;
import org.jurr.behringer.x32.osc.xremoteproxy.transports.Transport;
import org.jurr.java.wapi.WapiEvent;
import org.jurr.java.wapi.WapiException;
import org.jurr.java.wapi.WapiSession;
import org.jurr.java.wapi.WapiToken;
import org.jurr.java.wapi.client.WapiClient;
import org.jurr.java.wapi.client.transport.TcpWapiTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WapiTransport extends Transport
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final WapiClient client;
	private final ScheduledExecutorService retries = Executors.newSingleThreadScheduledExecutor();

	public WapiTransport(final SocketAddress socketAddress)
	{
		final var session = new WapiSession(socketAddress);
		final var transport = new TcpWapiTransport();

		client = new WapiClient(session, transport, false);
		client.getTokenRegistry().register(new WapiToken("$ctl.user.U1.1.bu.3"));
		client.getTokenRegistry().register(new WapiToken("$ctl.user.U1.2.bu.3"));
		client.getTokenRegistry().register(new WapiToken("$ctl.user.U1.3.bu.3"));
		client.getTokenRegistry().register(new WapiToken("$ctl.user.U1.4.bu.3"));
		client.getTokenRegistry().register(new WapiToken("$ctl.user.U2.1.bu.3"));
		client.getTokenRegistry().register(new WapiToken("$ctl.user.U2.2.bu.3"));
		client.getTokenRegistry().register(new WapiToken("$ctl.user.U2.3.bu.3"));
		client.getTokenRegistry().register(new WapiToken("$ctl.user.U2.4.bu.3"));
		client.getTokenRegistry().register(new WapiToken("$ctl.user.U3.1.bu.3"));
		client.getTokenRegistry().register(new WapiToken("$ctl.user.U3.2.bu.3"));
		client.getTokenRegistry().register(new WapiToken("$ctl.user.U3.3.bu.3"));
		client.getTokenRegistry().register(new WapiToken("$ctl.user.U3.4.bu.3"));
		client.getTokenRegistry().register(new WapiToken("$ctl.user.U4.1.bu.3"));
		client.getTokenRegistry().register(new WapiToken("$ctl.user.U4.2.bu.3"));
		client.getTokenRegistry().register(new WapiToken("$ctl.user.U4.3.bu.3"));
		client.getTokenRegistry().register(new WapiToken("$ctl.user.U4.4.bu.3"));

		// Reconnecting is up to the application; retry on a thread of its own, not in the listener
		client.addConnectionListener(cause -> {
			LOGGER.info("WING connection lost - will try to connect again: {}", cause.getMessage());
			retries.schedule(this::reconnect, 1, TimeUnit.SECONDS);
		});

		retries.schedule(this::reconnect, 0, TimeUnit.SECONDS);
	}

	private void reconnect()
	{
		try
		{
			client.wOpen();
			LOGGER.info("Connected to {}", client.getSession().socketAddress());
			synchronized (client)
			{
				client.notifyAll();
			}
		}
		catch (final WapiException e)
		{
			LOGGER.info("Connect failed: {}", e.getMessage());
			retries.schedule(this::reconnect, 2, TimeUnit.SECONDS);
		}
	}

	public WapiClient getClient()
	{
		return client;
	}

	@Override
	public WapiEventAndSource receive() throws IOException, ParseException, InterruptedException
	{
		synchronized (client)
		{
			while (!client.isConnected())
			{
				LOGGER.debug("Waiting for WING connection to be established before receiving message...");
				client.wait();
				LOGGER.debug("WING connection established; receiving messages again.");
			}
		}

		final var events = client.wGetParsedEvents(1);
		return new WapiEventAndSource(events.get(0), client.getSession().socketAddress());
	}

	@Override
	public void close() throws IOException
	{
		retries.close();
		client.close();
	}

	@Override
	public boolean isBlocking()
	{
		return true;
	}

	public void send(final WapiEvent wapiEvent, final SocketAddress source)
	{
		if (sameAddress(source).test(client.getSession().socketAddress()))
		{
			LOGGER.debug("Not sending to {}, as it is the same as the source address {}", client.getSession().socketAddress(), source);
			return;
		}

		if (!client.isConnected())
		{
			LOGGER.error("Cannot send WAPI event to {} because the client is not connected!", client.getSession().socketAddress());
			return;
		}

		switch (wapiEvent.type())
		{
		case F32:
			client.wSetTokenFloat(wapiEvent.token(), wapiEvent.value().getFloatValue());
			break;
		case I32:
			client.wSetTokenInt(wapiEvent.token(), wapiEvent.value().getIntValue());
			break;
		case S32:
			client.wSetTokenString(wapiEvent.token(), wapiEvent.value().getStringValue());
			break;
		default:
			throw new IllegalArgumentException("Unsupported WAPI event type: " + wapiEvent.type());
		}
	}
}
