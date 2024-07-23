package org.jurr.behringer.x32.osc.xremoteproxy.messages.qlcplus;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.CorruptOSCMessageException;

public class EncoderChangeQLCPlusOSCMessage extends AbstractQLCPlusOSCMessage
{
	private static final Pattern ADDRESS = Pattern.compile("^/encoder/(?<encoderId>[ABC]\\d{1})/value.*");

	public enum QLCPlusEncoder
	{
		// @formatter:off
		A1("A1"), A2("A2"), A3("A3"), A4("A4"),
		B1("B1"), B2("B2"), B3("B3"), B4("B4"),
		C1("C1"), C2("C2"), C3("C3"), C4("C4");
		// @formatter:on

		private final String encoderId;

		private QLCPlusEncoder(final String encoderId)
		{
			this.encoderId = encoderId;
		}

		public String getEncoderId()
		{
			return encoderId;
		}

		public static QLCPlusEncoder tryFromId(final String encoderId)
		{
			for (final QLCPlusEncoder encoder : QLCPlusEncoder.values())
			{
				if (encoder.getEncoderId().equals(encoderId))
				{
					return encoder;
				}
			}

			return null;
		}
	}

	private final QLCPlusEncoder encoder;
	private final float value;

	public static EncoderChangeQLCPlusOSCMessage fromOSCMessage(final OSCMessage oscMessage)
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

		final QLCPlusEncoder encoderId = QLCPlusEncoder.tryFromId(matcher.group("encoderId"));
		if (encoderId == null)
		{
			return null;
		}

		final float value = (Float) oscMessage.getArguments().get(0);

		return new EncoderChangeQLCPlusOSCMessage(encoderId, value);
	}

	public EncoderChangeQLCPlusOSCMessage(final QLCPlusEncoder encoder, final float value)
	{
		this.encoder = encoder;
		this.value = value;
	}

	public QLCPlusEncoder getEncoder()
	{
		return encoder;
	}

	public float getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return "EncoderChangeQLCPlusOSCMessage [encoder=" + encoder + ", value=" + value + "]";
	}

	@Override
	public OSCPacket toOSCPacket()
	{
		return new OSCMessage("/encoder/" + encoder.getEncoderId() + "/value", Collections.singletonList(value));
	}
}
