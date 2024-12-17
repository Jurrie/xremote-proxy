package org.jurr.behringer.x32.osc.xremoteproxy.messages.x32;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;

public abstract class AbstractX32OSCMessage extends AbstractOSCMessage
{
	protected AbstractX32OSCMessage(final SocketAddress source)
	{
		super(source);
	}

	protected static boolean byteArrayStartsWith(final byte[] array, final String prefix)
	{
		return byteArrayStartsWith(array, prefix.getBytes(StandardCharsets.US_ASCII));
	}

	protected static boolean byteArrayStartsWith(final byte[] array, final byte[] prefix)
	{
		if (prefix.length > array.length)
		{
			return false;
		}

		final byte[] truncatedArray = new byte[prefix.length];
		System.arraycopy(array, 0, truncatedArray, 0, prefix.length);

		return byteArrayEqual(truncatedArray, prefix);
	}

	protected static boolean byteArrayEqual(final byte[] a, final byte[] b)
	{
		if (a.length != b.length)
		{
			return false;
		}

		for (int i = 0; i < a.length; i++)
		{
			if (a[i] != b[i])
			{
				return false;
			}
		}

		return true;
	}
}
