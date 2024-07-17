package org.jurr.behringer.x32.osc.xremoteproxy.messages;

import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.AbstractEndpoint;

public class AbstractQLCPlusOSCMessage extends AbstractOSCMessage
{
	protected AbstractQLCPlusOSCMessage(final AbstractEndpoint source, final byte[] data)
	{
		super(source, data);
	}
}
