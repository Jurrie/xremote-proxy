package org.jurr.behringer.x32.osc.xremoteproxy.messages;

import com.illposed.osc.OSCPacket;

public abstract class AbstractOSCMessage
{
	protected AbstractOSCMessage()
	{
	}

	public abstract OSCPacket toOSCPacket();
}
