package org.jurr.behringer.x32.osc.xremoteproxy.wapi.messages;

import java.lang.invoke.MethodHandles;
import java.net.SocketAddress;

import org.jurr.java.wapi.WapiEvent;
import org.jurr.java.wapi.WapiToken;
import org.jurr.java.wapi.WapiValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ButtonChangeWapiMessage extends AbstractWapiMessage
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final int VALUE_PRESSED = 127;
	private static final int VALUE_RELEASED = 0;

	public enum WapiButton
	{
		// @formatter:off
		U1("$ctl.user.U1.1.bu.3"),
		U2("$ctl.user.U1.2.bu.3"),
		U3("$ctl.user.U1.3.bu.3"),
		U4("$ctl.user.U1.4.bu.3"),
		U5("$ctl.user.U2.1.bu.3"),
		U6("$ctl.user.U2.2.bu.3"),
		U7("$ctl.user.U2.3.bu.3"),
		U8("$ctl.user.U2.4.bu.3");
		// @formatter:on

		private final WapiToken token;

		private WapiButton(final String name)
		{
			token = new WapiToken(name);
		}

		public WapiToken getToken()
		{
			return token;
		}

		public String getName()
		{
			return token.name();
		}

		public static WapiButton fromHash(final int hash)
		{
			for (final WapiButton button : WapiButton.values())
			{
				if (button.token.hash() == hash)
				{
					return button;
				}
			}

			return null;
		}
	}

	private final WapiButton button;
	private final boolean pressed;

	public static ButtonChangeWapiMessage fromWapiEvent(final WapiEvent wapiEvent, final SocketAddress source)
	{
		final var button = WapiButton.fromHash(wapiEvent.token().hash());
		if (button == null)
		{
			LOGGER.trace("Received WAPI event with unknown token: {} - probably some other message", wapiEvent.token().name());
			return null;
		}

		final boolean pressed = wapiEvent.value().getIntValue() == VALUE_PRESSED;

		return new ButtonChangeWapiMessage(button, pressed, source);
	}

	public ButtonChangeWapiMessage(final WapiButton button, final boolean pressed, final SocketAddress source)
	{
		super(source);
		this.button = button;
		this.pressed = pressed;
	}

	public WapiButton getButton()
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
		return MethodHandles.lookup().lookupClass().getSimpleName() + " [button=" + button + ", pressed=" + pressed + "]";
	}

	@Override
	public WapiEvent toWapiEvent()
	{
		final WapiValue value = WapiValue.ofInt(pressed ? VALUE_PRESSED : VALUE_RELEASED);
		return new WapiEvent(button.getToken(), value.getType(), value);
	}
}
