package org.jurr.behringer.x32.osc.xremoteproxy.messages.qlcplus;

import java.net.SocketAddress;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.AbstractEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.RawOSCMessage;

public class QLCPlusOSCMessageFactory
{
	private QLCPlusOSCMessageFactory()
	{
	}

	public static AbstractOSCMessage fromData(final AbstractEndpoint<?> sourceEndpoint, final OSCPacket oscPacket, final SocketAddress source)
	{
		AbstractQLCPlusOSCMessage message = null;

		if (oscPacket instanceof OSCMessage oscMessage)
		{
			message = ButtonChangeQLCPlusOSCMessage.fromOSCMessage(oscMessage, source);
			if (message != null)
			{
				return message;
			}

			message = EncoderChangeQLCPlusOSCMessage.fromOSCMessage(oscMessage, source);
			if (message != null)
			{
				return message;
			}
		}

		return new RawOSCMessage(sourceEndpoint, oscPacket, source);
	}
}
