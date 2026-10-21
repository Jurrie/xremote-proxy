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

public class X32MessageRouter extends AbstractRouter
{
	@Override
	public ReceiveResult onMessageReceived(final AbstractEndpoint<?, ?> sourceEndpoint, final AbstractMessage message)
	{
		// To prevent sending back to the party that sends the original message to us first we need to retain the 'source' property.
		// So when converting an OSC message between X32 variant and QLC+ variant we copy the source property.

		if (message instanceof ButtonChangeX32OSCMessage buttonPressedMsg)
		{
			final ButtonChangeQLCPlusOSCMessage qlcPlusMessage = new ButtonChangeQLCPlusOSCMessage(mapToQLCPlus(buttonPressedMsg.getButton()), buttonPressedMsg.isPressed(), buttonPressedMsg.getSource());
			getBus().getEndpoints(QLCPlusEndpoint.class).forEach(client -> send(client, qlcPlusMessage));

			final var wingButton = mapToWapi(buttonPressedMsg.getButton());
			if (wingButton.isPresent())
			{
				final ButtonChangeWapiMessage wapiMessage = new ButtonChangeWapiMessage(wingButton.get(), buttonPressedMsg.isPressed(), buttonPressedMsg.getSource());
				getBus().getEndpoints(WapiEndpoint.class).forEach(client -> send(client, wapiMessage));
			}

			getBus().getEndpoints(X32Endpoint.class).forEach(client -> send(client, buttonPressedMsg));
		}
		else if (message instanceof EncoderChangeX32OSCMessage encoderChangeMsg)
		{
			final EncoderChangeQLCPlusOSCMessage qlcPlusMessage = new EncoderChangeQLCPlusOSCMessage(mapToQLCPlus(encoderChangeMsg.getEncoder()), encoderChangeMsg.getValue() / 127f, encoderChangeMsg.getSource());
			getBus().getEndpoints(QLCPlusEndpoint.class).forEach(client -> send(client, qlcPlusMessage));
			getBus().getEndpoints(X32Endpoint.class).forEach(client -> send(client, encoderChangeMsg));
		}
		else if (message instanceof MutegroupChangeX32OSCMessage mutegroupMsg)
		{
			final MutegroupChangeQLCPlusOSCMessage qlcPlusMessage = new MutegroupChangeQLCPlusOSCMessage(mutegroupMsg.getMutegroup(), mutegroupMsg.isPressed(), mutegroupMsg.getSource());
			getBus().getEndpoints(QLCPlusEndpoint.class).forEach(client -> send(client, qlcPlusMessage));
			getBus().getEndpoints(X32Endpoint.class).forEach(client -> send(client, mutegroupMsg));
		}
		else
		{
			return ReceiveResult.DID_NOT_HANDLE;
		}

		return ReceiveResult.HANDLED_CONTINUE_ROUTING;
	}

	private static QLCPlusButton mapToQLCPlus(final X32Button input)
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

	private static QLCPlusEncoder mapToQLCPlus(final X32Encoder input)
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

	private static Optional<WapiButton> mapToWapi(final X32Button input)
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
