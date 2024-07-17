package org.jurr.behringer.x32.osc.xremoteproxy.messages.x32;

import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.AbstractEndpoint;

public class RawX32OSCMessage extends AbstractX32OSCMessage
{
	protected RawX32OSCMessage(final AbstractEndpoint source, final byte[] data)
	{
		super(source, data);
	}
}
