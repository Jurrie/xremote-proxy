package org.jurr.behringer.x32.osc.xremoteproxy.wapi.transport;

import java.net.SocketAddress;

import org.jurr.behringer.x32.osc.xremoteproxy.transports.Packet;
import org.jurr.java.wapi.WapiEvent;

public class WapiEventAndSource implements Packet
{
	private final WapiEvent wapiEvent;
	private final SocketAddress source;

	public WapiEventAndSource(final WapiEvent wapiEvent, final SocketAddress source)
	{
		this.wapiEvent = wapiEvent;
		this.source = source;
	}

	public WapiEvent getWrappedWapiEvent()
	{
		return wapiEvent;
	}

	public SocketAddress getSource()
	{
		return source;
	}
}
