package org.jurr.behringer.x32.osc.xremoteproxy;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.jurr.behringer.x32.osc.xremoteproxy.clients.QLCPlusClient;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.x32.EmulatingX32Endpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.x32.X32Endpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.routers.X32ToQLCPlusRouter;
import org.slf4j.Logger;

public class Main
{
	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// TODO: Open a UDP socket for QLC+ instance.
	// TODO: Register with Behringer X32 using /xremote, and do this every 8 seconds.
	// TODO: Forward everything received from X32 to QLC+.
	// TODO: Forward everything received from QLC+ to X32, translating in the process.

	public static void main(String[] args) throws SocketException, UnknownHostException, InterruptedException
	{
		LOGGER.debug("Application starting.");

		final Bus hub = new Bus();

		final X32Endpoint x32Endpoint = new X32Endpoint(InetAddress.getByName("192.168.183.102"));
		hub.registerEndpoint(x32Endpoint);

		final EmulatingX32Endpoint emulatingX32Endpoint = new EmulatingX32Endpoint(12345);
		hub.registerEndpoint(emulatingX32Endpoint);

		final X32ToQLCPlusRouter x32ToQLCPlusRouter = new X32ToQLCPlusRouter();
		hub.registerRouter(x32ToQLCPlusRouter);

		final QLCPlusClient qlcPlusClient = new QLCPlusClient(InetAddress.getByName("192.168.183.102"), 9000);
		hub.registerClient(qlcPlusClient);

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
}
