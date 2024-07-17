package org.jurr.behringer.x32.osc.xremoteproxy.routers;

import org.jurr.behringer.x32.osc.xremoteproxy.clients.QLCPlusClient;
import org.jurr.behringer.x32.osc.xremoteproxy.commands.ButtonPressedQLCPlusOSCCommand;
import org.jurr.behringer.x32.osc.xremoteproxy.commands.ButtonPressedQLCPlusOSCCommand.QLCPlusButton;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.x32.ButtonPressedX32OSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.x32.ButtonPressedX32OSCMessage.X32Button;

public class X32ToQLCPlusRouter extends AbstractRouter
{
	@Override
	public void onMessageReceived(final AbstractOSCMessage message)
	{
		if (message instanceof ButtonPressedX32OSCMessage buttonPressedMsg)
		{
			final ButtonPressedQLCPlusOSCCommand qlcPlusCommand = new ButtonPressedQLCPlusOSCCommand(map(buttonPressedMsg.getButton()), buttonPressedMsg.isPressed());
			getBus().getClients(QLCPlusClient.class).forEach(client -> send(client, qlcPlusCommand));
		}
	}

	private static QLCPlusButton map(final X32Button input)
	{
		return switch (input)
		{
		case A5 -> QLCPlusButton.A5;
		case A6 -> QLCPlusButton.A6;
		case A7 -> QLCPlusButton.A7;
		case A8 -> QLCPlusButton.A8;
		case A9 -> QLCPlusButton.A9;
		case A10 -> QLCPlusButton.A10;
		case A11 -> QLCPlusButton.A11;
		case A12 -> QLCPlusButton.A12;
		case B5 -> QLCPlusButton.B5;
		case B6 -> QLCPlusButton.B6;
		case B7 -> QLCPlusButton.B7;
		case B8 -> QLCPlusButton.B8;
		case B9 -> QLCPlusButton.B9;
		case B10 -> QLCPlusButton.B10;
		case B11 -> QLCPlusButton.B11;
		case B12 -> QLCPlusButton.B12;
		case C5 -> QLCPlusButton.C5;
		case C6 -> QLCPlusButton.C6;
		case C7 -> QLCPlusButton.C7;
		case C8 -> QLCPlusButton.C8;
		case C9 -> QLCPlusButton.C9;
		case C10 -> QLCPlusButton.C10;
		case C11 -> QLCPlusButton.C11;
		case C12 -> QLCPlusButton.C12;
		};
	}
}
