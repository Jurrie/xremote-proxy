package org.jurr.behringer.x32.osc.xremoteproxy.commands;

public class ButtonPressedQLCPlusOSCCommand extends AbstractQLCPlusOSCCommand
{
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

		public static QLCPlusButton fromId(final String buttonId)
		{
			for (final QLCPlusButton button : QLCPlusButton.values())
			{
				if (button.getButtonId().equals(buttonId))
				{
					return button;
				}
			}

			throw new IllegalArgumentException("Unknown button ID: " + buttonId);
		}
	}

	private final QLCPlusButton button;
	private final boolean pressed;

	public ButtonPressedQLCPlusOSCCommand(final QLCPlusButton button, final boolean pressed)
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
}
