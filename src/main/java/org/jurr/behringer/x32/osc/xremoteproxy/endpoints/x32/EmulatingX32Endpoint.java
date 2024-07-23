package org.jurr.behringer.x32.osc.xremoteproxy.endpoints.x32;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.MultiClientUDPTransport;

/**
 * This is an endpoint for clients that send X32 OSC messages, but who we do not want to send /xremote messages to.
 */
public class EmulatingX32Endpoint extends AbstractX32Endpoint
{
	public EmulatingX32Endpoint(final InetSocketAddress local) throws IOException
	{
		super(new MultiClientUDPTransport(local));
	}
}
