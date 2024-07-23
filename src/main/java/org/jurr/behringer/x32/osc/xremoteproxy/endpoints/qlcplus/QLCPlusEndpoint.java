package org.jurr.behringer.x32.osc.xremoteproxy.endpoints.qlcplus;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.util.Collections;

import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCParseException;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.AbstractEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.MultiClientUDPTransport;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.qlcplus.AbstractQLCPlusOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.qlcplus.QLCPlusOSCMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QLCPlusEndpoint extends AbstractEndpoint<AbstractQLCPlusOSCMessage>
{
	// This class is responsible for receiving messages sent by QLC+ and converting them to X32 lingo.

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public QLCPlusEndpoint(final InetSocketAddress local, final InetSocketAddress remote) throws IOException
	{
		super(new MultiClientUDPTransport(local, Collections.singletonList(remote)));
	}

	@Override
	public String getName()
	{
		return "QLC+ endpoint";
	}

	@Override
	public void run()
	{
		while (isRunning())
		{
			try
			{
				final OSCPacket received = getTransport().receive();

				final AbstractOSCMessage message = QLCPlusOSCMessageFactory.fromData(this, received);

				getBus().messageReceived(this, message);
			}
			catch (IOException e)
			{
				if (isRunning())
				{
					LOGGER.error("Error receiving data from QLC+.", e);
				}
			}
			catch (OSCParseException e)
			{
				if (isRunning())
				{
					LOGGER.error("Error parsing OSC data from QLC+.", e);
				}
			}
		}
	}
}