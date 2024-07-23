package org.jurr.behringer.x32.osc.xremoteproxy.messages.x32;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.CorruptOSCMessageException;

public class ButtonChangeX32OSCMessage extends AbstractX32OSCMessage
{
	private static final Pattern ADDRESS = Pattern.compile("^/-stat/userpar/(?<buttonId>\\d{2})/value.*");
	private static final Integer VALUE_RELEASED = Integer.valueOf(0);
	private static final Integer VALUE_PRESSED = Integer.valueOf(127);

	public enum X32Button
	{
		// @formatter:off
		A5(1), A6(2), A7(3), A8(4), A9(5), A10(6), A11(7), A12(8),
		B5(9), B6(10), B7(11), B8(12), B9(13), B10(14), B11(15), B12(16),
		C5(17), C6(18), C7(19), C8(20), C9(21), C10(22), C11(23), C12(24);
		// @formatter:on

		private final int buttonId;

		private X32Button(final int buttonId)
		{
			this.buttonId = buttonId;
		}

		public int getButtonId()
		{
			return buttonId;
		}

		public static X32Button tryFromId(final int buttonId)
		{
			for (final X32Button button : X32Button.values())
			{
				if (button.getButtonId() == buttonId)
				{
					return button;
				}
			}

			return null;
		}
	}

	private final X32Button button;
	private final boolean pressed;

	public static ButtonChangeX32OSCMessage fromOSCMessage(final OSCMessage oscMessage)
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

		final X32Button button = X32Button.tryFromId(Integer.parseInt(matcher.group("buttonId")));
		if (button == null)
		{
			return null;
		}

		final boolean pressed = ((Integer) oscMessage.getArguments().get(0)).equals(VALUE_PRESSED);

		return new ButtonChangeX32OSCMessage(button, pressed);
	}

	public ButtonChangeX32OSCMessage(final X32Button button, final boolean pressed)
	{
		this.button = button;
		this.pressed = pressed;
	}

	public X32Button getButton()
	{
		return button;
	}

	public boolean isPressed()
	{
		return pressed;
	}

	@Override
	public OSCPacket toOSCPacket()
	{
		return new OSCMessage("/-stat/userpar/" + buttonToOSCAddressId(button) + "/value", Collections.singletonList(pressed ? VALUE_PRESSED : VALUE_RELEASED));
	}

	@Override
	public String toString()
	{
		return "ButtonChangeX32OSCMessage [button=" + button + ", pressed=" + pressed + "]";
	}

	private static String buttonToOSCAddressId(final X32Button button)
	{
		return button.getButtonId() > 9 ? Integer.toString(button.getButtonId()) : "0" + button.getButtonId();
	}
}
