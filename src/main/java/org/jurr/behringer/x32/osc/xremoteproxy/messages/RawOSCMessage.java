package org.jurr.behringer.x32.osc.xremoteproxy.messages;

import java.net.SocketAddress;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoint.AbstractUDPEndpoint;

public class RawOSCMessage extends AbstractOSCMessage implements RawMessage
{
	private final AbstractUDPEndpoint<?> sourceEndpoint;
	private final OSCPacket oscPacket;

	public RawOSCMessage(final AbstractUDPEndpoint<?> sourceEndpoint, final OSCPacket oscPacket, final SocketAddress source)
	{
		super(source);

		this.sourceEndpoint = sourceEndpoint;
		this.oscPacket = oscPacket;
	}

	public AbstractUDPEndpoint<?> getSourceEndpoint()
	{
		return sourceEndpoint;
	}

	public OSCPacket getOscPacket()
	{
		return oscPacket;
	}

	@Override
	public OSCPacket toOSCPacket()
	{
		return oscPacket;
	}

	@Override
	public String toString()
	{
		return switch (oscPacket)
		{
		case OSCMessage rawOSCMsg -> "RawOSCMessage [oscMessage address=" + rawOSCMsg.getAddress() + "]";
		case OSCBundle rawOSCBundle -> "RawOSCMessage [oscBundle timestamp=" + rawOSCBundle.getTimestamp() + "]";
		default -> "RawOSCMessage [oscPacket=" + oscPacket + "]";
		};
	}
}
