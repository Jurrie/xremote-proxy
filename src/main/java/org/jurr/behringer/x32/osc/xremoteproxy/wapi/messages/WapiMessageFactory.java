package org.jurr.behringer.x32.osc.xremoteproxy.wapi.messages;

import java.net.SocketAddress;

import org.jurr.behringer.x32.osc.xremoteproxy.wapi.endpoint.RawWapiMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.wapi.endpoint.WapiEndpoint;
import org.jurr.java.wapi.WapiEvent;

public class WapiMessageFactory
{
	private WapiMessageFactory()
	{
	}

	public static AbstractWapiMessage fromData(final WapiEndpoint sourceEndpoint, final WapiEvent wapiEvent, final SocketAddress source)
	{
		AbstractWapiMessage message = null;

		message = ButtonChangeWapiMessage.fromWapiEvent(wapiEvent, source);
		if (message != null)
		{
			return message;
		}

		return new RawWapiMessage(sourceEndpoint, wapiEvent, source);
	}
}
