package org.jurr.behringer.x32.osc.xremoteproxy.endpoints.x32;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.illposed.osc.BytesReceiver;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCSerializeException;
import com.illposed.osc.OSCSerializer;
import com.illposed.osc.OSCSerializerAndParserBuilder;
import com.illposed.osc.argument.ArgumentHandler;

/**
 * This class is a workaround for the fact that the X32 does not support the OSC 1.1 specification, which requires
 * messages to trail with a '\0'. This class will handle the "/xremote" messages differently, by not trailing
 * them with a '\0'.
 */
class X32OSCSerializerAndParserBuilder extends OSCSerializerAndParserBuilder
{
	@Override
	public OSCSerializer buildSerializer(final BytesReceiver output)
	{
		final OSCSerializer serializer = super.buildSerializer(output);

		return new X32OSCSerializer(serializer, output);
	}

	private static class X32OSCSerializer extends OSCSerializer
	{
		private final OSCSerializer serializer;
		private final BytesReceiver output;

		public X32OSCSerializer(final OSCSerializer serializer, final BytesReceiver output)
		{
			super(Collections.emptyList(), serializer.getProperties(), output);

			this.serializer = serializer;
			this.output = output;
		}

		@Override
		public void write(final OSCPacket packet) throws OSCSerializeException
		{
			if (packet instanceof OSCMessage message && message.getAddress().startsWith("/xremote"))
			{
				output.put(message.getAddress().getBytes(StandardCharsets.US_ASCII));
			}
			else
			{
				serializer.write(packet);
			}
		}

		@Override
		@SuppressWarnings("rawtypes")
		public Map<Class, ArgumentHandler> getClassToTypeMapping()
		{
			return serializer.getClassToTypeMapping();
		}

		@Override
		public Map<String, Object> getProperties()
		{
			return serializer.getProperties();
		}

		@Override
		public void writeOnlyTypeTags(final List<?> arguments) throws OSCSerializeException
		{
			serializer.writeOnlyTypeTags(arguments);
		}
	}
}
