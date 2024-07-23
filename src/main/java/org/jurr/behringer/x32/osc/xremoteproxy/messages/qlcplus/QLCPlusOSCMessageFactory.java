package org.jurr.behringer.x32.osc.xremoteproxy.messages.qlcplus;

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

	public static AbstractOSCMessage fromData(final AbstractEndpoint<?> source, final OSCPacket data)
	{
		AbstractQLCPlusOSCMessage message = null;

		if (data instanceof OSCMessage oscMessage)
		{
			message = ButtonChangeQLCPlusOSCMessage.fromOSCMessage(oscMessage);
			if (message != null)
			{
				return message;
			}

			message = EncoderChangeQLCPlusOSCMessage.fromOSCMessage(oscMessage);
			if (message != null)
			{
				return message;
			}
		}

		return new RawOSCMessage(source, data);
	}
}
