package org.jurr.behringer.x32.osc.xremoteproxy.routers;

import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.AbstractEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.qlcplus.QLCPlusEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.x32.X32Endpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.qlcplus.ButtonChangeQLCPlusOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.qlcplus.ButtonChangeQLCPlusOSCMessage.QLCPlusButton;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.qlcplus.EncoderChangeQLCPlusOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.qlcplus.EncoderChangeQLCPlusOSCMessage.QLCPlusEncoder;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.x32.ButtonChangeX32OSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.x32.ButtonChangeX32OSCMessage.X32Button;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.x32.EncoderChangeX32OSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.x32.EncoderChangeX32OSCMessage.X32Encoder;

public class X32ToQLCPlusRouter extends AbstractRouter
{
	@Override
	public ReceiveResult onMessageReceived(final AbstractEndpoint<?> sourceEndpoint, final AbstractOSCMessage message)
	{
		// To prevent sending back to the party that sends the original message to us first we need to retain the 'source' property.
		// So when converting an OSC message between X32 variant and QLC+ variant we copy the source property.

		if (message instanceof ButtonChangeX32OSCMessage buttonPressedMsg)
		{
			final ButtonChangeQLCPlusOSCMessage qlcPlusMessage = new ButtonChangeQLCPlusOSCMessage(map(buttonPressedMsg.getButton()), buttonPressedMsg.isPressed(), buttonPressedMsg.getSource());
			getBus().getEndpoints(QLCPlusEndpoint.class).forEach(client -> send(client, qlcPlusMessage));
			getBus().getEndpoints(X32Endpoint.class).forEach(client -> send(client, buttonPressedMsg));
		}
		else if (message instanceof EncoderChangeX32OSCMessage encoderChangeMsg)
		{
			final EncoderChangeQLCPlusOSCMessage qlcPlusMessage = new EncoderChangeQLCPlusOSCMessage(map(encoderChangeMsg.getEncoder()), encoderChangeMsg.getValue() / 127f, encoderChangeMsg.getSource());
			getBus().getEndpoints(QLCPlusEndpoint.class).forEach(client -> send(client, qlcPlusMessage));
			getBus().getEndpoints(X32Endpoint.class).forEach(client -> send(client, encoderChangeMsg));
		}
		else if (message instanceof ButtonChangeQLCPlusOSCMessage buttonPressedMsg)
		{
			final ButtonChangeX32OSCMessage x32Message = new ButtonChangeX32OSCMessage(map(buttonPressedMsg.getButton()), buttonPressedMsg.isPressed(), buttonPressedMsg.getSource());
			getBus().getEndpoints(X32Endpoint.class).forEach(client -> send(client, x32Message));
			getBus().getEndpoints(QLCPlusEndpoint.class).forEach(client -> send(client, buttonPressedMsg));
		}
		else if (message instanceof EncoderChangeQLCPlusOSCMessage encoderChangeMsg)
		{
			final EncoderChangeX32OSCMessage x32Message = new EncoderChangeX32OSCMessage(map(encoderChangeMsg.getEncoder()), (byte) (encoderChangeMsg.getValue() * 127), encoderChangeMsg.getSource());
			getBus().getEndpoints(X32Endpoint.class).forEach(client -> send(client, x32Message));
			getBus().getEndpoints(QLCPlusEndpoint.class).forEach(client -> send(client, encoderChangeMsg));
		}
		else
		{
			return ReceiveResult.DID_NOT_HANDLE;
		}

		return ReceiveResult.HANDLED_CONTINUE_ROUTING;
	}

	private static X32Button map(final QLCPlusButton input)
	{
		return switch (input)
		{
		case A5 -> X32Button.A5;
		case A6 -> X32Button.A6;
		case A7 -> X32Button.A7;
		case A8 -> X32Button.A8;
		case A9 -> X32Button.A9;
		case A10 -> X32Button.A10;
		case A11 -> X32Button.A11;
		case A12 -> X32Button.A12;
		case B5 -> X32Button.B5;
		case B6 -> X32Button.B6;
		case B7 -> X32Button.B7;
		case B8 -> X32Button.B8;
		case B9 -> X32Button.B9;
		case B10 -> X32Button.B10;
		case B11 -> X32Button.B11;
		case B12 -> X32Button.B12;
		case C5 -> X32Button.C5;
		case C6 -> X32Button.C6;
		case C7 -> X32Button.C7;
		case C8 -> X32Button.C8;
		case C9 -> X32Button.C9;
		case C10 -> X32Button.C10;
		case C11 -> X32Button.C11;
		case C12 -> X32Button.C12;
		};
	}

	private static X32Encoder map(final QLCPlusEncoder input)
	{
		return switch (input)
		{
		case A1 -> X32Encoder.A1;
		case A2 -> X32Encoder.A2;
		case A3 -> X32Encoder.A3;
		case A4 -> X32Encoder.A4;
		case B1 -> X32Encoder.B1;
		case B2 -> X32Encoder.B2;
		case B3 -> X32Encoder.B3;
		case B4 -> X32Encoder.B4;
		case C1 -> X32Encoder.C1;
		case C2 -> X32Encoder.C2;
		case C3 -> X32Encoder.C3;
		case C4 -> X32Encoder.C4;
		};
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

	private static QLCPlusEncoder map(final X32Encoder input)
	{
		return switch (input)
		{
		case A1 -> QLCPlusEncoder.A1;
		case A2 -> QLCPlusEncoder.A2;
		case A3 -> QLCPlusEncoder.A3;
		case A4 -> QLCPlusEncoder.A4;
		case B1 -> QLCPlusEncoder.B1;
		case B2 -> QLCPlusEncoder.B2;
		case B3 -> QLCPlusEncoder.B3;
		case B4 -> QLCPlusEncoder.B4;
		case C1 -> QLCPlusEncoder.C1;
		case C2 -> QLCPlusEncoder.C2;
		case C3 -> QLCPlusEncoder.C3;
		case C4 -> QLCPlusEncoder.C4;
		};
	}
}
