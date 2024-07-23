package org.jurr.behringer.x32.osc.xremoteproxy.messages.x32;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.CorruptOSCMessageException;

public class EncoderChangeX32OSCMessage extends AbstractX32OSCMessage
{
	private static final Pattern ADDRESS = Pattern.compile("^/-stat/userpar/(?<encoderId>\\d{2})/value.*");

	public enum X32Encoder
	{
		// @formatter:off
		A1(25), A2(26), A3(27), A4(28),
		B1(29), B2(30), B3(31), B4(32),
		C1(33), C2(34), C3(35), C4(36);
		// @formatter:on

		private final int encoderId;

		private X32Encoder(final int encoderId)
		{
			this.encoderId = encoderId;
		}

		public int getEncoderId()
		{
			return encoderId;
		}

		public static X32Encoder tryFromId(final int encoderId)
		{
			for (final X32Encoder encoder : X32Encoder.values())
			{
				if (encoder.getEncoderId() == encoderId)
				{
					return encoder;
				}
			}

			return null;
		}
	}

	private final X32Encoder encoder;
	private final byte value;

	public static EncoderChangeX32OSCMessage fromOSCMessage(final OSCMessage oscMessage)
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

		final X32Encoder encoder = X32Encoder.tryFromId(Integer.parseInt(matcher.group("encoderId")));
		if (encoder == null)
		{
			return null;
		}

		final byte value = ((Integer) oscMessage.getArguments().get(0)).byteValue();

		return new EncoderChangeX32OSCMessage(encoder, value);
	}

	public EncoderChangeX32OSCMessage(final X32Encoder encoder, final byte value)
	{
		this.encoder = encoder;
		this.value = value;
	}

	public X32Encoder getEncoder()
	{
		return encoder;
	}

	public byte getValue()
	{
		return value;
	}

	@Override
	public OSCPacket toOSCPacket()
	{
		return new OSCMessage("/-stat/userpar/" + encoderToOSCAddressId(encoder) + "/value", Collections.singletonList((int) value));
	}

	@Override
	public String toString()
	{
		return "EncoderChangeX32OSCMessage [encoder=" + encoder + ", value=" + value + "]";
	}

	private static String encoderToOSCAddressId(final X32Encoder encoder)
	{
		return Integer.toString(encoder.getEncoderId());
	}
}
