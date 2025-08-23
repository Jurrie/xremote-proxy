package org.jurr.behringer.x32.osc.xremoteproxy.messages.qlcplus;

import java.lang.invoke.MethodHandles;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.CorruptOSCMessageException;

public class MutegroupChangeQLCPlusOSCMessage extends AbstractQLCPlusOSCMessage
{
	private static final Pattern ADDRESS = Pattern.compile("^/mutegroup/(?<mutegroupId>\\d)/value");
	private static final Float VALUE_RELEASED = Float.valueOf("0.0");
	private static final Float VALUE_PRESSED = Float.valueOf("1.0");

	private final byte mutegroup;
	private final boolean pressed;

	public static MutegroupChangeQLCPlusOSCMessage fromOSCMessage(final OSCMessage oscMessage, final SocketAddress source)
	{
		final Matcher matcher = ADDRESS.matcher(oscMessage.getAddress());
		if (!matcher.matches())
		{
			return null;
		}

		if (oscMessage.getArguments().size() != 1 || !(oscMessage.getArguments().get(0) instanceof Float))
		{
			throw new CorruptOSCMessageException("Expected exactly one argument of type Float", oscMessage);
		}

		final byte mutegroupId = Byte.parseByte(matcher.group("mutegroupId"));

		final boolean pressed = ((Float) oscMessage.getArguments().get(0)).equals(VALUE_PRESSED);

		return new MutegroupChangeQLCPlusOSCMessage(mutegroupId, pressed, source);
	}

	public MutegroupChangeQLCPlusOSCMessage(final byte mutegroup, final boolean pressed, final SocketAddress source)
	{
		super(source);
		this.mutegroup = mutegroup;
		this.pressed = pressed;
	}

	public byte getMutegroup()
	{
		return mutegroup;
	}

	public boolean isPressed()
	{
		return pressed;
	}

	@Override
	public String toString()
	{
		return MethodHandles.lookup().lookupClass().getSimpleName() + " [mutegroup=" + mutegroup + ", pressed=" + pressed + "]";
	}

	@Override
	public OSCPacket toOSCPacket()
	{
		return new OSCMessage("/mutegroup/" + mutegroup + "/value", Collections.singletonList(pressed ? VALUE_PRESSED : VALUE_RELEASED));
	}
}
