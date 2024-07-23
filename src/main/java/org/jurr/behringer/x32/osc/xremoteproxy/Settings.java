package org.jurr.behringer.x32.osc.xremoteproxy;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.beust.jcommander.Parameter;
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

	@Parameter(names = { "--x32-host" }, description = "Hostname or IP address where the X32 lives", required = true)
	private String x32Host;

	@Parameter(names = { "--qlcplus-host" }, description = "Hostname or IP address where QLC+ lives", required = true)
	private String qlcPlusHost;

	@Parameter(names = { "--qlcplus-port" }, description = "Post where to send QLC+ messages to (default is 7700)")
	private int qlcPlusPort = 7700;

	@Parameter(names = { "--qlcplus-listen-host" }, description = "Hostname or IP address to listen on for QLC+ messages (default is 0.0.0.0)")
	private String qlcPlusListenHost;

	@Parameter(names = { "--qlcplus-listen-port" }, description = "Post where to listen on for QLC+ messages (default is 9000)")
	private int qlcPlusListenPort = 9000;

	@Parameter(names = { "--x32-listen-host" }, description = "Hostname or IP address to listen on for messages of X32 clients (default is 0.0.0.0)")
	private String x32ClientsListenHost;

	@Parameter(names = { "--x32-listen-port" }, description = "Port where to listen on for messages of X32 clients (default is " + X32Endpoint.X32_PORT + ")")
	private int x32ClientsListenPort = X32Endpoint.X32_PORT;

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

	public InetAddress getX32Host() throws UnknownHostException
	{
		return InetAddress.getByName(x32Host);
	}

	public InetSocketAddress getQLCPlusAddress() throws UnknownHostException
	{
		return new InetSocketAddress(InetAddress.getByName(qlcPlusHost), qlcPlusPort);
	}

	public InetSocketAddress getQLCPlusListenAddress() throws UnknownHostException
	{
		return qlcPlusListenHost == null ? new InetSocketAddress(qlcPlusListenPort) : new InetSocketAddress(InetAddress.getByName(qlcPlusListenHost), qlcPlusListenPort);
	}

	public InetSocketAddress getX32ClientsListenAddress() throws UnknownHostException
	{
		return x32ClientsListenHost == null ? new InetSocketAddress(x32ClientsListenPort) : new InetSocketAddress(InetAddress.getByName(x32ClientsListenHost), x32ClientsListenPort);
	}
}