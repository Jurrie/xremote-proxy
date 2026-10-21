package org.jurr.behringer.x32.osc.xremoteproxy.routers;

import org.jurr.behringer.x32.osc.xremoteproxy.endpoint.AbstractEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.qlcplus.endpoint.QLCPlusEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.qlcplus.messages.ButtonChangeQLCPlusOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.qlcplus.messages.ButtonChangeQLCPlusOSCMessage.QLCPlusButton;
import org.jurr.behringer.x32.osc.xremoteproxy.wapi.endpoint.WapiEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.wapi.messages.ButtonChangeWapiMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.wapi.messages.ButtonChangeWapiMessage.WapiButton;
import org.jurr.behringer.x32.osc.xremoteproxy.x32.endpoint.X32Endpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.x32.messages.ButtonChangeX32OSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.x32.messages.ButtonChangeX32OSCMessage.X32Button;

public class WapiMessageRouter extends AbstractRouter
{
	@Override
	public ReceiveResult onMessageReceived(final AbstractEndpoint<?, ?> sourceEndpoint, final AbstractMessage message)
	{
		// To prevent sending back to the party that sends the original message to us first we need to retain the 'source' property.
		// So when converting an OSC message between X32 variant and QLC+ variant we copy the source property.

		if (message instanceof ButtonChangeWapiMessage buttonPressedMsg)
		{
			final ButtonChangeQLCPlusOSCMessage qlcPlusMessage = new ButtonChangeQLCPlusOSCMessage(mapToQLCPlus(buttonPressedMsg.getButton()), buttonPressedMsg.isPressed(), buttonPressedMsg.getSource());
			getBus().getEndpoints(QLCPlusEndpoint.class).forEach(client -> send(client, qlcPlusMessage));

			final ButtonChangeX32OSCMessage x32Message = new ButtonChangeX32OSCMessage(mapToX32(buttonPressedMsg.getButton()), buttonPressedMsg.isPressed(), buttonPressedMsg.getSource());
			getBus().getEndpoints(X32Endpoint.class).forEach(client -> send(client, x32Message));

			getBus().getEndpoints(WapiEndpoint.class).forEach(client -> send(client, buttonPressedMsg));
		}
		else
		{
			return ReceiveResult.DID_NOT_HANDLE;
		}

		return ReceiveResult.HANDLED_CONTINUE_ROUTING;
	}

	private static QLCPlusButton mapToQLCPlus(final WapiButton input)
	{
		// TODO: This is highly specific to our use case!
		return switch (input)
		{
		case WapiButton.U1 -> QLCPlusButton.C5;
		case WapiButton.U2 -> QLCPlusButton.C6;
		case WapiButton.U3 -> QLCPlusButton.C7;
		case WapiButton.U4 -> QLCPlusButton.C8;
		case WapiButton.U5 -> QLCPlusButton.C9;
		case WapiButton.U6 -> QLCPlusButton.C10;
		case WapiButton.U7 -> QLCPlusButton.C11;
		case WapiButton.U8 -> QLCPlusButton.C12;
		};
	}

	private static X32Button mapToX32(final WapiButton input)
	{
		// TODO: This is highly specific to our use case!
		return switch (input)
		{
		case WapiButton.U1 -> X32Button.C5;
		case WapiButton.U2 -> X32Button.C6;
		case WapiButton.U3 -> X32Button.C7;
		case WapiButton.U4 -> X32Button.C8;
		case WapiButton.U5 -> X32Button.C9;
		case WapiButton.U6 -> X32Button.C10;
		case WapiButton.U7 -> X32Button.C11;
		case WapiButton.U8 -> X32Button.C12;
		};
	}
}
