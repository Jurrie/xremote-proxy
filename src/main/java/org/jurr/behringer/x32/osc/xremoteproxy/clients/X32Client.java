package org.jurr.behringer.x32.osc.xremoteproxy.clients;

import java.lang.invoke.MethodHandles;
import java.net.InetAddress;

import org.jurr.behringer.x32.osc.xremoteproxy.commands.AbstractQLCPlusOSCCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class X32Client extends AbstractClient<AbstractQLCPlusOSCCommand>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// This class is resposible for sending commands to the X32.

	public X32Client(final InetAddress x32Address)
	{
	}

	@Override
	public void send(final AbstractQLCPlusOSCCommand command)
	{
		LOGGER.debug("Unrecognized message type {}.", command.getClass().getCanonicalName());
	}
}
