package org.jurr.behringer.x32.osc.xremoteproxy.wapi.transport;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.SocketAddress;

import org.jurr.behringer.x32.osc.xremoteproxy.transports.ParseException;
import org.jurr.behringer.x32.osc.xremoteproxy.transports.Transport;
import org.jurr.java.wapi.WapiEvent;
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

	public WapiTransport(final SocketAddress socketAddress)
	{
		final var session = new WapiSession(socketAddress);
		final var transport = new TcpWapiTransport();

		client = new WapiClient(session, transport);
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

		// client.addEventListener(event -> LOGGER.info("Transition event: {} (hash: {}, type: {}) = {}", event.token().name(), event.token().hash(), event.type(), event.value()));
		LOGGER.info("Connected to {}:{}", socketAddress);
	}

	public WapiClient getClient()
	{
		return client;
	}

	@Override
	public WapiEventAndSource receive() throws IOException, ParseException
	{
		final var events = client.wGetParsedEvents(1);
		return new WapiEventAndSource(events.get(0), client.getSession().socketAddress());
	}

	@Override
	public void close() throws IOException
	{
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
