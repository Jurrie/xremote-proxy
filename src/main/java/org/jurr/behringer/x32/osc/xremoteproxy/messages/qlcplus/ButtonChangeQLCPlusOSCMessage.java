package org.jurr.behringer.x32.osc.xremoteproxy.messages.qlcplus;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.CorruptOSCMessageException;

public class ButtonChangeQLCPlusOSCMessage extends AbstractQLCPlusOSCMessage
{
	private static final Pattern ADDRESS = Pattern.compile("^/button/(?<buttonId>[ABC]\\d{1,2})/value.*");
	private static final Float VALUE_RELEASED = Float.valueOf("0.0");
	private static final Float VALUE_PRESSED = Float.valueOf("1.0");

	public enum QLCPlusButton
	{
		// @formatter:off
		A5("A5"), A6("A6"), A7("A7"), A8("A8"), A9("A9"), A10("A10"), A11("A11"), A12("A12"),
		B5("B5"), B6("B6"), B7("B7"), B8("B8"), B9("B9"), B10("B10"), B11("B11"), B12("B12"),
		C5("C5"), C6("C6"), C7("C7"), C8("C8"), C9("C9"), C10("C10"), C11("C11"), C12("C12");
		// @formatter:on

		private final String buttonId;

		private QLCPlusButton(final String buttonId)
		{
			this.buttonId = buttonId;
		}

		public String getButtonId()
		{
			return buttonId;
		}

		public static QLCPlusButton tryFromId(final String buttonId)
		{
			for (final QLCPlusButton button : QLCPlusButton.values())
			{
				if (button.getButtonId().equals(buttonId))
				{
					return button;
				}
			}

			return null;
		}
	}

	private final QLCPlusButton button;
	private final boolean pressed;

	public static ButtonChangeQLCPlusOSCMessage fromOSCMessage(final OSCMessage oscMessage)
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

		final QLCPlusButton buttonId = QLCPlusButton.tryFromId(matcher.group("buttonId"));
		if (buttonId == null)
		{
			return null;
		}

		final boolean pressed = ((Float) oscMessage.getArguments().get(0)).equals(VALUE_PRESSED);

		return new ButtonChangeQLCPlusOSCMessage(buttonId, pressed);
	}

	public ButtonChangeQLCPlusOSCMessage(final QLCPlusButton button, final boolean pressed)
	{
		this.button = button;
		this.pressed = pressed;
	}

	public QLCPlusButton getButton()
	{
		return button;
	}

	public boolean isPressed()
	{
		return pressed;
	}

	@Override
	public String toString()
	{
		return "ButtonChangeQLCPlusOSCMessage [button=" + button + ", pressed=" + pressed + "]";
	}

	@Override
	public OSCPacket toOSCPacket()
	{
		return new OSCMessage("/button/" + button.getButtonId() + "/value", Collections.singletonList(pressed ? VALUE_PRESSED : VALUE_RELEASED));
	}
}
