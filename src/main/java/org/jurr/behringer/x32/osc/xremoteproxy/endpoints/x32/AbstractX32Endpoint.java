package org.jurr.behringer.x32.osc.xremoteproxy.endpoints.x32;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCParseException;
import com.illposed.osc.transport.Transport;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.AbstractEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.x32.AbstractX32OSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.x32.X32OSCMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractX32Endpoint extends AbstractEndpoint<AbstractX32OSCMessage>
{
	protected AbstractX32Endpoint(final Transport transport)
	{
		super(transport);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public void run()
	{
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
}
