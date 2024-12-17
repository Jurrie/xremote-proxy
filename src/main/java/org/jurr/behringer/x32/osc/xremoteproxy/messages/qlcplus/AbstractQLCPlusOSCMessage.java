package org.jurr.behringer.x32.osc.xremoteproxy.messages.qlcplus;

import java.net.SocketAddress;

import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;

public abstract class AbstractQLCPlusOSCMessage extends AbstractOSCMessage
{
	protected AbstractQLCPlusOSCMessage(final SocketAddress source)
	{
		super(source);
	}
}
