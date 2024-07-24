package org.jurr.behringer.x32.osc.xremoteproxy.messages;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.AbstractEndpoint;

public class RawOSCMessage extends AbstractOSCMessage
{
	private final OSCPacket oscPacket;

	public RawOSCMessage(final AbstractEndpoint<?> source, final OSCPacket oscPacket)
	{
		super();

		this.oscPacket = oscPacket;
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
