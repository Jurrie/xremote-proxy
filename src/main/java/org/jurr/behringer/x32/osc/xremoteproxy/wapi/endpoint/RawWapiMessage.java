package org.jurr.behringer.x32.osc.xremoteproxy.wapi.endpoint;

import java.net.SocketAddress;

import org.jurr.behringer.x32.osc.xremoteproxy.wapi.messages.AbstractWapiMessage;
import org.jurr.java.wapi.WapiEvent;

public class RawWapiMessage extends AbstractWapiMessage
{
	private final WapiEndpoint sourceEndpoint;
	private final WapiEvent wapiEvent;

	public RawWapiMessage(final WapiEndpoint sourceEndpoint, final WapiEvent wapiEvent, final SocketAddress source)
	{
		super(source);

		this.sourceEndpoint = sourceEndpoint;
		this.wapiEvent = wapiEvent;
	}

	public WapiEndpoint getSourceEndpoint()
	{
		return sourceEndpoint;
	}

	public WapiEvent getWapiEvent()
	{
		return wapiEvent;
	}

	@Override
	public WapiEvent toWapiEvent()
	{
		return wapiEvent;
	}

	@Override
	public String toString()
	{
		return "RawWapiMessage [wapiEvent token=" + wapiEvent.token() + ", type=" + wapiEvent.type() + ", value=" + wapiEvent.value() + "]";
	}
}
