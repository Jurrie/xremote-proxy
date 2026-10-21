package org.jurr.behringer.x32.osc.xremoteproxy.qlcplus.endpoint;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.util.List;

import org.jurr.behringer.x32.osc.xremoteproxy.endpoint.AbstractUDPEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.qlcplus.messages.AbstractQLCPlusOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.qlcplus.messages.QLCPlusOSCMessageFactory;
import org.jurr.behringer.x32.osc.xremoteproxy.transports.MultiClientUDPTransport;
import org.jurr.behringer.x32.osc.xremoteproxy.transports.OSCPacketAndSource;
import org.jurr.behringer.x32.osc.xremoteproxy.transports.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QLCPlusEndpoint extends AbstractUDPEndpoint<AbstractQLCPlusOSCMessage>
{
	// This class is responsible for receiving messages sent by QLC+ and converting them to X32 lingo.

	public static final int DEFAULT_SEND_PORT = 7700;
	public static final int DEFAULT_RECEIVE_PORT = 9000;

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public QLCPlusEndpoint(final InetSocketAddress local, final List<InetSocketAddress> remotes) throws IOException
	{
		super(new MultiClientUDPTransport(local, remotes));
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
				final OSCPacketAndSource received = getTransport().receive();

				final AbstractOSCMessage message = QLCPlusOSCMessageFactory.fromData(this, received.getWrappedPacket(), received.getSource());

				getBus().messageReceived(this, message);
			}
			catch (IOException e)
			{
				if (isRunning())
				{
					LOGGER.error("Error receiving data from QLC+.", e);
				}
			}
			catch (ParseException e)
			{
				if (isRunning())
				{
					LOGGER.error("Error parsing OSC data from QLC+.", e);
				}
			}
			catch (Exception e)
			{
				if (isRunning())
				{
					LOGGER.error("Unknown error in QLC+ endpoint.", e);
				}
			}
		}
	}
}
