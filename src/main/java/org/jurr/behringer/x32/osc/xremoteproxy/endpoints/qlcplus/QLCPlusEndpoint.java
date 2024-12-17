package org.jurr.behringer.x32.osc.xremoteproxy.endpoints.qlcplus;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.util.List;

import com.illposed.osc.OSCParseException;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.AbstractEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.MultiClientUDPTransport;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.OSCPacketAndSource;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.qlcplus.AbstractQLCPlusOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.qlcplus.QLCPlusOSCMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QLCPlusEndpoint extends AbstractEndpoint<AbstractQLCPlusOSCMessage>
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
			catch (OSCParseException e)
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
