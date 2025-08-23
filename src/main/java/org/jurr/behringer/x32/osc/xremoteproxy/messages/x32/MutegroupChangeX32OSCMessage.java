package org.jurr.behringer.x32.osc.xremoteproxy.messages.x32;

import java.lang.invoke.MethodHandles;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.CorruptOSCMessageException;

public class MutegroupChangeX32OSCMessage extends AbstractX32OSCMessage
{
	private static final Pattern ADDRESS = Pattern.compile("^/config/mute/(?<mutegroupId>\\d)");
	private static final Integer VALUE_RELEASED = Integer.valueOf(0);
	private static final Integer VALUE_PRESSED = Integer.valueOf(1);

	private final byte mutegroup;
	private final boolean pressed;

	public static MutegroupChangeX32OSCMessage fromOSCMessage(final OSCMessage oscMessage, final SocketAddress source)
	{
		final Matcher matcher = ADDRESS.matcher(oscMessage.getAddress());
		if (!matcher.matches())
		{
			return null;
		}

		if (oscMessage.getArguments().size() != 1 || !(oscMessage.getArguments().get(0) instanceof Integer))
		{
			throw new CorruptOSCMessageException("Expected exactly one argument of type Integer", oscMessage);
		}

		final byte mutegroupId = Byte.parseByte(matcher.group("mutegroupId"));

		final boolean pressed = ((Integer) oscMessage.getArguments().get(0)).equals(VALUE_PRESSED);

		return new MutegroupChangeX32OSCMessage(mutegroupId, pressed, source);
	}

	public MutegroupChangeX32OSCMessage(final byte mutegroup, final boolean pressed, final SocketAddress source)
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
	public OSCPacket toOSCPacket()
	{
		return new OSCMessage("/config/mute/" + mutegroup, Collections.singletonList(pressed ? VALUE_PRESSED : VALUE_RELEASED));
	}

	@Override
	public String toString()
	{
		return MethodHandles.lookup().lookupClass().getSimpleName() + " [mutegroup=" + mutegroup + ", pressed=" + pressed + "]";
	}
}
