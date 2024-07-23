package org.jurr.behringer.x32.osc.xremoteproxy;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.qlcplus.QLCPlusEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.x32.EmulatingX32Endpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.x32.X32Endpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.routers.X32ToQLCPlusRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// private static final String IP = "127.0.0.1";
	// private static final String IP = "192.168.183.102";
	private static final String IP = "192.168.183.61";

	// TODO: Have this in an XML file for configuration

	public static void main(String[] args) throws InterruptedException, IOException
	{
		LOGGER.debug("Application starting.");

		killStupidJavaOSCDebugLogLine();

		final Bus hub = new Bus();

		final X32Endpoint x32Endpoint = new X32Endpoint(InetAddress.getByName(IP));
		hub.registerEndpoint(x32Endpoint);

		final EmulatingX32Endpoint emulatingX32Endpoint = new EmulatingX32Endpoint(new InetSocketAddress(12345));
		hub.registerEndpoint(emulatingX32Endpoint);

		final QLCPlusEndpoint qlcPlusEndpoint = new QLCPlusEndpoint(new InetSocketAddress(9000), new InetSocketAddress(InetAddress.getByName(IP), 7700));
		hub.registerEndpoint(qlcPlusEndpoint);

		final X32ToQLCPlusRouter x32ToQLCPlusRouter = new X32ToQLCPlusRouter();
		hub.registerRouter(x32ToQLCPlusRouter);

		LOGGER.debug("Application started.");
		try
		{
			System.in.read();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		LOGGER.debug("Application stopping.");
		hub.stop();

		LOGGER.debug("Application stopped.");
	}

	private static void killStupidJavaOSCDebugLogLine()
	{
		((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.illposed.osc.argument.handler.Activator")).setLevel(ch.qos.logback.classic.Level.INFO);
	}
}
