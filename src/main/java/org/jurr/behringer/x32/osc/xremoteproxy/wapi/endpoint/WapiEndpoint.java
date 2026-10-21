package org.jurr.behringer.x32.osc.xremoteproxy.wapi.endpoint;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.SocketAddress;

import org.jurr.behringer.x32.osc.xremoteproxy.Bus;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoint.AbstractEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.transports.ParseException;
import org.jurr.behringer.x32.osc.xremoteproxy.wapi.messages.AbstractWapiMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.wapi.messages.WapiMessageFactory;
import org.jurr.behringer.x32.osc.xremoteproxy.wapi.transport.WapiEventAndSource;
import org.jurr.behringer.x32.osc.xremoteproxy.wapi.transport.WapiTransport;
import org.jurr.java.wapi.WapiSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WapiEndpoint extends AbstractEndpoint<AbstractWapiMessage, WapiTransport>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static final int WAPI_PORT = WapiSession.DEFAULT_PORT;

	public WapiEndpoint(final SocketAddress socketAddress)
	{
		super(new WapiTransport(socketAddress));
	}

	@Override
	public void start(final Bus bus)
	{
		super.start(bus);

		LOGGER.info("{} connected to {}", getName(), getTransport().getClient().getSession().socketAddress());
	}

	@Override
	public void run()
	{
		while (isRunning())
		{
			try
			{
				final WapiEventAndSource received = getTransport().receive();

				final AbstractWapiMessage message = WapiMessageFactory.fromData(this, received.getWrappedWapiEvent(), received.getSource());

				getBus().messageReceived(this, message);
			}
			catch (IOException e)
			{
				if (isRunning())
				{
					LOGGER.error("Error receiving data from WAPI.", e);
				}
			}
			catch (ParseException e)
			{
				if (isRunning())
				{
					LOGGER.error("Error parsing data from WAPI.", e);
				}
			}
			catch (Exception e)
			{
				if (isRunning())
				{
					LOGGER.error("Unknown error in WAPI endpoint.", e);
				}
			}
		}
	}

	@Override
	public void send(final AbstractWapiMessage message) throws IOException
	{
		LOGGER.debug("Sending message to {}: {}", getName(), message);
		getTransport().send(message.toWapiEvent(), message.getSource());
	}
}
