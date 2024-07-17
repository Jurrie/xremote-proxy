package org.jurr.behringer.x32.osc.xremoteproxy.clients;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.jurr.behringer.x32.osc.xremoteproxy.commands.AbstractQLCPlusOSCCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QLCPlusClient extends AbstractClient<AbstractQLCPlusOSCCommand>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final InetAddress qlcPlusAddress;
	private final int qlcPlusPort;
	private final DatagramSocket datagramSocket;

	public QLCPlusClient(final InetAddress qlcPlusAddress, final int qlcPlusPort) throws SocketException
	{
		this.qlcPlusAddress = qlcPlusAddress;
		this.qlcPlusPort = qlcPlusPort;

		datagramSocket = new DatagramSocket();
	}

	@Override
	public void send(final AbstractQLCPlusOSCCommand message) throws IOException
	{
		LOGGER.debug("Sending message to QLC+: {}", message);

		final var dp = new DatagramPacket("TEST".getBytes(), 4, qlcPlusAddress, qlcPlusPort);
		datagramSocket.send(dp);
	}

	@Override
	public void signalStop()
	{
		datagramSocket.close();
		super.signalStop();
	}
}
