package org.jurr.behringer.x32.osc.xremoteproxy.routers;

import java.util.Optional;

import org.jurr.behringer.x32.osc.xremoteproxy.endpoint.AbstractEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.qlcplus.endpoint.QLCPlusEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.qlcplus.messages.ButtonChangeQLCPlusOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.qlcplus.messages.ButtonChangeQLCPlusOSCMessage.QLCPlusButton;
import org.jurr.behringer.x32.osc.xremoteproxy.qlcplus.messages.EncoderChangeQLCPlusOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.qlcplus.messages.EncoderChangeQLCPlusOSCMessage.QLCPlusEncoder;
import org.jurr.behringer.x32.osc.xremoteproxy.qlcplus.messages.MutegroupChangeQLCPlusOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.wapi.endpoint.WapiEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.wapi.messages.ButtonChangeWapiMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.wapi.messages.ButtonChangeWapiMessage.WapiButton;
import org.jurr.behringer.x32.osc.xremoteproxy.x32.endpoint.X32Endpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.x32.messages.ButtonChangeX32OSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.x32.messages.ButtonChangeX32OSCMessage.X32Button;
import org.jurr.behringer.x32.osc.xremoteproxy.x32.messages.EncoderChangeX32OSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.x32.messages.EncoderChangeX32OSCMessage.X32Encoder;
import org.jurr.behringer.x32.osc.xremoteproxy.x32.messages.MutegroupChangeX32OSCMessage;

public class QLCPlusMessageRouter extends AbstractRouter
{
	@Override
	public ReceiveResult onMessageReceived(final AbstractEndpoint<?, ?> sourceEndpoint, final AbstractMessage message)
	{
		// To prevent sending back to the party that sends the original message to us first we need to retain the 'source' property.
		// So when converting an OSC message between X32 variant and QLC+ variant we copy the source property.

		if (message instanceof ButtonChangeQLCPlusOSCMessage buttonPressedMsg)
		{
			final ButtonChangeX32OSCMessage x32Message = new ButtonChangeX32OSCMessage(mapToX32(buttonPressedMsg.getButton()), buttonPressedMsg.isPressed(), buttonPressedMsg.getSource());
			getBus().getEndpoints(X32Endpoint.class).forEach(client -> send(client, x32Message));

			final var wingButton = mapToWapi(buttonPressedMsg.getButton());
			if (wingButton.isPresent())
			{
				final ButtonChangeWapiMessage wapiMessage = new ButtonChangeWapiMessage(wingButton.get(), buttonPressedMsg.isPressed(), buttonPressedMsg.getSource());
				getBus().getEndpoints(WapiEndpoint.class).forEach(client -> send(client, wapiMessage));
			}

			getBus().getEndpoints(QLCPlusEndpoint.class).forEach(client -> send(client, buttonPressedMsg));
		}
		else if (message instanceof EncoderChangeQLCPlusOSCMessage encoderChangeMsg)
		{
			final EncoderChangeX32OSCMessage x32Message = new EncoderChangeX32OSCMessage(mapToX32(encoderChangeMsg.getEncoder()), (byte) (encoderChangeMsg.getValue() * 127), encoderChangeMsg.getSource());
			getBus().getEndpoints(X32Endpoint.class).forEach(client -> send(client, x32Message));
			getBus().getEndpoints(QLCPlusEndpoint.class).forEach(client -> send(client, encoderChangeMsg));
		}
		else if (message instanceof MutegroupChangeQLCPlusOSCMessage mutegroupMsg)
		{
			final MutegroupChangeX32OSCMessage x32Message = new MutegroupChangeX32OSCMessage(mutegroupMsg.getMutegroup(), mutegroupMsg.isPressed(), mutegroupMsg.getSource());
			getBus().getEndpoints(X32Endpoint.class).forEach(client -> send(client, x32Message));
			getBus().getEndpoints(QLCPlusEndpoint.class).forEach(client -> send(client, mutegroupMsg));
		}
		else
		{
			return ReceiveResult.DID_NOT_HANDLE;
		}

		return ReceiveResult.HANDLED_CONTINUE_ROUTING;
	}

	private static X32Button mapToX32(final QLCPlusButton input)
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

	private static X32Encoder mapToX32(final QLCPlusEncoder input)
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

	private static Optional<WapiButton> mapToWapi(final QLCPlusButton input)
	{
		// TODO: This is highly specific to our use case!
		return switch (input)
		{
		case C5 -> Optional.of(WapiButton.U1);
		case C6 -> Optional.of(WapiButton.U2);
		case C7 -> Optional.of(WapiButton.U3);
		case C8 -> Optional.of(WapiButton.U4);
		case C9 -> Optional.of(WapiButton.U5);
		case C10 -> Optional.of(WapiButton.U6);
		case C11 -> Optional.of(WapiButton.U7);
		case C12 -> Optional.of(WapiButton.U8);
		default -> Optional.empty();
		};
	}
}
