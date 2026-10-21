package org.jurr.behringer.x32.osc.xremoteproxy.messages;

import java.net.SocketAddress;

import com.illposed.osc.OSCPacket;

public abstract class AbstractOSCMessage extends AbstractMessage
{
	protected AbstractOSCMessage(final SocketAddress source)
	{
		super(source);
	}

	/**
	 * Should return the OSC packet that we can use to send to the device in question.
	 *
	 * @return
	 */
	public abstract OSCPacket toOSCPacket();
}
