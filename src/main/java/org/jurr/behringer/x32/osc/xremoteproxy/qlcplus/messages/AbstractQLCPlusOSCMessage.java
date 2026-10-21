package org.jurr.behringer.x32.osc.xremoteproxy.qlcplus.messages;

import java.net.SocketAddress;

import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;

public abstract class AbstractQLCPlusOSCMessage extends AbstractOSCMessage
{
	protected AbstractQLCPlusOSCMessage(final SocketAddress source)
	{
		super(source);
	}

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();
}
