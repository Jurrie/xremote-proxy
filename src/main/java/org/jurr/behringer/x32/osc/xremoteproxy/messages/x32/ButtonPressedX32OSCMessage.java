package org.jurr.behringer.x32.osc.xremoteproxy.messages.x32;

import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.AbstractEndpoint;

public class ButtonPressedX32OSCMessage extends AbstractX32OSCMessage
{
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

		public static X32Button fromId(final int buttonId)
		{
			for (final X32Button button : X32Button.values())
			{
				if (button.getButtonId() == buttonId)
				{
					return button;
				}
			}

			throw new IllegalArgumentException("Unknown button ID: " + buttonId);
		}
	}

	private final X32Button button;
	private final boolean pressed;

	public static ButtonPressedX32OSCMessage fromData(final AbstractEndpoint source, final byte[] data)
	{
		if (!byteArrayStartsWith(data, "/-stat/userpar/"))
		{
			return null;
		}

		final int buttonId = 0; // TODO: Parse me!
		final boolean pressed = false;

		return new ButtonPressedX32OSCMessage(source, data, buttonId, pressed);
	}

	private ButtonPressedX32OSCMessage(final AbstractEndpoint source, final byte[] data, final int buttonId, final boolean pressed)
	{
		super(source, data);

		button = X32Button.fromId(buttonId);
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
}
