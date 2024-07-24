package org.jurr.behringer.x32.osc.xremoteproxy;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.qlcplus.QLCPlusEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.x32.X32Endpoint;

public final class Settings
{
	public static final Settings INSTANCE = new Settings();

	private Settings()
	{
	}

	@Parameter(names = { "-h", "--help" }, description = "Show this help message", help = true)
	private boolean help;

	@Parameter(names = { "--log", "-l", "--verbose", "-v" }, description = "Verbose output")
	private boolean verboseOutput = false;

	@Parameter(names = { "--trace", "-t", "--debug", "-d" }, description = "Trace output (implies -v)")
	private boolean traceOutput = false;

	@Parameter(names = { "--x32" }, description = "Hostname (or IP address) and port where the X32 lives. Format is \"host:port\" and default port is " + X32Endpoint.X32_PORT + ". While typically you'ld only have one X32, this option can be used multiple times, and also accepts a comma-separated list.", required = true, converter = X32InetSocketAddressConverter.class)
	private List<InetSocketAddress> x32Addresses;

	@Parameter(names = { "--fake-x32" }, description = "Hostname (or IP address) and port where fake X32 servers live. A \"fake X32\" is a OSC endpoint that accepts X32 messages, but to which we not send /xremote commands. Format is \"host:port\" and default port is " + X32Endpoint.X32_PORT + ". This option can be used multiple times, and also accepts a comma-separated list.", converter = X32InetSocketAddressConverter.class)
	private List<InetSocketAddress> fakeX32Address = Collections.emptyList();

	@Parameter(names = { "--qlcplus" }, description = "Hostname (or IP address) and port where QLC+ lives. Format is \"host:port\" and default port is " + QLCPlusEndpoint.DEFAULT_SEND_PORT + ". While typically you'ld only have one QLC+ instance, this option can be used multiple times, and also accepts a comma-separated list.", required = true, converter = QLCPlusInetSocketAddressConverter.class)
	private List<InetSocketAddress> qlcPlusAddresses;

	@Parameter(names = { "--qlcplus-listen" }, description = "Hostname (or IP address) and port to listen on for QLC+ messages. Format is \"host:port\" and default is 0.0.0.0:" + QLCPlusEndpoint.DEFAULT_RECEIVE_PORT + ".", converter = QLCPlusListenInetSocketAddressConverter.class)
	private InetSocketAddress qlcPlusListenAddress = new InetSocketAddress(QLCPlusEndpoint.DEFAULT_RECEIVE_PORT);

	@Parameter(names = { "--x32-listen" }, description = "Hostname (or IP address) and port to listen on for messages of X32 clients. Format is \"host:port\" and default is 0.0.0.0:" + X32Endpoint.X32_PORT + ".", converter = X32InetSocketAddressConverter.class)
	private InetSocketAddress x32ListenAddress = new InetSocketAddress(X32Endpoint.X32_PORT);

	public boolean isHelp()
	{
		return help;
	}

	public boolean isVerboseOutput()
	{
		return verboseOutput || traceOutput;
	}

	public boolean isTraceOutput()
	{
		return traceOutput;
	}

	public List<InetSocketAddress> getX32Addresses()
	{
		return x32Addresses;
	}

	public List<InetSocketAddress> getFakeX32Addresses()
	{
		return fakeX32Address;
	}

	public List<InetSocketAddress> getQLCPlusAddresses()
	{
		return qlcPlusAddresses;
	}

	public InetSocketAddress getQLCPlusListenAddress()
	{
		return qlcPlusListenAddress;
	}

	public InetSocketAddress getX32ListenAddress()
	{
		return x32ListenAddress;
	}

	private abstract static class InetSocketAddressConverter implements IStringConverter<InetSocketAddress>
	{
		@Override
		public InetSocketAddress convert(final String value)
		{
			final String[] parts = value.split(":");
			final Integer defaultPort = getDefaultPort();
			if (parts.length == 1 && defaultPort != null)
			{
				return new InetSocketAddress(parts[0], defaultPort);
			}
			else if (parts.length == 2)
			{
				return new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
			}
			else
			{
				throw new ParameterException("Expected format is \"host:port\"");
			}
		}

		protected abstract Integer getDefaultPort();
	}

	private static class X32InetSocketAddressConverter extends InetSocketAddressConverter
	{
		@Override
		protected Integer getDefaultPort()
		{
			return X32Endpoint.X32_PORT;
		}
	}

	private static class QLCPlusInetSocketAddressConverter extends InetSocketAddressConverter
	{
		@Override
		protected Integer getDefaultPort()
		{
			return QLCPlusEndpoint.DEFAULT_SEND_PORT;
		}
	}

	private static class QLCPlusListenInetSocketAddressConverter extends InetSocketAddressConverter
	{
		@Override
		protected Integer getDefaultPort()
		{
			return QLCPlusEndpoint.DEFAULT_RECEIVE_PORT;
		}
	}
}