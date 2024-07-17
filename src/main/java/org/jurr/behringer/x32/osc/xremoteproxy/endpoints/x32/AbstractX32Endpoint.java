package org.jurr.behringer.x32.osc.xremoteproxy.endpoints.x32;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.AbstractEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.x32.AbstractX32OSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.x32.X32OSCMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractX32Endpoint extends AbstractEndpoint
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final DatagramSocket datagramSocket;

	protected AbstractX32Endpoint(final DatagramSocket datagramSocket)
	{
		this.datagramSocket = datagramSocket;
	}

	protected DatagramSocket getDatagramSocket()
	{
		return datagramSocket;
	}

	@Override
	public void run()
	{
		while (isRunning())
		{
			byte[] buffer = new byte[65536];
			final DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			try
			{
				datagramSocket.receive(reply);

				final AbstractX32OSCMessage message = X32OSCMessageFactory.fromData(this, reply.getData());

				getBus().messageReceived(message);
			}
			catch (IOException e)
			{
				if (isRunning())
				{
					LOGGER.error("Error receiving data from X32.", e);
				}
			}
		}
	}

	@Override
	public void signalStop()
	{
		datagramSocket.close();
		super.signalStop();
	}
}
