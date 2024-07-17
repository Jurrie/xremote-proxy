package org.jurr.behringer.x32.osc.xremoteproxy.messages.x32;

import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.AbstractEndpoint;

public class X32OSCMessageFactory
{
	// TODO: Enum?
	// private static final Pattern PATTERN_BUTTON_PRESSED = Pattern.compile("^/-stat/userpar/(<button>[:digit:]{2})/value.*");

	private X32OSCMessageFactory()
	{
	}

	public static AbstractX32OSCMessage fromData(final AbstractEndpoint source, final byte[] data)
	{
		new String(data);

		/*
		 * final Matcher matcherButtonPressed = PATTERN_BUTTON_PRESSED.matcher(dataAsString);
		 * if (matcherButtonPressed.matches())
		 * {
		 * // TODO: Get the actual value of 'pressed'
		 * return new ButtonPressedX32OSCMessage(source, data, Integer.parseInt(matcherButtonPressed.group("button")), true);
		 * }
		 */

		AbstractX32OSCMessage message = null;

		message = ButtonPressedX32OSCMessage.fromData(source, data);
		if (message != null)
		{
			return message;
		}

		return new RawX32OSCMessage(source, data);
	}
}
