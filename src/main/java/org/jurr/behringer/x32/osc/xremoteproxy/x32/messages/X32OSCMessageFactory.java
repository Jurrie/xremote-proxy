package org.jurr.behringer.x32.osc.xremoteproxy.x32.messages;

import java.net.SocketAddress;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoint.AbstractUDPEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.RawOSCMessage;

public class X32OSCMessageFactory
{
	private X32OSCMessageFactory()
	{
	}

	public static AbstractOSCMessage fromData(final AbstractUDPEndpoint<?> sourceEndPoint, final OSCPacket oscPacket, final SocketAddress source)
	{
		AbstractX32OSCMessage message = null;

		if (oscPacket instanceof OSCMessage oscMessage)
		{
			message = ButtonChangeX32OSCMessage.fromOSCMessage(oscMessage, source);
			if (message != null)
			{
				return message;
			}

			message = EncoderChangeX32OSCMessage.fromOSCMessage(oscMessage, source);
			if (message != null)
			{
				return message;
			}

			message = MutegroupChangeX32OSCMessage.fromOSCMessage(oscMessage, source);
			if (message != null)
			{
				return message;
			}
		}

		return new RawOSCMessage(sourceEndPoint, oscPacket, source);
	}
}
