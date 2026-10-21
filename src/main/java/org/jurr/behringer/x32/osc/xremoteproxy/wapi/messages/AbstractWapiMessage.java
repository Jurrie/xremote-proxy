package org.jurr.behringer.x32.osc.xremoteproxy.wapi.messages;

import java.net.SocketAddress;

import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractMessage;
import org.jurr.java.wapi.WapiEvent;

public abstract class AbstractWapiMessage extends AbstractMessage
{
	protected AbstractWapiMessage(final SocketAddress source)
	{
		super(source);
	}

	public abstract WapiEvent toWapiEvent();

}
